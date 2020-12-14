package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.LobbyLeftSuccessfulResponse;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author Marius Birk, Carsten Dekker
 *  @since 2020-12-02
 */

public class LobbyServiceTest {
    final EventBus bus = new EventBus();
    LobbyManagement lobbyManagement = new LobbyManagement();
    final UserStore userStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(userStore);
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);

    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");

    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected the event variable of this class gets updated
     * to its event and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     * @since 2019-10-10
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    /**
     * Helper method run before each test case
     * <p>
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     *
     * @since 2019-10-10
     */
    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method only unregisters the object of this class from the EventBus.
     *
     * @since 2019-10-10
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * This test shows that two Lobbies with same name can't be created.
     * There are two different users, who wants to create a lobby, but with the same name.
     *
     * @since 2020-12-02
     * @author Marius Birk, Carsten Dekker
     */
    @Test
    @DisplayName("Zwei Lobbies, gleicher Name")
    void duplicateLobbyTest() throws InterruptedException {
        String lobbyName = "Testlobby";

        UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
        UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");

        lock.await(2000, TimeUnit.MILLISECONDS);

        lobbyManagement.createLobby(lobbyName, userDTO);
        /** We except the first assertNotNull to be true.*/
        assertNotNull(lobbyManagement.getLobby(lobbyName).get());

        /** We expect the next line to success. We try to create a new lobby with the same name as the first one. But we dont want a new lobby, so it throws an exception. */
        Assertions.assertThrows(IllegalArgumentException.class, ()->lobbyManagement.createLobby(lobbyName, userDTO1));
    }

    /**
     * This test checks if lobbies can be created with a certain name and
     *
     * whether a lobby that is referenced by the RetrieveAllThisLobbyUsersRequest
     *
     * is also the same as the lobby itself.
     *
     * The lobby that was created by the User userDTO is also joined by userDTO1 and it is checked whether the
     *
     * lobby has references to the session of the users that joined the lobby.
     *
     * @author Marc Hermes
     * @since 2020-12-08
     */

    @Test
    void onRetrieveAllThisLobbyUsersRequest() {

        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        RetrieveAllThisLobbyUsersRequest retrieveAllThisLobbyUsersRequest = new RetrieveAllThisLobbyUsersRequest("testLobby");
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertSame(lobbyManagement.getLobby("testLobby").get().getName(), retrieveAllThisLobbyUsersRequest.getName());
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
        for(Session session :lobbyUsers) {
            assertTrue(userDTO==(session.getUser()) || userDTO1==(session.getUser()));
        }

    }

    /**
     * tests if a user leaves lobby
     *
     * @see LobbyService
     * @see LobbyLeaveUserRequest
     * @see UserLeftLobbyMessage
     * @since 2020-12-11
     */
    @Test
    void leaveLobbyTest() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        lobby.get().joinUser(userDTO1);
        LobbyLeaveUserRequest lobbyLeaveUserRequest = new LobbyLeaveUserRequest("testLobby", userDTO1);
        lobbyService.onLobbyLeaveUserRequest(lobbyLeaveUserRequest);
        assertTrue(event instanceof UserLeftLobbyMessage);
        assertFalse(lobby.get().getUsers().contains(userDTO1));
    }


    /**
     * tests if the owner leaves lobby and a new owner is chosen
     *
     * @see LobbyService
     * @see LobbyLeaveUserRequest
     * @see UserLeftLobbyMessage
     * @since 2020-12-11
     */
    @Test
    void leaveLobbyOwnerTest() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        lobby.get().joinUser(userDTO1);
        LobbyLeaveUserRequest lobbyLeaveUserRequest = new LobbyLeaveUserRequest("testLobby", userDTO);
        lobbyService.onLobbyLeaveUserRequest(lobbyLeaveUserRequest);
        assertTrue(event instanceof UserLeftLobbyMessage);
        assertFalse(lobby.get().getUsers().contains(userDTO));
        assertTrue(lobby.get().getOwner() == userDTO1);
    }

}