package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.chat.RequestChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.inject.Inject;

/**
 * Classes that manages chats
 *
 * @author René, Anton, Sergej
 * @since 2020-11-22
 *
 */
@SuppressWarnings("UnstableApiUsage")
public class ChatService  {

    private final EventBus eventBus;
    private static final Logger LOG = LogManager.getLogger(UserService.class);

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     * @since 2020-11-22
     */
    @Inject
    public ChatService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Posts a RequestChatMessage message on the EventBus and logs this action
     *
     * @param message Message the user wants to send to the server
     */
    public void sendMessage(RequestChatMessage message){
        eventBus.post(message);
        LOG.debug("User: " + message.getUser() + " sent message: '" + message.getMessage()+ "' to server.");
     }
}
