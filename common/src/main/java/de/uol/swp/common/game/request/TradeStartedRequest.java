package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

public class TradeStartedRequest extends AbstractGameRequest {


        private String tradeCode;

        public TradeStartedRequest(UserDTO user, String game, String tradeCode){
            this.user = user;
            this.name = game;
            this.tradeCode = tradeCode;
        }




        public String getTradeCode(){
            return tradeCode;
        }
    }

