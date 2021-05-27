package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * used to close the tradeWindow
 */
public class TradeEndedMessage extends AbstractGameMessage {

    String tradeCode;

    /**
     * constructor
     *
     * @param tradeCode String
     * @author Alexander Losse, Ricardo Mok
     * @enhenced by Sergej Tulnev
     * @since 2021-04-25
     * @since 2021-05-27
     */
    public TradeEndedMessage(String tradeCode, UserDTO user) {
        this.tradeCode = tradeCode;
        this.user = user;
    }

    /**
     * getter for the tradeCode
     *
     * @return String tradeCode
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    public String getTradeCode() {
        return tradeCode;
    }
}
