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
 *  @author René, Anton, Sergej
 *  @since 2020-11-22
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
     * posts the RequestChatMessage object on the EventBus. After testing the RequestChatMessage
     * it creates a ResponseChatMessage with the parameters from the RequestChatMessage and posts that
     * on the eventbus. Then it tests the ResponseChatMessage object.
     * So this test covers the full Client-Server Communication for a sent Chatmessage
     *
     * @since 2020-11-26
     */
    @Test
    void sendRequestChatMessageTest(){
        RequestChatMessage message = new RequestChatMessage("testMessage", 0, defaultUser.getUsername(), System.currentTimeMillis());
        bus.post(message);
    }

    @Subscribe
    void onRequestChatMessageTest(RequestChatMessage message) throws InterruptedException {
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertNotNull(message);

        ResponseChatMessage response = new ResponseChatMessage(message.getMessage(),message.getChat(),message.getUser(),message.getTime());
        assertEquals(response.getUser(), defaultUser.getUsername());
        assertFalse(response.getTime().isNaN());
        assertEquals(response.getChat(), 0);
        assertEquals(response.getMessage(), "testMessage");
        bus.post(response);
    }

    @Subscribe
    void onResponseChatMessageTest(ResponseChatMessage message) throws InterruptedException{
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertNotNull(message);

        assertEquals(message.getUser(), defaultUser.getUsername());
        assertFalse(message.getTime().isNaN());
        assertEquals(message.getChat(), 0);
        assertEquals(message.getMessage(), "testMessage");
    }
}
