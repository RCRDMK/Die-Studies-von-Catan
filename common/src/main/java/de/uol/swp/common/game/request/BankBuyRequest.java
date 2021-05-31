package de.uol.swp.common.game.request;

import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * The inform the server about a Bank Buy Request
 *
 */
public class BankBuyRequest extends AbstractGameRequest {

    private final String tradeCode;
    private final String chosenCard;
    private final ArrayList<TradeItem> chosenOffer;

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

    public String getTradeCode() {
        return tradeCode;
    }

    public String getChosenCard() {
        return chosenCard;
    }

    public ArrayList<TradeItem> getChosenOffer() {
        return chosenOffer;
    }
}