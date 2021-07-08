package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.common.game.request.PlayerReadyRequest;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.JoinOnGoingGameRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.lobby.request.StartGameRequest;
import de.uol.swp.common.user.UserDTO;


/**
 * Classes that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService {

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);
    private final EventBus eventBus;


    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @author Marco Grawunder
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Posts a request to create a password protected lobby on the EventBus.
     * Returns a boolean. If the Request is posted on the eventbus it returns true.
     * Is the String name blank or empty it returns false.
     * If the name is null, the exception is caught and posted on the bus.
     * Therefore we return also false, cause no lobby was created.
     *
     * @param name     Name chosen for the new lobby
     * @param user     User who wants to create the new lobby
     * @param password password for the lobby
     * @author René Meyer
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2021-06-05
     */
    public void createNewProtectedLobby(String name, UserDTO user, String password) {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(name, user, password);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to create a lobby on the EventBus.
     * Returns a boolean. If the Request is posted on the eventbus it returns true.
     * Is the String name blank or empty it returns false.
     * If the name is null, the exception is caught and posted on the bus.
     * Therefore we return also false, cause no lobby was created.
     *
     * @param name Name chosen for the new lobby
     * @param user User who wants to create the new lobby
     * @author Marco Grawunder
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String name, UserDTO user) {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(name, user);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to join a specified lobby on the EventBus
     *
     * @param name Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     * @author Marco Grawunder
     * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    public void joinLobby(String name, UserDTO user) {
        LobbyJoinUserRequest joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    /**
     * Posts a request to join a specified password protected lobby on the EventBus
     *
     * @param name Name of the protected lobby the user wants to join
     * @param user User who wants to join the lobby
     * @author René Meyer
     * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
     * @since 2021-06-05
     */
    public void joinProtectedLobby(String name, UserDTO user, String password) {
        LobbyJoinUserRequest joinUserRequest = new LobbyJoinUserRequest(name, user, password);
        eventBus.post(joinUserRequest);
    }

    /**
     * Posts a request to leave a specified lobby on the EventBus
     *
     * @param name Name of the lobby the user wants to leave
     * @param user User who wants to leave the lobby
     * @author ?
     * @see de.uol.swp.common.lobby.request.LobbyLeaveUserRequest
     * @since ?
     */

    public void leaveLobby(String name, UserDTO user) {
        LobbyLeaveUserRequest leaveUserRequest = new LobbyLeaveUserRequest(name, user);
        eventBus.post(leaveUserRequest);
    }

    /**
     * Creates a new RetrieveAllThisLobbyUsersRequest and puts it on the Eventbus
     * <p>
     *
     * @param lobbyName Name of the lobby of which the User list was requested
     * @author Marc Hermes, Ricardo Mook
     * @see de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest
     * @since 2020-12-02
     */
    public void retrieveAllThisLobbyUsers(String lobbyName) {
        RetrieveAllThisLobbyUsersRequest lobbyUsersRequest = new RetrieveAllThisLobbyUsersRequest(lobbyName);
        eventBus.post(lobbyUsersRequest);
    }

    /**
     * Posts a request to get a list of all existing lobbies on the EventBus
     *
     * @author Carsten Dekker and Marius Birk
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-04-12
     */
    public void retrieveAllLobbies() {
        RetrieveAllLobbiesRequest cmd = new RetrieveAllLobbiesRequest();
        eventBus.post(cmd);
    }

    /**
     * Posts a PlayerReadyRequest on the EventBus
     *
     * @param name Name of the lobby
     * @param user User who sends PlayerReadyRequest
     * @author Kirsitn
     * @see de.uol.swp.common.game.request.PlayerReadyRequest
     * @since 2021-02-04
     */
    public void sendPlayerReadyRequest(String name, UserDTO user, boolean ready) {
        PlayerReadyRequest playerReadyRequest = new PlayerReadyRequest(name, user, ready);
        eventBus.post(playerReadyRequest);
    }

    /**
     * Posts a request to start a game on the EventBus.
     *
     * @param name Name of the lobby of which User wants to start the game.
     * @param user User who wants to start the game.
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.lobby.request.StartGameRequest
     * @since 2021-01-24
     */
    public void startGame(String name, UserDTO user, String gameFieldVariant, int minimumAmountOfPlayers) {
        StartGameRequest startGameRequest = new StartGameRequest(name, user, gameFieldVariant, minimumAmountOfPlayers);
        eventBus.post(startGameRequest);
        LOG.debug("StartGameRequest posted on Eventbus");
    }

    /**
     * Posts a request to join an ongoing game on the EventBus
     *
     * @param name the name of the game to join
     * @param user the user who wants to join
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public void joinGame(String name, UserDTO user) {
        JoinOnGoingGameRequest joinOnGoingGameRequest = new JoinOnGoingGameRequest(name, user);
        eventBus.post(joinOnGoingGameRequest);
        LOG.debug("JoinOnGoingGameRequest posted on Eventbus");
    }
}
