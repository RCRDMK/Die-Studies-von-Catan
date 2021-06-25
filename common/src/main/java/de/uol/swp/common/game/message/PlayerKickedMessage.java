package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;


public class PlayerKickedMessage extends AbstractGameMessage {


    public PlayerKickedMessage(){

    }

    public PlayerKickedMessage(String gameName, UserDTO user){
        super(gameName, user);

    }
}
