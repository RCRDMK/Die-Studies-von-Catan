package de.uol.swp.common.game.message;

import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.user.UserDTO;

public class TradeStartedMessage extends AbstractGameRequest {
    private UserDTO user;
    private String game;
    private String tradeCode;

    public TradeStartedMessage(UserDTO user, String game, String tradeCode){
        this.user = user;
        this.game = game;
        this.tradeCode = tradeCode;
    }

    public UserDTO getUser() {
        return user;
    }

    public String getGame() {
        return game;
    }

    public String getTradeCode(){
        return tradeCode;
    }
}
