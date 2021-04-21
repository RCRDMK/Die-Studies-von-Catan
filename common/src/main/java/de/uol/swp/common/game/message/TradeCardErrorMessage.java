package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class TradeCardErrorMessage extends  AbstractGameMessage{

    private String tradeCode;

    public TradeCardErrorMessage(UserDTO user, String gameName, String tradeCode){
        this.user = user; // new UserDTO(user.getUsername(),"","");
        this.name = gameName;
        this.tradeCode = tradeCode;
    }


    public String getTradeCode() {
        return tradeCode;
    }
}
