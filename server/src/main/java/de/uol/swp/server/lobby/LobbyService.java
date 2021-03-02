package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.lobby.response.AlreadyJoinedThisLobbyResponse;
import de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.response.lobby.*;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles the lobby requests send by the users
 * <p>
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class LobbyService extends AbstractService {

    private final LobbyManagement lobbyManagement;
    private final AuthenticationService authenticationService;
    private static final Logger LOG = LogManager.getLogger(LobbyService.class);

    /**
     * Constructor
     * <p>
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
     * @see de.uol.swp.server.lobby.LobbyManagement
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @see LobbyCreatedSuccessfulResponse
     * @see de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest createLobbyRequest) {
        if (lobbyManagement.getLobby(createLobbyRequest.getName()).isEmpty()) {
            try{
                lobbyManagement.createLobby(createLobbyRequest.getName(), createLobbyRequest.getUser());
                sendToAll(new LobbyCreatedMessage(createLobbyRequest.getName(), createLobbyRequest.getUser()));
            }catch(IllegalArgumentException e){
                LOG.debug(e);
            }
            if (createLobbyRequest.getMessageContext().isPresent()) {
                sendToSpecificUser(createLobbyRequest.getMessageContext().get(), new LobbyCreatedSuccessfulResponse(createLobbyRequest.getName(), createLobbyRequest.getUser()));
            }
        } else {
            if (createLobbyRequest.getMessageContext().isPresent()) {
                sendToSpecificUser(createLobbyRequest.getMessageContext().get(), new LobbyAlreadyExistsResponse());
            }
        }
    }

    /**
     * Handles LobbyJoinUserRequests found on the EventBus
     * <p>
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a Lobby stored in the LobbyManagement and sends a UserJoinedLobbyMessage
     * to every user in the lobby.
     * If a lobby already has 4 users, this method will return a LobbyFullResponse to the user
     * who requested to join the lobby
     * If a lobby is not present, this method will return a JoinDeletedLobbyResponse to the user.
     * @param lobbyJoinUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @see de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse
     * @see de.uol.swp.common.user.response.lobby.JoinDeletedLobbyResponse
     * @author Marco Grawunder
     * @since 2019-10-08
     * <p>
     * Enhanced by Carsten Dekker
     * <p>
     * If a user already joined the lobby, he gets an AlreadyJoinedThisLobbyResponse.
     * @see de.uol.swp.common.lobby.response.AlreadyJoinedThisLobbyResponse
     * @since 2021-01-22
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyJoinUserRequest.getName());
        if (!lobby.isPresent()) {
            sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new JoinDeletedLobbyResponse(lobbyJoinUserRequest.getName()));
        }
        if (lobby.get().getUsers().size() < 4 && !lobby.get().getUsers().contains(lobbyJoinUserRequest.getUser()) && lobbyJoinUserRequest.getMessageContext().isPresent()) {
                lobby.get().joinUser(lobbyJoinUserRequest.getUser());
                sendToAllInLobby(lobbyJoinUserRequest.getName(), new UserJoinedLobbyMessage(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser()));
                sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new LobbyJoinedSuccessfulResponse(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser()));
                sendToAll(new LobbySizeChangedMessage(lobbyJoinUserRequest.getName()));
        } else {
            if (lobbyJoinUserRequest.getMessageContext().isPresent() && lobby.get().getUsers().size() == 4) {
                sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new LobbyFullResponse(lobbyJoinUserRequest.getName()));
            } else {
                sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new AlreadyJoinedThisLobbyResponse(lobbyJoinUserRequest.getName()));
            }
        }
    }

    /**
     * Handles LobbyLeaveUserRequests found on the EventBus
     * <p>
     * If a LobbyLeaveUserRequest is detected on the EventBus, this method is called.
     * It removes a user from a Lobby stored in the LobbyManagement and sends a
     * UserLeftLobbyMessage to every user in the lobby.
     *
     * If a lobby was deleted, this methode will return a JoinDeletedLobbyResponse to the user who requested to join the lobby
     *
     * @param lobbyLeaveUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @see LobbyLeftSuccessfulResponse
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Subscribe
    public void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyLeaveUserRequest.getName());
        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() == 1) {
                if (lobbyLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = lobbyLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new LobbyLeftSuccessfulResponse(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
                    lobbyManagement.dropLobby(lobbyLeaveUserRequest.getName());
                    sendToAll(new LobbyDroppedMessage(lobbyLeaveUserRequest.getName()));
                }
            } else if (lobby.get().getUsers() == null) {
                lobbyManagement.dropLobby(lobbyLeaveUserRequest.getName());
                sendToAll(new LobbyDroppedMessage(lobbyLeaveUserRequest.getName()));

            } else {
                if (lobbyLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = lobbyLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new LobbyLeftSuccessfulResponse(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
                }
                lobby.get().leaveUser(lobbyLeaveUserRequest.getUser());
                sendToAll(new LobbySizeChangedMessage(lobbyLeaveUserRequest.getName()));
                sendToAllInLobby(lobbyLeaveUserRequest.getName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
            }
        } else {
            throw new LobbyManagementException("Lobby unknown!");
        }
    }

    /**
     * Handles RetrieveAllThisLobbyUsersRequests found on the EventBus
     * <p>
     * If a RetrieveAllThisLobbyUsersRequests is detected on the EventBus, this method is called.
     * It prepares the sending of a AllThisLobbyUsersResponse for a specific user that sent the initial request.
     *
     * @param retrieveAllThisLobbyUsersRequest The RetrieveAllThisLobbyUsersRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-12-02
     */
    @Subscribe
    public void onRetrieveAllThisLobbyUsersRequest(RetrieveAllThisLobbyUsersRequest retrieveAllThisLobbyUsersRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(retrieveAllThisLobbyUsersRequest.getName());
        if (lobby.isPresent()) {
            List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
            if (retrieveAllThisLobbyUsersRequest.getMessageContext().isPresent()) {
                Optional<MessageContext> ctx = retrieveAllThisLobbyUsersRequest.getMessageContext();
                sendToSpecificUser(ctx.get(), new AllThisLobbyUsersResponse(lobbyUsers, retrieveAllThisLobbyUsersRequest.getName()));
            }
        }
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
     *<p>
     * @param message the message to be send to the users
     * @param ctx     the context of the message, here the session of the owner of the lobby
     * @author Marc Hermes
     * @see de.uol.swp.common.message.ResponseMessage
     * @see de.uol.swp.common.message.MessageContext
     * @since 2020-11-25
     */
    public void sendToSpecificUser(MessageContext ctx, ResponseMessage message) {
        ctx.writeAndFlush(message);
    }

    /**
     * This method retrieves the RetrieveAllLobbiesRequest and creates a AllCreatedLobbiesResponse with all
     * lobbies in the lobbyManagement.
     *
     * @param msg RetrieveAllLobbiesRequest
     * @author Carsten Dekker and Marius Birk
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

    /**
     * Handles LogoutRequests found on the EventBus
     *
     * If a LogoutRequest is detected on the EventBus, this method is called. It
     * gets all lobbies from the LobbyManagement and loops through them.
     * If the user is part of a lobby, he gets removed from it.
     * If he is the last user in the lobby, the lobby gets dropped.
     * Finally we log how many lobbies the user left.
     *
     * @param msg the LogoutRequest
     * @see de.uol.swp.common.user.request.LogoutRequest
     * @see de.uol.swp.common.lobby.request.LobbyLeaveUserRequest
     * @author René Meyer, Sergej Tulnev
     * @since 2021-01-22
     */
    @Subscribe
    public void onLogoutRequest(LogoutRequest msg) {
        if (msg.getSession().isPresent()) {
            Session session = msg.getSession().get();
            var userToLogOut = session.getUser();
            // Could be already logged out
            if (userToLogOut != null) {
                var lobbies = lobbyManagement.getAllLobbies();
                // Create lobbiesCopy because of ConcurrentModificationException,
                // so it doesn't matter when in the meantime the lobbies Object gets modified, while we still loop through it
                var lobbiesCopy = lobbies.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                // Loop lobbies
                Iterator<Map.Entry<String, Lobby>> it = lobbiesCopy.entrySet().iterator();
                var i = 0;
                while (it.hasNext()) {
                    Map.Entry<String, Lobby> entry = it.next();
                    Lobby lobby = entry.getValue();
                    if(lobby.getUsers().contains(userToLogOut)){
                        // leave every lobby the user is part of
                        var lobbyLeaveRequest = new LobbyLeaveUserRequest(lobby.getName(), (UserDTO) userToLogOut);
                        if(msg.getMessageContext().isPresent()){
                            lobbyLeaveRequest.setMessageContext(msg.getMessageContext().get());
                            this.onLobbyLeaveUserRequest(lobbyLeaveRequest);
                        }
                    }
                    i++;
                }
                var lobbyString = i>1? " lobbies":" lobby";
                LOG.debug("Left " + i + lobbyString+" for User: " + userToLogOut.getUsername());
            }
        }
    }

    public Optional<Lobby> getLobby(String lobbyName) {
        return lobbyManagement.getLobby(lobbyName);
    }

}
