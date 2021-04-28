package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractServerMessage;

public class ResponseEmptyChatMessage extends AbstractServerMessage {
    private final String message;
    private final String chat;
    private final String username;
    private final double time;

    /**
     * Constructor
     *
     * @param message  Text the Message should contain
     * @param chat     Chat the Massage was send in
     * @param username Username form the User how tries to send the message
     * @param time     Time when the User tries to send the message
     */

    public ResponseEmptyChatMessage(String message, String chat, String username, double time) {
        this.message = message;
        this.chat = chat;
        this.username = username;
        this.time = time;
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