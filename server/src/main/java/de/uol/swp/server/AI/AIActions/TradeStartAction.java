package de.uol.swp.server.AI.AIActions;

import java.util.ArrayList;
import java.util.UUID;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;

/**
 * The AIAction used to start trades
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class TradeStartAction extends AIAction {

    private final ArrayList<TradeItem> wishList;
    private final ArrayList<TradeItem> offerList;
    private final String tradeCode;

    /**
     * Constructor
     *
     * @param user      the user who wants to start the trade
     * @param gameName  the name of the game in which the trade is to be started
     * @param wishList  the wish list of items the user wants to receive
     * @param offerList the list of items the user offers in exchange
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public TradeStartAction(User user, String gameName, ArrayList<TradeItem> wishList, ArrayList<TradeItem> offerList) {
        super("TradeStart", user, gameName);
        this.offerList = offerList;
        this.wishList = wishList;
        this.tradeCode = UUID.randomUUID().toString().trim().substring(0, 7);
    }

    /**
     * Getter for the wishList
     *
     * @return the List of items the user wants
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public ArrayList<TradeItem> getWishList() {
        return wishList;
    }

    /**
     * Getter for the offerList
     *
     * @return the List of items the user offers
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public ArrayList<TradeItem> getOfferList() {
        return offerList;
    }

    /**
     * Getter for the tradeCode
     *
     * @return the String tradeCode identifying the trade
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getTradeCode() {
        return tradeCode;
    }
}
