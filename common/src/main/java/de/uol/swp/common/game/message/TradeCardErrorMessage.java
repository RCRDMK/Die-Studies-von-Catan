package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class TradeCardErrorMessage extends  AbstractGameMessage{
    private String itemName;
    private int itemCount;
    private int itemRealCount;
 //TODO constructor
    public  TradeCardErrorMessage(){

    }
    public TradeCardErrorMessage(UserDTO user, String gameName, String tradeCode, String itemName, int itemCount, int itemRealCount){
        this.user = new UserDTO(user.getUsername(),"","");
        this.name = gameName;
        this.itemCount = itemCount;
        this.itemRealCount = itemRealCount;
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getItemRealCount() {
        return itemRealCount;
    }
}
