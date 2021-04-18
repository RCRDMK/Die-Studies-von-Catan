package de.uol.swp.common.game.message;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * message informing the users that the trade is finished and its result
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-11
 */
public class TradeSuccessfulMessage extends AbstractGameMessage{

    private ArrayList<TradeItem> soldItems;
    private boolean tradeSuccessful;
    private UserDTO bidder;
    private ArrayList<TradeItem> bidItems;
    private String tradeCode;

    /**
     * the constructor
     *
     * boolean tradeSuccessful is set to false. This changes if a successful bidder is added.
     *
     * @param seller UserDTO of the seller(user who initiated the trade)
     * @param gameName String the name of the game
     * @param soldItems ArrayList<TradeItem> list of items sold by the seller
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeSuccessfulMessage(UserDTO seller, String gameName, ArrayList<TradeItem> soldItems, String tradeCode){
        this.user = seller;//new UserDTO(seller.getUsername(),"","");
        this.name = gameName;
        this.soldItems = soldItems;
        tradeSuccessful = false;
        this.tradeCode = tradeCode;
    }

    /**
     * adds the successful bidder to the message
     *
     * boolean tradeSuccessful is set to true
     *
     * @param bidder UserDTO of the successful bidder
     * @param bidItems ArrayList<TradeItem> list of items offered by the bidder
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public void addSuccessfulBidder(UserDTO bidder, ArrayList<TradeItem> bidItems){
        this.bidder = new UserDTO(bidder.getUsername(),"","");
        this.bidItems = bidItems;
        tradeSuccessful = true;
    }

    /**
     * getter for ArrayList<TradeItem> soldItems
     *
     * returns the items sold by the seller
     *
     * @return ArrayList<TradeItem> soldItems
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public ArrayList<TradeItem> getSoldItems() {
        return soldItems;
    }

    /**
     * getter for boolean tradeSuccessful
     *
     * @return boolean tradeSuccessful
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public boolean isTradeSuccessful() {
        return tradeSuccessful;
    }

    /**
     * getter for UserDTO bidder
     *
     * returns the successful bidder
     *
     * @return boolean tradeSuccessful
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public UserDTO getBidder() {
        return bidder;
    }

    /**
     * getter for ArrayList<TradeItem> bidItems
     *
     * returns the items the successful bidder offered
     *
     * @return ArrayList<TradeItem> bidItems
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public ArrayList<TradeItem> getBidItems() {
        return bidItems;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
