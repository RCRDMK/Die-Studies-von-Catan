package de.uol.swp.client.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.game.request.GameLeaveUserRequest;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test class for the GamePresenter
 *
 * @author Ricardo Mook, Alexander Losse
 * @since 2021-03-05
 */


public class GamePresenterTest {

    final EventBus bus = new EventBus();
    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");
    Object event;

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
     * This test checks if users can leave the game scene when pressing the leave game button.
     *
     * @author Ricardo Mook, Alexander Losse
     * @since 2021-03-05
     */
    @Test
    void onLeaveGame() {
        LobbyService lobbyService = new LobbyService(bus);
        lobbyService.createNewLobby("testLobby", userDTO);
        lobbyService.joinLobby("testLobby", userDTO1);
        GameService gameService = new GameService(bus);
        gameService.leaveGame("Testgame",userDTO);
        assertTrue(event instanceof GameLeaveUserRequest);
    }
}
