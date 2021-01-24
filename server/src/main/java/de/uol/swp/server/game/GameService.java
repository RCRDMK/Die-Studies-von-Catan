package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.message.RollDiceRequest;
import de.uol.swp.common.game.response.AllCreatedGamesResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.StartGameMessage;
import de.uol.swp.common.lobby.request.StartGameRequest;
import de.uol.swp.common.lobby.response.NotEnoughPlayersResponse;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.response.game.AllThisGameUsersResponse;
import de.uol.swp.common.user.response.game.GameCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyManagementException;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;


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

    @Subscribe
    public void onRetrieveAllThisGameUsersRequest(RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest) {
        Optional<Game> game = gameManagement.getGame(retrieveAllThisGameUsersRequest.getName());
        if (game.isPresent()) {
            List<Session> gameUsers = authenticationService.getSessions(game.get().getUsers());
            if (retrieveAllThisGameUsersRequest.getMessageContext().isPresent()) {
                Optional<MessageContext> ctx = retrieveAllThisGameUsersRequest.getMessageContext();
                sendToSpecificUser(ctx.get(), new AllThisGameUsersResponse(gameUsers));
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

    public void sendToSpecificUser(MessageContext ctx, ResponseMessage message) {
        ctx.writeAndFlush(message);
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
     * It rolls the dices and sends a ResponseChatMessage containing the user who roll the dice
     * and the result to every user in the lobby.
     *
     * @param rollDiceRequest The RollDiceRequest found on the EventBus
     * @author Kirstin, Pieter
     * @see de.uol.swp.common.game.message.RollDiceRequest
     * @since 2021-01-07
     */
    @Subscribe
    public void onRollDiceRequest (RollDiceRequest rollDiceRequest) {
        LOG.debug("Got new RollDiceRequest from user: " + rollDiceRequest.getUser());

        Dice dice = new Dice();
        dice.rollDice();
        String eyes = Integer.toString(dice.getEyes());
        if (dice.getEyes() == 8 || dice.getEyes() == 11) {
            ResponseChatMessage msg = new ResponseChatMessage("Player " + rollDiceRequest.getUser().getUsername() + " rolled an " + eyes, rollDiceRequest.getName(), "Dice", System.currentTimeMillis());
            post(msg);
        } else {
            ResponseChatMessage msg = new ResponseChatMessage("Player " + rollDiceRequest.getUser().getUsername() + " rolled a " + eyes, rollDiceRequest.getName(), "Dice", System.currentTimeMillis());
            post(msg);
        }

        LOG.debug("Posted ResponseChatMessage on eventBus");
    }


    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and
     * posts it on the EventBus
     *<p>
     * @param lobbyName Name of the lobby the players are in
     * @param message   the message to be send to the users
     * @see de.uol.swp.common.message.ServerMessage
     * @author Marco Grawunder
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
     * Else Method creates NotEnoughPlayersResponse and sends it to a specific user that sent the initial request.
     *
     * @param startGameRequest the StartGameRequest found on the EventBus
     * @see de.uol.swp.common.lobby.request.StartGameRequest
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    @Subscribe
    public void onStartGameRequest(StartGameRequest startGameRequest) {
        Optional<Lobby> lobby = lobbyService.getLobby(startGameRequest.getName());
        if (lobby.get().getUsers().size() > 1) {
            sendToAllInLobby(startGameRequest.getName(),new StartGameMessage(startGameRequest.getName(), startGameRequest.getUser()));
            LOG.debug("send StartGameMessage to all users");
            int seconds = 60;
            Timer timer = new Timer();
            class RemindTask extends TimerTask {
                public void run() {
                    if (gameManagement.getGame(lobby.get().getName()).isEmpty()) {
                        startGame(lobby);
                        if (gameManagement.getGame(lobby.get().getName()).isEmpty()) {
                            throw new LobbyManagementException("Not enough players ready to start the game");
                        }
                    }
                    timer.cancel();
                    lobby.get().setPlayersReadyToNull();
                }
            }
            timer.schedule(new RemindTask(), seconds*1000);
        } else {
            sendToSpecificUser(startGameRequest.getMessageContext().get(), new NotEnoughPlayersResponse());
        }
    }

    public void startGame(Optional<Lobby> lobby) {
        if (gameManagement.getGame(lobby.get().getName()).isEmpty()) {
            if (lobby.get().getPlayersReady().size() == lobby.get().getUsers().size()) {
                gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner());
                sendToAllInLobby(lobby.get().getName(), new GameCreatedMessage(lobby.get().getName()));
            }
        }
    }

    /**
     * Handles PlayerReadyRequest found on the EventBus
     *<p>
     * If a PlayerReadyRequest is detected on the EventBus, this method is called.
     * Method adds ready players to the
     * @param playerReadyRequest the PlayerReadyRequest found on the EventBus
     * @see de.uol.swp.common.game.request.PlayerReadyRequest
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    @Subscribe
    public void onPlayerReadyRequest(PlayerReadyRequest playerReadyRequest) {
        Optional<Lobby> lobby = lobbyService.getLobby(playerReadyRequest.getName());
        lobby.get().joinPlayerReady(playerReadyRequest.getUser());
        startGame(lobby);
    }

}
