package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * Request used for resolving of the DevelopmentCard Road Building
 *
 * @author Marc Hermes
 * @since 2021-05-04
 */
public class ResolveDevelopmentCardRoadBuildingRequest extends ResolveDevelopmentCardRequest {

    private final UUID street1;
    private final UUID street2;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public ResolveDevelopmentCardRoadBuildingRequest() {
        this.street1 = null;
        this.street2 = null;
    }

    /**
     * Constructor used for the resolution of the Road Building DevelopmentCard
     *
     * @param devCard  the name of the DevelopmentCard, should be "Road Building"
     * @param user     the user who wants to resolve the card
     * @param gameName the name of the game in which the card is to be resolved
     * @param street1  the UUID of the first street that is to be built
     * @param street2  the UUID of the second street that is to be built
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public ResolveDevelopmentCardRoadBuildingRequest(String devCard, UserDTO user, String gameName, UUID street1, UUID street2) {
        super(devCard, user, gameName);
        this.street1 = street1;
        this.street2 = street2;
    }

    /**
     * Getter for the UUID of the first street
     *
     * @return the UUID of the first street
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public UUID getStreet1() {
        return street1;
    }

    /**
     * Getter for the UUID of the second street
     *
     * @return the UUID of the second street
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public UUID getStreet2() {
        return street2;
    }
}
