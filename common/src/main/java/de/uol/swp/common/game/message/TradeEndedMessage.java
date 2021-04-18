package de.uol.swp.common.game.message;

public class TradeEndedMessage {

    String tradeCode;
    public TradeEndedMessage(String tradeCode){
        this.tradeCode = tradeCode;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
