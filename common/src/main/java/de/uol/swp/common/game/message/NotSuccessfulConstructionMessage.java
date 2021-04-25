package de.uol.swp.common.game.message;

import java.util.UUID;

/**
 * Delivers the information of a not successful construction, where it happened and by which player it was conducted.
 *
 * @author Kirstin Beyer
 * @since 2021-04-25
 */
public class NotSuccessfulConstructionMessage extends AbstractGameMessage {

    int playerIndex;
    UUID uuid;
    String typeOfNode;

    public NotSuccessfulConstructionMessage(int playerIndex, UUID uuid, String typeOfNode) {
        this.playerIndex = playerIndex;
        this.uuid = uuid;
        this.typeOfNode = typeOfNode;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getTypeOfNode() {
        return typeOfNode;
    }
}
