package de.uol.swp.common.game.response;

import de.uol.swp.common.game.request.PlayDevelopmentCardRequest;
import de.uol.swp.common.message.AbstractResponseMessage;

public class PlayDevelopmentCardResponse extends AbstractResponseMessage {
    final private String devCard;
    final private boolean canPlayCard;
    final private String userName;
    final private String gameName;

    public PlayDevelopmentCardResponse(String devCard, boolean canPlayCard, String userName, String gameName) {
        this.devCard = devCard;
        this.canPlayCard = canPlayCard;
        this.userName = userName;
        this.gameName = gameName;
    }

    public String getDevCard() {
        return devCard;
    }

    public boolean isCanPlayCard() {
        return canPlayCard;
    }

    public String getUserName() {
        return userName;
    }

    public String getGameName() {
        return gameName;
    }
}
