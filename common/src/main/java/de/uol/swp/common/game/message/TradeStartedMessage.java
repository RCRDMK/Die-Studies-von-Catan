package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class TradeStartedMessage {
    private UserDTO user;
    private String lobby;
    private String tradeCode;

    public TradeStartedMessage(UserDTO user, String lobby, String tradeCode){
        this.user = user;
        this.lobby = lobby;
        this.tradeCode = tradeCode;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getLobby() {
        return lobby;
    }

    public String getTradeCode(){
        return tradeCode;
    }
}
