package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.message.ServerInternalMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.*;

/**
 * Mapping authentication event bus calls to user management calls
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.AbstractService
 * @since 2019-08-30
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class AuthenticationService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(AuthenticationService.class);

    /**
     * The list of current logged in users
     */
    final private Map<Session, User> userSessions = new HashMap<>();

    private final UserManagement userManagement;

    /**
     * Constructor
     *
     * @param bus            The EventBus used throughout the entire server
     * @param userManagement object of the UserManagement to use
     * @author Marco Grawunder
     * @see de.uol.swp.server.usermanagement.UserManagement
     * @since 2019-08-30
     */
    @Inject
    public AuthenticationService(EventBus bus, UserManagement userManagement) throws SQLException {
        super(bus);
        this.userManagement = userManagement;
        this.userManagement.buildConnection();
    }

    /**
     * Searches the Session for a given user
     *
     * @param user user whose Session is to be searched
     * @return either empty Optional or Optional containing the Session
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.user.User
     * @since 2019-09-04
     */
    public Optional<Session> getSession(User user) {
        Optional<Map.Entry<Session, User>> entry = userSessions.entrySet().stream().filter(e -> e.getValue().equals(user)).findFirst();
        return entry.map(Map.Entry::getKey);
    }

    /**
     * Searches the Sessions for a Set of given users
     *
     * @param users Set of users whose Sessions are to be searched
     * @return List containing the Sessions that where found
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.user.User
     * @since 2019-10-08
     */
    public List<Session> getSessions(Set<User> users) {
        List<Session> sessions = new ArrayList<>();
        users.forEach(u -> {
            Optional<Session> session = getSession(u);
            session.ifPresent(sessions::add);
        });
        return sessions;
    }

    /**
     * Handles LoginRequests found on the EventBus
     * <p>
     * If a LoginRequest is detected on the EventBus, this method is called. It
     * tries to login a user via the UserManagement. If this succeeds the user and
     * his Session are stored in the userSessions Map and a ClientAuthorizedMessage
     * is posted on the EventBus otherwise a ServerExceptionMessage gets posted
     * there.
     * If a user is already logged in, a ServerExceptionMessage is posted on the bus. (René, Sergej)
     *
     * @param msg the LoginRequest
     * @author René, Sergej
     * @see de.uol.swp.common.user.request.LoginRequest
     * @see de.uol.swp.server.message.ClientAuthorizedMessage
     * @see de.uol.swp.server.message.ServerExceptionMessage
     * @since 2021-01-03
     */
    @Subscribe
    public void onLoginRequest(LoginRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new auth message with " + msg.getUsername() + " " + msg.getPassword());
        }
        ServerInternalMessage returnMessage;
        try {
            // Beim UserDTO Objekt muss nur der Username übergeben werden, da die equals() Methode nur checkt ob der Username übereinstimmt
            var loggedInUser = new UserDTO(msg.getUsername(), "", "");
            if (!userManagement.isLoggedIn(loggedInUser)) {
                User newUser = userManagement.login(msg.getUsername(), msg.getPassword());
                returnMessage = new ClientAuthorizedMessage(newUser);
                Session newSession = UUIDSession.create(newUser);
                userSessions.put(newSession, newUser);
                returnMessage.setSession(newSession);
            } else {
                LOG.debug("User " + msg.getUsername() + " already logged in!");
                returnMessage = new ServerExceptionMessage(new LoginException("User " + msg.getUsername() + " already logged in!"));
            }
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new ServerExceptionMessage(new LoginException("Cannot auth user " + msg.getUsername()));
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
        post(returnMessage);
    }

    /**
     * Handles LogoutRequests found on the EventBus
     * <p>
     * If a LogoutRequest is detected on the EventBus, this method is called. It
     * tries to logout a user via the UserManagement. If this succeeds the user and
     * his Session are removed from the userSessions Map and a UserLoggedOutMessage
     * is posted on the EventBus.
     *
     * @param msg the LogoutRequest
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.request.LogoutRequest
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-30
     */
    @Subscribe
    public void onLogoutRequest(LogoutRequest msg) {
        if (msg.getSession().isPresent()) {
            Session session = msg.getSession().get();
            User userToLogOut = userSessions.get(session);

            // Could be already logged out
            if (userToLogOut != null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Logging out user " + userToLogOut.getUsername());
                }

                userManagement.logout(userToLogOut);
                userSessions.remove(session);

                ServerMessage returnMessage = new UserLoggedOutMessage(userToLogOut.getUsername());
                post(returnMessage);

            }
        }
    }

    /**
     * Handles RetrieveAllOnlineUsersRequests found on the EventBus
     * <p>
     * If a RetrieveAllOnlineUsersRequest is detected on the EventBus, this method
     * is called. It posts a AllOnlineUsersResponse containing user objects for
     * every logged in user on the EventBus.
     *
     * @param msg RetrieveAllOnlineUsersRequest found on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-30
     */
    @Subscribe
    public void onRetrieveAllOnlineUsersRequest(RetrieveAllOnlineUsersRequest msg) {
        AllOnlineUsersResponse response = new AllOnlineUsersResponse(userSessions.values());
        response.initWithMessage(msg);
        post(response);
    }
}
