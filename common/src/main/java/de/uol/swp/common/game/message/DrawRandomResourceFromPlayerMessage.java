package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message, that will be send to draw a random resource from a player.
 * <p>
 *
 * @author Marius Birk
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @see de.uol.swp.common.user.User
 * @since 2021-04-28
 */

public class DrawRandomResourceFromPlayerMessage extends AbstractGameMessage {

    private User userToGetTheCard;
    private User userToLossTheCard;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Marius Birk
     * @since 2021-04-28
     */
    public DrawRandomResourceFromPlayerMessage() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param userToGetTheCard  User to get the card
     * @param userToLossTheCard User to Loss the card
     * @author Marius Birk
     * @since 2021-04-28
     */
    public DrawRandomResourceFromPlayerMessage(User userToGetTheCard, User userToLossTheCard) {
        this.userToGetTheCard = userToGetTheCard;
        this.userToLossTheCard = userToLossTheCard;
    }

    /**
     * getter for the user that gets a card
     *
     * @return the user from that gets a card
     * @author Marius Birk
     * @since 2021-04-28
     */

    public User getUserToGetTheCard() {
        return userToGetTheCard;
    }

    /**
     * getter for the user from who a card will be drawn
     *
     * @return the user from who the card will be drawn
     * @author Marius Birk
     * @since 2021-04-28
     */
    public User getUserToLossTheCard() {
        return userToLossTheCard;
    }
}