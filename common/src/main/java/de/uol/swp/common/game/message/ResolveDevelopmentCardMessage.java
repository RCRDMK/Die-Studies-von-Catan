package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class ResolveDevelopmentCardMessage extends AbstractGameMessage{
    final private String devCard;

    public ResolveDevelopmentCardMessage(String devCard, UserDTO user, String gameName) {
        super(gameName,(UserDTO) user.getWithoutPassword());
        this.devCard = devCard;
    }

    public String getDevCard() {
        return devCard;
    }
}
