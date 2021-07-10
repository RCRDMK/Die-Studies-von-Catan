package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.cheat.CheatService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Service that handles the chat
 * <p>
 *
 * @author René Meyer, Anton Nikiforov, Sergej Tulnev
 * @see de.uol.swp.server.AbstractService
 * @since 2020-11-22
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class ChatService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(UserService.class);
    private final CheatService cheatService;


    /**
     * ChatService Constructor
     * <p>
     *
     * @param bus          the EvenBus used throughout the server
     * @param cheatService cheatService to check the ChatMessages for cheats
     * @author René Meyer, Sergej Tulnev
     * @see EventBus
     * @see CheatService
     * @since 2019-10-08
     */
    @Inject
    public ChatService(CheatService cheatService, EventBus bus) {
        super(bus);
        this.cheatService = cheatService;
    }

    /**
     * Handles RequestChatMessages detected on the EventBus
     * <p>
     * If a RequestChatMessage is detected on the EventBus, this method is called.
     * It will store the received Message in the chatList HashMap and post a ResponseChatMessage on the EventBus if the chatMessage isn't a cheat
     * If the chatMessage is a cheat, it calls the cheatService.parseExecuteCheat function and passes the
     * RequestChatMessage as argument
     *
     * @param message The RequestChatMessage found on the EventBus
     * @author René Meyer, Sergej Tulnev
     * @see RequestChatMessage
     * @see CheatService
     * @since 2020-11-30
     */
    @Subscribe
    public void onRequestChatMessage(RequestChatMessage message) {
        //  Proceed when message isn't a cheat
        if (!cheatService.isCheat(message)) {
            // Store Message in chatList
            LOG.debug("Got new chat message from user: " + message.getUsername() + " with content: '" + message
                    .getMessage() + "' and added it to the messageList");
            ResponseChatMessage msg = new ResponseChatMessage(message.getMessage(), message.getChat(),
                    message.getUsername(), message.getTime());
            post(msg);
            LOG.debug("Posted ResponseChatMessage on eventBus");
        } else {
            // Parse & Execute Cheat codes
            LOG.debug("Cheat message " + message.getMessage() + " sent by " + message.getUsername());
            cheatService.parseExecuteCheat(message);
        }
    }

}
