package de.uol.swp.client.chat;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseEmptyChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author René, Anton, Sergej
 * @since 2020-11-26
 */

public class ChatServiceTest {
    final EventBus bus = new EventBus();
    final ChatService chatService = new ChatService(bus);
    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    /**
     * Handles DeadEvents detected on the EventBus
     *
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
     *
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
     *
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
     *
     * This test first creates a new RequestChatMessage object. It then
     * calls the chatService sendMessage function and passes the object as parameter
     * and waits for it to post an RequestChatMessage object on the EventBus.
     * The test fails if the chatService can't send a message within one second .
     * The test also fails if the dummy Username, Message or ChatID isn't equal to the request object properties.
     * It also fails if the request object getTime() function doesn't return a valid double.
     *
     * @throws InterruptedException
     * @since 2020-12-10
     */
    @Test
    void sendMessageTest() throws InterruptedException{
        RequestChatMessage message = new RequestChatMessage("testMessage", 0, defaultUser.getUsername(), System.currentTimeMillis());
        chatService.sendMessage(message);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RequestChatMessage);

        RequestChatMessage request = (RequestChatMessage) event;

        assertEquals(request.getUsername(), defaultUser.getUsername());
        assertFalse(request.getTime().isNaN());
        assertEquals(request.getChat(), "testLobby");
        assertEquals(request.getMessage(), "testMessage");
    }

    @Test
    void onSendMessageTest() throws InterruptedException {
        RequestChatMessage message = new RequestChatMessage("", 0, defaultUser.getUsername(), System.currentTimeMillis());
        chatService.sendMessage(message);

        lock.await(1000, TimeUnit.MILLISECONDS);


        assertTrue(event instanceof ResponseEmptyChatMessage);

        ResponseEmptyChatMessage response = (ResponseEmptyChatMessage) event;

        assertEquals(response.getUser(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), 0);
        assertEquals(response.getMessage(), "");

    }

    @Test
    void sendWhiteSpaceMessageTest() throws InterruptedException {
        RequestChatMessage message = new RequestChatMessage("    ", 0, defaultUser.getUsername(), System.currentTimeMillis());
        chatService.sendMessage(message);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ResponseEmptyChatMessage);

        ResponseEmptyChatMessage response = (ResponseEmptyChatMessage) event;

        assertEquals(response.getUser(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), 0);
        assertEquals(response.getMessage(), "    ");

    }

    @Test
    void sendNullMessageTest() throws InterruptedException {
        RequestChatMessage message = new RequestChatMessage(null, 0, defaultUser.getUsername(), System.currentTimeMillis());
        chatService.sendMessage(message);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ResponseEmptyChatMessage);

        ResponseEmptyChatMessage response = (ResponseEmptyChatMessage) event;

        assertEquals(response.getUser(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), 0);
        assertEquals(response.getMessage(), "null");

    }
}
