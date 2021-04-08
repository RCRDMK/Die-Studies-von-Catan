package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * RequestChatMessage class
 * <p>
 * Contains message, chat, username, time and their Getters
 *
 * @author René Meyer
 * @see AbstractRequestMessage
 * @since 2020-11-30
 */
public class RequestChatMessage extends AbstractRequestMessage {
    private final String message;
    private final String chat;
    private final String username;
    private final double time;

    /**
     * Constructor
     * @author René Meyer
     * @since 2020-11-30
     */
    public RequestChatMessage(String message, String chat, String username, double time) {
        this.message = message;
        this.time = time;
        this.chat = chat;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public String getChat() {
        return chat;
    }

    public String getUsername() {
        return username;
    }

    public Double getTime() {
        return time;
    }
}
