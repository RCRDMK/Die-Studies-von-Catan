package de.uol.swp.common.game.message;

public class TradeEndedMessage extends AbstractGameMessage {

    String tradeCode;

    public TradeEndedMessage(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
