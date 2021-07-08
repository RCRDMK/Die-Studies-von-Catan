package de.uol.swp.common.game.request;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * The inform the server about a Bank Buy Request
 *
 * @author Anton Nikiforov
 */
public class BankBuyRequest extends AbstractGameRequest {

    private  String tradeCode;
    private  String chosenCard;
    private  ArrayList<TradeItem> chosenOffer;

    /**
     * Default Constructor needs for serialization
     *
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankBuyRequest() {

    }

    /**
     * The constructor
     *
     * @param name          the game name
     * @param user          the user who wanna buy
     * @param tradeCode     the tradeCode
     * @param chosenCard    the name form the card he wanna buy
     * @param chosenOffer    the chosenOffer he chose
     *
     * @author Anton Nikiforov
     * @since 2021-05-31
     */
    public BankBuyRequest(String name, UserDTO user, String tradeCode, String chosenCard, ArrayList<TradeItem> chosenOffer) {
        this.name = name;
        this.user = (UserDTO) user.getWithoutPassword();
        this.tradeCode = tradeCode;
        this.chosenCard = chosenCard;
        this.chosenOffer = chosenOffer;
    }

    /**
     * getter method to get the tradeCode when trading with the bank
     *
     * @return tradeCode of the trade
     * @author Anton Nikiforov
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * getter method to get the chosen ressourceCard when trading with the bank
     *
     * @return chosen ressourceCard of the trade
     * @author Anton Nikiforov
     */
    public String getChosenCard() {
        return chosenCard;
    }

    /**
     * getter method to get the chosen offer when trading with the bank
     *
     * @return chosen offer of the trade
     * @author Anton Nikiforov
     */
    public ArrayList<TradeItem> getChosenOffer() {
        return chosenOffer;
    }
}