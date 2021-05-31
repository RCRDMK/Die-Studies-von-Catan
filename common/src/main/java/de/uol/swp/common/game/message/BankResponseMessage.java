package de.uol.swp.common.game.message;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * Message sent by the server when bank response
 * <p>
 *
 * @author Anton Nikiforov
 * @since 2021-05-29
 */
public class BankResponseMessage extends AbstractGameMessage {

    private String tradeCode;
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
     * @param user UserDTO
     * @param tradeCode String
     * @param bankOffer offer from bank
     *
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankResponseMessage(UserDTO user, String tradeCode, ArrayList<ArrayList<TradeItem>> bankOffer) {
        this.user = user;
        this.tradeCode = tradeCode;
        this.bankOffer = bankOffer;
    }

    public ArrayList<ArrayList<TradeItem>> getBankOffer() {
        return bankOffer;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
