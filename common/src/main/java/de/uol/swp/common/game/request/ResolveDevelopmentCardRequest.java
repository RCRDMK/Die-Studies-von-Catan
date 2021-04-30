package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

public class ResolveDevelopmentCardRequest extends AbstractGameRequest {
    final private String devCard;
    final private String resource1;
    final private String resource2;
    final private UUID street1;
    final private UUID street2;

    // Constructor for Monopoly DevelopmentCard
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName, String resource) {
        super(gameName, user);
        this.devCard = devCard;
        this.resource1 = resource;
        this.resource2 = "";
        this.street1 = null;
        this.street2 = null;
    }

    // Constructor for Road Building DevelopmentCard
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName, UUID street1, UUID street2) {
        super(gameName, user);
        this.devCard = devCard;
        this.resource1 = "";
        this.resource2 = "";
        this.street1 = street1;
        this.street2 = street2;
    }

    // Constructor for Year of Plenty DevelopmentCard
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName, String resource1, String resource2) {
        super(gameName, user);
        this.devCard = devCard;
        this.resource1 = resource1;
        this.resource2 = resource2;
        this.street1 = null;
        this.street2 = null;
    }

    public String getDevCard() {
        return devCard;
    }

    public String getResource1() {
        return resource1;
    }

    public String getResource2() {
        return resource2;
    }

    public UUID getStreet1() {
        return street1;
    }

    public UUID getStreet2() {
        return street2;
    }
}
