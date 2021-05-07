package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public class PlayDevelopmentCardMonopolyAction extends PlayDevelopmentCardAction{

    private final String resource;
    public PlayDevelopmentCardMonopolyAction(User user, String gameName, String devCard, String resource) {
        super(user, gameName, devCard);
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
