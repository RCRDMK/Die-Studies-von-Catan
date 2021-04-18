package de.uol.swp.common.game.message;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * this message informs the potential bidders about the item for sell
 *
 * it is send to the users in game who are not the seller
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-11
 */
public class TradeOfferInformBiddersMessage extends AbstractGameMessage{


    private String tradeCode;
    private ArrayList<TradeItem> sellingItems;

    /**
     * constructor
     *
     * the user in this message is not the recipient but the user who started the trade
     *
     * @param seller UserDTO of the person who started the trade and wants to sell an item
     * @param gameName String the name of the game
     * @param tradeCode tradeCode String ID of the trade
     * @param sellingItems ArrayList<TradeItem> List of items the seller wants to sell
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeOfferInformBiddersMessage(UserDTO seller, String gameName, String tradeCode, ArrayList<TradeItem> sellingItems){
        this.user = seller;//new UserDTO(seller.getUsername(),"","");
        this.name = gameName;
        this.tradeCode = tradeCode;
        this.sellingItems = sellingItems;
    }


    /**
     * getter for String tradeCode
     *
     * @return String tradeCode
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * getter for ArrayList<TradeItem> sellingItems
     *
     * @return ArrayList<TradeItem> sellingItems
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public ArrayList<TradeItem> getSellingItems() {
        return sellingItems;
    }
}
