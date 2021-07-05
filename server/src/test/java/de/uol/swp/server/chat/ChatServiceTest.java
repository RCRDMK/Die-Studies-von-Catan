package de.uol.swp.server.chat;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.cheat.CheatService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for the ChatService Test
 * <p>
 * Contains sendRequestChatMessageTest and onResponseChatMessageTest
 *
 * @author René, Anton, Sergej
 * @see ChatService
 * @since 2020-11-22
 */
public class ChatServiceTest {
    final EventBus bus = new EventBus();
    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final CountDownLatch lock = new CountDownLatch(1);
    MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    UserService userService = new UserService(bus, userManagement);
    GameManagement gameManagement = new GameManagement();
    LobbyManagement lobbyManagement = new LobbyManagement();
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, userManagement), bus);
    GameService gameService = new GameService(gameManagement, lobbyService,
            new AuthenticationService(bus, userManagement), bus, userService);
    final CheatService cheatService = new CheatService(gameService, bus);
    Object event;
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    final ChatService chatService = new ChatService(cheatService, bus);
    public ChatServiceTest() throws SQLException {
    }

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
     * Test for the ChatService
     * <p>
     * This test first creates a new RequestChatMessage object. It then
     * posts the RequestChatMessage object on the EventBus via calling
     * the onRequestChatMessage() function in the chatService.
     * Then it tests if the event is an instance of the ResponseChatMessage
     * After that it checks if the RequestChatMessage variables match the ResponseChatMessage variables
     * So this test covers the full Client-Server Communication for a sent chatmessage
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2020-12-10
     */
    @Test
    void sendRequestChatMessageTest() {
        RequestChatMessage message = new RequestChatMessage("testMessage", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        message.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {
                bus.post(message);
            }

            @Override
            public void writeAndFlush(ServerMessage message) {
                bus.post(message);
            }
        });
        chatService.onRequestChatMessage(message);
        assertTrue(event instanceof ResponseChatMessage);
        var responseChatMessage = (ResponseChatMessage) event;
        assertNotNull(responseChatMessage);
        assertEquals(responseChatMessage.getMessage(), "testMessage");
        assertEquals(responseChatMessage.getChat(), "testLobby");
        assertEquals(responseChatMessage.getUsername(), defaultUser.getUsername());
        assertFalse(responseChatMessage.getTime().isNaN());
    }
}
