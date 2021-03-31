package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.RollDiceRequest;
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
    private int Players;

    /**
     * Constructor
     * <p>
     *
     * @param gameManagement        The management class for creating, storing and deleting
     *                              games
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
                sendToAll(new GameDroppedMessage(gameLeaveUserRequest.getName()));

            } else {
                if (gameLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = gameLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new GameLeftSuccessfulResponse(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser()));
                }
                game.get().leaveUser(gameLeaveUserRequest.getUser());
                sendToAll(new GameSizeChangedMessage(gameLeaveUserRequest.getName()));
                sendToAllInGame(gameLeaveUserRequest.getName(), new UserLeftGameMessage(gameLeaveUserRequest.getName(), gameLeaveUserRequest.getUser()));
            }
        } else {
            throw new GameManagementException("Game unknown!");
        }
    }

    /**
     * Handles RetrieveAllThisGameUsersRequests found on the EventBus
     * <p>
     * If a RetrieveAllThisGameUsersRequests is detected on the EventBus, this method is called.
     * It prepares the sending of a AllThisGameUsersResponse for a specific user that sent the initial request.
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
     * If a RollDiceRequest is detected on the EventBus, this method is called.
     * It rolls the dices and sends a ResponseChatMessage containing the user who rolls the dice
     * and the result is shown to every user in the game.
     *
     * @param rollDiceRequest The RollDiceRequest found on the EventBus
     * @author Kirstin, Pieter
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-01-07
     *
     * Enhanced by Carsten Dekker
     * @since 2021-03-31
     */
    @Subscribe
    public void onRollDiceRequest(RollDiceRequest rollDiceRequest) {
        LOG.debug("Got new RollDiceRequest from user: " + rollDiceRequest.getUser());

        Dice dice = new Dice();
        dice.rollDice();
        String eyes = Integer.toString(dice.getEyes());
        try {
            String chatMessage;
            var chatId = "game_" + rollDiceRequest.getName();
            if (dice.getEyes() == 8 || dice.getEyes() == 11) {
                chatMessage = "Player " + rollDiceRequest.getUser().getUsername() + " rolled an " + eyes;
            } else {
                chatMessage = "Player " + rollDiceRequest.getUser().getUsername() + " rolled a " + eyes;
            }
            ResponseChatMessage msg = new ResponseChatMessage(chatMessage, chatId, rollDiceRequest.getUser().getUsername(), System.currentTimeMillis());
            post(msg);
            LOG.debug("Posted ResponseChatMessage on eventBus");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }


    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and
     * posts it on the EventBus
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
     * If a StartGameRequest is detected on the EventBus, this method is called.
     * If the number of players in the lobby is more than 1, Method creates StartGameRequest with name of the lobby and user,
     * which will be sent to all players in the lobby.
     * It starts a timer and tries to start the game afterwards. If no game was created a NotEnoughPlayersResponse is sent to
     * all users that are ready to start the game.
     * Else Method creates NotLobbyOwnerResponse, NotEnoughPlayersResponse or GameAlreadyExistsResponse and sends it to a specific user that sent the initial request.
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     *
     * @param startGameRequest the StartGameRequest found on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.lobby.request.StartGameRequest
     * @since 2021-01-24
     */
    @Subscribe
    public void onStartGameRequest(StartGameRequest startGameRequest) {
        Optional<Lobby> lobby = lobbyService.getLobby(startGameRequest.getName());
        if (gameManagement.getGame(lobby.get().getName()).isEmpty() && lobby.get().getUsers().size() > 1 && startGameRequest.getUser().toString().equals(lobby.get().getOwner().toString())) {
            lobby.get().setPlayersReadyToNull();
            Players = 0;
            sendToAllInLobby(startGameRequest.getName(), new StartGameMessage(startGameRequest.getName(), startGameRequest.getUser()));
            int seconds = 60;
            Timer timer = new Timer();
            class RemindTask extends TimerTask {
                public void run() {
                    if (gameManagement.getGame(lobby.get().getName()).isEmpty() && Players != lobby.get().getUsers().size()) {
                        Players = lobby.get().getUsers().size();
                        try {
                            startGame(lobby);
                        } catch (GameManagementException e) {
                            LOG.debug(e);
                            sendToListOfUsers(lobby.get().getUsers(), lobby.get().getName(), new NotEnoughPlayersMessage(lobby.get().getName()));
                        }
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
     * A new game is created if at least 2 players are to start the game and if not already a game exists.
     * All players ready are joined to the game and a GameCreatedMessage is send to all players in the game.
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     *
     * @param lobby lobby that wants to start a game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */

    public void startGame(Optional<Lobby> lobby) {
        if (lobby.get().getPlayersReady().size() > 1) {
            gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner());
            Optional<Game> game = gameManagement.getGame(lobby.get().getName());
            for (User user : lobby.get().getPlayersReady()) {
                game.get().joinUser(user);
                sendToSpecificUserInGame(game, new GameCreatedMessage(game.get().getName(), (UserDTO) user, game.get().getGameField()), user);
            }
        } else {
            throw new GameManagementException("Not enough Players ready!");
        }
    }

    /**
     * Handles PlayerReadyRequest found on the EventBus
     * <p>
     * If a PlayerReadyRequest is detected on the EventBus, this method is called.
     * Method adds ready players to the PlayerReady list and counts the number of player responses in variable Player
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
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
            Players += 1;
            lobby.get().joinPlayerReady(playerReadyRequest.getUser());
        } else if (!playerReadyRequest.getBoolean()) {
            Players += 1;
        }
        if (Players == lobby.get().getUsers().size() && gameManagement.getGame(lobby.get().getName()).isEmpty()) {
            try {
                startGame(lobby);
            } catch (GameManagementException e) {
                LOG.debug(e);
                sendToListOfUsers(lobby.get().getPlayersReady(), lobby.get().getName(), new NotEnoughPlayersMessage(lobby.get().getName()));
            }
        }
    }

}
