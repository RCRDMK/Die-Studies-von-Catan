package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;

import java.util.ArrayList;

public class TradeBidAction extends AIAction{

    private final ArrayList<TradeItem> bidList;
    private final ArrayList<TradeItem> sellerWishList = new ArrayList<>();

    private final String tradeCode ;
    public TradeBidAction(User user, String gameName, ArrayList<TradeItem> bidList, String tradeCode){
        super("TradeStart", user, gameName);
        this.bidList = bidList;
        this.tradeCode = tradeCode;
    }

    public ArrayList<TradeItem> getBidList() {
        return bidList;
    }

    public ArrayList<TradeItem> getSellerWishList() {
        return sellerWishList;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
