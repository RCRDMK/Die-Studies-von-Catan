package de.uol.swp.server.usermanagement;

import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class AuthenticationServiceTest {

    final User user = new UserDTO("name",
            "7a63639a836e576ee85336536040ce3b351fa0af1315f7c97e2956a298c72aced9acaae7be8fc36ba66bd20cd459a7321785c5a4af35ce6a620549f8f9d6c7dc",
            "email@test.de");
    final User user2 = new UserDTO("name2",
            "bb886534800b205dd707d7e116e32eaa0563c7dfac8fdcf70e7cd83cc9d155c114c5c278e1a66bb6a37dd79badcb0a4d3acf3224508d82188a9ac2e0bf42ee7c",
            "email@test.de2");
    final User user3 = new UserDTO("name3",
            "3a97ca2b95d063f0aa32e29d1ec9ac47733d10aa9c540df04dd01b90b346ccaa0af47f4ab28d01077c82efa808c464908a5f0962f94ebb92d3ec9b473ed3a2be",
            "email@test.de3");


    final EventBus bus = new EventBus();
    final MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    final GameManagement gameManagement = new GameManagement();
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);
    final UserService userService = new UserService(bus, userManagement);
    final GameService gameService = new GameService(gameManagement, lobbyService, authService, bus, userService);
    private Object event;

    AuthenticationServiceTest() throws SQLException {
    }

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
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
    void loginTest() throws Exception {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        MessageContext messageContext = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };
        loginRequest.setMessageContext(messageContext);
        bus.post(loginRequest);
        assertTrue(userManagement.isLoggedIn(user));
        // is message send
        assertTrue(event instanceof ClientAuthorizedMessage);
        userManagement.logout(user);
        userManagement.dropUser(user);
    }

    @Test
    void loginTestFail() throws Exception {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword() + "äüö");
        bus.post(loginRequest);

        assertFalse(userManagement.isLoggedIn(user));
        assertTrue(event instanceof ServerExceptionMessage);
        userManagement.logout(user);
        userManagement.dropUser(user);
    }

    @Test
    void logoutTest() throws Exception {
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

    private void loginUser(User userToLogin) throws Exception {
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
    void loginLoggedInUser() throws Exception {
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
    void loggedInUsers() throws Exception {
        loginUser(user2);
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);


        assertTrue(event instanceof AllOnlineUsersResponse);
        assertEquals(((AllOnlineUsersResponse) event).getUsers().size(), 1);
        assertEquals(((AllOnlineUsersResponse) event).getUsers().get(0), user2);
        userManagement.logout(user2);
        userManagement.dropUser(user2);
    }

    @Test
    void twoLoggedInUsers() throws Exception {
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
    void loggedInUsersEmpty() {
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        bus.post(request);

        assertTrue(event instanceof AllOnlineUsersResponse);

        assertTrue(((AllOnlineUsersResponse) event).getUsers().isEmpty());

    }

    @Test
    void getSessionsForUsersTest() throws Exception {
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
     * Now we create a test lobby. We check if the lobbies size is 1.
     * After that we prepare the logoutRequest and call the onLogoutRequest method.
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
    void exitViaXButtonTest() throws Exception {
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
        assertEquals(lobby.get().getUsers().size(), 2);

        lobby.get().joinPlayerReady(user);
        lobby.get().joinPlayerReady(user3);
        gameService.startGame(lobby.get(), "");

        // Create game and let user and user2 join and check if game size = 2
        var game = gameManagement.getGame("testLobby");
        assertTrue(game.isPresent());
        game.get().joinUser(user3);
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
        logoutRequest.setMessageContext(ctx);
        authService.onLogoutRequest(logoutRequest);
        gameService.onLogoutRequest(logoutRequest);
        lobbyService.onLogoutRequest(logoutRequest);

        // Post LogoutRequest for user2 on eventBus
        final LogoutRequest logoutRequest2 = new LogoutRequest();
        logoutRequest2.setSession(sessionUser2.get());
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
        logoutRequest2.setMessageContext(ctx2);
        authService.onLogoutRequest(logoutRequest2);
        gameService.onLogoutRequest(logoutRequest2);
        lobbyService.onLogoutRequest(logoutRequest2);

        // After logging out (simulating x button) both users, there has to be 0 lobbies and 0 games.
        var games = gameService.getGameManagement().getAllGames();
        assertEquals(games.size(), 0);
        lobbies = lobbyManagement.getAllLobbies();
        assertEquals(lobbies.size(), 0);
        userManagement.dropUser(user);
        userManagement.dropUser(user3);
    }

}