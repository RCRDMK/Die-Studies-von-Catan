package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;

import java.util.ArrayList;
import java.util.UUID;

public class TradeStartAction extends AIAction{

    private final ArrayList<TradeItem> wishList;
    private final ArrayList<TradeItem> offerList;
    private final String tradeCode ;
    public TradeStartAction(User user, String gameName, ArrayList<TradeItem> wishList, ArrayList<TradeItem> offerList){
        super("TradeStart", user, gameName);
        this.offerList = offerList;
        this.wishList = wishList;
        this.tradeCode = UUID.randomUUID().toString().trim().substring(0, 7);
    }

    public ArrayList<TradeItem> getWishList() {
        return wishList;
    }

    public ArrayList<TradeItem> getOfferList() {
        return offerList;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
