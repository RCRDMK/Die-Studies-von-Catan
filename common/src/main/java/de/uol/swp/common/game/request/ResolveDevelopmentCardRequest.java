package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * The request sent from the client when he wants to resolve a certain DevelopmentCard
 *
 * @author Marc Hermes
 * @since 2021-05-03
 */
public class ResolveDevelopmentCardRequest extends AbstractGameRequest {
    final private String devCard;
    final private String resource1;
    final private String resource2;
    final private UUID street1;
    final private UUID street2;

    // TODO: neue klasse? abstrakte Klasse ResolveDevelopmentCardRequest

    /**
     * Constructor used for the resolution of the Monopoly DevelopmentCard
     *
     * @param devCard  the name of the DevelopmentCard, should be "Monopoly"
     * @param user     the user who wants to resolve the card
     * @param gameName the name of the game in which the card is to be resolved
     * @param resource the name of the resource used for the resolution of the Monopoly card
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName, String resource) {
        super(gameName, user);
        this.devCard = devCard;
        this.resource1 = resource;
        this.resource2 = "";
        this.street1 = null;
        this.street2 = null;
    }

    // TODO: neue klasse? abstrakte Klasse ResolveDevelopmentCardRequest

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
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName, UUID street1, UUID street2) {
        super(gameName, user);
        this.devCard = devCard;
        this.resource1 = "";
        this.resource2 = "";
        this.street1 = street1;
        this.street2 = street2;
    }


    // TODO: neue klasse? abstrakte Klasse ResolveDevelopmentCardRequest

    /**
     * Constructor used for the resolution of the Year of Plenty DevelopmentCard
     *
     * @param devCard   the name of the DevelopmentCard, should be "Year of Plenty"
     * @param user      the user who wants to resolve the card
     * @param gameName  the name of the game in which the card is to be resolved
     * @param resource1 the name of the first resource for Year of Plenty
     * @param resource2 the name of the second resource for Year of Plenty
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName, String resource1, String resource2) {
        super(gameName, user);
        this.devCard = devCard;
        this.resource1 = resource1;
        this.resource2 = resource2;
        this.street1 = null;
        this.street2 = null;
    }

    /**
     * Getter for the name of the DevelopmentCard
     *
     * @return the String name of the DevelopmentCard
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public String getDevCard() {
        return devCard;
    }

    /**
     * Getter for the name of the first resource
     *
     * @return the String name of the first resource
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public String getResource1() {
        return resource1;
    }

    /**
     * Getter for the name of the second resource
     *
     * @return the String name of the second resource
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public String getResource2() {
        return resource2;
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
