package de.uol.swp.common.game.message;

import java.util.UUID;

public class SuccessfulMovedRobberMessage extends AbstractGameMessage {
    private final UUID newField;

    public SuccessfulMovedRobberMessage(UUID newField) {
        this.newField = newField;
    }

    public UUID getNewField() {
        return newField;
    }
}
