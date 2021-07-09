package de.uol.swp.client.chat;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseEmptyChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René, Anton, Sergej
 * @see ChatService
 * @since 2020-11-26
 */

public class ChatServiceTest {
    final EventBus bus = new EventBus();
    final ChatService chatService = new ChatService(bus);
    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    Object event;

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

    /**
     * Test for the ChatService
     * <p>
     * This test first creates a new RequestChatMessage object. It then
     * calls the chatService sendMessage function and passes the object as parameter
     * and waits for it to post an RequestChatMessage object on the EventBus.
     * The test fails if the chatService can't send a message within one second .
     * The test also fails if the dummy Username, Message or ChatID isn't equal to the request object properties.
     * It also fails if the request object getTime() function doesn't return a valid double.
     *
     * @author Marco Grawunder
     * @author René Meyer
     * @since 2020-12-16
     */
    @Test
    @DisplayName("Sends a normal Message")
    void sendMessageTest() {
        RequestChatMessage message = new RequestChatMessage("testMessage", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message);

        assertTrue(event instanceof RequestChatMessage);

        RequestChatMessage request = (RequestChatMessage) event;

        assertEquals(request.getUsername(), defaultUser.getUsername());
        assertFalse(request.getTime().isNaN());
        assertEquals(request.getChat(), "testLobby");
        assertEquals(request.getMessage(), "testMessage");
    }

    @Test
    @DisplayName("Sends a Message with no String in it")
    void sendEmptyMessageTest() {
        RequestChatMessage message = new RequestChatMessage("", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message);

        assertTrue(event instanceof ResponseEmptyChatMessage);

        ResponseEmptyChatMessage response = (ResponseEmptyChatMessage) event;

        assertEquals(response.getUsername(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), "testLobby");
        assertEquals(response.getMessage(), "");

    }

    @Test
    @DisplayName("Sends a Message with Blanks")
    void sendWhiteSpaceMessageTest() {
        RequestChatMessage message = new RequestChatMessage("    ", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message);

        assertTrue(event instanceof ResponseEmptyChatMessage);

        ResponseEmptyChatMessage response = (ResponseEmptyChatMessage) event;

        assertEquals(response.getUsername(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), "testLobby");
        assertEquals(response.getMessage(), "    ");

    }

    @Test
    @DisplayName("Sends an empty Message")
    void onSendNullMessageTest() {
        RequestChatMessage message = new RequestChatMessage(null, "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message);

        assertTrue(event instanceof ResponseEmptyChatMessage);

        ResponseEmptyChatMessage response = (ResponseEmptyChatMessage) event;

        assertEquals(response.getUsername(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), "testLobby");
        assertEquals(response.getMessage(), "null");

    }

    @Test
    @DisplayName("Sends multiple Messages")
    void onSendMultipleMessageTest() {
        RequestChatMessage message0 = new RequestChatMessage("Test0", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message0);
        RequestChatMessage message1 = new RequestChatMessage("catanprofi", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message1);
        RequestChatMessage message2 = new RequestChatMessage("captain", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        chatService.sendMessage(message2);

        assertTrue(event instanceof RequestChatMessage);

        RequestChatMessage request = (RequestChatMessage) event;

        assertEquals(request.getUsername(), defaultUser.getUsername());
        assertFalse(request.getTime().isNaN());
        assertEquals(request.getChat(), "testLobby");
        assertEquals(request.getMessage(), "Test0");
    }
}
