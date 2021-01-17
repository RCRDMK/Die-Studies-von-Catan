package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

public class RetrieveAllLobbiesForSpecificUserRequest extends AbstractRequestMessage {

    private User user;
    public RetrieveAllLobbiesForSpecificUserRequest(User user){
        this.user = user;
    }
    public User getUser() {
        return user;
    }
}
