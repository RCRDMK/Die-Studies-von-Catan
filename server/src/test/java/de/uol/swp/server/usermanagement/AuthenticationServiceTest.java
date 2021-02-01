package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class AuthenticationServiceTest {

    private final CountDownLatch lock = new CountDownLatch(1);

    final User user = new UserDTO("name", "password", "email@test.de");
    final User user2 = new UserDTO("name2", "password2", "email@test.de2");
    final User user3 = new UserDTO("name3", "password3", "email@test.de3");


    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement();
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);
    private Object event;

    AuthenticationServiceTest() throws SQLException {
    }

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    @Test
    void loginTest() throws InterruptedException, SQLException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(userManagement.isLoggedIn(user));
        // is message send
        assertTrue(event instanceof ClientAuthorizedMessage);
        userManagement.dropUser(user);
    }

    @Test
    void loginTestFail() throws InterruptedException, SQLException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword() + "äüö");
        bus.post(loginRequest);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertFalse(userManagement.isLoggedIn(user));
        assertTrue(event instanceof ServerExceptionMessage);
        userManagement.dropUser(user);
    }

    @Test
    void logoutTest() throws InterruptedException, SQLException {
        loginUser(user);
        Optional<Session> session = authService.getSession(user);

        assertTrue(session.isPresent());
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());

        bus.post(logoutRequest);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertFalse(userManagement.isLoggedIn(user));
        assertFalse(authService.getSession(user).isPresent());
        assertTrue(event instanceof UserLoggedOutMessage);
    }

    private void loginUser(User userToLogin) throws SQLException {
        userManagement.createUser(userToLogin);
        final LoginRequest loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
        bus.post(loginRequest);

        assertTrue(userManagement.isLoggedIn(userToLogin));
        userManagement.dropUser(userToLogin);
    }

    /**
     *  This test makes sure that a user can't login when he already is.
     * <p>
     *  The test calls the loginUser function twice for the same user. And then
     *  checks if the event is an instance of ServerExceptionMessage.
     *  It also checks if the Exception of the ServerExceptionMessage is an instance of LoginException
     *  Finally it also checks if the Exception Message equals "User ... already logged in!"
     *
     * @author Sergej, René
     * @since 2021-01-03
     * @see javax.security.auth.login.LoginException
     */
    @Test
    void loginLoggedInUser() throws SQLException {
        loginUser(user);
        loginUser(user);

        assertTrue(event instanceof ServerExceptionMessage);
        var exception = ((ServerExceptionMessage) event).getException();
        assertTrue(exception instanceof LoginException);
        assertEquals(exception.getMessage() , "User " +user.getUsername()+ " already logged in!");
    }

    @Test
    void loggedInUsers() throws InterruptedException, SQLException {
        loginUser(user);

        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        assertEquals(((AllOnlineUsersResponse) event).getUsers().size(), 1);
        assertEquals(((AllOnlineUsersResponse) event).getUsers().get(0), user);

    }

    // TODO: replace with parametrized test
    @Test
    void twoLoggedInUsers() throws InterruptedException, SQLException {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        Collections.sort(users);

        loginUser(user);
        loginUser(user2);

        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        List<User> returnedUsers = new ArrayList<>(((AllOnlineUsersResponse) event).getUsers());

        assertEquals(returnedUsers.size(), 2);

        Collections.sort(returnedUsers);
        assertEquals(returnedUsers, users);

    }


    @Test
    void loggedInUsersEmpty() throws InterruptedException {
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof AllOnlineUsersResponse);

        assertTrue(((AllOnlineUsersResponse) event).getUsers().isEmpty());

    }

    @Test
    void getSessionsForUsersTest() throws SQLException {
        loginUser(user);
        loginUser(user2);
        loginUser(user3);
        Set<User> users = new TreeSet<>();
        users.add(user);
        users.add(user2);
        users.add(user3);


        Optional<Session> session1 = authService.getSession(user);
        Optional<Session> session2 = authService.getSession(user2);
        Optional<Session> session3 = authService.getSession(user2);

        assertTrue(session1.isPresent());
        assertTrue(session2.isPresent());
        assertTrue(session3.isPresent());

        List<Session> sessions = authService.getSessions(users);

        assertEquals(sessions.size(), 3);
        assertTrue(sessions.contains(session1.get()));
        assertTrue(sessions.contains(session2.get()));
        assertTrue(sessions.contains(session3.get()));

    }
    /**
     * This Test is for the X-Button Exit.
     * <p>
     * First we login the user and retrieve the session.
     * Now we create a testlobby. We check if the lobbies size is 1.
     * After that we prepare the logoutrequest and call the onLogoutRequest method.
     * The user should be logged out and the lobby dropped, because he was the only one in the lobby.
     * If the lobbies count is 0 now, the test passed successfully
     *
     * @since 2021-01-17
     * @author René Meyer, Sergej Tulnev
     */
    @Test
    @DisplayName("X Button exit")
    void exitViaXButtonTest() throws SQLException {
        // Login User and create lobby
        loginUser(user);
        Optional<Session> session = authService.getSession(user);
        assertTrue(session.isPresent());
        lobbyManagement.createLobby("testLobby", user);
        var lobbies = lobbyManagement.getAllLobbies();
        assertEquals((long) lobbies.size(), 1);
        // Prepare Logout Request
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());
        MessageContext ctx = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };
        logoutRequest.setSession(session.get());
        logoutRequest.setMessageContext(ctx);
        lobbyService.onLogoutRequest(logoutRequest);
        // User logged out, lobbies count has to be zero
        lobbies = lobbyManagement.getAllLobbies();
        assertEquals((long) lobbies.size(), 0);
    }
}