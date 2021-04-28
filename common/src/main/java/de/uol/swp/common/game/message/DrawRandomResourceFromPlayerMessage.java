package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class DrawRandomResourceFromPlayerMessage extends AbstractGameMessage {
    private String userName;

    public DrawRandomResourceFromPlayerMessage(String name, UserDTO user, String userName) {
        super(name, user);
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
