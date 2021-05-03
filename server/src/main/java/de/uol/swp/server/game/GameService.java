package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.AllCreatedGamesResponse;
import de.uol.swp.common.game.response.GameAlreadyExistsResponse;
import de.uol.swp.common.game.response.NotLobbyOwnerResponse;
import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.StartGameMessage;
import de.uol.swp.common.lobby.request.StartGameRequest;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.response.game.AllThisGameUsersResponse;
import de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import de.uol.swp.server.lobby.LobbyManagementException;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.Opt;

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
        Optional<Game> game = gameManagement.getGame(gameLeaveUserRequest.getName());
        Optional<Lobby> lobby = lobbyService.getLobby(gameLeaveUserRequest.getName());
        if (game.isPresent()) {
            if (game.get().getUsers().size() == 1) {
                if (gameLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = gameLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new GameLeftSuccessfulResponse(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser()));
                    gameManagement.dropGame(gameLeaveUserRequest.getName());
                    lobby.ifPresent(value -> value.setGameStarted(false));
                    sendToAll(new GameDroppedMessage(gameLeaveUserRequest.getName()));
                }
            } else if (game.get().getUsers() == null) {
                gameManagement.dropGame(gameLeaveUserRequest.getName());
                lobby.ifPresent(value -> value.setGameStarted(false));
                sendToAll(new GameDroppedMessage(gameLeaveUserRequest.getName()));

            } else {
                if (gameLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = gameLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new GameLeftSuccessfulResponse(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser()));
                }
                game.get().leaveUser(gameLeaveUserRequest.getUser());
                sendToAll(new GameSizeChangedMessage(gameLeaveUserRequest.getName()));
                ArrayList<UserDTO> usersInGame = new ArrayList<>();
                for (User user : game.get().getUsers()) usersInGame.add((UserDTO) user);
                sendToAllInGame(gameLeaveUserRequest.getName(), new UserLeftGameMessage(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser(), usersInGame));
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
        Optional<Game> game = gameManagement.getGame(retrieveAllThisGameUsersRequest.getName());
        if (game.isPresent()) {
            List<Session> gameUsers = authenticationService.getSessions(game.get().getUsers());
            if (retrieveAllThisGameUsersRequest.getMessageContext().isPresent()) {
                Optional<MessageContext> ctx = retrieveAllThisGameUsersRequest.getMessageContext();
                sendToSpecificUser(ctx.get(), new AllThisGameUsersResponse(gameUsers, retrieveAllThisGameUsersRequest.getName()));
            }
        }
    }

    /**
     * Handles incoming build requests.
     *
     * @param message
     * @author Pieter Vogt
     * @since 2021-04-15
     */
    @Subscribe
    public void onConstructionMessage(ConstructionMessage message) {
        LOG.debug("Recieved new ConstructionMessage from user " + message.getUser());
        Optional<Game> game = gameManagement.getGame(message.getGame());
        int playerIndex = 666;
        for (int i = 0; i < game.get().getUsersList().size(); i++) {
            if (game.get().getUsersList().get(i).equals(message.getUser())) {
                playerIndex = i;
                break;
            }
        }
        try {
            if (message.getTypeOfNode().equals("BuildingNode")) { //If the node from the message is a building node...
                for (MapGraph.BuildingNode buildingNode : game.get().getMapGraph().getBuildingNodeHashSet()) {
                    if (message.getUuid().equals(buildingNode.getUuid())) { // ... and if the node in the message is a node in the MapGraph BuildingNodeSet...
                        if (buildingNode.buildOrDevelopSettlement(playerIndex)) {
                            game.get().getMapGraph().addBuiltBuilding(buildingNode);
                            sendToAllInGame(game.get().getName(), new SuccessfulConstructionMessage(playerIndex,
                                    message.getUuid(), "BuildingNode"));
                            break;
                        }
                    }
                }
            } else {
                for (MapGraph.StreetNode streetNode : game.get().getMapGraph().getStreetNodeHashSet()) {
                    if (message.getUuid().equals(streetNode.getUuid())) {
                        if (streetNode.buildRoad(playerIndex)) {
                            sendToAllInGame(game.get().getName(), new SuccessfulConstructionMessage(playerIndex,
                                    message.getUuid(), "StreetNode"));
                            break;
                        }

                    }
                }
            }


        } catch (GameManagementException e) {
            LOG.debug(e);
            System.out.println("Player " + message.getUser() + " tried to build at node with UUID: " + message.getUuid() + " but it did not work.");
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

    public void sendToListOfUsers(Set<User> receiver, String gameName, ServerMessage message) {
        message.setReceiver(authenticationService.getSessions(receiver));
        post(message);
    }

    public void sendToSpecificUser(MessageContext ctx, ResponseMessage message) {
        ctx.writeAndFlush(message);
    }

    /**
     * Sends a message to a specific user in the given game
     *
     * @param game    Optional<Game> game
     * @param message ServerMessage message
     * @param user    User user
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-03-11
     */
    public void sendToSpecificUserInGame(Optional<Game> game, ServerMessage message, User user) {
        if (game.isPresent()) {
            List<Session> theList = new ArrayList<>();
            theList.add(authenticationService.getSession(user).get());
            message.setReceiver(theList);
            post(message);
        } else {
            throw new GameManagementException("Game unknown!");
        }
    }

    /**
     * This method is invoked, when a ResourcesToDiscardRequest is posted on the eventbus.
     * <p>
     *     If a ResourcesToDiscardRequest is posted on the eventbus this method is invoked. First it checks if the game is present
     *     or not. After that it iterates over the list of users in the game to get the right one. If the method found the right user,
     *     it sets the new inventory for the user. At the end we call the updateInventory method, to get the right inventory
     *     back to the client.
     * @param resourcesToDiscardRequest request with a hashmap(inventory), the game name and the user, that send it.
     * @author Marius Birk
     * @since 2021-05-03
     */
    @Subscribe
    public void onResourcesToDiscard(ResourcesToDiscardRequest resourcesToDiscardRequest){
        //TODO Hier muss nach Implementierung der Bank nochmal etwas ergänzt werden.
        Optional<Game> game = gameManagement.getGame(resourcesToDiscardRequest.getName());
        if(game.isPresent()){
            for(User user: game.get().getUsers()){
                if(user.equals(resourcesToDiscardRequest.getUser())){
                    game.get().getInventory(user).lumber.setNumber(resourcesToDiscardRequest.getInventory().get("Lumber"));
                    game.get().getInventory(user).grain.setNumber(resourcesToDiscardRequest.getInventory().get("Grain"));
                    game.get().getInventory(user).wool.setNumber(resourcesToDiscardRequest.getInventory().get("Wool"));
                    game.get().getInventory(user).brick.setNumber(resourcesToDiscardRequest.getInventory().get("Brick"));
                    game.get().getInventory(user).ore.setNumber(resourcesToDiscardRequest.getInventory().get("Ore"));
                }
            }
            updateInventory(game);
        }
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
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @see de.uol.swp.common.chat.ResponseChatMessage
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

        Dice dice = new Dice();
        dice.rollDice();

        // Check if cheatEyes number is provided in rollDiceRequest, if so -> set Eyes manually on dice
        // for the roll cheat, else ignore and use rolledDice
        if (rollDiceRequest.getCheatEyes() > 0) {
            dice.setEyes(rollDiceRequest.getCheatEyes());
        }

        if (dice.getEyes() == 7) {
            Optional<Game> game = gameManagement.getGame(rollDiceRequest.getName());
            if (game.isPresent()) {
                MoveRobberMessage moveRobberMessage = new MoveRobberMessage(rollDiceRequest.getName(), (UserDTO) rollDiceRequest.getUser());
                sendToSpecificUserInGame(gameManagement.getGame(rollDiceRequest.getName()), moveRobberMessage, rollDiceRequest.getUser());

                for (User user : game.get().getUsers()) {
                    if (game.get().getInventory(user).getResource() >= 7) {
                        if (game.get().getInventory(user).getResource() % 2 != 0) {
                            TooMuchResourceCardsMessage tooMuchResourceCardsMessage = new TooMuchResourceCardsMessage(game.get().getName(), (UserDTO) user, ((game.get().getInventory(user).getResource() - 1) / 2));
                            sendToSpecificUserInGame(game, tooMuchResourceCardsMessage, user);
                        } else {
                            TooMuchResourceCardsMessage tooMuchResourceCardsMessage = new TooMuchResourceCardsMessage(game.get().getName(), (UserDTO) user, (game.get().getInventory(user).getResource() / 2));
                            sendToSpecificUserInGame(game, tooMuchResourceCardsMessage, user);
                        }
                    }
                }
            }
        } else {
            distributeResources(dice.getEyes(), rollDiceRequest.getName());
        }

        try {
            String chatMessage;
            var chatId = "game_" + rollDiceRequest.getName();
            if (dice.getEyes() == 8 || dice.getEyes() == 11) {
                chatMessage = "Player " + rollDiceRequest.getUser().getUsername() + " rolled an " + dice.getEyes();
            } else {
                chatMessage = "Player " + rollDiceRequest.getUser().getUsername() + " rolled a " + dice.getEyes();
            }
            ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId,
                    rollDiceRequest.getUser().getUsername(), System.currentTimeMillis());
            post(msg);
            LOG.debug("Posted ResponseChatMessage on eventBus");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * Handles the distribution of resources to the users in the opening turn
     * <p>
     * This method handles the distribution of the resources to the users. First the method gets the game and gets the
     * coressponding hexagons. After that the method gives the second built buildings in the opening turn the resource.
     *
     * @param gameName Name of the Game
     * @author Philip Nitsche
     * @since 2021-04-24
     */
    public void distributeResources(String gameName) {
        Optional<Game> game = gameManagement.getGame(gameName);
        if (game.isPresent()) {
            for (MapGraph.Hexagon hexagon : game.get().getMapGraph().getHexagonHashSet())
                for (int i = 0; i < game.get().getLastBuildingOfOpeningTurn().size(); i++)
                    if (hexagon.getBuildingNodes().contains(game.get().getLastBuildingOfOpeningTurn().get(i))) {
                        //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
                        switch (hexagon.getTerrainType()) {
                            case 1:
                                game.get().getInventory(game.get().getUser(i)).lumber.incNumber();
                                break;
                            case 2:
                                game.get().getInventory(game.get().getUser(i)).grain.incNumber();
                                break;
                            case 3:
                                game.get().getInventory(game.get().getUser(i)).wool.incNumber();
                                break;
                            case 4:
                                game.get().getInventory(game.get().getUser(i)).brick.incNumber();
                                break;
                            case 5:
                                game.get().getInventory(game.get().getUser(i)).ore.incNumber();
                                break;
                            default:
                                break;
                        }
                    }
        }
    }

    /**
     * Handles the distribution of resources to the users
     * <p>
     * This method handles the distribution of the resources to the users. First the method gets the game and gets the
     * coressponding hexagons. After that the method checks if the diceToken on the field is equal to the
     * rolled amount of eyes and increases the resource of the user by one(village), two(city).
     *
     * @param eyes     Number of eyes rolled with dice
     * @param gameName Name of the Game
     * @author Marius Birk, Carsten Dekker, Philip Nitsche
     * @since 2021-04-06
     */
    public void distributeResources(int eyes, String gameName) {
        Optional<Game> game = gameManagement.getGame(gameName);
        if (game.isPresent()) {
            for (MapGraph.Hexagon hexagon : game.get().getMapGraph().getHexagonHashSet()) {
                if (hexagon.getDiceToken() == eyes) {
                    for (MapGraph.BuildingNode buildingNode : hexagon.getBuildingNodes()) {
                        if (buildingNode.getOccupiedByPlayer() != 666) {
                            Inventory inventory = game.get().getInventory(game.get().getUser(buildingNode.getOccupiedByPlayer()));
                            //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
                            switch (hexagon.getTerrainType()) {
                                case 1:
                                    if (buildingNode.getSizeOfSettlement() == 1) {
                                        inventory.lumber.incNumber();
                                    } else if (buildingNode.getSizeOfSettlement() == 2) {
                                        inventory.lumber.incNumber();
                                        inventory.lumber.incNumber();
                                    }
                                    break;
                                case 2:
                                    if (buildingNode.getSizeOfSettlement() == 1) {
                                        inventory.grain.incNumber();
                                    } else if (buildingNode.getSizeOfSettlement() == 2) {
                                        inventory.grain.incNumber();
                                        inventory.grain.incNumber();
                                    }
                                    break;
                                case 3:
                                    if (buildingNode.getSizeOfSettlement() == 1) {
                                        inventory.wool.incNumber();
                                    } else if (buildingNode.getSizeOfSettlement() == 2) {
                                        inventory.wool.incNumber();
                                        inventory.wool.incNumber();
                                    }
                                    break;
                                case 4:
                                    if (buildingNode.getSizeOfSettlement() == 1) {
                                        inventory.brick.incNumber();
                                    } else if (buildingNode.getSizeOfSettlement() == 2) {
                                        inventory.brick.incNumber();
                                        inventory.brick.incNumber();
                                    }
                                    break;
                                case 5:
                                    if (buildingNode.getSizeOfSettlement() == 1) {
                                        inventory.ore.incNumber();
                                    } else if (buildingNode.getSizeOfSettlement() == 2) {
                                        inventory.ore.incNumber();
                                        inventory.ore.incNumber();
                                    }
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
     * This method is invoked if a RobbersNewFieldMessage is detected on the event bus.
     * <p>
     *     First the method checks if the game is present or not. If it is present, a list of usernames is initialized.
     *     After that, the method iterates over every hexagon in the MapGraph to detect the old place of the robber and to
     *     set it on false. Now the method checks if the new fields UUID is the same as the UUID of the hexagon and set the
     *     occupiedByRobber attribute on true. The new position is now send to all in the game.
     *     Next, the method checks if the new places buildingspots are occupied by players and put their names in the freshly instantiated
     *     list of usernames. If all buildingspots are checked, a ChoosePlayerMessage is send to the user who rolled a 7.
     *
     * @param robbersNewFieldMessage The message, that will be send, if a user rolled a 7.
     * @autor Marius Birk
     * @since 2021-04-25
     */
    @Subscribe
    public void onRobbersNewFieldRequest(RobbersNewFieldMessage robbersNewFieldMessage) {
        Optional<Game> game = gameManagement.getGame(robbersNewFieldMessage.getName());
        if (game.isPresent()) {
            List<String> userList = new ArrayList<>();
            //Indicate the old robbers place and "deactivate" the occupiedByRobber option.
            for (MapGraph.Hexagon hexagon : game.get().getMapGraph().getHexagonHashSet()) {
                if (hexagon.isOccupiedByRobber()) {
                    hexagon.setOccupiedByRobber(false);
                }
                if (hexagon.getUuid().equals(robbersNewFieldMessage.getNewField())) {
                    //If the UUIDs match, the new field ist set to occupied
                    hexagon.setOccupiedByRobber(true);
                    sendToAllInGame(robbersNewFieldMessage.getName(), new SuccessfullMovedRobberMessage(hexagon.getUuid()));
                }
                //check if the building nodes at the hexagon are occupied by players
                for (int i = 0; i < hexagon.getBuildingNodes().size(); i++) {
                    if (hexagon.getBuildingNodes().get(i).getOccupiedByPlayer() != 666) {
                        if (!userList.contains(game.get().getUser(hexagon.getBuildingNodes().get(i).getOccupiedByPlayer()).getUsername())) {
                            userList.add(game.get().getUser(hexagon.getBuildingNodes().get(i).getOccupiedByPlayer()).getUsername());
                        }
                    }
                }
            }
            ChoosePlayerMessage choosePlayerMessage = new ChoosePlayerMessage(game.get().getName(), robbersNewFieldMessage.getUser(), userList);
            sendToSpecificUserInGame(game, choosePlayerMessage, robbersNewFieldMessage.getUser());
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
        Optional<Lobby> lobby = lobbyService.getLobby(startGameRequest.getName());
        Set<User> usersInLobby = lobby.get().getUsers();
        if (gameManagement.getGame(lobby.get().getName()).isEmpty() && usersInLobby.size() > 1 && startGameRequest.getUser().getUsername().equals(lobby.get().getOwner().getUsername())) {
            lobby.get().setPlayersReadyToNull();
            lobby.get().setGameFieldVariant(startGameRequest.getGameFieldVariant());
            lobby.get().setGameShouldStart(true);
            sendToAllInLobby(startGameRequest.getName(), new StartGameMessage(startGameRequest.getName(), startGameRequest.getUser()));
            int seconds = 60;
            Timer timer = new Timer();
            class RemindTask extends TimerTask {
                public void run() {
                    Set<User> users = new TreeSet<>(usersInLobby);
                    if (lobby.get().getPlayersReady().size() != 0) {
                        users.removeAll(lobby.get().getPlayersReady());
                    } else {
                        users.clear();
                    }
                    if (lobby.get().getPlayersReady().size() > 1 && gameManagement.getGame(lobby.get().getName()).isEmpty()) {
                        try {
                            startGame(lobby, lobby.get().getGameFieldVariant());
                            // TODO: sollte wahrscheinlich keine notenoughplayersmessage sein, sondern "You missed the game start"
                            sendToListOfUsers(users, lobby.get().getName(), new NotEnoughPlayersMessage(lobby.get().getName()));
                        } catch (GameManagementException e) {
                            LOG.debug(e);
                        }
                    } else if (lobby.get().getPlayersReady().size() < 2 && lobby.get().getGameShouldStart()) {
                        sendToListOfUsers(users, lobby.get().getName(), new NotEnoughPlayersMessage(lobby.get().getName()));
                    }
                    timer.cancel();
                }
            }
            timer.schedule(new RemindTask(), seconds * 1000);
        } else if (!startGameRequest.getUser().toString().equals(lobby.get().getOwner().toString())) {
            sendToSpecificUser(startGameRequest.getMessageContext().get(), new NotLobbyOwnerResponse(lobby.get().getName()));
        } else if (gameManagement.getGame(lobby.get().getName()).isPresent()) {
            sendToSpecificUser(startGameRequest.getMessageContext().get(), new GameAlreadyExistsResponse(lobby.get().getName()));
        } else if (lobby.get().getUsers().size() < 2) {
            sendToListOfUsers(lobby.get().getUsers(), lobby.get().getName(), new NotEnoughPlayersMessage(lobby.get().getName()));
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

    public void startGame(Optional<Lobby> lobby, String gameFieldVariant) {
        if (lobby.get().getPlayersReady().size() > 1) {
            gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), gameFieldVariant);
            Optional<Game> game = gameManagement.getGame(lobby.get().getName());
            ArrayList<UserDTO> usersInGame = new ArrayList<>();
            for (User user : lobby.get().getPlayersReady()) {
                user = userService.retrieveUserInformation(user);
                game.get().joinUser(user);
                usersInGame.add((UserDTO) user);

            }
            lobby.get().setPlayersReadyToNull();
            lobby.get().setRdyResponsesReceived(0);
            lobby.get().setGameStarted(true);
            post(new GameStartedMessage(lobby.get().getName()));
            for (User user : game.get().getUsers()) {
                sendToSpecificUserInGame(game, new GameCreatedMessage(game.get().getName(), (UserDTO) user, game.get().getMapGraph(), usersInGame), user);
            }
            game.get().setUpUserArrayList();
            game.get().setUpInventories();
            sendToAllInGame(game.get().getName(), new NextTurnMessage(game.get().getName(), game.get().getUser(game.get().getTurn()).getUsername(), game.get().getTurn()));
        } else {
            throw new GameManagementException("Not enough Players ready!");
        }
        lobby.get().setGameShouldStart(false);
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
        Optional<Lobby> lobby = lobbyService.getLobby(playerReadyRequest.getName());
        if (playerReadyRequest.getBoolean()) {
            lobby.get().incrementRdyResponsesReceived();
            lobby.get().joinPlayerReady(playerReadyRequest.getUser());
        } else if (!playerReadyRequest.getBoolean()) {
            lobby.get().incrementRdyResponsesReceived();
        }
        if (lobby.get().getRdyResponsesReceived() == lobby.get().getUsers().size() && gameManagement.getGame(lobby.get().getName()).isEmpty()) {
            try {
                startGame(lobby, lobby.get().getGameFieldVariant());
            } catch (GameManagementException e) {
                LOG.debug(e);
                sendToListOfUsers(lobby.get().getPlayersReady(), lobby.get().getName(), new NotEnoughPlayersMessage(lobby.get().getName()));
            }
        }
    }

    /**
     * Handles EndTurnRequests found on the eventbus.
     *
     * <p>If an EndTurnRequest is found on the eventbus, this method checks if the sender is the player with the
     * current turn. If so, the method calls the nextRound method to increment the turncount. After that, the method
     * sends a NextTurnMessage to all participants of the current game, telling them in wich turn they are now, in wich
     * game and whos turn is up now.</p>
     *
     * @param request Transports the games name and the senders UserDTO.
     * @author Pieter Vogt, Philip Nitsche
     * @since 2021-03-26
     */
    @Subscribe
    public void onEndTurnRequest(EndTurnRequest request) {
        Optional<Game> game = gameManagement.getGame(request.getName());
        if (request.getUser().getUsername().equals(game.get().getUser(game.get().getTurn()).getUsername())) {
            try {
                boolean priorGamePhase = game.get().isStartingTurns();
                game.get().nextRound();
                if (priorGamePhase == true && game.get().isStartingTurns() == false) {
                    distributeResources(request.getName());
                }
                sendToAllInGame(game.get().getName(), new NextTurnMessage(game.get().getName(),
                        game.get().getUser(game.get().getTurn()).getUsername(), game.get().getTurn()));
            } catch (GameManagementException e) {
                LOG.debug(e);
                System.out.println("Sender " + request.getUser().getUsername() + " was not player with current turn");
            }
        }
    }

    /**
     * Handles BuyDevelopmentCardRequest found on the eventbus.
     *
     * <p>
     * Gets the game from the gameManagement and retrieves the inventory from the user. Then the method checks if enough
     * ressources are available to buy a development card. If there are enough ressources, then the method gets the next
     * development card from the development card deck and sends a message with the development card to the user. If
     * there are not enough ressources a NoEnoughRessourcesMessage is send to the user.
     * </p>
     *
     * @param request Transports the senders UserDTO
     * @author Marius Birk
     * @since 2021-04-03
     */
    @Subscribe
    public void onBuyDevelopmentCardRequest(BuyDevelopmentCardRequest request) {
        Optional<Game> game = gameManagement.getGame(request.getName());
        if (game.isPresent()) {
            if (request.getUser().equals(gameManagement.getGame(request.getName()).get().getUser(gameManagement.getGame(request.getName()).get().getTurn()))) {
                Inventory inventory = game.get().getInventory(request.getUser());
                if (inventory.wool.getNumber() >= 1 && inventory.ore.getNumber() >= 1 && inventory.grain.getNumber() >= 1) {
                    String devCard = game.get().getDevelopmentCardDeck().drawnCard();

                    inventory.wool.decNumber();
                    inventory.ore.decNumber();
                    inventory.grain.decNumber();
                    BuyDevelopmentCardMessage response = new BuyDevelopmentCardMessage(devCard);
                    sendToSpecificUserInGame(game, response, request.getUser());
                } else {
                    NotEnoughRessourcesMessage nerm = new NotEnoughRessourcesMessage();
                    nerm.setName(game.get().getName());
                    sendToSpecificUserInGame(game, nerm, request.getUser());
                }
            }
        }
    }

    /**
     * Method to update private and public inventories in a game
     * <p>
     * If game exists, method sends two types of messages with updated information about inventories.
     * PrivateInventoryChangeMessage is send to specific player in the game. PublicInventoryChangeMessage is send to all
     * players in the game.
     * <p>
     *
     * @param game game that wants to update private and public inventories
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */
    public void updateInventory(Optional<Game> game) {
        if (game.isPresent()) {
            for (User user : game.get().getUsers()) {
                HashMap privateInventory = game.get().getInventory(user).getPrivateView();
                HashMap publicInventory = game.get().getInventory(user).getPublicView();
                PrivateInventoryChangeMessage privateInventoryChangeMessage = new PrivateInventoryChangeMessage(game.get().getName(), (UserDTO) user, privateInventory);
                sendToSpecificUserInGame(game, privateInventoryChangeMessage, user);
                PublicInventoryChangeMessage publicInventoryChangeMessage = new PublicInventoryChangeMessage(publicInventory, user);
                sendToAllInGame(game.get().getName(), publicInventoryChangeMessage);
            }
        }
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
        System.out.println("Got message " + request.getUser().getUsername());
        Optional<Game> game = gameManagement.getGame(request.getName());
        //TODO: Wird nur zum testen verwendet

        if (game.isPresent()) {
            boolean numberOfCardsCorrect = true;

            for (TradeItem tradeItem : request.getTradeItems()) {
                boolean notEnoughInInventoryCheck = tradeItem.getCount() > (int) game.get().getInventory(request.getUser()).getPrivateView().get(tradeItem.getName());
                if (tradeItem.getCount() < 0 || notEnoughInInventoryCheck == true) {
                    numberOfCardsCorrect = false;
                    break;
                }
            }

            if (numberOfCardsCorrect == true) {
                String tradeCode = request.getTradeCode();
                if (!game.get().getTradeList().containsKey(tradeCode)) {
                    game.get().addTrades(new Trade(request.getUser(), request.getTradeItems()), tradeCode);

                    System.out.println("added Trade " + tradeCode + " by User: " + request.getUser().getUsername() + " items: " + request.getTradeItems());

                    for (User user : game.get().getUsers()) {
                        if (!request.getUser().equals(user)) {
                            TradeOfferInformBiddersMessage tradeOfferInformBiddersMessage = new TradeOfferInformBiddersMessage(request.getUser(), request.getName(), tradeCode, request.getTradeItems(), (UserDTO) user, request.getWishItems());
                            sendToSpecificUserInGame(game, tradeOfferInformBiddersMessage, user);
                            System.out.println("Send TradeOfferInformBiddersMessage to " + user.getUsername());
                        }
                    }
                } else {
                    Trade trade = game.get().getTradeList().get(request.getTradeCode());
                    trade.addBid(request.getUser(), request.getTradeItems());
                    System.out.println("added bid to " + tradeCode + " by User: " + request.getUser().getUsername() + " items: " + request.getTradeItems());
                    if (trade.getBids().size() == game.get().getUsers().size() - 1) {
                        System.out.println("bids full");
                        TradeInformSellerAboutBidsMessage tisabm = new TradeInformSellerAboutBidsMessage(trade.getSeller(), request.getName(), tradeCode, trade.getBidders(), trade.getBids());
                        sendToSpecificUserInGame(game, tisabm, trade.getSeller());
                        System.out.println("Send TradeInformSellerAboutBidsMessage to " + trade.getSeller().getUsername());
                    }
                }
            } else {
                System.out.println("Nicht genug im Inventar");
                TradeCardErrorMessage tcem = new TradeCardErrorMessage(request.getUser(), request.getName(), request.getTradeCode());
                sendToSpecificUserInGame(game, tcem, request.getUser());
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
        Optional<Game> game = gameManagement.getGame(request.getName());
        if (game.isPresent()) {
            Trade trade = game.get().getTradeList().get(request.getTradeCode());

            if (request.getTradeAccepted() == true) {
                Inventory inventorySeller = game.get().getInventory(trade.getSeller());
                Inventory inventoryBidder = game.get().getInventory(request.getUser());

                for (TradeItem soldItem : trade.getSellingItems()) {
                    inventorySeller.decCard(soldItem.getName(), soldItem.getCount());
                    inventoryBidder.incCard(soldItem.getName(), soldItem.getCount());
                }
                for (TradeItem bidItem : trade.getBids().get(request.getUser())) {
                    inventorySeller.incCard(bidItem.getName(), bidItem.getCount());
                    inventoryBidder.decCard(bidItem.getName(), bidItem.getCount());
                }
            }
            tradeEndedChatMessageHelper(game.get().getName(), request.getTradeCode(), request.getUser().getUsername(), request.getTradeAccepted());
            sendToAllInGame(request.getName(), new TradeEndedMessage(request.getTradeCode()));
            game.get().removeTrade(request.getTradeCode());
        }
    }

    /**
     * help method to deliver a chatmessage to all players of the game how the trade ended
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
        Optional<Game> game = gameManagement.getGame(request.getName());
        UserDTO user = request.getUser();
        TradeStartedMessage tsm = new TradeStartedMessage(user, request.getName(), request.getTradeCode());
        sendToSpecificUserInGame(game, tsm, user);
    }

    /**
     * Draws a random card from the user, that was chosen from the player that moved the robber.
     * <p>
     * If a DrawRandomResourceFromPlayerMessage is detected on the eventbus, this method will be invoked. First the
     * method checks if the game is present and then gets the inventory of the user, from who the card will be drawn.
     * After that, a random resource will be chosen and the method iterates over the inventory in search of the
     * random resource. If it found the method, the number of the resource will be decreased and the resource will
     * be increased in the inventory of the player that moved the robber.
     *
     * @param drawRandomResourceFromPlayerMessage
     * @author Marius Birk
     * @since 2021-05-01
     */
    @Subscribe
    public void onDrawRandomResourceFromPlayerMessage(DrawRandomResourceFromPlayerMessage drawRandomResourceFromPlayerMessage) {
        Optional<Game> game = gameManagement.getGame(drawRandomResourceFromPlayerMessage.getName());
        if (game.isPresent()) {
            HashMap inventory = game.get().getInventory(new UserDTO(drawRandomResourceFromPlayerMessage.getChosenName(), "", "")).getPrivateView();
            String random = randomResource();
            inventory.keySet().forEach(e -> {
                if (e.equals(random)) {
                    game.get().getInventory(drawRandomResourceFromPlayerMessage.getUser()).incCard(random, 1);
                    game.get().getInventory(new UserDTO(drawRandomResourceFromPlayerMessage.getChosenName(), "", "")).decCard(random, 1);
                }
            });
            updateInventory(game);
        }
    }

    /**
     * The method chosses a random resource and returns it.
     * <p>
     *   This method will be invoked, if the name of a random resource is needed. For that, it creates a List of resources
     *   and initializes a random number between 1 and 5. To get now a random name of resource, we substrate 1 and
     *   invoke the get() method of the List and return that value.
     *
     * @return the name of a resource
     * @author Marius Birk
     * @since 2021-04-29
     */
    public String randomResource() {
        List<String> resources = new ArrayList();
        resources.add("Wool");
        resources.add("Lumber");
        resources.add("Brick");
        resources.add("Grain");
        resources.add("Ore");

        int random = (int) Math.floor(Math.random() * (4 - 0 +1)) + 0;
        return resources.get(random);
    }

    public GameManagement getGameManagement() {
        return gameManagement;
    }
}
