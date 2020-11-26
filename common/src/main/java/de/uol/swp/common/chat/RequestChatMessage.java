package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractRequestMessage;

public class RequestChatMessage extends AbstractRequestMessage {
    private final String message;
    private final int chat;
    private final String user;
    private final double time;

    public RequestChatMessage(String message, int chat, String username, double time){
        this.user= username;
        this.message = message;
        this.time = time;
        this.chat = chat;
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

    public Double getTime() {
        return time;
    }
}
