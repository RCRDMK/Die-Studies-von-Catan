package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public class TradeOfferAcceptAction extends AIAction{

    private final String tradeCode;
    private final Boolean tradeAccepted;
    private final User acceptedBidder;
    public TradeOfferAcceptAction(User user, String gameName, String tradeCode, Boolean tradeAccepted, User acceptedBidder) {
        super("TradeOfferAccept", user, gameName);
        this.tradeCode = tradeCode;
        this.tradeAccepted = tradeAccepted;
        this.acceptedBidder = acceptedBidder;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public Boolean getTradeAccepted() {
        return tradeAccepted;
    }

    public User getAcceptedBidder() {
        return acceptedBidder;
    }
}
