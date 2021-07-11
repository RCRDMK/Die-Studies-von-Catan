package de.uol.swp.common.game.message;

import java.util.UUID;

/**
 * This class gets called when the Robber was sucessfully moved on the gamefield
 *
 * @author Marius Birk
 */
public class SuccessfulMovedRobberMessage extends AbstractGameMessage {
    private final UUID newField;

    public SuccessfulMovedRobberMessage(UUID newField) {
        this.newField = newField;
    }

    public UUID getNewField() {
        return newField;
    }
}
