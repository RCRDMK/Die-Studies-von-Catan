package de.uol.swp.common.game.message;

import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

public class RobbersNewFieldMessage extends AbstractGameRequest {

    UUID newField;

    public RobbersNewFieldMessage(String name, UserDTO user, UUID newField) {
        super(name, user);
        this.newField = newField;
    }

    public UUID getNewField() {
        return newField;
    }
}
