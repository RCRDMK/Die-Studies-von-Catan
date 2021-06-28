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

    public NotSuccessfulConstructionMessage(User player, String typeOfNode) {
        this.player = player;
        this.typeOfNode = typeOfNode;
    }

    public User getPlayer() {
        return player;
    }

    public String getTypeOfNode() {
        return typeOfNode;
    }
}