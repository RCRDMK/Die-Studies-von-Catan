package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class TradeOfferInformBiddersMessage extends AbstractGameMessage{

        UserDTO seller;
        String tradeCode;
        String item;
        int itemCount;
    public TradeOfferInformBiddersMessage(UserDTO seller, String gameName, String tradeCode, String item, int itemCount){
        this.seller = seller;
        this.name = gameName;
        this.tradeCode = tradeCode;
        this.item = item;
        this.itemCount = itemCount;
    }

}
