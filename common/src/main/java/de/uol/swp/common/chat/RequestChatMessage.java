package de.uol.swp.common.chat;

import de.uol.swp.common.message.AbstractMessage;

public class RequestChatMessage extends AbstractMessage {
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
