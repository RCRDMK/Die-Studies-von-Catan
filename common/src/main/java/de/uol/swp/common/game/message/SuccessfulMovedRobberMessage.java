package de.uol.swp.common.game.message;

import java.util.UUID;

/**
 * This class gets called when the Robber was successfully moved on the gamefield
 *
 * @author Marius Birk
 */
public class SuccessfulMovedRobberMessage extends AbstractGameMessage {
    private final UUID newField;

    /**
     * constructor
     * @param newField UUID
     */
    public SuccessfulMovedRobberMessage(UUID newField) {
        this.newField = newField;
    }

    /**
     * getter for the UUID newField
     *
     * @return UUID newField
     */
    public UUID getNewField() {
        return newField;
    }
}
