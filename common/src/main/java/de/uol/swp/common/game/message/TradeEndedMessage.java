package de.uol.swp.common.game.message;

/**
 * used to close the tradeWindow
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-25
 */
public class TradeEndedMessage extends AbstractGameMessage {

    String tradeCode;

    /**
     * constructor
     *
     * @param tradeCode String
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    public TradeEndedMessage(String name, String tradeCode) {
        this.name = name;
        this.tradeCode = tradeCode;
    }

    /**
     * default constructor
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    public TradeEndedMessage() {
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
