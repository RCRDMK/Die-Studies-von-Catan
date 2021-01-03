package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.LobbyFullResponse;
import de.uol.swp.common.user.response.JoinDeletedLobbyResponse;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marius Birk, Carsten Dekker, Pieter Vogt, Kirstin Beyer
 * @since 2020-12-02
 */

public class LobbyServiceTest {
    final EventBus bus = new EventBus();
    LobbyManagement lobbyManagement = new LobbyManagement();
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, new UserManagement(new MainMemoryBasedUserStore())), bus);
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
     * This test shows that a maximum of 4 users can join a lobby.
     * <p>
     * Additionally the test checks if the user receives a LobbyFullResponse Message on the Bus
     * when he tries to join a full lobby (Added by René)
     *
     * @author Pieter Vogt, Kirstin Beyer, René Meyer
     * @since 2020-12-21
     */
    @Test
    @DisplayName("Join Versuch Lobby voll - LobbyFullResponse")
    void LobbyJoinTest() throws LobbyManagementException {
        String lobbyName = "TestLobby";
        UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
        UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");
        UserDTO userDTO2 = new UserDTO("Test", "lustig1", "peterlustig@uol.de");
        UserDTO userDTO3 = new UserDTO("Test2", "lustig2", "test.lustig@uol.de");
        UserDTO userDTO4 = new UserDTO("Peter1", "lustig3", "peter1lustig@uol.de");

        CreateLobbyRequest clr = new CreateLobbyRequest(lobbyName, userDTO);
        LobbyJoinUserRequest ljur1 = new LobbyJoinUserRequest(lobbyName, userDTO1);
        LobbyJoinUserRequest ljur2 = new LobbyJoinUserRequest(lobbyName, userDTO2);
        LobbyJoinUserRequest ljur3 = new LobbyJoinUserRequest(lobbyName, userDTO3);
        LobbyJoinUserRequest ljur4 = new LobbyJoinUserRequest(lobbyName, userDTO4);

        lobbyService.onCreateLobbyRequest(clr);

        assertNotNull(lobbyManagement.getLobby(lobbyName).get());

        lobbyService.onLobbyJoinUserRequest(ljur1);
        lobbyService.onLobbyJoinUserRequest(ljur2);
        lobbyService.onLobbyJoinUserRequest(ljur3);
        assertEquals(4, lobbyManagement.getLobby(lobbyName).get().getUsers().size());

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
        ljur4.setMessageContext(ctx);
        lobbyService.onLobbyJoinUserRequest(ljur4);

        assertEquals(4, lobbyManagement.getLobby(lobbyName).get().getUsers().size());

        assertFalse(lobbyManagement.getLobby(lobbyName).get().getUsers().contains(userDTO4));

        assertTrue(event instanceof LobbyFullResponse);
    }


    /**
     * This test checks if a User want´s join a deleted lobby.
     *
     *
     * @author Sergej
     */
    @Test
    void joinDeletedLobbyTest() {
        UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
        lobbyManagement.createLobby("testLobby", userDTO);
        lobbyManagement.dropLobby("testLobby");
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
        LobbyJoinUserRequest ljur1 = new LobbyJoinUserRequest("testLobby", userDTO1);
        ljur1.setMessageContext(ctx);
        assertThrows(NoSuchElementException.class, () -> lobbyService.onLobbyJoinUserRequest(ljur1));
        assertTrue(event instanceof JoinDeletedLobbyResponse);
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