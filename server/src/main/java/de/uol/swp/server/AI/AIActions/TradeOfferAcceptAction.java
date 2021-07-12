package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * The AIAction used to accept offers of other players
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class TradeOfferAcceptAction extends AIAction {

    private final String tradeCode;
    private final Boolean tradeAccepted;
    private final User acceptedBidder;

    /**
     * Constructor
     *
     * @param user           the user who wants to accept the trade offers
     * @param gameName       the name of the game in which the trade offer is to be accepted
     * @param tradeCode      the String tradeCode identifying the trade
     * @param tradeAccepted  boolean value, false when none is accepted, true if one is accepted
     * @param acceptedBidder the User whose offer was accepted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public TradeOfferAcceptAction(User user, String gameName, String tradeCode, Boolean tradeAccepted,
                                  User acceptedBidder) {
        super("TradeOfferAccept", user, gameName);
        this.tradeCode = tradeCode;
        this.tradeAccepted = tradeAccepted;
        this.acceptedBidder = acceptedBidder;
    }

    /**
     * Getter for the tradeCode
     *
     * @return the String identifying the the Trade
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * Getter for the tradeAccepted boolean
     *
     * @return false when none was accepted, true if one was accepted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public Boolean getTradeAccepted() {
        return tradeAccepted;
    }

    /**
     * Getter for the accepted Bidder
     *
     * @return the User whose bid was accepted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public User getAcceptedBidder() {
        return acceptedBidder;
    }
}
