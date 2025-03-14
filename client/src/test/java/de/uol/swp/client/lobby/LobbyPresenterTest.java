package de.uol.swp.client.lobby;

import java.util.ArrayList;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test Class for the LobbyPresenter
 *
 * @author Marc Hermes
 * @since 2020-12-08
 */

class LobbyPresenterTest {

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
     * This test checks if lobbies can be created with a certain name and
     * <p>
     * and if RetrieveAllThisLobbyUsersRequest can be posted on the event bus thus becoming and event.
     * <p>
     * It also checks if the method retrieveAllThisLobbyUsers() of the LobbyService(client) can be called
     * <p>
     * successfully to create a RetrieveALlThisLobbyUsersRequest.
     *
     * @author Marc Hermes
     * @since 2020-12-08
     */

    @Test
    void createdSuccessful() {
        LobbyService lobbyService = new LobbyService(bus);
        CreateLobbyRequest message = new CreateLobbyRequest("testLobby", userDTO);
        lobbyService.createNewLobby("testLobby", userDTO);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(userDTO);
        lobbyService.retrieveAllThisLobbyUsers(message2.getName());
        assertTrue(event instanceof RetrieveAllThisLobbyUsersRequest);

    }

    /**
     * This test checks if lobbies can be created with a certain name, left and
     * <p>
     * and if RetrieveAllThisLobbyUsersRequest can be posted on the event bus thus becoming and event.
     * <p>
     * It also checks if the method retrieveAllThisLobbyUsers() of the LobbyService(client) can be called
     * <p>
     * successfully to create a RetrieveALlThisLobbyUsersRequest.
     *
     * @author Marc Hermes
     * @since 2020-12-08
     */

    @Test
    void leftSuccessful() {
        LobbyService lobbyService = new LobbyService(bus);
        CreateLobbyRequest message = new CreateLobbyRequest("testLobby", userDTO);
        lobbyService.createNewLobby("testLobby", userDTO);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(userDTO);
        lobbyService.joinLobby("testLobby", userDTO1);
        ArrayList<UserDTO> users = new ArrayList<>();
        users.add(userDTO);
        //Jetzt verlässt der userDTO1 die Lobby.
        UserLeftLobbyMessage message3 = new UserLeftLobbyMessage("testLobby", userDTO1, users, userDTO.getUsername());
        lobbyService.retrieveAllThisLobbyUsers(message2.getName());
        assertTrue(event instanceof RetrieveAllThisLobbyUsersRequest);
    }

    /**
     * This test checks if lobbies can be created with a certain name, joined  and
     * <p>
     * if RetrieveAllThisLobbyUsersRequest can be posted on the event bus thus becoming and event.
     * <p>
     * It also checks if the method retrieveAllThisLobbyUsers() of the LobbyService(client) can be called
     * <p>
     * successfully to create a RetrieveALlThisLobbyUsersRequest.
     *
     * @author Marc Hermes
     * @since 2020-12-08
     */

    @Test
    void joinedSuccessful() {
        LobbyService lobbyService = new LobbyService(bus);
        lobbyService.createNewLobby("testLobby", userDTO);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(userDTO);
        lobbyService.joinLobby("testLobby", userDTO1);
        ArrayList<UserDTO> users = new ArrayList<>();
        users.add((UserDTO) userDTO1);
        UserJoinedLobbyMessage message3 = new UserJoinedLobbyMessage("testLobby", userDTO1, users);
        lobbyService.retrieveAllThisLobbyUsers(message2.getName());
        assertTrue(event instanceof RetrieveAllThisLobbyUsersRequest);
    }

}