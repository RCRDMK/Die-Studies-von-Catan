package de.uol.swp.common.game.message;

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
     * @since 2021-04-25
     */
    public TradeEndedMessage(String tradeCode) {
        this.tradeCode = tradeCode;
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
