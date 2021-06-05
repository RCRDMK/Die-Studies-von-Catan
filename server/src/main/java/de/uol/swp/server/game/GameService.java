package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.dto.StatsDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.JoinOnGoingGameMessage;
import de.uol.swp.common.lobby.message.StartGameMessage;
import de.uol.swp.common.lobby.request.JoinOnGoingGameRequest;
import de.uol.swp.common.lobby.request.StartGameRequest;
import de.uol.swp.common.lobby.response.JoinOnGoingGameResponse;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.server.AI.AIToServerTranslator;
import de.uol.swp.server.AI.RandomAI;
import de.uol.swp.server.AI.TestAI;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import de.uol.swp.server.lobby.LobbyManagementException;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Handles the game requests send by the users
 *
 * @author Kirstin, Pieter
 * @since 2021-01-07
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class GameService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);
    private final GameManagement gameManagement;
    private final LobbyService lobbyService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    /**
     * Constructor
     * <p>
     *
     * @param gameManagement        The management class for creating, storing and deleting games
     * @param authenticationService the user management
     * @param eventBus              the server-wide EventBus
     * @since 2021-01-07
     */
    @Inject
    public GameService(GameManagement gameManagement, LobbyService lobbyService, AuthenticationService authenticationService, EventBus eventBus, UserService userService) {
        super(eventBus);
        this.gameManagement = gameManagement;
        this.authenticationService = authenticationService;
        this.lobbyService = lobbyService;
        this.userService = userService;
    }

    @Subscribe
    public void onGameLeaveUserRequest(GameLeaveUserRequest gameLeaveUserRequest) {
        Optional<Game> optionalGame = gameManagement.getGame(gameLeaveUserRequest.getName());
        Optional<Lobby> lobby = lobbyService.getLobby(gameLeaveUserRequest.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            if (game.getUsers().size() == 1) {
                if (gameLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = gameLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new GameLeftSuccessfulResponse(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser()));
                    gameManagement.dropGame(gameLeaveUserRequest.getName());
                    lobby.ifPresent(value -> value.setGameStarted(false));
                    sendToAll(new GameDroppedMessage(gameLeaveUserRequest.getName()));
                }
            } else if (game.getUsers() == null) {
                gameManagement.dropGame(gameLeaveUserRequest.getName());
                lobby.ifPresent(value -> value.setGameStarted(false));
                sendToAll(new GameDroppedMessage(gameLeaveUserRequest.getName()));

            } else {
                if (gameLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = gameLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new GameLeftSuccessfulResponse(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser()));
                }
                game.leaveUser(gameLeaveUserRequest.getUser());
                sendToAll(new GameSizeChangedMessage(gameLeaveUserRequest.getName()));
                ArrayList<UserDTO> usersInGame = new ArrayList<>();
                for (User user : game.getUsers()) usersInGame.add((UserDTO) user);
                sendToAllInGame(gameLeaveUserRequest.getName(), new UserLeftGameMessage(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser(), usersInGame));
                // Check if the player leaving the game is the turnPlayer, so that the AI may replace him now
                if (gameLeaveUserRequest.getUser().equals(game.getUser(game.getTurn()))) {
                    if (!game.rolledDiceThisTurn() && !game.isStartingTurns()) {
                        RollDiceRequest rdr = new RollDiceRequest(game.getName(), game.getUser(game.getTurn()));
                        onRollDiceRequest(rdr);
                    }
                    startTurnForAI((GameDTO) game);
                }
            }
        } else {
            throw new GameManagementException("Game unknown!");
        }
    }

    /**
     * Handles RetrieveAllThisGameUsersRequests found on the EventBus
     * <p>
     * If a RetrieveAllThisGameUsersRequests is detected on the EventBus, this method is called. It prepares the sending
     * of a AllThisGameUsersResponse for a specific user that sent the initial request.
     *
     * @param retrieveAllThisGameUsersRequest The RetrieveAllThisGameUsersRequest found on the EventBus
     * @author Iskander Yusupov
     * @see de.uol.swp.common.game.Game
     * @since 2021-01-15
     */
    @Subscribe
    public void onRetrieveAllThisGameUsersRequest(RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest) {
        Optional<Game> optionalGame = gameManagement.getGame(retrieveAllThisGameUsersRequest.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            ArrayList<User> gameUsers = game.getUsersList();
            Set<User> actualUsers = game.getUsers();
            if (retrieveAllThisGameUsersRequest.getMessageContext().isPresent()) {
                Optional<MessageContext> ctx = retrieveAllThisGameUsersRequest.getMessageContext();
                sendToSpecificUser(ctx.get(), new AllThisGameUsersResponse(actualUsers, gameUsers, retrieveAllThisGameUsersRequest.getName()));
            }
        }
    }

    /**
     * Handles incoming build requests.
     *
     * @param message Contains the data needed to change the mapGraph
     * @author Pieter Vogt
     * @since 2021-04-15
     */
    @Subscribe
    public boolean onConstructionMessage(ConstructionRequest message) {
        LOG.debug("Received new ConstructionMessage from user " + message.getUser());
        Optional<Game> optionalGame = gameManagement.getGame(message.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            Inventory inventory = game.getInventory(message.getUser());
            int playerIndex = 666;
            if (message.getUuid() != null) {
                for (int i = 0; i < game.getUsersList().size(); i++) {
                    if (game.getUsersList().get(i).equals(message.getUser())) {
                        playerIndex = i;
                        break;
                    }
                }
                try {
                    if (message.getTypeOfNode().equals("BuildingNode")) { //If the node from the message is a building node...
                        for (MapGraph.BuildingNode buildingNode : game.getMapGraph().getBuildingNodeHashSet()) {
                            if (message.getUuid().equals(buildingNode.getUuid())) { // ... and if the node in the message is a node in the MapGraph BuildingNodeSet...
                                if (game.isStartingTurns() || ((buildingNode.getSizeOfSettlement() == 0 && inventory.lumber.getNumber() > 0 && inventory.brick.getNumber() > 0
                                        && inventory.wool.getNumber() > 0 && inventory.grain.getNumber() > 0) ||
                                        (buildingNode.getSizeOfSettlement() == 1 && inventory.ore.getNumber() > 2 && inventory.grain.getNumber() > 1))) {
                                    if (buildingNode.tryBuildOrDevelopSettlement(playerIndex, game.getStartingPhase())) {
                                        buildingNode.buildOrDevelopSettlement(playerIndex);
                                        game.getMapGraph().addBuiltBuilding(buildingNode);
                                        sendToAllInGame(game.getName(), new SuccessfulConstructionMessage(game.getName(), message.getUser().getWithoutPassword(), playerIndex,
                                                message.getUuid(), "BuildingNode"));
                                        if (buildingNode.getSizeOfSettlement() == 1) {
                                            if (!game.isStartingTurns()) {
                                                takeResource(game, message.getUser(), "Lumber", 1);
                                                takeResource(game, message.getUser(), "Brick", 1);
                                                takeResource(game, message.getUser(), "Wool", 1);
                                                takeResource(game, message.getUser(), "Grain", 1);
                                            }
                                            inventory.settlement.decNumber();
                                            inventory.setVictoryPoints(inventory.getVictoryPoints() + 1);
                                        } else if (buildingNode.getSizeOfSettlement() == 2) {
                                            takeResource(game, message.getUser(), "Ore", 3);
                                            takeResource(game, message.getUser(), "Grain", 2);
                                            inventory.settlement.incNumber();
                                            inventory.city.decNumber();
                                            inventory.setVictoryPoints(inventory.getVictoryPoints() + 1);
                                        }
                                        if (game.isStartingTurns() && game.getMapGraph().getNumOfRoads()[playerIndex] == game.getStartingPhase()
                                                && game.getMapGraph().getNumOfRoads()[playerIndex] == game.getMapGraph().getNumOfBuildings()[playerIndex]) {
                                            endTurn(game, message.getUser());
                                        }

                                        updateInventory(game);
                                        return true;
                                    } //else sendToAllInGame(game.getName(), new NotSuccessfulConstructionMessage(playerIndex, message.getUuid(), "BuildingNode"));
                                } else {
                                    NotEnoughRessourcesMessage nerm = new NotEnoughRessourcesMessage();
                                    nerm.setName(game.getName());
                                    sendToSpecificUserInGame(nerm, message.getUser());
                                }
                            }
                        }
                    } else {
                        for (MapGraph.StreetNode streetNode : game.getMapGraph().getStreetNodeHashSet()) {
                            if (message.getUuid().equals(streetNode.getUuid())) {
                                if (game.isStartingTurns() || (inventory.lumber.getNumber() > 0 && inventory.brick.getNumber() > 0) || game.getCurrentCard().equals("Road Building")) {
                                    if (streetNode.tryBuildRoad(playerIndex, game.getStartingPhase())) {
                                        streetNode.buildRoad(playerIndex);
                                        if (!game.isStartingTurns() && !game.getCurrentCard().equals("Road Building")) {
                                            takeResource(game, message.getUser(), "Lumber", 1);
                                            takeResource(game, message.getUser(), "Brick", 1);
                                        }
                                        sendToAllInGame(game.getName(), new SuccessfulConstructionMessage(game.getName(), message.getUser().getWithoutPassword(), playerIndex,
                                                message.getUuid(), "StreetNode"));
                                        if (game.isStartingTurns() && game.getMapGraph().getNumOfRoads()[playerIndex] == game.getStartingPhase()
                                                && game.getMapGraph().getNumOfRoads()[playerIndex] == game.getMapGraph().getNumOfBuildings()[playerIndex]) {
                                            endTurn(game, message.getUser());
                                        }

                                        inventory.road.decNumber();
                                        updateInventory(game);
                                        return true;
                                    } //else sendToAllInGame(game.getName(), new NotSuccessfulConstructionMessage(playerIndex, message.getUuid(), "StreetNode"));
                                } else {
                                    NotEnoughRessourcesMessage nerm = new NotEnoughRessourcesMessage();
                                    nerm.setName(game.getName());
                                    sendToSpecificUserInGame(nerm, message.getUser());
                                }
                            }
                        }
                    }


                } catch (GameManagementException e) {
                    LOG.debug(e);
                    LOG.debug("Player " + message.getUser() + " tried to build at node with UUID: " + message.getUuid() + " but it did not work.");
                }
            }
        }
        return false;
    }

    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and posts it on the EventBus
     * <p>
     *
     * @param lobbyName Name of the lobby the players are in
     * @param message   the message to be send to the users
     * @author Marco Grawunder
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyService.getLobby(lobbyName);

        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        } else {
            throw new LobbyManagementException("Lobby unknown!");
        }
    }

    public void sendToAllInGame(String gameName, ServerMessage message) {
        Optional<Game> game = gameManagement.getGame(gameName);

        if (game.isPresent()) {
            message.setReceiver(authenticationService.getSessions(game.get().getUsers()));
            post(message);
        } else {
            throw new GameManagementException("Game unknown!");

        }
    }

    public void sendToListOfUsers(Set<User> receiver, ServerMessage message) {
        message.setReceiver(authenticationService.getSessions(receiver));
        post(message);
    }

    public void sendToSpecificUser(MessageContext ctx, ResponseMessage message) {
        ctx.writeAndFlush(message);
    }

    /**
     * Sends a message to a specific user in the given game
     *
     * @param message ServerMessage message
     * @param user    User user
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-03-11
     */
    public void sendToSpecificUserInGame(ServerMessage message, User user) {
        List<Session> theList = new ArrayList<>();
        authenticationService.getSession(user).ifPresent(theList::add);
        message.setReceiver(theList);
        post(message);
    }

    /**
     * This method is invoked, when a ResourcesToDiscardRequest is posted on the eventbus.
     * <p>
     * If a ResourcesToDiscardRequest is posted on the eventbus this method is invoked. First it checks if the game is present
     * or not. After that it iterates over the list of users in the game to get the right one. If the method found the right user,
     * it sets the new inventory for the user. At the end we call the updateInventory method, to get the right inventory
     * back to the client.
     * enhanced by Anton Nikiforov
     *
     * @param resourcesToDiscardRequest request with a hashmap(inventory), the game name and the user, that send it.
     * @author Marius Birk
     * @since 2021-05-19
     * @since 2021-05-03
     */
    @Subscribe
    public void onResourcesToDiscard(ResourcesToDiscardRequest resourcesToDiscardRequest) {
        Optional<Game> optionalGame = gameManagement.getGame(resourcesToDiscardRequest.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            takeResource(game, resourcesToDiscardRequest.getUser(), "Lumber", game.getInventory(resourcesToDiscardRequest.getUser()).getSpecificResourceAmount("Lumber") - resourcesToDiscardRequest.getInventory().get("Lumber"));
            takeResource(game, resourcesToDiscardRequest.getUser(), "Brick", game.getInventory(resourcesToDiscardRequest.getUser()).getSpecificResourceAmount("Brick") - resourcesToDiscardRequest.getInventory().get("Brick"));
            takeResource(game, resourcesToDiscardRequest.getUser(), "Grain", game.getInventory(resourcesToDiscardRequest.getUser()).getSpecificResourceAmount("Grain") - resourcesToDiscardRequest.getInventory().get("Grain"));
            takeResource(game, resourcesToDiscardRequest.getUser(), "Wool", game.getInventory(resourcesToDiscardRequest.getUser()).getSpecificResourceAmount("Wool") - resourcesToDiscardRequest.getInventory().get("Wool"));
            takeResource(game, resourcesToDiscardRequest.getUser(), "Ore", game.getInventory(resourcesToDiscardRequest.getUser()).getSpecificResourceAmount("Ore") - resourcesToDiscardRequest.getInventory().get("Ore"));
            updateInventory(game);
        }
    }

    @Subscribe
    public void onRetrieveAllGamesRequest(RetrieveAllGamesRequest msg) {
        AllCreatedGamesResponse response = new AllCreatedGamesResponse(this.gameManagement.getAllGames().values());
        response.initWithMessage(msg);
        post(response);
    }

    /**
     * Handles RollDiceRequests found on the EventBus
     * <p>
     * If a RollDiceRequest is detected on the EventBus, this method is called. It rolls the dices and sends a
     * ResponseChatMessage containing the user who roll the dice and the result to every user in the lobby. If a
     * RollDiceRequest is detected on the EventBus, this method is called. It rolls the dices and sends a
     * ResponseChatMessage containing the user who rolls the dice and the result is shown to every user in the game.
     *
     * @param rollDiceRequest The RollDiceRequest found on the EventBus
     * @author Kirstin, Pieter
     * @see RollDiceRequest
     * @see ResponseChatMessage
     * @since 2021-01-07
     * <p>
     * enhanced by René Meyer, Sergej Tulnev
     * @since 2021-04-17
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-03-31
     * <p>
     * Enhanced by Marius Birk & Carsten Dekker
     * @since 2021-4-06
     */
    @Subscribe
    public void onRollDiceRequest(RollDiceRequest rollDiceRequest) {
        LOG.debug("Got new RollDiceRequest from user: " + rollDiceRequest.getUser());
        Optional<Game> optionalGame = gameManagement.getGame(rollDiceRequest.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            if (rollDiceRequest.getUser().equals(game.getUser(game.getTurn()))) {
                Dice dice = new Dice();
                dice.rollDice();
                // Check if cheatEyes number is provided in rollDiceRequest, if so -> set Eyes manually on dice
                // for the roll cheat, else ignore and use rolledDice
                if (rollDiceRequest.getCheatEyes() > 0) {
                    dice.setEyes(rollDiceRequest.getCheatEyes());
                }
                int addedEyes = dice.getDiceEyes1() + dice.getDiceEyes2();
                game.setLastRolledDiceValue(addedEyes);
                if (addedEyes == 7 && game.getUsers().contains(rollDiceRequest.getUser())) {
                    MoveRobberMessage moveRobberMessage = new MoveRobberMessage(rollDiceRequest.getName(), (UserDTO) rollDiceRequest.getUser());
                    sendToSpecificUserInGame(moveRobberMessage, rollDiceRequest.getUser());
                } else {
                    distributeResources(addedEyes, rollDiceRequest.getName());
                }
                //Auskommentierter Bereich sinnvoll für einen Interaktionslog
                /*try {
                    String chatMessage;
                    var chatId = "game_" + rollDiceRequest.getName();
                    if (addedEyes == 8 || addedEyes == 11) {
                        chatMessage = "Player " + rollDiceRequest.getUser().getUsername() + " rolled an " + addedEyes;
                    } else {
                        chatMessage = "Player " + rollDiceRequest.getUser().getUsername() + " rolled a " + addedEyes;
                    }
                    ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId,
                            rollDiceRequest.getUser().getUsername(), System.currentTimeMillis());
                    post(msg);
                    LOG.debug("Posted ResponseChatMessage on eventBus");
                } catch (Exception e) {
                    LOG.debug(e);
                }*/
                try {
                    RollDiceResultMessage result = new RollDiceResultMessage(dice.getDiceEyes1(), dice.getDiceEyes2(), game.getTurn(), game.getName());
                    sendToAllInGame(game.getName(), result);
                } catch (Exception e) {
                    LOG.debug(e);
                }
            } else {
                LOG.debug("It is not your turn. :) " + rollDiceRequest.getUser());
            }
        }
    }


    /**
     * Handles the distribution of resources to the users in the opening turn
     * <p>
     * This method handles the distribution of the resources to the users. First the method gets the game and gets the
     * corresponding hexagons. After that the method gives the second built buildings in the opening turn the resource.
     * coressponding hexagons. After that the method gives the second built buildings in the opening turn the resource.
     * <p>
     * enhanced by Anton Nikiforov
     *
     * @param gameName Name of the Game
     * @author Philip Nitsche
     * @since 2021-05-19
     * @since 2021-04-24
     */
    public void distributeResources(String gameName) {
        Optional<Game> optionalGame = gameManagement.getGame(gameName);
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet())
                for (int i = 0; i < game.getLastBuildingOfOpeningTurn().size(); i++)
                    if (hexagon.getBuildingNodes().contains(game.getLastBuildingOfOpeningTurn().get(i))) {
                        //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
                        switch (hexagon.getTerrainType()) {
                            case 1:
                                giveResource(game, game.getUser(i), "Lumber", 1);
                                break;
                            case 2:
                                giveResource(game, game.getUser(i), "Grain", 1);
                                break;
                            case 3:
                                giveResource(game, game.getUser(i), "Wool", 1);
                                break;
                            case 4:
                                giveResource(game, game.getUser(i), "Brick", 1);
                                break;
                            case 5:
                                giveResource(game, game.getUser(i), "Ore", 1);
                                break;
                            default:
                                break;
                        }
                    }
            updateInventory(game);
        }
    }

    /**
     * Handles the distribution of resources to the users
     * <p>
     * This method handles the distribution of the resources to the users. First the method gets the game and gets the
     * corresponding hexagons. After that the method checks if the diceToken on the field is equal to the
     * rolled amount of eyes and increases the resource of the user by one(village), two(city).
     * <p>
     * enhanced by Anton Nikiforov
     *
     * @param eyes     Number of eyes rolled with dice
     * @param gameName Name of the Game
     * @author Marius Birk, Carsten Dekker, Philip Nitsche
     * @since 2021-05-19
     * @since 2021-04-06
     */
    public void distributeResources(int eyes, String gameName) {
        Optional<Game> optionalGame = gameManagement.getGame(gameName);
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
                if (hexagon.getDiceToken() == eyes) {
                    for (MapGraph.BuildingNode buildingNode : hexagon.getBuildingNodes()) {
                        if (buildingNode.getOccupiedByPlayer() != 666 && !hexagon.isOccupiedByRobber()) {
                            //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
                            switch (hexagon.getTerrainType()) {
                                case 1:
                                    giveResource(game, game.getUser(buildingNode.getOccupiedByPlayer()), "Lumber", buildingNode.getSizeOfSettlement());
                                    break;
                                case 2:
                                    giveResource(game, game.getUser(buildingNode.getOccupiedByPlayer()), "Grain", buildingNode.getSizeOfSettlement());
                                    break;
                                case 3:
                                    giveResource(game, game.getUser(buildingNode.getOccupiedByPlayer()), "Wool", buildingNode.getSizeOfSettlement());
                                    break;
                                case 4:
                                    giveResource(game, game.getUser(buildingNode.getOccupiedByPlayer()), "Brick", buildingNode.getSizeOfSettlement());
                                    break;
                                case 5:
                                    giveResource(game, game.getUser(buildingNode.getOccupiedByPlayer()), "Ore", buildingNode.getSizeOfSettlement());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
            updateInventory(game);
        }
    }

    /**
     * Gives the given User the entered resource from the bank
     * <p>
     * It takes the resource from the bank and gives them to the user one by one until
     * the amount is reached or the bank is empty.
     * Than if it's the first time where the bank gos empty for this resourceTyp,
     * it posts a chat message in the game.
     *
     * @param game        where we are
     * @param user        who wants the resource
     * @param resourceTyp he wants
     * @param amount      of the resource
     * @return success
     * @author Anton Nikiforov
     * @since 2012-05-19
     */
    public boolean giveResource(Game game, User user, String resourceTyp, int amount) {
        if (resourceTyp.equals("")) return false;
        else {
            Inventory bank = game.getBankInventory();
            boolean success = bank.getSpecificResourceAmount(resourceTyp) >= amount;
            boolean firstTime = bank.getSpecificResourceAmount(resourceTyp) != 0 && bank.getSpecificResourceAmount(resourceTyp) <= amount;
            for (int i = amount; i > 0; i--) {
                if (bank.getSpecificResourceAmount(resourceTyp) > 0) {
                    bank.decCardStack(resourceTyp, 1);
                    game.getInventory(user).incCardStack(resourceTyp, 1);
                } else break;
            }
            if (firstTime) {
                String chatMessage = resourceTyp + " storage is empty now";
                String chatId = "game_" + game.getName();
                ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId, "Bank", System.currentTimeMillis());
                post(msg);
            }
            return success;
        }
    }

    /**
     * Takes the entered Resource from given User
     * <p>
     * It takes the resource from the user and gives them to the bank one by one until
     * the amount is reached or the inventory form user is empty.
     * Than if the bank was empty for this resourceTyp, it posts a chat message in the game
     * that the resource storage is now filled.
     *
     * @param game        where we are
     * @param user        who wants to give the resource
     * @param resourceTyp he wants to give
     * @param amount      of the resource
     * @return success
     * @author Anton Nikiforov
     * @since 2012-04-09
     */
    public boolean takeResource(Game game, User user, String resourceTyp, int amount) {
        if (resourceTyp.equals("")) return false;
        else {
            Inventory bank = game.getBankInventory();
            boolean success = game.getInventory(user).getSpecificResourceAmount(resourceTyp) >= amount;
            boolean wasEmpty = bank.getSpecificResourceAmount(resourceTyp) == 0 && amount > 0;
            for (int i = amount; i > 0; i--) {
                if (game.getInventory(user).getSpecificResourceAmount(resourceTyp) > 0) {
                    game.getInventory(user).decCardStack(resourceTyp, 1);
                    bank.incCardStack(resourceTyp, 1);
                } else break;
            }
            if (wasEmpty) {
                String chatMessage = resourceTyp + " storage is now filled";
                String chatId = "game_" + game.getName();
                ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId, "Bank", System.currentTimeMillis());
                post(msg);
            }
            return success;
        }
    }

    /**
     * This method is invoked if a RobbersNewFieldMessage is detected on the event bus.
     * <p>
     * First the method checks if the game is present or not. If it is present, a list of usernames is initialized.
     * After that, the method iterates over every hexagon in the MapGraph to detect the old place of the robber and to
     * set it on false. Now the method checks if the new fields UUID is the same as the UUID of the hexagon and set the
     * occupiedByRobber attribute on true. The new position is now send to all in the game.
     * Next, the method checks if the new places buildingSpots are occupied by players and put their names in the freshly instantiated
     * list of usernames. If all buildingSpots are checked, a ChoosePlayerMessage is send to the user who rolled a 7.
     * <p>
     * enhanced by Marc Hermes 2021-05-25
     *
     * @param robbersNewFieldrequest The message, that will be send, if a user rolled a 7.
     * @author Marius Birk
     * @since 2021-04-25
     */
    @Subscribe
    public void onRobbersNewFieldRequest(RobbersNewFieldRequest robbersNewFieldrequest) {
        Optional<Game> optionalGame = gameManagement.getGame(robbersNewFieldrequest.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            List<String> userList = new ArrayList<>();
            //Indicate the old robbers place and "deactivate" the occupiedByRobber option.
            for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
                if (hexagon.isOccupiedByRobber()) {
                    hexagon.setOccupiedByRobber(false);
                }
                if (hexagon.getUuid().equals(robbersNewFieldrequest.getNewField())) {
                    //If the UUIDs match, the new field is set to occupied
                    hexagon.setOccupiedByRobber(true);
                    for (MapGraph.BuildingNode node : hexagon.getBuildingNodes()) {
                        if (node.getOccupiedByPlayer() != 666) {
                            if (!userList.contains(game.getUser(node.getOccupiedByPlayer()).getUsername()) && !robbersNewFieldrequest.getUser().equals(game.getUser(node.getOccupiedByPlayer()))) {
                                if (game.getInventory(game.getUser(node.getOccupiedByPlayer())).sumResource() > 0) {
                                    userList.add(game.getUser(node.getOccupiedByPlayer()).getUsername());
                                }
                            }
                        }
                    }
                    sendToAllInGame(robbersNewFieldrequest.getName(), new SuccessfullMovedRobberMessage(hexagon.getUuid()));
                }
            }
            // If the robber wasn't moved because of the Knight DevelopmentCard do this
            if (!game.getCurrentCard().equals("Knight")) {
                tooMuchResources(game);
            }
            if (game.getUsers().contains(robbersNewFieldrequest.getUser())) {
                ChoosePlayerMessage choosePlayerMessage = new ChoosePlayerMessage(game.getName(), robbersNewFieldrequest.getUser(), userList);
                sendToSpecificUserInGame(choosePlayerMessage, robbersNewFieldrequest.getUser());
            }
        }
    }

    /**
     * Handles StartGameRequest found on the EventBus
     * <p>
     * If a StartGameRequest is detected on the EventBus, this method is called. If the number of players in the lobby
     * is more than 1, Method creates StartGameRequest with name of the lobby and user, which will be sent to all
     * players in the lobby. It starts a timer and tries to start the game afterwards. If no game was created a
     * NotEnoughPlayersResponse is sent to all users that are ready to start the game. Else Method creates
     * NotLobbyOwnerResponse, NotEnoughPlayersResponse or GameAlreadyExistsResponse and sends it to a specific user that
     * sent the initial request.
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05 enhanced by Marc Hermes 2021-03-25
     *
     * @param startGameRequest the StartGameRequest found on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.lobby.request.StartGameRequest
     * @since 2021-01-24
     */
    @Subscribe
    public void onStartGameRequest(StartGameRequest startGameRequest) {
        Optional<Lobby> optionalLobby = lobbyService.getLobby(startGameRequest.getName());
        if (optionalLobby.isPresent()) {
            Lobby lobby = optionalLobby.get();
            Set<User> usersInLobby = lobby.getUsers();
            if (gameManagement.getGame(lobby.getName()).isEmpty() && startGameRequest.getMinimumAmountOfPlayers() > 0 && startGameRequest.getMinimumAmountOfPlayers() < 5 && startGameRequest.getUser().equals(lobby.getOwner())) {
                lobby.setPlayersReadyToNull();
                lobby.setGameFieldVariant(startGameRequest.getGameFieldVariant());
                lobby.setMinimumAmountOfPlayers(startGameRequest.getMinimumAmountOfPlayers());
                sendToAllInLobby(startGameRequest.getName(), new StartGameMessage(startGameRequest.getName(), startGameRequest.getUser()));
                Timer gameStartTimer = lobby.startTimerForGameStart();
                int seconds = 60;
                try {
                    class RemindTask extends TimerTask {

                        public void run() {
                            Set<User> users = new TreeSet<>(usersInLobby);
                            if (lobby.getPlayersReady().size() != 0) {
                                users.removeAll(lobby.getPlayersReady());
                            } else {
                                users.clear();
                            }
                            if (lobby.getPlayersReady().size() > 0 && gameManagement.getGame(lobby.getName()).isEmpty()) {
                                try {
                                    startGame(lobby, lobby.getGameFieldVariant());
                                    // TODO: sollte wahrscheinlich keine notenoughplayersmessage sein, sondern "You missed the game start"
                                    sendToListOfUsers(users, new NotEnoughPlayersMessage(lobby.getName()));
                                } catch (GameManagementException e) {
                                    LOG.debug(e);
                                }
                            } else if (lobby.getPlayersReady().size() < 1) {
                                sendToListOfUsers(users, new NotEnoughPlayersMessage(lobby.getName()));
                            }
                        }
                    }
                    gameStartTimer.schedule(new RemindTask(), seconds * 1000);

                } catch (Exception e) {
                    LOG.debug(e);
                }

            } else if (!startGameRequest.getUser().toString().equals(lobby.getOwner().toString())) {
                if (startGameRequest.getMessageContext().isPresent()) {
                    sendToSpecificUser(startGameRequest.getMessageContext().get(), new NotLobbyOwnerResponse(lobby.getName()));
                }
            } else if (gameManagement.getGame(lobby.getName()).isPresent()) {
                if (startGameRequest.getMessageContext().isPresent()) {
                    sendToSpecificUser(startGameRequest.getMessageContext().get(), new GameAlreadyExistsResponse(lobby.getName()));
                }
            } else if (lobby.getUsers().size() < 2) {
                sendToListOfUsers(lobby.getUsers(), new NotEnoughPlayersMessage(lobby.getName()));
            }
        }
    }

    /**
     * Method to create and start a game
     * <p>
     * A new game is created if at least 2 players are to start the game and if not already a game exists. All players
     * ready are joined to the game and a GameCreatedMessage is send to all players in the game.
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05 enhanced by Pieter Vogt 2021-03-26 enhanced by Marc Hermes
     * 2021-03-25
     *
     * @param lobby lobby that wants to start a game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */

    public void startGame(Lobby lobby, String gameFieldVariant) {
        if (lobby.getPlayersReady().size() > 0) {
            Set<User> newUserList = new TreeSet<>();
            for (User user : lobby.getUsers()) {
                newUserList.add(userService.retrieveUserInformation(user));
            }
            gameManagement.createGame(lobby.getName(), lobby.getOwner(), newUserList, gameFieldVariant);
            Optional<Game> optionalGame = gameManagement.getGame(lobby.getName());
            if (optionalGame.isPresent()) {
                Game game = optionalGame.get();
                for (User user : lobby.getPlayersReady()) {
                    game.joinUser(user);
                }
                lobby.setPlayersReadyToNull();
                lobby.setRdyResponsesReceived(0);
                lobby.stopTimerForGameStart();
                lobby.setGameStarted(true);
                game.setAmountOfPlayers(lobby.getMinimumAmountOfPlayers());
                game.setUpUserArrayList();
                game.setUpInventories();
                post(new GameStartedMessage(lobby.getName()));
                for (User user : game.getUsers()) {
                    sendToSpecificUserInGame(new GameCreatedMessage(game.getName(), (UserDTO) user, game.getMapGraph(), game.getUsersList(), game.getUsers(), gameFieldVariant), user);
                }
                updateInventory(game);
                sendToAllInGame(game.getName(), new NextTurnMessage(game.getName(), game.getUser(game.getTurn()).getUsername(), game.getTurn(), game.isStartingTurns()));
            }
        } else {
            lobby.setPlayersReadyToNull();
            lobby.setRdyResponsesReceived(0);
            lobby.stopTimerForGameStart();
            throw new GameManagementException("Not enough Players ready!");
        }
    }

    /**
     * Handles PlayerReadyRequest found on the EventBus
     * <p>
     * If a PlayerReadyRequest is detected on the EventBus, this method is called. Method adds ready players to the
     * PlayerReady list and counts the number of player responses in variable Player enhanced by Alexander Losse,
     * Ricardo Mook 2021-03-05
     * <p>
     * enhanced by Marc Hermes 2021-03-25
     *
     * @param playerReadyRequest the PlayerReadyRequest found on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.request.PlayerReadyRequest
     * @since 2021-01-24
     */
    @Subscribe
    public void onPlayerReadyRequest(PlayerReadyRequest playerReadyRequest) {
        Optional<Lobby> optionalLobby = lobbyService.getLobby(playerReadyRequest.getName());
        if (optionalLobby.isPresent()) {
            Lobby lobby = optionalLobby.get();
            if (playerReadyRequest.getBoolean()) {
                lobby.incrementRdyResponsesReceived();
                lobby.joinPlayerReady(playerReadyRequest.getUser());
            } else if (!playerReadyRequest.getBoolean()) {
                lobby.incrementRdyResponsesReceived();
            }
            if (lobby.getRdyResponsesReceived() == lobby.getUsers().size() && gameManagement.getGame(lobby.getName()).isEmpty()) {
                try {
                    startGame(lobby, lobby.getGameFieldVariant());
                } catch (GameManagementException e) {
                    LOG.debug(e);
                    sendToListOfUsers(lobby.getPlayersReady(), new NotEnoughPlayersMessage(lobby.getName()));
                }
            }
        }
    }

    /**
     * Handles JoinOnGoingGameRequests from the client found on the EventBus
     * <p>
     * First check if the game of the lobby has already started and the game is present
     * Then check if the user is in the lobby and not already part of the game.
     * If everything checks out, join the user in the game, thus replacing an AI player in the game
     * and send a Response to the user who joined as well as inform all other users in the game that
     * a new user joined the game.
     *
     * @param request the JoinOnGoingGameRequest detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void onJoinOnGoingGameRequest(JoinOnGoingGameRequest request) {
        Optional<Lobby> optionalLobby = lobbyService.getLobby(request.getName());
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalLobby.isPresent() && request.getMessageContext().isPresent()) {
            Lobby lobby = optionalLobby.get();
            var response = new JoinOnGoingGameResponse(lobby.getName(), request.getUser(), false, null, null, null, lobby.getGameFieldVariant(), "The game doesn't exist!");
            if (lobby.getGameStarted() && optionalGame.isPresent()) {
                Game game = optionalGame.get();
                boolean foundUser = false;
                User user = null;
                for (User usr : game.getUsersList()) {
                    if (request.getSession().equals(authenticationService.getSession(usr))) {
                        user = usr;
                        foundUser = true;
                        break;
                    }
                }
                if (foundUser && user != null && game.getTradeList().size() == 0) {
                    if (!game.getUsers().contains(user)) {
                        game.joinUser(user);
                        // send information to user
                        response = new JoinOnGoingGameResponse(game.getName(), request.getUser(), true, game.getMapGraph(), game.getUsersList(), game.getUsers(), lobby.getGameFieldVariant(), "");
                        sendToSpecificUser(request.getMessageContext().get(), response);
                        var gameMessage = new JoinOnGoingGameMessage(game.getName(), request.getUser(), game.getUsersList(), game.getUsers());
                        sendToAllInGame(game.getName(), gameMessage);
                        updateInventory(game);
                        var currentTurnMessage = new NextTurnMessage(game.getName(), game.getUser(game.getTurn()).getUsername(), game.getTurn(), game.isStartingTurns());
                        sendToAllInGame(game.getName(), currentTurnMessage);
                    }
                } else {
                    String reason = game.getTradeList().size() != 0 ? "A Trade is currently ongoing." : "You are not registered in this game!";
                    response = new JoinOnGoingGameResponse(game.getName(), request.getUser(), false, null, null, null, lobby.getGameFieldVariant(), reason);
                    sendToSpecificUser(request.getMessageContext().get(), response);

                }
            } else {
                sendToSpecificUser(request.getMessageContext().get(), response);
            }
        }
    }

    /**
     * Handles EndTurnRequests found on the eventbus.
     *
     * <p>If an EndTurnRequest is found on the eventbus, this method checks if the sender is the player with the
     * current turn. If so, the method calls the nextRound method to increment the turnCount. After that, the method
     * sends a NextTurnMessage to all participants of the current game, telling them in which turn they are now, in which
     * game and who's turn is up now.</p>
     * Additionally the server here checks if a player has achieved the minimum 10 victory points to trigger the summary screen
     * <p>
     * enhanced by René Meyer, Sergej Tulnev
     *
     * @param request Transports the games name and the senders UserDTO.
     * @author Pieter Vogt, Philip Nitsche
     * @author Pieter Vogt
     * @since 2021-04-18
     * @since 2021-03-26
     */
    @Subscribe
    public void onEndTurnRequest(EndTurnRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            LOG.debug("EndTurn Request");
            endTurn(game, request.getUser());
        }
    }

    public void endTurn(Game game, UserDTO user) {
        if (user.equals(game.getUser(game.getTurn())) && game.getCurrentCard().equals("") && (game.rolledDiceThisTurn() || game.isStartingTurns())) {
            try {
                boolean priorGamePhase = game.isStartingTurns();
                game.nextRound();
                if (priorGamePhase && !game.isStartingTurns()) {
                    distributeResources(game.getName());
                }
                sendToAllInGame(game.getName(), new NextTurnMessage(game.getName(),
                        game.getUser(game.getTurn()).getUsername(), game.getTurn(), game.isStartingTurns()));
                // Check if the turnPlayer is an actual user in the game, if not, start the AI
                if (!game.getUsers().contains(game.getUser(game.getTurn()))) {
                    if (!game.isStartingTurns()) {
                        RollDiceRequest rdr = new RollDiceRequest(game.getName(), game.getUser(game.getTurn()));
                        onRollDiceRequest(rdr);
                    }
                    startTurnForAI((GameDTO) game);

                }
            } catch (GameManagementException e) {
                LOG.debug(e);
                LOG.debug("Sender " + user.getUsername() + " was not player with current turn");
            }
        }
    }


    /**
     * Method used to call the AI and start it's turn
     * <p>
     * When this method is called, a new randomAI object is created.
     * Also the AIToServerTranslator is used to translate the AIActions given by
     * the startTurnAction() call.
     *
     * @param game the game that the AI is supposed to play in
     * @author Marc Hermes
     * @since 2021-05-11
     */
    public void startTurnForAI(GameDTO game) {
        if (game.isUsedForTest()) {
            game.setLastRolledDiceValue(5);
            TestAI testAI = new TestAI(game);
            AIToServerTranslator.translate(testAI.startTurnOrder(), this);
        } else {
            //RandomAI randomAI = new RandomAI(game);
            LOG.debug("Rufe random AI auf");
            AIToServerTranslator.translate(new RandomAI(game).startTurnOrder(), this);
        }
    }


    /**
     * Handles BuyDevelopmentCardRequest found on the eventbus.
     *
     * <p>
     * Gets the game from the gameManagement and retrieves the inventory from the user. Then the method checks if enough
     * resources are available to buy a development card. If there are enough resources, then the method gets the next
     * development card from the development card deck and sends a message with the development card to the user. If
     * there are not enough resources a NoEnoughResourcesMessage is send to the user.
     * </p>
     * Checks if enough development cards are still available to be bought.
     * If not, then sends corresponding message in the game chat.
     * enhanced by Anton Nikiforov, Alexander Losse, Iskander Yusupov
     *
     * @param request Transports the senders UserDTO
     * @author Marius Birk
     * @since 2021-05-16
     * @since 2021-04-03
     */
    @Subscribe
    public void onBuyDevelopmentCardRequest(BuyDevelopmentCardRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            if (request.getUser().equals(game.getUser(game.getTurn())) && optionalGame.get().rolledDiceThisTurn()) {
                Inventory inventory = game.getInventory(request.getUser());
                if (inventory.wool.getNumber() >= 1 && inventory.ore.getNumber() >= 1 && inventory.grain.getNumber() >= 1) {
                    String devCard = game.getDevelopmentCardDeck().drawnCard();
                    if (devCard != null) {
                        takeResource(game, request.getUser(), "Wool", 1);
                        takeResource(game, request.getUser(), "Ore", 1);
                        takeResource(game, request.getUser(), "Grain", 1);
                        inventory.incCardStack(devCard, 1);
                        game.rememberDevCardBoughtThisTurn(devCard, 1);
                        BuyDevelopmentCardMessage response = new BuyDevelopmentCardMessage(request.getName(), request.getUser(), devCard);
                        sendToSpecificUserInGame(response, request.getUser());
                    } else {
                        var chatId = "game_" + game.getName();
                        ResponseChatMessage msg = new ResponseChatMessage("No development cards are available.", chatId, "Bank", System.currentTimeMillis());
                        post(msg);
                        LOG.debug("Posted ResponseChatMessage on eventBus");
                    }
                } else {
                    NotEnoughRessourcesMessage nerm = new NotEnoughRessourcesMessage();
                    nerm.setName(game.getName());
                    sendToSpecificUserInGame(nerm, request.getUser());
                }
            }
            updateInventory(game);
        }
    }

    /**
     * Handles Requests from a client to play a DevelopmentCard
     * <p>
     * If the game exists, the player who sent the request is the turnPlayer and
     * if a DevelopmentCard wasn't already played this turn, or is currently being played,
     * then remove the DevelopmentCard from the inventory of the player and inform him that he
     * may proceed with the resolution of the card. If something went wrong, also inform him.
     * <p>
     * enhanced by Alexander Losse on 2021-05-30
     *
     * @param request the PlayDevelopmentCardRequest sent by the client
     * @author Marc Hermes
     * @since 2021-05-01
     */
    @Subscribe
    public void onPlayDevelopmentCardRequest(PlayDevelopmentCardRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            User turnPlayer = game.getUser(game.getTurn());
            if (request.getUser().equals(turnPlayer)) {
                Inventory inventory = game.getInventory(turnPlayer);
                String devCard = request.getDevCard();
                String currentCardOfGame = game.getCurrentCard();
                boolean alreadyPlayedCard = game.playedCardThisTurn();
                //TODO: delete these 4, only used for testing
                /*inventory.cardMonopoly.incNumber();
                inventory.cardRoadBuilding.incNumber();
                inventory.cardYearOfPlenty.incNumber();
                inventory.cardKnight.incNumber();*/

                //checks if user can play developmentCard
                if (!game.canUserPlayDevCard(request.getUser(), devCard) && game.rolledDiceThisTurn()) {
                    devCard = "default";
                }
                switch (devCard) {

                    case "Monopoly":
                        if (inventory.cardMonopoly.getNumber() > 0 && currentCardOfGame.equals("") && (!alreadyPlayedCard || game.isUsedForTest())) {
                            game.setCurrentCard("Monopoly");
                            game.setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.cardMonopoly.decNumber();
                            updateInventory(game);
                            break;
                        }

                    case "Road Building":
                        if (inventory.cardRoadBuilding.getNumber() > 0 && currentCardOfGame.equals("") && (!alreadyPlayedCard || game.isUsedForTest()) && inventory.road.getNumber() > 1) {
                            // TODO: check if the player is allowed to even attempt to build 2 streets i.e. not possible when there are no legal spaces to build 2 streets
                            // TODO: probs very complicated to check that, so maybe just ignore that fringe scenario???
                            game.setCurrentCard("Road Building");
                            game.setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.cardRoadBuilding.decNumber();
                            updateInventory(game);
                            break;
                        }

                    case "Year of Plenty":
                        if (inventory.cardYearOfPlenty.getNumber() > 0 && currentCardOfGame.equals("") && (!alreadyPlayedCard || game.isUsedForTest())) {
                            // TODO: Check if there theoretically are resources left in the bank that could be obtained for the player
                            game.setCurrentCard("Year of Plenty");
                            game.setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.cardYearOfPlenty.decNumber();
                            updateInventory(game);
                            break;
                        }

                    case "Knight":
                        if (inventory.cardKnight.getNumber() > 0 && currentCardOfGame.equals("") && (!alreadyPlayedCard || game.isUsedForTest())) {
                            game.setCurrentCard("Knight");
                            game.setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.getName());
                            MoveRobberMessage moveRobberMessage = new MoveRobberMessage(request.getName(), request.getUser());
                            response.initWithMessage(request);
                            post(response);
                            inventory.setPlayedKnights(inventory.getPlayedKnights() + 1);
                            inventory.cardKnight.decNumber();
                            sendToSpecificUserInGame(moveRobberMessage, request.getUser());
                            updateInventory(game);
                            break;
                        }

                    default:
                        PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, false, turnPlayer.getUsername(), game.getName());
                        response.initWithMessage(request);
                        post(response);
                        break;
                }

            } else {
                PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(request.getDevCard(), false, request.getUser().getUsername(), game.getName());
                response.initWithMessage(request);
                post(response);
            }
        }
    }

    /**
     * Handles Requests from a client to resolve a DevelopmentCard
     * <p>
     * If the game exists, the player who sent the request is the turnPlayer and
     * if the DevelopmentCard that is currently being played equals the one in this request,
     * then try to resolve the DevelopmentCard accordingly and inform players of this game that the
     * resolution was successful. If something went wrong, inform the turnPlayer so that he may try again.
     * <p>
     * enhanced by Anton Nikiforov "Year of Plenty" bank
     *
     * @param request the ResolveDevelopmentCardRequest sent from the client
     * @author Marc Hermes
     * @since 2021-05-01
     * @since 2021-05-11
     */
    @Subscribe
    public void onResolveDevelopmentCardRequest(ResolveDevelopmentCardRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            User turnPlayer = game.getUser(game.getTurn());
            String gameName = game.getName();
            String devCard = request.getDevCard();

            if (request.getUser().equals(turnPlayer) && request.getDevCard().equals(game.getCurrentCard())) {
                Inventory turnPlayerInventory = game.getInventory(turnPlayer);
                ResolveDevelopmentCardMessage message = new ResolveDevelopmentCardMessage(devCard, (UserDTO) turnPlayer, gameName);
                ResolveDevelopmentCardNotSuccessfulResponse notSuccessfulResponse = new ResolveDevelopmentCardNotSuccessfulResponse(devCard, turnPlayer.getUsername(), gameName);
                boolean resolvedDevelopmentCardSuccessfully = false;

                switch (devCard) {
                    case "Monopoly":
                        if (request instanceof ResolveDevelopmentCardMonopolyRequest) {
                            ResolveDevelopmentCardMonopolyRequest monopolyRequest = (ResolveDevelopmentCardMonopolyRequest) request;
                            String resource = monopolyRequest.getResource();
                            if (resource.equals("Lumber") || resource.equals("Brick") || resource.equals("Ore") || resource.equals("Grain") || resource.equals("Wool")) {
                                for (User user : game.getUsersList()) {
                                    if (!user.equals(turnPlayer)) {
                                        Inventory x = game.getInventory(user);
                                        turnPlayerInventory.incCardStack(resource, x.getSpecificResourceAmount(resource));
                                        x.decCardStack(resource, x.getSpecificResourceAmount(resource));
                                        resolvedDevelopmentCardSuccessfully = true;
                                    }
                                }
                            }
                        }
                        if (resolvedDevelopmentCardSuccessfully) {
                            sendToAllInGame(gameName, message);
                            updateInventory(game);
                            game.setCurrentCard("");
                        } else {
                            notSuccessfulResponse.initWithMessage(request);
                            notSuccessfulResponse.setErrorDescription("Please select a valid resource");
                            post(notSuccessfulResponse);
                        }
                        break;

                    case "Road Building":
                        if (request instanceof ResolveDevelopmentCardRoadBuildingRequest) {
                            ResolveDevelopmentCardRoadBuildingRequest roadBuildingRequest = (ResolveDevelopmentCardRoadBuildingRequest) request;
                            // TODO: currently known bug, if only 1 of the streets can be built, the server will still build the street and make the user try again to build 2 streets
                            // TODO: thus we need to check before actually building the streets if BOTH streets can be built
                            ConstructionRequest constructionRequest1 = new ConstructionRequest((UserDTO) turnPlayer, gameName, roadBuildingRequest.getStreet1(), "StreetNode");
                            ConstructionRequest constructionRequest2 = new ConstructionRequest((UserDTO) turnPlayer, gameName, roadBuildingRequest.getStreet2(), "StreetNode");
                            boolean successful1 = onConstructionMessage(constructionRequest1);
                            boolean successful2 = onConstructionMessage(constructionRequest2);
                            if (!(successful1 && successful2)) {
                                notSuccessfulResponse.initWithMessage(request);
                                notSuccessfulResponse.setErrorDescription("Please select 2 valid building spots for the streets");
                                post(notSuccessfulResponse);
                            } else {
                                sendToAllInGame(gameName, message);
                                game.setCurrentCard("");
                                updateInventory(game);
                            }
                        } else {
                            notSuccessfulResponse.initWithMessage(request);
                            post(notSuccessfulResponse);
                        }
                        break;

                    case "Year of Plenty":
                        if (request instanceof ResolveDevelopmentCardYearOfPlentyRequest) {
                            ResolveDevelopmentCardYearOfPlentyRequest yearOfPlentyRequest = (ResolveDevelopmentCardYearOfPlentyRequest) request;
                            boolean successful1 = giveResource(game, turnPlayer, yearOfPlentyRequest.getResource1(), 1);
                            boolean successful2 = giveResource(game, turnPlayer, yearOfPlentyRequest.getResource2(), 1);
                            if (!(successful1 && successful2)) {
                                if (successful1) takeResource(game, turnPlayer, yearOfPlentyRequest.getResource1(), 1);
                                if (successful2) takeResource(game, turnPlayer, yearOfPlentyRequest.getResource2(), 1);
                                notSuccessfulResponse.initWithMessage(request);
                                notSuccessfulResponse.setErrorDescription("Please select 2 valid resources");
                                post(notSuccessfulResponse);
                                break;
                            }
                            sendToAllInGame(gameName, message);
                            game.setCurrentCard("");
                            updateInventory(game);
                        } else {
                            notSuccessfulResponse.initWithMessage(request);
                            post(notSuccessfulResponse);
                        }
                        break;

                    case "Knight":
                        if (request instanceof ResolveDevelopmentCardKnightRequest) {
                            ResolveDevelopmentCardKnightRequest knightRequest = (ResolveDevelopmentCardKnightRequest) request;
                            RobbersNewFieldRequest rnfm = new RobbersNewFieldRequest(gameName, (UserDTO) turnPlayer, knightRequest.getField());
                            onRobbersNewFieldRequest(rnfm);
                            game.setCurrentCard("");
                            sendToAllInGame(gameName, message);
                            turnPlayerInventory.setPlayedKnights(turnPlayerInventory.getPlayedKnights() + 1);
                            checkForLargestArmy(game);
                            updateInventory(game);
                        } else {
                            notSuccessfulResponse.initWithMessage(request);
                            post(notSuccessfulResponse);
                        }
                        break;
                }
            }
        }

    }

    /**
     * This method evaluates if a user gets the largest army card
     * <p>
     * This method gets invoked by the onResolveDevelopmentCardRequest method and creates an ArrayList with all user
     * inventories from the right game. With the given inventories this method evaluates, who gets the largest army
     * card.
     *
     * @param game current game that is played
     * @author Carsten Dekker
     * @since 2021-05-27
     */
    public void checkForLargestArmy(Game game) {
        if (game.getInventoryWithLargestArmy() == null && game.getInventory(game.getUser(game.getTurn())).getPlayedKnights() > 2) {
            game.getInventory(game.getUser(game.getTurn())).setLargestArmy(true);
            game.setInventoryWithLargestArmy(game.getInventory(game.getUser(game.getTurn())));
        } else if (game.getInventoryWithLargestArmy() != null) {
            if (game.getInventory(game.getUser(game.getTurn())).getPlayedKnights() > game.getInventoryWithLargestArmy().getPlayedKnights()) {
                if (!game.getUser(game.getTurn()).equals(game.getInventoryWithLargestArmy().getUser()))
                    game.getInventoryWithLargestArmy().setLargestArmy(false);
                game.setInventoryWithLargestArmy(game.getInventory(game.getUser(game.getTurn())));
                game.getInventoryWithLargestArmy().setLargestArmy(true);
            }
        }
    }

    /**
     * Method to update private and public inventories in a game
     * <p>
     * If game exists, method sends two types of messages with updated information about inventories.
     * PrivateInventoryChangeMessage is send to specific player in the game. PublicInventoryChangeMessage is send to all
     * players in the game.
     * Also checks if a player has more than 10 victory points and then sends a GameFinishedMessage to all players in the game
     * This is done here because the win instantly gets triggered and not only when a player ends his turn
     * <p>
     * enhanced by Carsten Dekker ,Marc Johannes Hermes, Marius Birk, Iskander Yusupov
     *
     * @param game game that wants to update private and public inventories
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-05-07
     * enhanced by René Meyer
     * @since 2021-05-07
     * @since 2021-04-08
     */
    public void updateInventory(Game game) {
        for (User user : game.getUsers()) {
            HashMap<String, Integer> privateInventory = game.getInventory(user).getPrivateView();
            PrivateInventoryChangeMessage privateInventoryChangeMessage = new PrivateInventoryChangeMessage(game.getName(), user, privateInventory);
            sendToSpecificUserInGame(privateInventoryChangeMessage, user);
            var inventory = game.getInventory(user);
            // If user has 10 victory points, he wins and the Summary Screen gets shown for every user in the game.
            if (inventory.getVictoryPoints() >= 10) {
                //Retrieve all stats
                //Retrieve inventories from all users
                var inventories = game.getInventoriesArrayList();
                //Create statsDTO object
                var statsDTO = new StatsDTO(game.getName(), user.getUsername(), game.getTradeList().size(), game.getOverallTurns(), inventories);
                //Send GameFinishedMessage to all users in game
                sendToAllInGame(game.getName(), new GameFinishedMessage(statsDTO));
                LOG.debug("User " + user.getUsername() + " has atleast 10 victory points and won.");
            }
        }
        ArrayList<HashMap<String, Integer>> publicInventories = new ArrayList<>();
        for (User user : game.getUsersList()) {
            publicInventories.add(game.getInventory(user).getPublicView());
        }
        PublicInventoryChangeMessage publicInventoryChangeMessage = new PublicInventoryChangeMessage(game.getName(), publicInventories);
        sendToAllInGame(game.getName(), publicInventoryChangeMessage);
    }

    /**
     * Returns the gameManagement
     *
     * @return the gameManagement
     */
    public GameManagement getGameManagement() {
        return this.gameManagement;
    }

    /**
     * Handles LogoutRequests found on the EventBus
     * <p>
     * If a LogoutRequest is detected on the EventBus, this method is called. It gets all games from the GameManagement
     * and loops through them. If the user is part of a game, he gets removed from it. If he is the last user in the
     * game, the game gets dropped. Finally we log how many games the user left.
     *
     * @param request LogoutRequest found on the eventBus
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.common.user.request.LogoutRequest
     * @see de.uol.swp.common.game.request.GameLeaveUserRequest
     * @see de.uol.swp.server.lobby.LobbyService
     * @since 2021-04-08
     */
    @Subscribe
    public void onLogoutRequest(LogoutRequest request) {
        if (request.getSession().isPresent()) {
            Session session = request.getSession().get();
            var userToLogOut = session.getUser();
            // Could be already logged out
            if (userToLogOut != null) {
                var games = gameManagement.getAllGames();
                // Create gamesCopy because of ConcurrentModificationException,
                // so it doesn't matter when in the meantime the games Object gets modified, while we still loop through it
                var gamesCopy = games.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                // Loop games
                Iterator<Map.Entry<String, Game>> it = gamesCopy.entrySet().iterator();
                var i = 0;
                while (it.hasNext()) {
                    Map.Entry<String, Game> entry = it.next();
                    Game game = entry.getValue();
                    if (game.getUsers().contains(userToLogOut)) {
                        // leave every game the user is part of
                        var gameLeaveUserRequest = new GameLeaveUserRequest(game.getName(), (UserDTO) userToLogOut);
                        if (request.getMessageContext().isPresent()) {
                            gameLeaveUserRequest.setMessageContext(request.getMessageContext().get());
                            this.onGameLeaveUserRequest(gameLeaveUserRequest);
                        }
                    }
                    i++;
                }
                var lobbyString = i > 1 ? " games" : " game";
                LOG.debug("Left " + i + lobbyString + " for User: " + userToLogOut.getUsername());
            }
        }
    }

    /**
     * either initiates a new trade or adds a bid to an existing trade
     * <p>
     * the method checks if the user has enough items in his inventory
     * if check not successful the methods sends an error message to the user
     * if successful the method checks if the String tradeCode already exists
     * if the tradeCode does not exists, the methods initiates a new trade. The user who send the TradeItemRequest becomes the seller
     * the method sends TradeOfferInformBiddersMessage to the other users in the game, informing about them new trade
     * if the tradeCOde does exists, the method adds a new bidder to the specified trade
     * if all users, who are not the seller) have send their bid, the method informs the seller about the the offers(TradeInformSellerAboutBidsMessage)
     *
     * @param request TradeItemRequest
     * @author Alexander Losse, Ricardo Mook
     * @see TradeItem
     * @see Trade
     * @see TradeItemRequest
     * @see TradeOfferInformBiddersMessage
     * @see TradeInformSellerAboutBidsMessage
     * @since 2021-04-11
     */
    @Subscribe
    public void onTradeItemRequest(TradeItemRequest request) {
        LOG.debug("Got message " + request.getUser().getUsername());
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());

        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();

            /*// TODO: Wird nur zum testen verwendet
            game.getInventory(request.getUser()).incCardStack("Lumber", 5);
            game.getInventory(request.getUser()).incCardStack("Brick", 5);
            game.getInventory(request.getUser()).incCardStack("Grain", 5);
            game.getInventory(request.getUser()).incCardStack("Wool", 5);
            game.getInventory(request.getUser()).incCardStack("Ore", 5);*/

            boolean numberOfCardsCorrect = true;

            for (TradeItem tradeItem : request.getTradeItems()) {
                boolean notEnoughInInventoryCheck = tradeItem.getCount() > game.getInventory(request.getUser()).getPrivateView().get(tradeItem.getName());
                if (tradeItem.getCount() < 0 || notEnoughInInventoryCheck) {
                    numberOfCardsCorrect = false;
                    break;
                }
            }

            if (numberOfCardsCorrect) {
                String tradeCode = request.getTradeCode();
                if (!game.getTradeList().containsKey(tradeCode)) {
                    game.addTrades(new Trade(request.getUser(), request.getTradeItems(), request.getWishItems()), tradeCode);

                    LOG.debug("added Trade " + tradeCode + " by User: " + request.getUser().getUsername() + " items: " + request.getTradeItems());

                    for (User user : game.getUsersList()) {
                        if (!request.getUser().equals(user)) {
                            TradeOfferInformBiddersMessage tradeOfferInformBiddersMessage = new TradeOfferInformBiddersMessage(request.getUser(), request.getName(), tradeCode, request.getTradeItems(), (UserDTO) user, request.getWishItems());
                            if (!game.getUsers().contains(user)) {
                                if (!game.isUsedForTest()) {
                                    AIToServerTranslator.translate(new RandomAI((GameDTO) game).tradeBidOrder(tradeOfferInformBiddersMessage), this);
                                } else
                                    AIToServerTranslator.translate(new TestAI((GameDTO) game).tradeBidOrder(tradeOfferInformBiddersMessage), this);
                            } else {
                                sendToSpecificUserInGame(tradeOfferInformBiddersMessage, user);
                                LOG.debug("Send TradeOfferInformBiddersMessage to " + user.getUsername());
                            }
                        }
                    }
                } else {
                    Trade trade = game.getTradeList().get(request.getTradeCode());
                    trade.addBid(request.getUser(), request.getTradeItems());
                    LOG.debug("added bid to " + tradeCode + " by User: " + request.getUser().getUsername() + " items: " + request.getTradeItems());
                    if (trade.getBids().size() == game.getUsersList().size() - 1) {
                        LOG.debug("bids full");
                        TradeInformSellerAboutBidsMessage tisabm = new TradeInformSellerAboutBidsMessage(trade.getSeller(), request.getName(), tradeCode, trade.getBidders(), trade.getBids());
                        if (!game.getUsers().contains(game.getUser(game.getTurn()))) {
                            if (!game.isUsedForTest()) {
                                AIToServerTranslator.translate(new RandomAI((GameDTO) game).continueTurnOrder(tisabm, trade.getWishList()), this);
                            } else
                                AIToServerTranslator.translate(new TestAI((GameDTO) game).continueTurnOrder(tisabm, trade.getWishList()), this);
                        } else {
                            sendToSpecificUserInGame(tisabm, trade.getSeller());
                        }
                        LOG.debug("Send TradeInformSellerAboutBidsMessage to " + trade.getSeller().getUsername());
                    }
                }
            } else {
                LOG.debug("Nicht genug im Inventar");
                TradeCardErrorMessage tcem = new TradeCardErrorMessage(request.getUser(), request.getName(), request.getTradeCode());
                sendToSpecificUserInGame(tcem, request.getUser());
            }
        }
    }

    /**
     * finalises the trade
     * <p>
     * if a bid was accepted by the seller
     * trades the items between the users
     * if rejected, nothing happens
     * calls tradeEndedChatMessageHelper to inform the players about the result of the trade
     * TradeEndedMessage is send to all player in game
     * the specified trade is removed from the game
     *
     * @param request TradeChoiceRequest containing the choice the seller made
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-13
     */
    @Subscribe
    public void onTradeChoiceRequest(TradeChoiceRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            Trade trade = game.getTradeList().get(request.getTradeCode());

            if (request.getTradeAccepted() && !request.getUser().getUsername().equals(trade.getSeller().getUsername())) {
                Inventory inventorySeller = game.getInventory(trade.getSeller());
                Inventory inventoryBidder = game.getInventory(request.getUser());

                for (TradeItem soldItem : trade.getSellingItems()) {
                    inventorySeller.decCardStack(soldItem.getName(), soldItem.getCount());
                    inventoryBidder.incCardStack(soldItem.getName(), soldItem.getCount());
                }
                for (TradeItem bidItem : trade.getBids().get(request.getUser())) {
                    inventorySeller.incCardStack(bidItem.getName(), bidItem.getCount());
                    inventoryBidder.decCardStack(bidItem.getName(), bidItem.getCount());
                }
            }
            tradeEndedChatMessageHelper(game.getName(), request.getTradeCode(), request.getUser().getUsername(), request.getTradeAccepted());
            sendToAllInGame(request.getName(), new TradeEndedMessage(request.getName(), request.getTradeCode()));
            game.removeTrade(request.getTradeCode());
            updateInventory(game);
        }
    }

    /**
     * help method to deliver a chatMessage to all players of the game how the trade ended
     *
     * @param gameName     the game name
     * @param tradeCode    the trade code
     * @param winnerBidder the winners name
     * @param success      bool if successful or not
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    private void tradeEndedChatMessageHelper(String gameName, String tradeCode, String winnerBidder, Boolean success) {
        try {
            String chatMessage;
            var chatId = "game_" + gameName;
            if (success) {
                chatMessage = "The offer from Player " + winnerBidder + " was accepted at trade: " + tradeCode;
            } else {
                chatMessage = "None of the bids was accepted. Sorry! :(";
            }
            ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId, "TradeInfo", System.currentTimeMillis());
            post(msg);
            LOG.debug("Posted ResponseChatMessage on eventBus");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * sends tradeStartedMessage to the seller when his request to start a trade is handled by the server
     *
     * @param request TradeStartRequest
     * @author Alexander Losse, Ricardo Mook
     * @see TradeStartRequest
     * @since 2021-04-11
     */
    @Subscribe
    public void onTradeStartedRequest(TradeStartRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.get().rolledDiceThisTurn()) {
            UserDTO user = request.getUser();
            TradeStartedMessage tsm = new TradeStartedMessage(user, request.getName(), request.getTradeCode());
            sendToSpecificUserInGame(tsm, user);
        }
    }

    /**
     * Handles BankRequest found on the EventBus
     * <p>
     * First it checks whether and which harbor the player has.
     * Then it uses this information to create the correct offer.
     * At the end it sends the BankResponseMessage to the SpecificUserInGame
     *
     * @param request BankRequest
     * @author Anton Nikiforov
     * @see TradeItem
     * @see BankResponseMessage
     * @since 2021-05-29
     */
    @Subscribe
    public void onBankRequest(BankRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            Inventory inventory = game.getInventory(request.getUser());
            ArrayList<ArrayList<TradeItem>> bankOffer = new ArrayList<>();
            if (game.getBankInventory().getSpecificResourceAmount(request.getCardName()) > 0) {

                boolean lumberHarbor = false;
                boolean brickHarbor = false;
                boolean grainHarbor = false;
                boolean woolHarbor = false;
                boolean oreHarbor = false;
                boolean anyHarbor = false;

                for (MapGraph.BuildingNode buildingNode : game.getMapGraph().getBuiltBuildings()) {
                    if (game.getUser(buildingNode.getOccupiedByPlayer()).equals(request.getUser())) {
                        // 1 = 2:1 Wool, 2 = 2:1 Brick, 3 = 2:1 Lumber, 4 = 2:1 Grain, 5 = 2:1 Ore, 6 = 3:1 Any
                        switch (buildingNode.getTypeOfHarbor()) {
                            case 1:
                                woolHarbor = true;
                                break;
                            case 2:
                                brickHarbor = true;
                                break;
                            case 3:
                                lumberHarbor = true;
                                break;
                            case 4:
                                grainHarbor = true;
                                break;
                            case 5:
                                oreHarbor = true;
                                break;
                            case 6:
                                anyHarbor = true;
                                break;
                        }
                    }
                }
                int defaultRate = anyHarbor ? 3 : 4;

                ArrayList<TradeItem> lumberOffer = buildOffer("Lumber", defaultRate);
                ArrayList<TradeItem> brickOffer = buildOffer("Brick", defaultRate);
                ArrayList<TradeItem> grainOffer = buildOffer("Grain", defaultRate);
                ArrayList<TradeItem> woolOffer = buildOffer("Wool", defaultRate);
                ArrayList<TradeItem> oreOffer = buildOffer("Ore", defaultRate);

                if (lumberHarbor) lumberOffer.get(0).setCount(2);
                if (brickHarbor) brickOffer.get(1).setCount(2);
                if (grainHarbor) grainOffer.get(2).setCount(2);
                if (woolHarbor) woolOffer.get(3).setCount(2);
                if (oreHarbor) oreOffer.get(4).setCount(2);

                if (lumberOffer.get(0).getCount() > inventory.lumber.getNumber()) lumberOffer.get(0).setNotEnough(true);
                if (brickOffer.get(1).getCount() > inventory.brick.getNumber()) brickOffer.get(0).setNotEnough(true);
                if (grainOffer.get(2).getCount() > inventory.grain.getNumber()) grainOffer.get(0).setNotEnough(true);
                if (woolOffer.get(3).getCount() > inventory.wool.getNumber()) woolOffer.get(0).setNotEnough(true);
                if (oreOffer.get(4).getCount() > inventory.ore.getNumber()) oreOffer.get(0).setNotEnough(true);

                if (!request.getCardName().equals("Lumber")) bankOffer.add(lumberOffer);
                if (!request.getCardName().equals("Brick")) bankOffer.add(brickOffer);
                if (!request.getCardName().equals("Grain")) bankOffer.add(grainOffer);
                if (!request.getCardName().equals("Wool")) bankOffer.add(woolOffer);
                if (!request.getCardName().equals("Ore")) bankOffer.add(oreOffer);
            }

            BankResponseMessage bankResponseMessage = new BankResponseMessage(request.getUser(), request.getTradeCode(), request.getCardName(), bankOffer);
            sendToSpecificUserInGame(bankResponseMessage, request.getUser());
        }
    }

    /**
     * Helper method to build one offer
     * <p>
     * It takes the parameters and build with it an offer
     *
     * @param cardName String
     * @param count    int
     * @return offer ArrayList<TradeItem>
     * @author Anton Nikiforov
     * @see TradeItem
     * @since 2021.05.31
     */
    public ArrayList<TradeItem> buildOffer(String cardName, int count) {
        ArrayList<TradeItem> offer = new ArrayList<>();
        offer.add(new TradeItem("Lumber", cardName.equals("Lumber") ? count : 0));
        offer.add(new TradeItem("Brick", cardName.equals("Brick") ? count : 0));
        offer.add(new TradeItem("Grain", cardName.equals("Grain") ? count : 0));
        offer.add(new TradeItem("Wool", cardName.equals("Wool") ? count : 0));
        offer.add(new TradeItem("Ore", cardName.equals("Ore") ? count : 0));
        return offer;
    }

    /**
     * Handles BankBuyRequest found on the EventBus
     * <p>
     * handles the sale
     * send a chad massage if success
     * and ends the tab
     *
     * @param request BankBuyRequest
     * @author Anton Nikiforov
     * @see TradeItem
     * @see BankResponseMessage
     * @since 2021-05-29
     */
    @Subscribe
    public void onBankBuyRequest(BankBuyRequest request) {
        Optional<Game> optionalGame = gameManagement.getGame(request.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            User user = request.getUser();
            if (request.getChosenOffer() != null) {
                for (TradeItem item : request.getChosenOffer()) {
                    if (item.getCount() > 0) {
                        takeResource(game, user, item.getName(), item.getCount());
                    }
                }
                giveResource(game, user, request.getChosenCard(), 1);
                updateInventory(game);
                post(new ResponseChatMessage(user.getUsername() + " just had a successful trade with the bank.", "game_" + request.getName(), "TradeInfo", System.currentTimeMillis()));
            }
            post(new TradeEndedMessage(request.getName(), request.getTradeCode()));
        }
    }

    /**
     * Draws a random card from the user, that was chosen from the player that moved the robber.
     * <p>
     * If a DrawRandomResourceFromPlayerMessage is detected on the eventbus, this method will be invoked. First the
     * method checks if the game is present and then gets the inventory of the user, from whom the card will be drawn.
     * After that, a random resource will be chosen and the method iterates over the inventory in search of the
     * random resource. If it found the resource, the number of the resource will be decreased and the resource will
     * be increased in the inventory of the player that moved the robber.
     * If the message comes from an AI Player no random resource will be selected, as the AI already randomly selected one during
     * it's calculations.
     * <p>
     * enhanced by Marc Hermes 2021-05-25
     *
     * @param drawRandomResourceFromPlayerRequest the drawRandomResourceFromPlayerMessage detected on the event bus
     * @author Marius Birk
     * @since 2021-05-01
     */
    @Subscribe
    public void onDrawRandomResourceFromPlayerMessage(DrawRandomResourceFromPlayerRequest drawRandomResourceFromPlayerRequest) {
        Optional<Game> optionalGame = gameManagement.getGame(drawRandomResourceFromPlayerRequest.getName());
        if (optionalGame.isPresent()) {
            Game game = optionalGame.get();
            String resource = "";
            // Check if the player who wants to draw a random resource is an AI player in which case he already drew a random resource by himself
            if (!game.getUsers().contains(drawRandomResourceFromPlayerRequest.getUser())) {
                resource = drawRandomResourceFromPlayerRequest.getResource();
            }

            for (User user : game.getUsersList()) {
                if (user.getUsername().equals(drawRandomResourceFromPlayerRequest.getChosenName())) {
                    HashMap<String, Integer> inventory = game.getInventory(user).getPrivateView();
                    if (resource.equals("")) {
                        resource = randomResource(inventory);
                    }
                    game.getInventory(user).decCardStack(resource, 1);
                    game.getInventory(drawRandomResourceFromPlayerRequest.getUser()).incCardStack(resource, 1);
                    updateInventory(game);
                    break;

                }
            }
        }
    }

    /**
     * This method checks if the users have more than 7 resources.
     * <p>
     * This method checks if the users have more than 7 resources and sends the user a tooMuchResourceCardMessage.
     * For every user in the game, the method checks if the user has more than 7 resource cards. If this is true,
     * it checks if the number of resources is even or uneven and sends a TooMuchResourceCardsMessage to every specfic user.
     * <p>
     * enhanced by Marc Hermes, Alexander Losse on 2021-05-22
     *
     * @param game Game that the users play
     * @author Marius Birk
     * @since 2021-05-13
     */
    public void tooMuchResources(Game game) {
        for (User user : game.getUsersList()) {
            if (game.getInventory(user).sumResource() > 7) {
                TooMuchResourceCardsMessage tooMuchResourceCardsMessage;
                if (game.getInventory(user).sumResource() % 2 != 0) {
                    tooMuchResourceCardsMessage = new TooMuchResourceCardsMessage(game.getName(), (UserDTO) user, ((game.getInventory(user).sumResource() - 1) / 2), game.getInventory(user).getPrivateView());
                } else {
                    tooMuchResourceCardsMessage = new TooMuchResourceCardsMessage(game.getName(), (UserDTO) user, (game.getInventory(user).sumResource() / 2), game.getInventory(user).getPrivateView());
                }
                // Check if the player isn't an actual player -> activate AI instead
                if (!game.getUsers().contains(user) && !game.getUser(game.getTurn()).equals(user)) {
                    if (!game.isUsedForTest()) {
                        AIToServerTranslator.translate(new RandomAI((GameDTO) game).discardResourcesOrder(tooMuchResourceCardsMessage), this);
                    } else
                        AIToServerTranslator.translate(new TestAI((GameDTO) game).discardResourcesOrder(tooMuchResourceCardsMessage), this);
                } else {
                    sendToSpecificUserInGame(tooMuchResourceCardsMessage, user);
                }
            }
        }
    }

    /**
     * The method chooses a random resource from given inventory and returns it.
     * <p>
     * This method will be invoked, if the name of a random resource from given inventory is needed. For that, it creates a List of resources from the given inventory
     * and initializes a random number. To get now a random name of resource, we substrate 1 and
     * invoke the get() method of the List and return that value.
     * <p>
     * enhanced by Anton Nikiforov "Year of Plenty" bank
     *
     * @param inventory form Player
     * @return the name of a resource
     * @author Marius Birk
     * @since 2021-04-29
     * @since 2021-05-23
     */
    public String randomResource(HashMap<String, Integer> inventory) {
        List<String> resources = new ArrayList<>();
        if (inventory.get("Lumber") > 0) resources.add("Lumber");
        if (inventory.get("Brick") > 0) resources.add("Brick");
        if (inventory.get("Grain") > 0) resources.add("Grain");
        if (inventory.get("Wool") > 0) resources.add("Wool");
        if (inventory.get("Ore") > 0) resources.add("Ore");
        return resources.get((int) (Math.random() * resources.size()));
    }
}
