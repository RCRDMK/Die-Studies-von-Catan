package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.*;
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
import de.uol.swp.server.AI.RandomAI;
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
    public boolean onConstructionMessage(ConstructionRequest message) {
        LOG.debug("Recieved new ConstructionMessage from user " + message.getUser());
        Optional<Game> game = gameManagement.getGame(message.getGame());
        int playerIndex = 666;
        if (message.getUuid() != null) {
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
                                sendToAllInGame(game.get().getName(), new SuccessfulConstructionMessage(game.get().getName(), message.getUser().getWithoutPassword(), playerIndex,
                                        message.getUuid(), "BuildingNode"));
                                return true;
                            }
                        }
                    }
                } else {
                    for (MapGraph.StreetNode streetNode : game.get().getMapGraph().getStreetNodeHashSet()) {
                        if (message.getUuid().equals(streetNode.getUuid())) {
                            if (streetNode.buildRoad(playerIndex)) {
                                sendToAllInGame(game.get().getName(), new SuccessfulConstructionMessage(game.get().getName(), message.getUser().getWithoutPassword(), playerIndex,
                                        message.getUuid(), "StreetNode"));
                                return true;
                            }

                        }
                    }
                }


            } catch (GameManagementException e) {
                LOG.debug(e);
                System.out.println("Player " + message.getUser() + " tried to build at node with UUID: " + message.getUuid() + " but it did not work.");
            }
        }
        return false;
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
        Optional<Game> game = gameManagement.getGame(rollDiceRequest.getName());
        if (game.isPresent()) {
            if (rollDiceRequest.getUser().equals(game.get().getUser(game.get().getTurn()))) {
                Dice dice = new Dice();
                dice.rollDice();
                // Check if cheatEyes number is provided in rollDiceRequest, if so -> set Eyes manually on dice
                // for the roll cheat, else ignore and use rolledDice
                if (rollDiceRequest.getCheatEyes() > 0) {
                    dice.setEyes(rollDiceRequest.getCheatEyes());
                }
                int addedEyes = dice.getDiceEyes1() + dice.getDiceEyes2();
                if (addedEyes == 7) {
                    //TODO Hier müsste der Räuber aktiviert werden.
                } else {
                    distributeResources(addedEyes, rollDiceRequest.getName());
                    game.get().setLastRolledDiceValue(addedEyes);
                }
                try {
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
                }
                try {
                    RollDiceResultMessage result = new RollDiceResultMessage(dice.getDiceEyes1(), dice.getDiceEyes2(), game.get().getTurn(), game.get().getName());
                    sendToAllInGame(game.get().getName(), result);
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
        if (gameManagement.getGame(lobby.get().getName()).isEmpty() && usersInLobby.size() > 1 && startGameRequest.getUser().getUsername().equals(lobby.get().getOwner().getUsername()) && !lobby.get().getGameShouldStart()) {
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
                    lobby.get().setGameShouldStart(false);
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
            sendToAllInGame(game.get().getName(), new NextTurnMessage(game.get().getName(), game.get().getUser(game.get().getTurn()).getUsername(), game.get().getTurn(), game.get().isStartingTurns()));
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
        if (game.isPresent()) {
            if (request.getUser().getUsername().equals(game.get().getUser(game.get().getTurn()).getUsername()) && game.get().getCurrentCard().equals("")) {
                try {
                    boolean priorGamePhase = game.get().isStartingTurns();
                    game.get().nextRound();
                    if (priorGamePhase == true && game.get().isStartingTurns() == false) {
                        distributeResources(request.getName());
                    }
                    sendToAllInGame(game.get().getName(), new NextTurnMessage(game.get().getName(),
                            game.get().getUser(game.get().getTurn()).getUsername(), game.get().getTurn(), game.get().isStartingTurns()));
                    // Check if the size of actual players is smaller than the size of intended players, then activate AI
                    if (game.get().getUsers().size() < game.get().getUsersList().size()) {
                        RollDiceRequest rdr = new RollDiceRequest(game.get().getName(), game.get().getUser(game.get().getTurn()));
                        onRollDiceRequest(rdr);
                        useAI((GameDTO) game.get());
                    }
                } catch (GameManagementException e) {
                    LOG.debug(e);
                    System.out.println("Sender " + request.getUser().getUsername() + " was not player with current turn");
                }
            }
        }
    }

    public void useAI(GameDTO game) {
        RandomAI randomAI = new RandomAI(game);
        randomAI.startTurnAction();

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
            if (request.getUser().getUsername().equals(game.get().getUser(game.get().getTurn()).getUsername())) {
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
     * Handles Requests from a client to play a DevelopmentCard
     * <p>
     * If the game exists, the player who sent the request is the turnPlayer and
     * if a DevelopmentCard wasn't already played this turn, or is currently being played,
     * then remove the DevelopmentCard from the inventory of the player and inform him that he
     * may proceed with the resolution of the card. If something went wrong, also inform him.
     *
     * @param request the PlayDevelopmentCardRequest sent by the client
     * @author Marc Hermes
     * @since 2021-05-01
     */
    @Subscribe
    public void onPlayDevelopmentCardRequest(PlayDevelopmentCardRequest request) {
        Optional<Game> game = gameManagement.getGame(request.getName());
        if (game.isPresent()) {
            User turnPlayer = game.get().getUser(game.get().getTurn());
            if (request.getUser().getUsername().equals(turnPlayer.getUsername())) {
                Inventory inventory = game.get().getInventory(turnPlayer);
                String devCard = request.getDevCard();
                String currentCardOfGame = game.get().getCurrentCard();
                boolean alreadyPlayedCard = game.get().playedCardThisTurn();
                //TODO: delete these 3, only used for testing
                inventory.cardMonopoly.incNumber();
                inventory.cardRoadBuilding.incNumber();
                inventory.cardYearOfPlenty.incNumber();
                // TODO: Check if the card was bought THIS turn, because it cannot be used then
                switch (devCard) {

                    case "Monopoly":
                        if (inventory.cardMonopoly.getNumber() > 0 && currentCardOfGame.equals("") && !alreadyPlayedCard) {
                            game.get().setCurrentCard("Monopoly");
                            game.get().setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.get().getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.cardMonopoly.decNumber();
                            break;
                        }

                    case "Road Building":
                        if (inventory.cardRoadBuilding.getNumber() > 0 && currentCardOfGame.equals("") && !alreadyPlayedCard && inventory.road.getNumber() > 1) {
                            // TODO: check if the player is allowed to even attempt to build 2 streets i.e. not possible when there are no legal spaces to build 2 streets
                            // TODO: probs very complicated to check that, so maybe just ignore that fringe scenario???
                            game.get().setCurrentCard("Road Building");
                            game.get().setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.get().getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.cardRoadBuilding.decNumber();
                            break;
                        }

                    case "Year of Plenty":
                        if (inventory.cardYearOfPlenty.getNumber() > 0 && currentCardOfGame.equals("") && !alreadyPlayedCard) {
                            // TODO: Check if there theoretically are resources left in the bank that could be obtained for the player
                            game.get().setCurrentCard("Year of Plenty");
                            game.get().setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.get().getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.cardYearOfPlenty.decNumber();
                            break;
                        }

                    case "Knight":
                        if (inventory.cardKnight.getNumber() > 0 && currentCardOfGame.equals("") && !alreadyPlayedCard) {
                            game.get().setCurrentCard("Knight");
                            game.get().setPlayedCardThisTurn(true);
                            PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, true, turnPlayer.getUsername(), game.get().getName());
                            response.initWithMessage(request);
                            post(response);
                            inventory.setPlayedKnights(inventory.getPlayedKnights() + 1);
                            inventory.cardKnight.decNumber();
                            break;
                        }

                    default:
                        PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(devCard, false, turnPlayer.getUsername(), game.get().getName());
                        response.initWithMessage(request);
                        post(response);
                        break;
                }

            } else {
                PlayDevelopmentCardResponse response = new PlayDevelopmentCardResponse(request.getDevCard(), false, request.getUser().getUsername(), game.get().getName());
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
     *
     * @param request the ResolveDevelopmentCardRequest sent from the client
     * @author Marc Hermes
     * @since 2021-05-01
     */
    @Subscribe
    public void onResolveDevelopmentCardRequest(ResolveDevelopmentCardRequest request) {
        Optional<Game> game = gameManagement.getGame(request.getName());
        if (game.isPresent()) {
            User turnPlayer = game.get().getUser(game.get().getTurn());
            String gameName = game.get().getName();
            String devCard = request.getDevCard();

            if (request.getUser().getUsername().equals(turnPlayer.getUsername()) && request.getDevCard().equals(game.get().getCurrentCard())) {
                Inventory turnPlayerInventory = game.get().getInventory(turnPlayer);
                ResolveDevelopmentCardMessage message = new ResolveDevelopmentCardMessage(devCard, (UserDTO) turnPlayer, gameName);
                ResolveDevelopmentCardNotSuccessfulResponse notSuccessfulResponse = new ResolveDevelopmentCardNotSuccessfulResponse(devCard, turnPlayer.getUsername(), gameName);
                boolean resolvedDevelopmentCardSuccessfully = false;

                switch (devCard) {
                    case "Monopoly":
                        if (request instanceof ResolveDevelopmentCardMonopolyRequest) {
                            ResolveDevelopmentCardMonopolyRequest monopolyRequest = (ResolveDevelopmentCardMonopolyRequest) request;
                            String resource = monopolyRequest.getResource();
                            for (User user : game.get().getUsers()) {
                                if (!user.equals(turnPlayer)) {
                                    Inventory x = game.get().getInventory(user);
                                    switch (resource) {
                                        case "Lumber":
                                            turnPlayerInventory.incCard(resource, x.lumber.getNumber());
                                            x.lumber.decNumber(x.lumber.getNumber());
                                            resolvedDevelopmentCardSuccessfully = true;
                                            break;
                                        case "Ore":
                                            turnPlayerInventory.incCard(resource, x.ore.getNumber());
                                            x.ore.decNumber(x.ore.getNumber());
                                            resolvedDevelopmentCardSuccessfully = true;
                                            break;
                                        case "Wool":
                                            turnPlayerInventory.incCard(resource, x.wool.getNumber());
                                            x.wool.decNumber(x.wool.getNumber());
                                            resolvedDevelopmentCardSuccessfully = true;
                                            break;
                                        case "Brick":
                                            turnPlayerInventory.incCard(resource, x.brick.getNumber());
                                            x.brick.decNumber(x.brick.getNumber());
                                            resolvedDevelopmentCardSuccessfully = true;
                                            break;
                                        case "Grain":
                                            turnPlayerInventory.incCard(resource, x.grain.getNumber());
                                            x.grain.decNumber(x.grain.getNumber());
                                            resolvedDevelopmentCardSuccessfully = true;
                                            break;
                                        default:
                                            resolvedDevelopmentCardSuccessfully = false;
                                            break;

                                    }
                                }
                            }
                        }
                        if (resolvedDevelopmentCardSuccessfully) {
                            sendToAllInGame(gameName, message);
                            updateInventory(game);
                            game.get().setCurrentCard("");
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
                                game.get().setCurrentCard("");
                            }
                        } else {
                            notSuccessfulResponse.initWithMessage(request);
                            post(notSuccessfulResponse);
                        }
                        break;

                    case "Year of Plenty":
                        // TODO: implement bank resources and "not successful" functionality i.e. when the bank doesnt have enough resources
                        if (request instanceof ResolveDevelopmentCardYearOfPlentyRequest) {
                            ResolveDevelopmentCardYearOfPlentyRequest yearOfPlentyRequest = (ResolveDevelopmentCardYearOfPlentyRequest) request;

                            boolean successful1 = turnPlayerInventory.incCard(yearOfPlentyRequest.getResource1(), 1);
                            boolean successful2 = turnPlayerInventory.incCard(yearOfPlentyRequest.getResource2(), 1);
                            if (!(successful1 && successful2)) {
                                turnPlayerInventory.decCard(yearOfPlentyRequest.getResource1(), 1);
                                turnPlayerInventory.decCard(yearOfPlentyRequest.getResource2(), 1);
                                notSuccessfulResponse.initWithMessage(request);
                                notSuccessfulResponse.setErrorDescription("Please select 2 valid resources");
                                post(notSuccessfulResponse);
                                break;
                            }
                            sendToAllInGame(gameName, message);
                            game.get().setCurrentCard("");
                            updateInventory(game);
                        } else {
                            notSuccessfulResponse.initWithMessage(request);
                            post(notSuccessfulResponse);
                        }
                        break;

                    case "Knight":
                        // TODO: implement functionality of moving the robber/bandit
                        sendToAllInGame(gameName, message);
                        break;

                    default:
                        notSuccessfulResponse.initWithMessage(request);
                        notSuccessfulResponse.setErrorDescription("Not a valid DevelopmentCard");
                        post(notSuccessfulResponse);
                        break;
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
                HashMap<String, Integer> privateInventory = game.get().getInventory(user).getPrivateView();
                HashMap<String, Integer> publicInventory = game.get().getInventory(user).getPublicView();
                PrivateInventoryChangeMessage privateInventoryChangeMessage = new PrivateInventoryChangeMessage(user.getWithoutPassword(), game.get().getName(), privateInventory);
                sendToSpecificUserInGame(game, privateInventoryChangeMessage, user);
                PublicInventoryChangeMessage publicInventoryChangeMessage = new PublicInventoryChangeMessage(publicInventory, user, game.get().getName());
                sendToAllInGame(game.get().getName(), publicInventoryChangeMessage);
            }
        }
    }

    /**
     * Returns the gameManagement
     *
     * @return
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
        System.out.println("Got message " + request.getUser().getUsername());
        Optional<Game> game = gameManagement.getGame(request.getName());
        // TODO: Wird nur zum testen verwendet
      /*  game.get().getInventory(request.getUser()).incCard("Lumber", 10);
        game.get().getInventory(request.getUser()).incCard("Ore", 10);
        game.get().getInventory(request.getUser()).incCard("Wool", 10);
        game.get().getInventory(request.getUser()).incCard("Grain", 10);
        game.get().getInventory(request.getUser()).incCard("Brick", 10);
        Inventory easyPrüfen = game.get().getInventory(request.getUser());
*/
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

            if (request.getTradeAccepted() == true && !request.getUser().getUsername().equals(trade.getSeller().getUsername())) {
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
}
