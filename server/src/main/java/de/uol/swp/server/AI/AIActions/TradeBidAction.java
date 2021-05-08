package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;

import java.util.ArrayList;

/**
 * The AIAction used to participate in an ongoing trade through bidding
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class TradeBidAction extends AIAction {

    private final ArrayList<TradeItem> bidList;
    private final String tradeCode;

    /**
     * Constructor
     *
     * @param user      the user who wants to bid
     * @param gameName  the name in which the bid is to be done
     * @param bidList   the list of bids to be placed
     * @param tradeCode the String identifying the Trade
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public TradeBidAction(User user, String gameName, ArrayList<TradeItem> bidList, String tradeCode) {
        super("TradeStart", user, gameName);
        this.bidList = bidList;
        this.tradeCode = tradeCode;
    }

    /**
     * Getter for the bidList
     *
     * @return the List of items to be bid
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public ArrayList<TradeItem> getBidList() {
        return bidList;
    }

    /**
     * Getter for the tradeCode
     *
     * @return the String tradeCode identifying the Trade
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getTradeCode() {
        return tradeCode;
    }
}
