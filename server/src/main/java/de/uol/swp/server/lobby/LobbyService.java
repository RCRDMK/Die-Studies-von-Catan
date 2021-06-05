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
import de.uol.swp.common.user.User;
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
 *
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
     *
     * @param lobbyManagement       The management class for creating, storing and deleting lobbies
     * @param authenticationService the user management
     * @param eventBus              the server-wide EventBus
     * @author Marco Grawunder
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
     * If a CreateLobbyRequest is detected on the EventBus, this method is called. It creates a new Lobby via the
     * LobbyManagement using the parameters from the request and sends a LobbyCreatedMessage to every connected user
     * <p>
     * It also creates a LobbyCreatedSuccessfulResponse and sends it to the owner of the Lobby, by looking at the
     * context of the createLobbyRequest
     * <p>
     * Method was enhanced by Marc Hermes, 2020-11-25
     * <p>
     * Enhanced the Method with a query, so that if a lobby with the same name, as a lobby that already exists, can't be
     * created. Also there is a LobbyAlreadyExistsResponse sent to the user, that wanted to create the lobby.
     * <p>
     * Method enhanced by Marius Birk and Carsten Dekker, 2020-12-02
     * <p>
     * Method enhanced by René Meyer for password protected lobbies, 2021-06-05
     *
     * @param createLobbyRequest The CreateLobbyRequest found on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.server.lobby.LobbyManagement
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @see LobbyCreatedSuccessfulResponse
     * @see de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse
     * @since 2019-10-08
     */
    @Subscribe
    public void onCreateLobbyRequest(CreateLobbyRequest createLobbyRequest) {
        if (lobbyManagement.getLobby(createLobbyRequest.getName()).isEmpty()) {
            if (createLobbyRequest.getPassword() == null || createLobbyRequest.getPassword().isEmpty()) {
                lobbyManagement.createLobby(createLobbyRequest.getName(), createLobbyRequest.getUser());
            } else {
                lobbyManagement.createProtectedLobby(createLobbyRequest.getName(), createLobbyRequest.getUser(), createLobbyRequest.getPassword());
                LOG.debug("Created password protected Lobby: " + createLobbyRequest.getName() + "with password: " + createLobbyRequest.getPassword());
            }
            sendToAll(new LobbyCreatedMessage(createLobbyRequest.getName(), createLobbyRequest.getUser()));
            if (createLobbyRequest.getMessageContext().isPresent()) {
                sendToSpecificUser(createLobbyRequest.getMessageContext().get(), new LobbyCreatedSuccessfulResponse(createLobbyRequest.getName(), createLobbyRequest.getUser()));
                LOG.debug("Created Lobby: " + createLobbyRequest.getName() + "without password.");
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
     *
     * @param lobbyJoinUserRequest The LobbyJoinUserRequest found on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @see de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse
     * @see de.uol.swp.common.user.response.lobby.JoinDeletedLobbyResponse
     * @see de.uol.swp.common.lobby.response.AlreadyJoinedThisLobbyResponse
     * @since 2019-10-08
     * <p>
     * Enhanced by Carsten Dekker
     * enhanced by Marc Hermes 2021-03-25
     * enhanced by René Meyer 2021-06-05 for password protected lobby support
     * <p>
     * If a user already joined the lobby, he gets an AlreadyJoinedThisLobbyResponse.
     * @since 2021-01-22
     */
    @Subscribe
    public void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyJoinUserRequest.getName());
        if (!lobby.isPresent()) {
            sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new JoinDeletedLobbyResponse(lobbyJoinUserRequest.getName()));
        }
        if (lobby.get().getUsers().size() < 4 && !lobby.get().getUsers().contains(lobbyJoinUserRequest.getUser()) && lobbyJoinUserRequest.getMessageContext().isPresent() && (lobbyJoinUserRequest.getPassword() == null || lobbyJoinUserRequest.getPassword().isEmpty())) {
            lobby.get().joinUser(lobbyJoinUserRequest.getUser());
            ArrayList<UserDTO> usersInLobby = new ArrayList<>();
            for (User user : lobby.get().getUsers()) usersInLobby.add(UserDTO.createWithoutPassword(user));
            sendToAllInLobby(lobbyJoinUserRequest.getName(), new UserJoinedLobbyMessage(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser(), usersInLobby));
            sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new LobbyJoinedSuccessfulResponse(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser()));
            sendToAll(new LobbySizeChangedMessage(lobbyJoinUserRequest.getName()));
        }
        // in case password in lobbyJoinUserRequest is present
        else if (lobby.get().getUsers().size() < 4 && !lobby.get().getUsers().contains(lobbyJoinUserRequest.getUser()) && lobbyJoinUserRequest.getMessageContext().isPresent() && (lobbyJoinUserRequest.getPassword() != null || !lobbyJoinUserRequest.getPassword().isEmpty())) {
            // if password is correct
            if (lobby.get().getPasswordHash() == lobbyJoinUserRequest.getPassword().hashCode()) {
                lobby.get().joinUser(lobbyJoinUserRequest.getUser());
                ArrayList<UserDTO> usersInLobby = new ArrayList<>();
                for (User user : lobby.get().getUsers()) usersInLobby.add(UserDTO.createWithoutPassword(user));
                sendToAllInLobby(lobbyJoinUserRequest.getName(), new UserJoinedLobbyMessage(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser(), usersInLobby));
                sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new LobbyJoinedSuccessfulResponse(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser()));
                sendToAll(new LobbySizeChangedMessage(lobbyJoinUserRequest.getName()));
            } else {
                sendToSpecificUser(lobbyJoinUserRequest.getMessageContext().get(), new WrongLobbyPasswordResponse(lobbyJoinUserRequest.getName()));
            }

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
     * <p>
     * If a lobby was deleted, this methode will return a JoinDeletedLobbyResponse to the user who requested to join the lobby
     * <p>
     * enhanced by Marc Hermes 2021-03-25
     *
     * @param lobbyLeaveUserRequest The LobbyJoinUserRequest found on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @see LobbyLeftSuccessfulResponse
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
            } else {
                if (lobbyLeaveUserRequest.getMessageContext().isPresent()) {
                    Optional<MessageContext> ctx = lobbyLeaveUserRequest.getMessageContext();
                    sendToSpecificUser(ctx.get(), new LobbyLeftSuccessfulResponse(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
                }
                lobby.get().leaveUser(lobbyLeaveUserRequest.getUser());
                sendToAll(new LobbySizeChangedMessage(lobbyLeaveUserRequest.getName()));
                ArrayList<UserDTO> remainingUsers = new ArrayList<>();
                for (User user : lobby.get().getUsers()) remainingUsers.add(UserDTO.createWithoutPassword(user));
                sendToAllInLobby(lobbyLeaveUserRequest.getName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser(), remainingUsers, lobby.get().getOwner().getUsername()));
            }
        } else {
            throw new LobbyManagementException("Lobby unknown!");
        }
    }

    /**
     * Handles RetrieveAllThisLobbyUsersRequests found on the EventBus
     * <p>
     * If a RetrieveAllThisLobbyUsersRequests is detected on the EventBus, this method is called. It prepares the
     * sending of a AllThisLobbyUsersResponse for a specific user that sent the initial request.
     *
     * @param retrieveAllThisLobbyUsersRequest The RetrieveAllThisLobbyUsersRequest found on the EventBus
     * @author Marc Hermes, Ricardo Mook
     * @see de.uol.swp.common.lobby.Lobby
     * @since 2020-12-02
     */
    @Subscribe
    public void onRetrieveAllThisLobbyUsersRequest(RetrieveAllThisLobbyUsersRequest retrieveAllThisLobbyUsersRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(retrieveAllThisLobbyUsersRequest.getName());
        if (lobby.isPresent()) {
            List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
            if (retrieveAllThisLobbyUsersRequest.getMessageContext().isPresent()) {
                Optional<MessageContext> ctx = retrieveAllThisLobbyUsersRequest.getMessageContext();
                sendToSpecificUser(ctx.get(), new AllThisLobbyUsersResponse(lobbyUsers, retrieveAllThisLobbyUsersRequest.getName(), lobby.get().getOwner().getUsername()));
            }
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
     * <p>
     *
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
     * This method retrieves the RetrieveAllLobbiesRequest and creates a AllCreatedLobbiesResponse with all lobbies in
     * the lobbyManagement.
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
     * <p>
     * If a LogoutRequest is detected on the EventBus, this method is called. It
     * gets all lobbies from the LobbyManagement and loops through them.
     * If the user is part of a lobby, he gets removed from it.
     * If he is the last user in the lobby, the lobby gets dropped.
     * Finally we log how many lobbies the user left.
     *
     * @param msg the LogoutRequest
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.common.user.request.LogoutRequest
     * @see de.uol.swp.common.lobby.request.LobbyLeaveUserRequest
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
                    if (lobby.getUsers().contains(userToLogOut)) {
                        // leave every lobby the user is part of
                        var lobbyLeaveRequest = new LobbyLeaveUserRequest(lobby.getName(), (UserDTO) userToLogOut);
                        if (msg.getMessageContext().isPresent()) {
                            lobbyLeaveRequest.setMessageContext(msg.getMessageContext().get());
                            this.onLobbyLeaveUserRequest(lobbyLeaveRequest);
                        }
                    }
                    i++;
                }
                var lobbyString = i > 1 ? " lobbies" : " lobby";
                LOG.debug("Left " + i + lobbyString + " for User: " + userToLogOut.getUsername());
            }
        }
    }

    public Optional<Lobby> getLobby(String lobbyName) {
        return lobbyManagement.getLobby(lobbyName);
    }

}
