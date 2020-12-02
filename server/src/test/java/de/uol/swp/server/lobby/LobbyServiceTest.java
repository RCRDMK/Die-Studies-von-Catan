package de.uol.swp.server.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyAlreadyExistsMessage;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

        CreateLobbyRequest clr = new CreateLobbyRequest(lobbyName, userDTO);
        CreateLobbyRequest clr2 = new CreateLobbyRequest(lobbyName, userDTO1);


        bus.post(clr);
        lock.await(1000, TimeUnit.MILLISECONDS);

        lobbyManagement.createLobby(lobbyName, userDTO);

        assertNotNull(lobbyManagement.getLobby(lobbyName).get());

        bus.post(clr2);

        assertNotEquals(lobbyManagement.getLobby(lobbyName).get(), userDTO1);

    }
}
