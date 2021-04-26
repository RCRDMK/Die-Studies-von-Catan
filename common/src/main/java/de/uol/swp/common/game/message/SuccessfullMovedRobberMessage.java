package de.uol.swp.common.game.message;

import java.util.UUID;

public class SuccessfullMovedRobberMessage extends AbstractGameMessage {
    private UUID newField;

    public SuccessfullMovedRobberMessage(UUID newField) {
        this.newField = newField;
    }

    public UUID getNewField() {
        return newField;
    }
}
