package de.uol.swp.client.main.Event;

import de.uol.swp.common.user.User;

public class ChangeToMainViewEvent {

    private User user;

    public ChangeToMainViewEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
