package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test Class for the LobbyService (Server)
 *
 * @author Marc Hermes
 * @since 2020-12-08
 */

class LobbyServiceTest {

    final EventBus bus = new EventBus();
    Object event;
    final UserStore userStore = new MainMemoryBasedUserStore();
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final UserManagement userManagement = new UserManagement(userStore);
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);

    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();

    }

    @BeforeEach
    void setUp() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void tearDown() {
        bus.unregister(this);
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
}