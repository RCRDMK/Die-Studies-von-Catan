package de.uol.swp.common.game.message;

import java.util.ArrayList;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

/**
 * Message sent by the server when bank response
 * <p>
 *
 * @author Anton Nikiforov
 * @since 2021-05-29
 */
public class BankResponseMessage extends AbstractGameMessage {

    private String tradeCode;
    private String cardName;
    private ArrayList<ArrayList<TradeItem>> bankOffer;

    /**
     * Default Constructor needs for serialization
     *
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankResponseMessage() {

    }

    /**
     * Constructor
     *
     * @param user      UserDTO
     * @param tradeCode String
     * @param cardName  the name form the card he wanna buy
     * @param bankOffer offer from bank
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankResponseMessage(UserDTO user, String tradeCode, String cardName,
                               ArrayList<ArrayList<TradeItem>> bankOffer) {
        this.user = user;
        this.tradeCode = tradeCode;
        this.cardName = cardName;
        this.bankOffer = bankOffer;

    }

    /**
     * getter for the tradeCode variable.
     *
     * @return the tradecode
     * @author Anton Nikiforov
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * getter for the cardName variable.
     *
     * @return the cardName
     * @author Anton Nikiforov
     */

    public String getCardName() {
        return cardName;
    }

    /**
     * getter for the bankOffer list.
     *
     * @return the bankOffer
     * @author Anton Nikiforov
     */
    public ArrayList<ArrayList<TradeItem>> getBankOffer() {
        return bankOffer;
    }
}
