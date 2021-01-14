package de.uol.swp.client.lobby.Event;

import de.uol.swp.common.user.User;

public class ChangeToLobbyViewEvent {

    private String name;
    private User user;
    public ChangeToLobbyViewEvent(User joinedLobbyUser, String lobbyToChangeTo) {
        this.user = joinedLobbyUser;
        this.name = lobbyToChangeTo;

    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }
}
