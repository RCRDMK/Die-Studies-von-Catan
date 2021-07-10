package de.uol.swp.server.usermanagement;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.PingRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.PingResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.message.ServerInternalMessage;

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
    private final Timer timer = new Timer();
    private final ActiveUserList activeUserList = new ActiveUserList();

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
    public AuthenticationService(EventBus bus, UserManagement userManagement) {
        super(bus);
        this.userManagement = userManagement;
        startTimerForActiveUserList();
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
        Optional<Map.Entry<Session, User>> entry = userSessions.entrySet().stream()
                .filter(e -> e.getValue().equals(user)).findFirst();
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
     * @author René, Sergej, Philip, Marc
     * @see de.uol.swp.common.user.request.LoginRequest
     * @see de.uol.swp.server.message.ClientAuthorizedMessage
     * @see de.uol.swp.server.message.ServerExceptionMessage
     * @since 2021-03-14
     */
    @Subscribe
    public void onLoginRequest(LoginRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new auth message with " + msg.getUsername());
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
                activeUserList.addActiveUser(newUser);
                returnMessage.setSession(newSession);
            } else {
                LOG.debug("User " + msg.getUsername() + " already logged in!");
                returnMessage = new ServerExceptionMessage(
                        new LoginException("User " + msg.getUsername() + " already logged in!"));
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
     * @author Marco, Philip, Marc
     * @see de.uol.swp.common.user.request.LogoutRequest
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2021-03-14
     */
    @Subscribe
    public void onLogoutRequest(LogoutRequest msg) {
        if (msg.getSession().isPresent()) {
            Session session = msg.getSession().get();
            User userToLogout = userSessions.get(session);

            // Could be already logged out
            if (userToLogout != null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Logging out user " + userToLogout.getUsername());
                }

                userManagement.logout(userToLogout);
                activeUserList.removeActiveUser(userToLogout);
                userSessions.remove(session);

                ServerMessage returnMessage = new UserLoggedOutMessage(userToLogout.getUsername());
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

    /**
     * Handles PingRequests found on the EventBus
     * <p>
     * If a PingRequest is detected on the EventBus, this method is called.
     * It sends a PingResponse back to the User.
     * It tells the ActiveUserList the last send Ping time from this User.
     *
     * @param pingRequest The PingRequest found on the EventBus
     * @author Philip Nitsche
     * @see de.uol.swp.common.user.request.PingRequest
     * @since 2021-01-22
     */
    @Subscribe
    private void onPingRequest(PingRequest pingRequest) {
        activeUserList.updateActiveUser(pingRequest.getUser(), pingRequest.getTime());
        ResponseMessage returnMessage;
        returnMessage = new PingResponse(pingRequest.getUser().getUsername(), pingRequest.getTime());
        if (pingRequest.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(pingRequest.getMessageContext().get());
            post(returnMessage);
        }
    }

    /**
     * Starts a Ping Timer
     * <p>
     * Starts a Ping Timer which checks every 60 seconds if the Users
     * are still Online with a start delay of 30 seconds.
     *
     * @author Philip, Marc
     * @since 2021-01-22
     */
    public void startTimerForActiveUserList() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<User> userToLogout = activeUserList.checkActiveUser();
                if (userToLogout.size() >= 1) {
                    for (int i = 0; i < userToLogout.size(); i++) {
                        Optional<Session> session = getSession(userToLogout.get(i));
                        if (session.isPresent()) {
                            LogoutRequest logoutRequest = new LogoutRequest();
                            logoutRequest.setSession(session.get());
                            onLogoutRequest(logoutRequest);
                        }
                    }
                }
            }
        }, 30000, 60000);
    }

}
