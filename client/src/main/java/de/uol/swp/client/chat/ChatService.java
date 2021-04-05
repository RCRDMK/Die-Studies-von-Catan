package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseEmptyChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * Service Class that manages all chats
 * <p>
 * The ChatService class contains the sendMessage(RequestChatMessage) method.
 *
 * @author René, Anton, Sergej
 * @see RequestChatMessage
 * @see EventBus
 * @see ResponseEmptyChatMessage
 * @since 2020-11-22
 */
@SuppressWarnings("UnstableApiUsage")
public class ChatService {

    private final EventBus eventBus;
    private double lastSendMessage;
    private static final Logger LOG = LogManager.getLogger(UserService.class);

    /**
     * Constructor
     * <p>
     * Constructor for the ChatService
     *
     * @param eventBus The EventBus set in ClientModule
     * @author René, Anton, Sergej
     * @see de.uol.swp.client.di.ClientModule
     * @see EventBus
     * @since 2020-11-22
     */
    @Inject
    public ChatService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Sends the message the user wants to send to the server
     * <p>
     * Posts a RequestChatMessage message on the EventBus and logs this action
     * Posts a ResponseEmptyChatMessage message if the message is Empty
     * checks whether a message has been sent in the last second and then does not send any more out.
     *
     * @param message RequestChatMessage that the user wants to send to the server
     * @author René, Sergej, Anton
     * @see RequestChatMessage
     * @see ResponseEmptyChatMessage
     * @since 2020-11-22
     */

    public void sendMessage(RequestChatMessage message) {
        try {
            if (message.getTime() - lastSendMessage >= 1000) {
                if (!message.getMessage().isEmpty() && message.getMessage() != null && !message.getMessage().isBlank()) {
                    eventBus.post(message);
                    LOG.debug("User: " + message.getUsername() + " sent message: '" + message.getMessage() + "' to server.");
                    lastSendMessage = message.getTime();

                } else {
                    ResponseEmptyChatMessage msg = new ResponseEmptyChatMessage(message.getMessage(), message.getChat(), message.getUsername(), message.getTime());
                    eventBus.post(msg);
                    LOG.debug("Posted ResponseEmptyChatMessage on eventBus" + message.getTime() + lastSendMessage);
                    lastSendMessage = message.getTime();
                }
            }
        } catch (NullPointerException e) {
            ResponseEmptyChatMessage msg = new ResponseEmptyChatMessage("null", message.getChat(), message.getUsername(), message.getTime());
            eventBus.post(msg);
            LOG.debug("Posted ResponseEmptyChatMessage on eventBus");
            lastSendMessage = message.getTime();
        }
    }
}
