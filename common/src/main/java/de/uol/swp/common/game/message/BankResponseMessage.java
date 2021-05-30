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

    private ArrayList<ArrayList<TradeItem>> bankOffer;
    private boolean ressourceInBank;
    private String tradeCode;

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
     * @param ressourceInBank if bank can sale
     *
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankResponseMessage(UserDTO user, String tradeCode, ArrayList<ArrayList<TradeItem>> bankOffer, boolean ressourceInBank) {
        this.user = user;
        this.tradeCode = tradeCode;
        this.bankOffer = bankOffer;
        this.ressourceInBank = ressourceInBank;
    }

    public ArrayList<ArrayList<TradeItem>> getBankOffer() {
        return bankOffer;
    }

    public boolean isRessourceInBank() {
        return ressourceInBank;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
