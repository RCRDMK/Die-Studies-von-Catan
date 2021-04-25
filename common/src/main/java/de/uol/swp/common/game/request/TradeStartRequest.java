package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

public class TradeStartRequest extends AbstractGameRequest {


        private String tradeCode;

        public TradeStartRequest(UserDTO user, String game, String tradeCode){
            this.user = new UserDTO(user.getUsername(),"","");
            this.name = game;
            this.tradeCode = tradeCode;
        }




        public String getTradeCode(){
            return tradeCode;
        }
    }

