package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

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
     *
     * and if RetrieveAllThisLobbyUsersRequest can be posted on the event bus thus becoming and event.
     *
     * It also checks if the method retrieveAllThisLobbyUsers() of the LobbyService(client) can be called
     *
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
     *
     * and if RetrieveAllThisLobbyUsersRequest can be posted on the event bus thus becoming and event.
     *
     * It also checks if the method retrieveAllThisLobbyUsers() of the LobbyService(client) can be called
     *
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
        //Jetzt verlässt der userDTO1 die Lobby.
        UserLeftLobbyMessage message3 = new UserLeftLobbyMessage("testLobby", userDTO1,userDTO.getUsername());
        lobbyService.retrieveAllThisLobbyUsers(message2.getName());
        assertTrue(event instanceof RetrieveAllThisLobbyUsersRequest);
    }

    /**
     * This test checks if lobbies can be created with a certain name, joined  and
     *
     * if RetrieveAllThisLobbyUsersRequest can be posted on the event bus thus becoming and event.
     *
     * It also checks if the method retrieveAllThisLobbyUsers() of the LobbyService(client) can be called
     *
     * successfully to create a RetrieveALlThisLobbyUsersRequest.
     *
     * @author Marc Hermes
     * @since 2020-12-08
     */

    @Test
    void joinedSuccessful() {
        LobbyService lobbyService = new LobbyService(bus);
        CreateLobbyRequest message = new CreateLobbyRequest("testLobby", userDTO);
        lobbyService.createNewLobby("testLobby", userDTO);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(userDTO);
        lobbyService.joinLobby("testLobby", userDTO1);
        UserJoinedLobbyMessage message3 = new UserJoinedLobbyMessage("testLobby", userDTO1);
        lobbyService.retrieveAllThisLobbyUsers(message2.getName());
        assertTrue(event instanceof RetrieveAllThisLobbyUsersRequest);
    }

}