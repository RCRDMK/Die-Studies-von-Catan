package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActiveUserListTest {


    private final CountDownLatch lock = new CountDownLatch(1);

    final User user = new UserDTO("name", "password", "email@test.de");
    final User user2 = new UserDTO("name2", "password2", "email@test.de2");

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement();
    final AuthenticationService authService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authService, bus);
    private Object event;

    public ActiveUserListTest() throws SQLException {
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
    void addActiveUserTest() throws InterruptedException, SQLException {
        userManagement.createUser(user);
        final LoginRequest loginRequest1 = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest1);

        userManagement.createUser(user2);
        final LoginRequest loginRequest2 = new LoginRequest(user2.getUsername(), user2.getPassword());
        bus.post(loginRequest2);
        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(ActiveUserList.activeUserTable.containsKey("name"));
        assertTrue(ActiveUserList.activeUserTable.containsKey("name2"));
        userManagement.dropUser(user);
        userManagement.dropUser(user2);
    }

    @Test
    void removeActiveUserTest() throws InterruptedException, SQLException {
        userManagement.createUser(user);
        final LoginRequest loginRequest1 = new LoginRequest(user.getUsername(), user.getPassword());
        bus.post(loginRequest1);

        userManagement.createUser(user2);
        final LoginRequest loginRequest2 = new LoginRequest(user2.getUsername(), user2.getPassword());
        bus.post(loginRequest2);
        lock.await(1000, TimeUnit.MILLISECONDS);

        Optional<Session> session = authService.getSession(user);
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());
        bus.post(logoutRequest);

        lock.await(1000, TimeUnit.MILLISECONDS);
        assertFalse(ActiveUserList.activeUserTable.containsKey("name"));
        assertTrue(ActiveUserList.activeUserTable.containsKey("name2"));
        userManagement.dropUser(user2);
    }
}
