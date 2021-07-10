package de.uol.swp.common.game.message;

import java.util.UUID;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Delivers the information of a successful construction, where it happened and by which player it was conducted.
 *
 * @author Pieter Vogt
 * @since 2021-04-15
 */
public class SuccessfulConstructionMessage extends AbstractGameMessage {

    int playerIndex;
    UUID uuid;
    String typeOfNode;

    /**
     * Constructor for the SuccessfulConstructionMessage, that gets the game name, the user who builded, the playerIndex,
     * the uuid of the node and the type of Node as parameters.
     *
     * @param gameName    name of the game or the lobby
     * @param user        user, who builded the building
     * @param playerIndex index of the player
     * @param uuid        the id of the node where something was build
     * @param typeOfNode  the type of node
     * @author Pieter Vogt
     * @since 2021-04-15
     */
    public SuccessfulConstructionMessage(String gameName, User user, int playerIndex, UUID uuid, String typeOfNode) {
        super(gameName, (UserDTO) user);
        this.playerIndex = playerIndex;
        this.uuid = uuid;
        this.typeOfNode = typeOfNode;
    }

    /**
     * getter for the player index
     *
     * @return returns player index
     * @auhor Pieter Vogt
     * @since 2021-04-15
     */
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * getter for the id
     *
     * @return returns uuid
     * @auhor Pieter Vogt
     * @since 2021-04-15
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * getter for the type of node
     *
     * @return returns the typeOfNode
     * @auhor Pieter Vogt
     * @since 2021-04-15
     */
    public String getTypeOfNode() {
        return typeOfNode;
    }
}
