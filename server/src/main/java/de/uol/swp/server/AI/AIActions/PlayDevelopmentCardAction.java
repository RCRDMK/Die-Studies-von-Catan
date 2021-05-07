package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public abstract class PlayDevelopmentCardAction extends AIAction{

    private final String devCard;
    public PlayDevelopmentCardAction(User user, String gameName, String devCard) {
        super("PlayDevelopmentCard",user, gameName);
        this.devCard = devCard;
    }

    public String getDevCard() {
        return devCard;
    }


}
