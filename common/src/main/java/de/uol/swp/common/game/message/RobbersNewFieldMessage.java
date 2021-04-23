package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

public class RobbersNewFieldMessage extends AbstractGameMessage {

    UUID newField;

    public RobbersNewFieldMessage(String name, UserDTO user, UUID newField) {
        super(name, user);
        this.newField = newField;
    }

    public UUID getNewField() {
        return newField;
    }

    public void setNewField(UUID newField) {
        this.newField = newField;
    }
}
