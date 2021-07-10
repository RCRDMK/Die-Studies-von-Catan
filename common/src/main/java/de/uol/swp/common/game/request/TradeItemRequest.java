package de.uol.swp.common.game.request;

import java.util.ArrayList;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

/**
 * The request to inform the server about the items to be traded
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-11
 */
public class TradeItemRequest extends AbstractGameRequest {

    private final ArrayList<TradeItem> tradeItems;
    private final ArrayList<TradeItem> wishItems;
    private final String tradeCode;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-30
     */
    public TradeItemRequest() {
        tradeItems = null;
        wishItems = null;
        tradeCode = null;
    }

    /**
     * the constructor
     *
     * @param user       UserDTO of the user who wants to trade the items
     * @param gameName   String name of the game
     * @param tradeItems ArrayList<TradeItem> containing the items to be traded
     * @param tradeCode  String  ID of the trade
     * @param wishItems  the potential wishList of the seller
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeItemRequest(UserDTO user, String gameName, ArrayList<TradeItem> tradeItems, String tradeCode,
                            ArrayList<TradeItem> wishItems) {
        this.user = new UserDTO(user.getUsername(), "", "");
        this.name = gameName;
        this.tradeItems = fillEmptySpotsInList(tradeItems);
        this.tradeCode = tradeCode;
        this.wishItems = fillEmptySpotsInList(wishItems);
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
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public String getTradeCode() {
        return tradeCode;
    }


    /**
     * returns an ArrayList<TradeItem> where missing TradeItems are included
     * <p>
     * if a TradeItem is missing in the ArrayList<TradeItem>, the method adds an TradeItem with count = 0
     *
     * @param itemList ArrayList<TradeItem> to be updated
     * @return ArrayList<TradeItem>
     * @author Marc Hermes, Alexander Losse
     * @since 2021-05-22
     */
    private ArrayList<TradeItem> fillEmptySpotsInList(ArrayList<TradeItem> itemList) {
        boolean lumberExists = false;
        boolean oreExists = false;
        boolean woolExists = false;
        boolean grainExists = false;
        boolean brickExists = false;

        if (itemList.size() < 5) {
            for (TradeItem tradeItem : itemList) {
                String itemName = tradeItem.getName();
                switch (itemName) {
                    case "Lumber":
                        lumberExists = true;
                        break;
                    case "Ore":
                        oreExists = true;
                        break;
                    case "Wool":
                        woolExists = true;
                        break;
                    case "Grain":
                        grainExists = true;
                        break;
                    case "Brick":
                        brickExists = true;
                        break;
                }
            }

            if (!lumberExists) {
                itemList.add(new TradeItem("Lumber", 0));
            }
            if (!oreExists) {
                itemList.add(new TradeItem("Ore", 0));
            }
            if (!woolExists) {
                itemList.add(new TradeItem("Wool", 0));
            }
            if (!grainExists) {
                itemList.add(new TradeItem("Grain", 0));
            }
            if (!brickExists) {
                itemList.add(new TradeItem("Brick", 0));
            }
        }
        return itemList;
    }
}
