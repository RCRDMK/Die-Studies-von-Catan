package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

public class PlayDevelopmentCardRequest extends AbstractGameRequest{

    final private String devCard;

    public PlayDevelopmentCardRequest(String devCard, String gameName, UserDTO user) {
        super(gameName,user);
        this.devCard = devCard;
    }

    public String getDevCard() {
        return devCard;
    }

}
