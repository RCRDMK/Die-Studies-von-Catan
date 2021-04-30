package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

public class ResolveDevelopmentCardRequest extends AbstractGameRequest{
    final private String devCard;

    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName) {
        super(gameName, user);
        this.devCard = devCard;
    }

    public String getDevCard() {
        return devCard;
    }
}
