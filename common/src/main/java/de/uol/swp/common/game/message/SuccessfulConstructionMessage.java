package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * Delivers the information of a successful construction, where it happened and by wich player it was conducted.
 *
 * @author Pieter Vogt
 * @since 2021-04-15
 */
public class SuccessfulConstructionMessage extends AbstractGameMessage {

    int playerIndex;
    UUID uuid;
    String typeOfNode;

    public SuccessfulConstructionMessage(String gameName, User user, int playerIndex, UUID uuid, String typeOfNode) {
        super(gameName, (UserDTO) user);
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
