package de.uol.swp.common.game.request;

import java.util.UUID;

import de.uol.swp.common.user.UserDTO;

public class RobbersNewFieldRequest extends AbstractGameRequest {

    UUID newField;

    public RobbersNewFieldRequest(String name, UserDTO user, UUID newField) {
        super(name, user);
        this.newField = newField;
    }

    public UUID getNewField() {
        return newField;
    }
}
