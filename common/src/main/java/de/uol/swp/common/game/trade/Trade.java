package de.uol.swp.common.game.trade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import de.uol.swp.common.user.UserDTO;


/**
 * The Trade class saves the information of one trade
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 */
public class Trade implements Serializable {
    //the seller
    private final UserDTO seller;
    //the bidders, also used to save the keys of the bids HashMap
    private final ArrayList<UserDTO> bidders;
    //the items to be sold
    private final ArrayList<TradeItem> sellingItems;
    //saves the bids(Bidders and their offers)
    private final HashMap<UserDTO, ArrayList<TradeItem>> bids;
    private final ArrayList<TradeItem> wishList;

    /**
     * constructor
     * <p>
     * creates a trade with a seller and his offer
     *
     * @param seller       UserDTO of the user who started the trade
     * @param sellingItems ArrayList<TradeItem> of the Items to be sold
     * @param wishList     the original wishList of this Trade
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public Trade(UserDTO seller, ArrayList<TradeItem> sellingItems, ArrayList<TradeItem> wishList) {
        this.seller = seller;
        this.sellingItems = sellingItems;
        bidders = new ArrayList<>();
        bids = new HashMap<>();
        this.wishList = wishList;
    }


    /**
     * adds a bidder to the trade
     * <p>
     * saves the bidder in an ArrayList<UserDTO>
     * saves the offered items in HashMap<UserDTO,ArrayList<TradeItem>> Key: UserDTO bidder, Value: ArrayList<TradeItem> bidItems
     *
     * @param bidder   UserDTO of the bidder
     * @param bidItems ArrayList<TradeItem> of the items offered by the bidder
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public void addBid(UserDTO bidder, ArrayList<TradeItem> bidItems) {
        bidders.add(bidder);
        bids.put(bidder, bidItems);
    }


    /**
     * getter for the seller
     *
     * @return UserDTO seller
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
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public ArrayList<TradeItem> getSellingItems() {
        return sellingItems;
    }


    /**
     * getter for the bids
     *
     * @return HashMap<UserDTO, ArrayList < TradeItem>> bids
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-12
     */
    public HashMap<UserDTO, ArrayList<TradeItem>> getBids() {
        return bids;
    }

    /**
     * getter for the wishList
     *
     * @return the original wishList of the trade
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-21
     */
    public ArrayList<TradeItem> getWishList() {
        return wishList;
    }
}
