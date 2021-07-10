package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Delivers the information of a not successful construction and by which player it was conducted.
 *
 * @author Philip Nitsche
 * @since 2021-06-21
 */
public class NotSuccessfulConstructionMessage extends AbstractGameMessage {

    User player;
    String typeOfNode;

    /**
     * Constructor
     *
     * @param player
     * @param typeOfNode
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public NotSuccessfulConstructionMessage(User player, String typeOfNode) {
        this.player = player;
        this.typeOfNode = typeOfNode;
    }

    /**
     * getter for the player
     *
     * @return the player
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public User getPlayer() {
        return player;
    }

    /**
     * getter for the type of node
     *
     * @return the typeOfNode
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public String getTypeOfNode() {
        return typeOfNode;
    }
}