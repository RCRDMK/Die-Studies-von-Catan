package de.uol.swp.common.game.message;

/**
 * Message sent by the server when there was a trade
 * <p>
 *
 * @author Philip Nitsche
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @see de.uol.swp.common.user.User
 * @since 2021-06-21
 */

public class TradeEndedLogMessage extends AbstractGameMessage {

    private String tradeCode;
    private String winnerBidder;
    private Boolean success;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public TradeEndedLogMessage() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param success      trade was complete
     * @param tradeCode    Code of the trade
     * @param winnerBidder winner of the trade
     * @author Philip Nitsche
     * @since 2021-06-21
     */
    public TradeEndedLogMessage(String tradeCode, String winnerBidder, Boolean success) {
        this.tradeCode = tradeCode;
        this.winnerBidder = winnerBidder;
        this.success = success;
    }

    /**
     * When this method is called the trade code for the trade it identifies gets returned
     *
     * @return tradeCode of the trade
     * @author Philip Nitsche
     */
    public String getTradeCode() {
        return tradeCode;
    }

    /**
     * When this method is called it returns the bid which won the trade
     *
     * @return bid which was chosen by the seller
     * @author Philip Nitsche
     */
    public String getWinnerBidder() {
        return winnerBidder;
    }

    /**
     * When this method is called it returns if the trade was a success or not
     *
     * @return boolean value of the success of the trade
     * @author Philip Nitsche
     */
    public Boolean getSuccess() {
        return success;
    }

}