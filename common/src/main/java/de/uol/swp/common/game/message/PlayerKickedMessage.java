package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;


public class PlayerKickedMessage extends AbstractGameMessage {
    private final boolean toBan;

    public PlayerKickedMessage() {
        toBan = false;
    }

    public PlayerKickedMessage(String gameName, UserDTO user, boolean toBan) {
        super(gameName, user);
        this.toBan = toBan;
    }

    public boolean isToBan() {
        return toBan;
    }
}
