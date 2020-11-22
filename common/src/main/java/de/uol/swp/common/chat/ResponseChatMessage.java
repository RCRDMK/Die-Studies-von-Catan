package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractServerMessage;

public class ResponseChatMessage extends AbstractServerMessage {
    private final String message;
    private final int chat;
    private final String user;
    private final double time;

    public ResponseChatMessage(String message, int chat, String user, double time) {
        this.message = message;
        this.chat = chat;
        this.user = user;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public int getChat() {
        return chat;
    }

    public String getUser() {
        return user;
    }

    public double getTime() {
        return time;
    }
}
