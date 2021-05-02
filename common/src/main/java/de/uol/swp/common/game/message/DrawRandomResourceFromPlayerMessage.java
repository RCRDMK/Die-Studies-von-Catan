package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class DrawRandomResourceFromPlayerMessage extends AbstractGameMessage {
    private String chosenName;

    public DrawRandomResourceFromPlayerMessage(String name, UserDTO user, String chosenName) {
        super(name, user);
        this.chosenName = chosenName;
    }

    public String getChosenName() {
        return this.chosenName;
    }
}
