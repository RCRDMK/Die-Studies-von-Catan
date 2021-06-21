package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message sent by the server when there was a trade
 * <p>
 *
 * @author Philip Nitsche
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @see de.uol.swp.common.user.User
 * @since 2021-06-21
 */

public class DrawRandomResourceFromPlayerMessage extends AbstractGameMessage {

    private User userToGetTheCard;
    private User userToLossTheCard;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public DrawRandomResourceFromPlayerMessage() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param userToGetTheCard  User to get the card
     * @param userToLossTheCard User to Loss the card
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public DrawRandomResourceFromPlayerMessage(User userToGetTheCard, User userToLossTheCard) {
        this.userToGetTheCard = userToGetTheCard;
        this.userToLossTheCard = userToLossTheCard;
    }

    public User getUserToGetTheCard() {
        return userToGetTheCard;
    }

    public User getUserToLossTheCard() {
        return userToLossTheCard;
    }
}