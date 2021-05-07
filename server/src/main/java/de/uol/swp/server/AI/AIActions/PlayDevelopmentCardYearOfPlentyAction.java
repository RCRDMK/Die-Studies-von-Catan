package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public class PlayDevelopmentCardYearOfPlentyAction extends PlayDevelopmentCardAction{

    private final String resource1;
    private final String resource2;
    public PlayDevelopmentCardYearOfPlentyAction(User user, String gameName, String devCard, String resource1, String resource2) {
        super(user, gameName, devCard);
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    public String getResource1() {
        return resource1;
    }

    public String getResource2() {
        return resource2;
    }
}
