package de.uol.swp.common.game.message;

public class TradeNotSuccessfulMessage extends AbstractGameMessage {
    private String tradeCode;

    public TradeNotSuccessfulMessage(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getTradeCode() {
        return this.tradeCode;
    }
}

