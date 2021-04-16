package de.uol.swp.server.chat;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    final ChatService chatService = new ChatService(bus);
    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

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
     * posts the RequestChatMessage object on the EventBus. After testing the RequestChatMessage
     * it creates a ResponseChatMessage with the parameters from the RequestChatMessage and posts that
     * on the eventbus. Then it tests the ResponseChatMessage object.
     * So this test covers the full Client-Server Communication for a sent Chatmessage
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2020-12-10
     */
    @Test
    void sendRequestChatMessageTest() {
        RequestChatMessage message = new RequestChatMessage("testMessage", "testLobby", defaultUser.getUsername(),
                System.currentTimeMillis());
        bus.post(message);
    }

    @Subscribe
    void onRequestChatMessageTest(RequestChatMessage message) throws InterruptedException {
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertNotNull(message);

        ResponseChatMessage response = new ResponseChatMessage(message.getMessage(), message.getChat(),
                message.getUsername(), message.getTime());
        assertEquals(response.getMessage(), "testMessage");
        assertEquals(response.getChat(), "testLobby");
        assertEquals(response.getUsername(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        bus.post(response);
    }

    @Subscribe
    void onResponseChatMessageTest(ResponseChatMessage message) throws InterruptedException {
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertNotNull(message);

        assertEquals(message.getMessage(), "testMessage");
        assertEquals(message.getChat(), "testLobby");
        assertEquals(message.getUsername(), defaultUser.getUsername());
        assertFalse(message.getTime().isNaN());
    }
}
