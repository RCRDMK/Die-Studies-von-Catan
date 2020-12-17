package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractServerMessage;

public class ResponseEmptyChatMessage extends AbstractServerMessage {
    private final String message;
    private final String chat;
    private final String username;
    private final double time;

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