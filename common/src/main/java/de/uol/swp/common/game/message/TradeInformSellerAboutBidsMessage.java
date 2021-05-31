package de.uol.swp.common.game.message;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Message that informs the seller about the bids
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-11
 */
public class TradeInformSellerAboutBidsMessage extends AbstractGameMessage {
    private final ArrayList<UserDTO> bidders;
    private final HashMap<UserDTO, ArrayList<TradeItem>> bids;
    private final String tradeCode;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-30
     */
    public TradeInformSellerAboutBidsMessage() {
        this.bidders = null;
        this.bids = null;
        this.tradeCode = null;
    }

    /**
     * the constructor
     *
     * @param seller    UserDTO seller
     * @param gameName  String the name of the game
     * @param tradeCode String ID of the trade
     * @param bidders   the list of UserDTO of the bidders
     * @param bids      HashMap<UserDTO, ArrayList<TradeItem>> - Key: UserDTO of bidder, Value: List with TradeItems
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public TradeInformSellerAboutBidsMessage(UserDTO seller, String gameName, String tradeCode, ArrayList<UserDTO> bidders, HashMap<UserDTO, ArrayList<TradeItem>> bids) {
        this.name = gameName;
        this.user = new UserDTO(seller.getUsername(), "", "");
        this.bidders = bidders;
        this.bids = bids;
        this.tradeCode = tradeCode;

    }

    /**
     * returns an ArrayList<UserDTO> containing the bidders
     *
     * @return ArrayList<UserDTO> bidders
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public ArrayList<UserDTO> getBidders() {
        return bidders;
    }

    /**
     * returns a HashMap<UserDTO,ArrayList<TradeItem>> containing bids
     * <p>
     * Key: UserDTO of bidder, Value: List with TradeItems
     *
     * @return HashMap<UserDTO, ArrayList < TradeItem>> bids
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-11
     */
    public HashMap<UserDTO, ArrayList<TradeItem>> getBids() {
        return bids;
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

}
