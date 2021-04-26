package de.uol.swp.common.game.trade;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * The Trade class saves the information of one trade
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 */
public class Trade {
    //the seller
    private UserDTO seller;
    //the bidders, also used to save the keys of the bids HashMap
    private ArrayList<UserDTO> bidders;
    //the items to be sold
    private ArrayList<TradeItem> sellingItems;
    //saves the bids(Bidders and their offers)
    private HashMap<UserDTO,ArrayList<TradeItem>> bids;

    /**
     * constructor
     *
     * creates a trade with a seller and his offer
     *
     * @param seller UserDTO of the user who started the trade
     * @param sellingItems ArrayList<TradeItem> of the Items to be sold
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public Trade(UserDTO seller, ArrayList<TradeItem> sellingItems){
        this.seller = seller;
        this.sellingItems = sellingItems;
        bidders = new ArrayList();
        bids = new HashMap<UserDTO, ArrayList<TradeItem>>();
    }


    /**
     * adds a bidder to the trade
     *
     * saves the bidder in an ArrayList<UserDTO>
     * saves the offered items in HashMap<UserDTO,ArrayList<TradeItem>> Key: UserDTO bidder, Value: ArrayList<TradeItem> bidItems
     *
     * @param bidder UserDTO of the bidder
     * @param bidItems ArrayList<TradeItem> of the items offered by the bidder
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     *
     */
    public void addBid(UserDTO bidder, ArrayList<TradeItem> bidItems){
        bidders.add(bidder);
        bids.put(bidder, bidItems);
    }


    /**
     * getter for the seller
     *
     * @return UserDTO seller
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public UserDTO getSeller() {
        return seller;
    }

    /**
     * getter for the bidders
     *
     * @return ArrayList<UserDTO> bidders
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public ArrayList<UserDTO> getBidders() {
        return bidders;
    }


    /**
     * getter for the items to be sold
     *
     * @return ArrayList<TradeItem> sellingItems
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public ArrayList<TradeItem> getSellingItems() {
        return sellingItems;
    }


    /**
     * getter for the bids
     *
     * @return HashMap<UserDTO,ArrayList<TradeItem>> bids
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public HashMap<UserDTO, ArrayList<TradeItem>> getBids() {
        return bids;
    }
}
