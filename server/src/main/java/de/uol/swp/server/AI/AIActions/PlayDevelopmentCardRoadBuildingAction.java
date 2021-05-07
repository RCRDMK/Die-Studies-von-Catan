package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

import java.util.UUID;

public class PlayDevelopmentCardRoadBuildingAction extends PlayDevelopmentCardAction{

    private final UUID street1;
    private final UUID street2;
    public PlayDevelopmentCardRoadBuildingAction(User user, String gameName, String devCard, UUID street1, UUID street2) {
        super(user, gameName, devCard);
        this.street1 = street1;
        this.street2 = street2;
    }

    public UUID getStreet1() {
        return street1;
    }

    public UUID getStreet2() {
        return street2;
    }
}
