package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

public class TradeBidRequest extends AbstractGameRequest{

        String item;
        int itemCount;
        String tradeCode;
    public TradeBidRequest(UserDTO user, String gameName, String item, int itemCount, String tradeCode){
        this.user = user;
        this.name = gameName;
        this.item = item;
        this.itemCount = itemCount;
        this.tradeCode = tradeCode;
    }

    public String getItem() {
        return item;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getTradeCode() {
        return tradeCode;
    }
}
