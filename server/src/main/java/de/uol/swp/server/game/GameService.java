package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.TerrainFieldContainer;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.AllCreatedGamesResponse;
import de.uol.swp.common.game.response.GameAlreadyExistsResponse;
import de.uol.swp.common.game.response.NotLobbyOwnerResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.StartGameMessage;
import de.uol.swp.common.lobby.request.StartGameRequest;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.game.AllThisGameUsersResponse;
import de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import de.uol.swp.server.lobby.LobbyManagementException;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


/**
 * Handles the game requests send by the users
 *
 * @author Kirstin, Pieter
 * @since 2021-01-07
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class GameService extends AbstractService {

    private final GameManagement gameManagement;
    private final LobbyService lobbyService;
    private final AuthenticationService authenticationService;
    private static final Logger LOG = LogManager.getLogger(GameService.class);

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
    public GameService(GameManagement gameManagement, LobbyService lobbyService, AuthenticationService authenticationService, EventBus eventBus) {
        super(eventBus);
        this.gameManagement = gameManagement;
        this.authenticationService = authenticationService;
        this.lobbyService = lobbyService;
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
                    sendToAll(new GameDroppedMessage(gameLeaveUserRequest.getName()));
                }
            } else if (game.get().getUsers() == null) {
                gameManagement.dropGame(gameLeaveUserRequest.getName());
                lobby.get().setGameStarted(false);
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
     * ResponseChatMessage containing the user who roll the dice and the result to every user in the lobby.
     * If a RollDiceRequest is detected on the EventBus, this method is called.
     * It rolls the dices and sends a ResponseChatMessage containing the user who rolls the dice
     * and the result is shown to every user in the game.
     *
     * @param rollDiceRequest The RollDiceRequest found on the EventBus
     * @author Kirstin, Pieter
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-01-07
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
        if (dice.getEyes() == 7) {
            //TODO Hier müsste der Räuber aktiviert werden.
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
            ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId, rollDiceRequest.getUser().getUsername(), System.currentTimeMillis());
            post(msg);
            LOG.debug("Posted ResponseChatMessage on eventBus");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * Handles the distribution of resources to the users
     * <p>
     * This method handles the distribution of the resources to the users. First the method gets the game and gets the coressponding
     * terrainfieldcontainer. After that the method checks if the diceToken on the field is equal to the rolled amount of eyes and increases the resource of the user by one.
     * To Do is, that not every user gets the ressource.
     *
     * @param eyes     Number of eyes rolled with dice
     * @param gameName Name of the Game
     * @author Marius Birk, Carsten Dekker
     * @since 2021-04-06
     */
    public void distributeResources(int eyes, String gameName) {
        Optional<Game> game = gameManagement.getGame(gameName);

        if (game.isPresent()) {
            //TODO Sobald eine Bank implementiert ist, müssen die Ressourcen natürlich noch bei der Bank abgezogen werden.
            //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
            TerrainFieldContainer[] temp = game.get().getGameField().getTFCs();
            for (TerrainFieldContainer terrainFieldContainer : temp) {
                if (terrainFieldContainer.getDiceTokens() == eyes) {
                    switch (terrainFieldContainer.getFieldType()) {
                        case 1:
                            for (User user : game.get().getUsers()) {
                                //TODO Wenn Stadt angrenzend an Field, dann gebe User Resource, Erstmal wird an jeden Resource geben.
                                game.get().getInventory(user).lumber.incNumber();
                            }
                            break;
                        case 2:
                            for (User user : game.get().getUsers()) {
                                //TODO Wenn Stadt angrenzend an Field, dann gebe User Resource, Erstmal wird an jeden Resource geben
                                game.get().getInventory(user).grain.incNumber();
                            }
                            break;
                        case 3:
                            for (User user : game.get().getUsers()) {
                                //TODO Wenn Stadt angrenzend an Field, dann gebe User Resource, Erstmal wird an jeden Resource geben
                                game.get().getInventory(user).wool.incNumber();
                            }
                            break;
                        case 4:
                            for (User user : game.get().getUsers()) {
                                //TODO Wenn Stadt angrenzend an Field, dann gebe User Resource, Erstmal wird an jeden Resource geben
                                game.get().getInventory(user).brick.incNumber();
                            }
                            break;
                        case 5:
                            for (User user : game.get().getUsers()) {
                                //TODO Wenn Stadt angrenzend an Field, dann gebe User Ressource, Erstmal wird an jeden Ressource geben
                                game.get().getInventory(user).ore.incNumber();
                            }
                            break;
                        default:
                            break;
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
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     * enhanced by Marc Hermes 2021-03-25
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
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05 enhanced by Pieter Vogt 2021-03-26
     * enhanced by Marc Hermes 2021-03-25
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
                game.get().joinUser(user);
                usersInGame.add((UserDTO) user);

            }
            lobby.get().setPlayersReadyToNull();
            lobby.get().setRdyResponsesReceived(0);
            for (User user : game.get().getUsers()) {
                sendToSpecificUserInGame(game, new GameCreatedMessage(game.get().getName(), (UserDTO) user, game.get().getGameField(), usersInGame), user);
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
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Subscribe
    public void onEndTurnRequest(EndTurnRequest request) {
        if (request.getUser().getUsername().equals(gameManagement.getGame(request.getName()).get().getUser(gameManagement.getGame(request.getName()).get().getTurn()).getUsername())) {
            try {
                gameManagement.getGame(request.getName()).get().nextRound();
                sendToAllInGame(gameManagement.getGame(request.getName()).get().getName(), new NextTurnMessage(gameManagement.getGame(request.getName()).get().getName(), gameManagement.getGame(request.getName()).get().getUser(gameManagement.getGame(request.getName()).get().getTurn()).getUsername(), gameManagement.getGame(request.getName()).get().getTurn()));
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
     * Gets the game from the gameManagement and retrieves the inventory from the user. Then the method
     * checks if enough ressources are available to buy a development card. If there are enough ressources, then
     * the method gets the next development card from the development card deck and sends a message with the development card to the user.
     * If there are not enough ressources a NoEnoughRessourcesMessage is send to the user.
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
}
