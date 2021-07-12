package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * ResponseChatMessage class
 * <p>
 * Contains message, chat, username, time and their Getters
 *
 * @author René Meyer
 * @see AbstractServerMessage
 * @since 2020-11-30
 */
public class ResponseEmptyChatMessage extends AbstractServerMessage {
    private final String message;
    private final String chat;
    private final String username;
    private final double time;

    /**
     * Constructor
     *
     * @param message  Text that the Message should contain
     * @param chat     Chat where the massage was send in
     * @param username Username form the User how tries to send the message
     * @param time     Time when the User tries to send the message
     * @author René Meyer
     * @since 2020-11-30
     */
    public ResponseEmptyChatMessage(String message, String chat, String username, double time) {
        this.message = message;
        this.chat = chat;
        this.username = username;
        this.time = time;
    }

    /**
     * Getter for the message
     *
     * @return the String message Text that the message contains
     * @author René Meyer
     * @since 2020-11-30
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the chat
     *
     * @return the String chat Chat where the massage was send in
     * @author René Meyer
     * @since 2020-11-30
     */
    public String getChat() {
        return chat;
    }

    /**
     * Getter for the username
     *
     * @return the String username Username form the user how tries to send the message
     * @author René Meyer
     * @since 2020-11-30
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the time
     *
     * @return the Double time Time when the user tries to send the message
     * @author René Meyer
     * @since 2020-11-30
     */
    public Double getTime() {
        return time;
    }
}