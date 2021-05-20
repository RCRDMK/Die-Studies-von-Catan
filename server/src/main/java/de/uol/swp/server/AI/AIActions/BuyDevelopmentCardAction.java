package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * The AIAction used for buying developmentCards
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public class BuyDevelopmentCardAction extends AIAction {

    /**
     * Constructor
     *
     * @param user     the User who wants to buy the card
     * @param gameName the name in which the card is to be bought
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public BuyDevelopmentCardAction(User user, String gameName) {
        super("BuyDevelopmentCard", user, gameName);
    }
}
