package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class DropUserRequest extends AbstractRequestMessage {
    final private User user;

    public DropUserRequest(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
