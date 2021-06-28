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

    public String getTradeCode() {
        return tradeCode;
    }

    public String getWinnerBidder() {
        return winnerBidder;
    }

    public Boolean getSuccess() {
        return success;
    }

}