package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.LobbyDroppedMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.lobby.response.AlreadyJoinedThisLobbyResponse;
import de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse;
import de.uol.swp.common.user.response.lobby.JoinDeletedLobbyResponse;
import de.uol.swp.common.user.response.lobby.LobbyFullResponse;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marius Birk, Carsten Dekker, Pieter Vogt, Kirstin Beyer
 * @since 2020-12-02
 */

public class LobbyServiceTest {
    final EventBus bus = new EventBus();
    MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    LobbyManagement lobbyManagement = new LobbyManagement();
    UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, userManagement),
            bus);
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");

    final User defaultUser = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");

    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    public LobbyServiceTest() throws SQLException {
    }

    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected the event variable of this class gets updated
     * to its event and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     * @author Marco Grawunder
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
     * @author Marco Grawunder
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
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    private void loginUser() {
        LoginRequest loginRequest = new LoginRequest(defaultUser.getUsername(), defaultUser.getPassword());
        authenticationService.onLoginRequest(loginRequest);
    }

    @Test
    public void sendToAllInLobbyFailTest() {
        AbstractServerMessage message = new AbstractServerMessage();
        assertThrows(LobbyManagementException.class, () -> lobbyService.sendToAllInLobby("Test", message));
    }

    @Test
    void createLobbyTestLobbyExistsResponse() throws SQLException, InterruptedException {
        loginUser();
        CreateLobbyRequest request1 = new CreateLobbyRequest("Test", (UserDTO) defaultUser);
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
        request1.setMessageContext(ctx);
        lobbyService.onCreateLobbyRequest(request1);

        CreateLobbyRequest request = new CreateLobbyRequest("Test", (UserDTO) defaultUser);
        MessageContext ctx1 = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };
        request.setMessageContext(ctx1);
        lobbyService.onCreateLobbyRequest(request);

        assertTrue(event instanceof LobbyAlreadyExistsResponse);
    }

    @Test
    public void leaveLobbyAndDropLobbyTest() throws SQLException, InterruptedException {
        loginUser();
        CreateLobbyRequest request1 = new CreateLobbyRequest("Test", (UserDTO) defaultUser);
        lobbyService.onCreateLobbyRequest(request1);
        assertNotNull(lobbyManagement.getLobby("Test").get());

        LobbyLeaveUserRequest request = new LobbyLeaveUserRequest("Test", (UserDTO) defaultUser);
        request.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        });
        lobbyService.onLobbyLeaveUserRequest(request);

        assertTrue(event instanceof LobbyDroppedMessage);
    }

    @Test
    public void leaveLobbyUnknownLobbyTest() {
        lobbyManagement.createLobby("Test", defaultUser);
        assertNotNull(lobbyManagement.getLobby("Test").get());


        LobbyLeaveUserRequest request = new LobbyLeaveUserRequest("test", (UserDTO) defaultUser);
        request.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        });
        assertThrows(LobbyManagementException.class, () -> lobbyService.onLobbyLeaveUserRequest(request));

    }

    /**
     * This test shows that two Lobbies with same name can't be created.
     * There are two different users, who wants to create a lobby, but with the same name.
     *
     * @author Marius Birk, Carsten Dekker
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Zwei Lobbies, gleicher Name")
    void duplicateLobbyTest() {
        CreateLobbyRequest request = new CreateLobbyRequest("Test", (UserDTO) defaultUser);
        request.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        });
        lobbyService.onCreateLobbyRequest(request);

        User user = new UserDTO("default", "", "");
        CreateLobbyRequest request1 = new CreateLobbyRequest("Test", (UserDTO) user);
        request1.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        });

        lobbyService.onCreateLobbyRequest(request1);

        assertTrue(event instanceof LobbyAlreadyExistsResponse);
    }

    @Test
    public void retrieveAllThisLobbyUsersRequestTest() {
        CreateLobbyRequest request = new CreateLobbyRequest("Test", (UserDTO) defaultUser);
        request.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        });
        lobbyService.onCreateLobbyRequest(request);

        RetrieveAllThisLobbyUsersRequest request1 = new RetrieveAllThisLobbyUsersRequest("Test");
        MessageContext context = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };
        request1.setMessageContext(context);
        lobbyService.onRetrieveAllThisLobbyUsersRequest(request1);

        assertTrue(event instanceof AllThisLobbyUsersResponse);
    }

    @Test
    public void retrieveAllLobbiesRequestTest() throws SQLException, InterruptedException {
        loginUser();
        RetrieveAllLobbiesRequest request = new RetrieveAllLobbiesRequest();
        MessageContext context = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };

        request.setMessageContext(context);
        lobbyService.onRetrieveAllLobbiesRequest(request);
        assertTrue(event instanceof AllCreatedLobbiesResponse);
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

        ljur1.setMessageContext(ctx);
        ljur2.setMessageContext(ctx);
        ljur3.setMessageContext(ctx);

        lobbyService.onLobbyJoinUserRequest(ljur1);
        lobbyService.onLobbyJoinUserRequest(ljur2);
        lobbyService.onLobbyJoinUserRequest(ljur3);
        assertEquals(4, lobbyManagement.getLobby(lobbyName).get().getUsers().size());


        ljur4.setMessageContext(ctx);
        lobbyService.onLobbyJoinUserRequest(ljur4);

        assertEquals(4, lobbyManagement.getLobby(lobbyName).get().getUsers().size());

        assertFalse(lobbyManagement.getLobby(lobbyName).get().getUsers().contains(userDTO4));

        assertTrue(event instanceof LobbyFullResponse);
    }

    /**
     * This test shows that a user can join a lobby only once.
     * <p>
     * Additionally the test checks if the user receives a AlreadyJoinedThisLobbyResponse Message on the Bus
     * when he tries to join lobby he is already in.
     *
     * @author Carsten Dekker
     * @since 2021-01-22
     */
    @Test
    @DisplayName("Join Versuch Lobby schon einmal beigetreten")
    void LobbyAlreadyJoinedTest() throws LobbyManagementException {
        String lobbyName = "TestLobby";
        UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");

        CreateLobbyRequest clr = new CreateLobbyRequest(lobbyName, userDTO);
        LobbyJoinUserRequest ljur1 = new LobbyJoinUserRequest(lobbyName, userDTO);

        lobbyService.onCreateLobbyRequest(clr);

        assertNotNull(lobbyManagement.getLobby(lobbyName).get());

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

        ljur1.setMessageContext(ctx);

        lobbyService.onLobbyJoinUserRequest(ljur1);

        assertEquals(1, lobbyManagement.getLobby(lobbyName).get().getUsers().size());

        assertTrue(event instanceof AlreadyJoinedThisLobbyResponse);
    }


    /**
     * This test checks if a User want´s join a deleted lobby.
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

        LobbyJoinUserRequest ljur1 = new LobbyJoinUserRequest("testLobby", (UserDTO) defaultUser);
        ljur1.setMessageContext(ctx);
        assertThrows(NoSuchElementException.class, () -> lobbyService.onLobbyJoinUserRequest(ljur1));
        assertTrue(event instanceof JoinDeletedLobbyResponse);
    }

    /**
     * This test checks if lobbies can be created with a certain name and
     * <p>
     * whether a lobby that is referenced by the RetrieveAllThisLobbyUsersRequest
     * <p>
     * is also the same as the lobby itself.
     * <p>
     * The lobby that was created by the User userDTO is also joined by userDTO1 and it is checked whether the
     * <p>
     * lobby has references to the session of the users that joined the lobby.
     *
     * @author Marc Hermes
     * @since 2020-12-08
     */

    @Test
    void onRetrieveAllThisLobbyUsersRequest() {
        lobbyManagement.createLobby("testLobby", defaultUser);
        RetrieveAllThisLobbyUsersRequest retrieveAllThisLobbyUsersRequest = new RetrieveAllThisLobbyUsersRequest(
                "testLobby");
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertSame(lobbyManagement.getLobby("testLobby").get().getName(),
                retrieveAllThisLobbyUsersRequest.getName());
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(defaultUser);
        List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
        for (Session session : lobbyUsers) {
            assertTrue(defaultUser == (session.getUser()) || defaultUser == (session.getUser()));
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
        lobbyManagement.createLobby("testLobby", defaultUser);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        lobby.get().joinUser(defaultUser);

        lobby.get().joinUser(new UserDTO("default", "", ""));
        LobbyLeaveUserRequest lobbyLeaveUserRequest = new LobbyLeaveUserRequest("testLobby", (UserDTO) defaultUser);
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
        lobbyLeaveUserRequest.setMessageContext(ctx);
        lobbyService.onLobbyLeaveUserRequest(lobbyLeaveUserRequest);
        System.out.println(lobby.get().getUsers());
        assertTrue(event instanceof UserLeftLobbyMessage);
        assertFalse(lobby.get().getUsers().contains(defaultUser));
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
    void leaveLobbyOwnerTest() throws InterruptedException {
        lobbyManagement.createLobby("testLobby", defaultUser);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        lobby.get().joinUser(defaultUser);

        User user = new UserDTO("test", "", "");
        lobby.get().joinUser(user);

        LobbyLeaveUserRequest lobbyLeaveUserRequest = new LobbyLeaveUserRequest("testLobby", (UserDTO) defaultUser);
        lobbyService.onLobbyLeaveUserRequest(lobbyLeaveUserRequest);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof UserLeftLobbyMessage);
        assertFalse(lobby.get().getUsers().contains(defaultUser));
    }


    @Test
    public void userLogoutDropLobby() throws InterruptedException {
        loginUser();
        MessageContext msg = new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        };
        LogoutRequest request = new LogoutRequest();
        assertTrue(event instanceof ClientAuthorizedMessage);
        request.setSession(((ClientAuthorizedMessage) event).getSession().get());
        request.setMessageContext(msg);

        lobbyService.onCreateLobbyRequest(new CreateLobbyRequest("Test", (UserDTO) defaultUser));
        lock.await(1000,TimeUnit.MILLISECONDS);

        lobbyService.onLogoutRequest(request);


        assertFalse(lobbyManagement.getAllLobbies().containsKey("Test"));
    }
}