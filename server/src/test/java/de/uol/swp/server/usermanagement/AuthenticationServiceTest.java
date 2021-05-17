package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.Lobby;
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
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.*;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class AuthenticationServiceTest {

    private final CountDownLatch lock = new CountDownLatch(1);

    final User user = new UserDTO("name", "7a63639a836e576ee85336536040ce3b351fa0af1315f7c97e2956a298c72aced9acaae7be8fc36ba66bd20cd459a7321785c5a4af35ce6a620549f8f9d6c7dc", "email@test.de");
    final User user2 = new UserDTO("name2", "bb886534800b205dd707d7e116e32eaa0563c7dfac8fdcf70e7cd83cc9d155c114c5c278e1a66bb6a37dd79badcb0a4d3acf3224508d82188a9ac2e0bf42ee7c", "email@test.de2");
    final User user3 = new UserDTO("name3", "3a97ca2b95d063f0aa32e29d1ec9ac47733d10aa9c540df04dd01b90b346ccaa0af47f4ab28d01077c82efa808c464908a5f0962f94ebb92d3ec9b473ed3a2be", "email@test.de3");


    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement();
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);
    final UserService userService = new UserService(bus, userManagement);
    final GameManagement gameManagement = new GameManagement();
    final GameService gameService = new GameService(gameManagement, lobbyService, authService, bus, userService);
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
    void registerBus() throws SQLException {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    @BeforeEach
    void testPreparation() throws SQLException {
        if(userManagement.retrieveAllUsers().contains(user)) {
            userManagement.logout(user);
            userManagement.dropUser(user);
        } else if (userManagement.retrieveAllUsers().contains(user2)) {
            userManagement.logout(user2);
            userManagement.dropUser(user2);
        } else if (userManagement.retrieveAllUsers().contains(user3)) {
            userManagement.logout(user3);
            userManagement.dropUser(user3);
        }
    }

    @Test
    void loginTest() throws InterruptedException, SQLException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest);
        assertTrue(userManagement.isLoggedIn(user));
        // is message send
        assertTrue(event instanceof ClientAuthorizedMessage);
        userManagement.logout(user);
        userManagement.dropUser(user);
    }

    @Test
    void loginTestFail() throws InterruptedException, SQLException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword() + "äüö");
        bus.post(loginRequest);

        assertFalse(userManagement.isLoggedIn(user));
        assertTrue(event instanceof ServerExceptionMessage);
        userManagement.logout(user);
        userManagement.dropUser(user);
    }

    @Test
    void logoutTest() throws InterruptedException, SQLException {
        loginUser(user2);
        Optional<Session> session = authService.getSession(user2);

        assertTrue(session.isPresent());
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());

        bus.post(logoutRequest);


        assertFalse(userManagement.isLoggedIn(user2));
        assertFalse(authService.getSession(user2).isPresent());
        assertTrue(event instanceof UserLoggedOutMessage);
        userManagement.logout(user2);
        userManagement.dropUser(user2);
    }

    private void loginUser(User userToLogin) throws SQLException, InterruptedException {
        userManagement.createUser(userToLogin);
        final LoginRequest loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
        bus.post(loginRequest);
    }

    /**
     * This test makes sure that a user can't login when he already is.
     * <p>
     * The test calls the loginUser function twice for the same user. And then
     * checks if the event is an instance of ServerExceptionMessage.
     * It also checks if the Exception of the ServerExceptionMessage is an instance of LoginException
     * Finally it also checks if the Exception Message equals "User ... already logged in!"
     *
     * @author Sergej, René
     * @see javax.security.auth.login.LoginException
     * @since 2021-01-03
     */
    @Test
    void loginLoggedInUser() throws SQLException, InterruptedException {
        loginUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest);

        assertTrue(event instanceof ServerExceptionMessage);
        var exception = ((ServerExceptionMessage) event).getException();
        assertTrue(exception instanceof LoginException);
        assertEquals(exception.getMessage(), "User " + user.getUsername() + " already logged in!");
        userManagement.logout(user);
        userManagement.dropUser(user);
    }

    @Test
    void loggedInUsers() throws InterruptedException, SQLException {
        loginUser(user2);
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);


        assertTrue(event instanceof AllOnlineUsersResponse);
        assertEquals(((AllOnlineUsersResponse) event).getUsers().size(), 1);
        assertEquals(((AllOnlineUsersResponse) event).getUsers().get(0), user2);
        userManagement.logout(user2);
        userManagement.dropUser(user2);
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

        assertTrue(event instanceof AllOnlineUsersResponse);

        List<User> returnedUsers = new ArrayList<>(((AllOnlineUsersResponse) event).getUsers());

        assertEquals(returnedUsers.size(), 2);

        Collections.sort(returnedUsers);
        assertEquals(returnedUsers, users);

        userManagement.logout(user);
        userManagement.dropUser(user);
        userManagement.logout(user2);
        userManagement.dropUser(user2);
    }


    @Test
    void loggedInUsersEmpty() throws InterruptedException {
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        assertTrue(event instanceof AllOnlineUsersResponse);

        assertTrue(((AllOnlineUsersResponse) event).getUsers().isEmpty());

    }

    @Test
    void getSessionsForUsersTest() throws SQLException, InterruptedException {
        loginUser(user);
        loginUser(user2);
        loginUser(user3);
        Set<User> users = new TreeSet<>();
        users.add(user);
        users.add(user2);
        users.add(user3);


        Optional<Session> session1 = authService.getSession(user);
        Optional<Session> session2 = authService.getSession(user2);
        Optional<Session> session3 = authService.getSession(user3);

        assertTrue(session1.isPresent());
        assertTrue(session2.isPresent());
        assertTrue(session3.isPresent());

        List<Session> sessions = authService.getSessions(users);

        assertEquals(sessions.size(), 3);
        assertTrue(sessions.contains(session1.get()));
        assertTrue(sessions.contains(session2.get()));
        assertTrue(sessions.contains(session3.get()));

        userManagement.logout(user);
        userManagement.dropUser(user);
        userManagement.logout(user2);
        userManagement.dropUser(user2);
        userManagement.logout(user3);
        userManagement.dropUser(user3);
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
     * <p>
     * enhanced 2021-04-08
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2021-01-17
     */
    @Test
    @DisplayName("X Button exit")
    void exitViaXButtonTest() throws SQLException, InterruptedException {
        // Login User and check session
        loginUser(user);
        Optional<Session> sessionUser = authService.getSession(user);
        assertTrue(sessionUser.isPresent());

        // Login User2 and check session
        loginUser(user3);
        Optional<Session> sessionUser2 = authService.getSession(user3);
        assertTrue(sessionUser2.isPresent());

        // Create testLobby with user as owner
        lobbyManagement.createLobby("testLobby", user);

        // Check if only 1 lobby exists
        var lobbies = lobbyManagement.getAllLobbies();
        assertEquals((long) lobbies.size(), 1);

        // Get lobby object and check if it is present
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());

        // User2 joins lobby and check if lobby size now = 2
        lobby.get().joinUser(user3);
        lobby = lobbyManagement.getLobby("testLobby");
        assertEquals(lobby.get().getUsers().size(), 2);

        // Create game and let user and user2 join and check if game size = 2
        gameManagement.createGame("testGame", user, "random");
        var game = gameManagement.getGame("testGame");
        assertTrue(game.isPresent());
        game.get().joinUser(user3);
        game = gameManagement.getGame("testGame");
        assertEquals(game.get().getUsers().size(), 2);

        // Post LogoutRequest for user on eventBus
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(sessionUser.get());
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
        logoutRequest.setSession(sessionUser.get());
        logoutRequest.setMessageContext(ctx);
        bus.post(logoutRequest);

        // Post LogoutRequest for user2 on eventBus
        final LogoutRequest logoutRequest2 = new LogoutRequest();
        logoutRequest.setSession(sessionUser2.get());
        MessageContext ctx2 = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };
        logoutRequest2.setSession(sessionUser2.get());
        logoutRequest2.setMessageContext(ctx2);
        bus.post(logoutRequest2);

        // After logging out (simulating x button) both users, there has to be 0 lobbies and 0 games.
        var games = gameManagement.getAllGames();
        assertEquals(games.size(), 0);
        lobbies = lobbyManagement.getAllLobbies();
        assertEquals(lobbies.size(), 0);
        userManagement.dropUser(user);
        userManagement.dropUser(user3);
    }
}