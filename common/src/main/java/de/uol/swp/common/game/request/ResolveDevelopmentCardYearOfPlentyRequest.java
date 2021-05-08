package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request used for resolving the DevelopmentCard Year of Plenty
 *
 * @author Marc Hermes
 * @since 2021-05-04
 */
public class ResolveDevelopmentCardYearOfPlentyRequest extends ResolveDevelopmentCardRequest {

    private final String resource1;
    private final String resource2;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public ResolveDevelopmentCardYearOfPlentyRequest() {
        this.resource1 = null;
        this.resource2 = null;
    }

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
    public ResolveDevelopmentCardYearOfPlentyRequest(String devCard, UserDTO user, String gameName, String resource1, String resource2) {
        super(devCard, user, gameName);
        this.resource1 = resource1;
        this.resource2 = resource2;
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
}
