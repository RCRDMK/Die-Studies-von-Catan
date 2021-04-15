package de.uol.swp.common.game.message;

import java.util.UUID;

public class SuccessfulConstructionMessage extends AbstractGameMessage {

    //Fields

    int playerIndex;
    UUID uuid;
    String typeOfNode;

    //Constructor

    public SuccessfulConstructionMessage(int playerIndex, UUID uuid, String typeOfNode) {
        this.playerIndex = playerIndex;
        this.uuid = uuid;
        this.typeOfNode = typeOfNode;
    }

    //Getter Setter


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
