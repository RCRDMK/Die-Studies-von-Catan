package de.uol.swp.common.game.response;

import de.uol.swp.common.game.message.ResolveDevelopmentCardMessage;
import de.uol.swp.common.message.AbstractResponseMessage;

public class ResolveDevelopmentCardNotSuccessful extends AbstractResponseMessage {
    final private String devCard;
    final private String userName;
    final private String gameName;

    public ResolveDevelopmentCardNotSuccessful(String devCard, String userName, String gameName) {
        this.devCard = devCard;
        this.userName = userName;
        this.gameName = gameName;
    }

    public String getDevCard() {
        return devCard;
    }

    public String getUserName() {
        return userName;
    }

    public String getGameName() {
        return gameName;
    }
}
