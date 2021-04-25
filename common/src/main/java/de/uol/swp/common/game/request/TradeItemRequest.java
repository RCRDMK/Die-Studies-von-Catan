package de.uol.swp.common.game.request;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * The inform the server about the items to be traded
 *
 */
public class TradeItemRequest extends AbstractGameRequest{

    private ArrayList<TradeItem> tradeItems;
    private ArrayList<TradeItem> wishItems;
    private String tradeCode;

    /**
     * the constructor
     *
     * @param user UserDTO of the user who wants to trade the items
     * @param gameName String name of the game
     * @param tradeItems ArrayList<TradeItem> containing the items to be traded
     * @param tradeCode String  ID of the trade
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeItemRequest(UserDTO user, String gameName, ArrayList<TradeItem> tradeItems, String tradeCode, ArrayList<TradeItem> wishItems){
        this.user = new UserDTO(user.getUsername(),"","");
        this.name = gameName;
        this.tradeItems = tradeItems;
        this.tradeCode = tradeCode;
        this.wishItems = wishItems;
    }

    /**
     * returns the tradeItems
     *
     * @return ArrayList<TradeItem> tradeItems
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public ArrayList<TradeItem> getTradeItems() {
        return tradeItems;
    }

    /**
     * returns the wishItems
     *
     * @return ArrayList<TradeItem> wishItems
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public ArrayList<TradeItem> getWishItems() {
        return wishItems;
    }

    /**
     * return the trade code
     *
     * @return String tradeCode
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public String getTradeCode() {
        return tradeCode;
    }
}
