package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles the lobby requests send by the users
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class LobbyService extends AbstractService {

    private final LobbyManagement lobbyManagement;
    private final AuthenticationService authenticationService;

    final private Map<Session, User> userSessions = new HashMap<>();

    /**
     * Constructor
     *
     * @param lobbyManagement       The management class for creating, storing and deleting
     *                              lobbies
     * @param authenticationService the user management
     * @param eventBus              the server-wide EventBus
     * @since 2019-10-08
     */
    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService, EventBus eventBus) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;

    }


    /**
     * Handles CreateLobbyRequests found on the EventBus
     * <p>
     * If a CreateLobbyRequest is detected on the EventBus, this method is called.
     * It creates a new Lobby via the LobbyManagement using the parameters from the
     * request and sends a LobbyCreatedMessage to every connected user
     * <p>
     * It also creates a LobbyCreatedSuccessfulResponse and sends it to the owner of the Lobby, by looking at the context
     * of the createLobbyRequest
     * <p>
     * Method was enhanced by Marc Hermes, 2020-11-25
     * <p>
     * Enhanced the Method with a query, so that if a lobby with the same name, as a lobby that already exists, can't be created.
     * Also there is a LobbyAlreadyExistsResponse sent to the user, that wanted to create the lobby.
     * <p>
     * Method enhanced by Marius Birk and Carsten Dekker, 2020-12-02
     *
     * @param createLobbyRequest The CreateLobbyRequest found on the EventBus
     * @see de.uol.swp.server.lobby.LobbyManagement#createLobby(String, User)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @see de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse
     * @see de.uol.swp.common.lobby.message.LobbyAlreadyExistsMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest createLobbyRequest) {
        if (lobbyManagement.getLobby(createLobbyRequest.getName()).isEmpty()) {
            lobbyManagement.createLobby(createLobbyRequest.getName(), createLobbyRequest.getUser());
            sendToAll(new LobbyCreatedMessage(createLobbyRequest.getName(), createLobbyRequest.getUser()));
            if (createLobbyRequest.getMessageContext().isPresent()) {
                Optional<MessageContext> ctx = createLobbyRequest.getMessageContext();
                sendToOwner(ctx.get(), new LobbyCreatedSuccessfulResponse(createLobbyRequest.getName(), createLobbyRequest.getUser()));
            }
        } else {
            Optional<MessageContext> ctx = createLobbyRequest.getMessageContext();
            if (createLobbyRequest.getMessageContext().isPresent()) {
                sendToOwner(ctx.get(), new LobbyAlreadyExistsMessage());
            }
        }
    }

    /**
     * Handles LobbyJoinUserRequests found on the EventBus
     * <p>
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a Lobby stored in the LobbyManagement and sends a UserJoinedLobbyMessage
     * to every user in the lobby.
     *
     * @param lobbyJoinUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyJoinUserRequest.getName());

        if (lobby.isPresent()) {
            lobby.get().joinUser(lobbyJoinUserRequest.getUser());
            sendToAllInLobby(lobbyJoinUserRequest.getName(), new UserJoinedLobbyMessage(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser()));
        } else {
            throw new LobbyManagementException("Lobby unknown!");
        }
    }

    /**
     * Handles LobbyLeaveUserRequests found on the EventBus
     * <p>
     * If a LobbyLeaveUserRequest is detected on the EventBus, this method is called.
     * It removes a user from a Lobby stored in the LobbyManagement and sends a
     * UserLeftLobbyMessage to every user in the lobby.
     *
     * @param lobbyLeaveUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyLeaveUserRequest.getName());
        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() == 1) {
                sendToAllInLobby(lobbyLeaveUserRequest.getName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
                lobbyManagement.dropLobby(lobbyLeaveUserRequest.getName());
            } else if (lobby.get().getUsers() == null) {
                lobbyManagement.dropLobby(lobbyLeaveUserRequest.getName());
            } else {
                lobby.get().leaveUser(lobbyLeaveUserRequest.getUser());
                sendToAllInLobby(lobbyLeaveUserRequest.getName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
            }
        } else {
            throw new LobbyManagementException("Lobby unknown!");
        }
    }

    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and
     * posts it on the EventBus
     *
     * @param lobbyName Name of the lobby the players are in
     * @param message   the message to be send to the users
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        } else {
            throw new LobbyManagementException("Lobby unknown!");
        }
    }

    /**
     * Prepares a given ResponseMessage to be send to the owner of lobby and
     * posts it on the EventBus
     *
     * @param message the message to be send to the users
     * @param ctx     the context of the message, here the session of the owner of the lobby
     * @author Marc Hermes
     * @see de.uol.swp.common.message.ResponseMessage
     * @see de.uol.swp.common.message.MessageContext
     * @since 2020-11-25
     */
    public void sendToOwner(MessageContext ctx, ResponseMessage message) {
        ctx.writeAndFlush(message);
    }

    /**
     * This method retrieves the RetrieveAllLobbiesRequest and creates a AllCreatedLobbiesResponse with all
     * lobbies in the lobbyManagement.
     *
     * @author Carsten Dekker and Marius Birk
     * @param msg RetrieveAllLobbiesRequest
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @see de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse
     * @since 2020-04-12
     */
    @Subscribe
    public void onRetrieveAllLobbiesRequest(RetrieveAllLobbiesRequest msg) {
        AllCreatedLobbiesResponse response = new AllCreatedLobbiesResponse(this.lobbyManagement.getAllLobbies().values());
        response.initWithMessage(msg);
        post(response);
    }
}
