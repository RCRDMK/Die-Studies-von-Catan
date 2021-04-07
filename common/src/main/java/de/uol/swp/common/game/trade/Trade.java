package de.uol.swp.common.game.trade;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;

public class Trade {
    private UserDTO seller;
    private ArrayList<UserDTO> bidders;
    private TradeItem sellingItem;
    private HashMap<UserDTO,TradeItem> bids;

    public Trade(UserDTO seller, String itemName, int itemCount){
        this.seller = seller;
        sellingItem = new TradeItem(itemName, itemCount);
        bidders = new ArrayList();
        bids = new HashMap<UserDTO,TradeItem>();
    }

    public void addBid(UserDTO bidder, String itemName, int itemCount){
        bidders.add(bidder);
        bids.put(bidder, new TradeItem(itemName, itemCount));
    }

    public UserDTO getSeller() {
        return seller;
    }

    public ArrayList<UserDTO> getBidders() {
        return bidders;
    }

    public TradeItem getSellingItem() {
        return sellingItem;
    }

    public HashMap<UserDTO, TradeItem> getBids() {
        return bids;
    }
}
