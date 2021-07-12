package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * The inform the server about a Bank Request
 *
 * @author Anton Nikiforov
 */
public class BankRequest extends AbstractGameRequest {

    private String tradeCode;
    private String cardName;

    /**
     * Default Constructor needs for serialization
     *
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankRequest() {

    }

    /**
     * The constructor
     *
     * @param name      the game name
     * @param user      the user who wanna buy
     * @param tradeCode the tradeCode
     * @param cardName  the name form the card he wanna buy
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    public BankRequest(String name, UserDTO user, String tradeCode, String cardName) {
        this.name = name;
        this.user = (UserDTO) user.getWithoutPassword();
        this.tradeCode = tradeCode;
        this.cardName = cardName;
    }

    /**
     * getter method to get the tradeCode when starting a trade with the bank
     *
     * @return tradeCode of the trade
     * @author Anton Nikiforov
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * getter method to get the cardName when starting a trade with the bank
     *
     * @return cardName
     * @author Anton Nikiforov
     */
    public String getCardName() {
        return cardName;
    }
}