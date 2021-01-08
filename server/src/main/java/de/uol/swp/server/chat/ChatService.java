package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.LinkedList;

/**
 * Mapping vom event bus calls to chat service calls
 *
 * @see de.uol.swp.server.AbstractService
 * @author René, Anton, Sergej
 * @since 2020-11-22
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class ChatService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(UserService.class);
    private final LinkedList<RequestChatMessage> messageList = new LinkedList();
    /**
     * Constructor
     *
     * @param bus the EvenBus used throughout the server
     * @since 2019-10-08
     */
    @Inject
    public ChatService(EventBus bus) {
        super(bus);
    }

    /**
     * Handles RequestChatMessages found on the EventBus
     * If a RequestChatMessage is detected on the EventBus, this method is called.
     * It will store the received Message in the chatList HashMap and post a ResponseChatMessage on the EventBus
     *
     * @param message The RequestChatMessage found on the EventBus
     */
    @Subscribe
    private void onRequestChatMessage(RequestChatMessage message){
        // Store Message in chatList
        this.messageList.add(message);
        LOG.debug("Got new chat message from user: " + message.getUsername() + " with content: '" + message.getMessage() +"' and added it to the messageList");
        ResponseChatMessage msg = new ResponseChatMessage(message.getMessage(), message.getChat(), message.getUsername(), message.getTime());
        post(msg);
        LOG.debug("Posted ResponseChatMessage on eventBus");
    }
}
