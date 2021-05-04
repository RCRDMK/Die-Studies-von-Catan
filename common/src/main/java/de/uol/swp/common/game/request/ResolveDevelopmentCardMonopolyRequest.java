package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request used for resolving of the DevelopmentCard Monopoly
 *
 * @author Marc Hermes
 * @since 2021-05-04
 */
public class ResolveDevelopmentCardMonopolyRequest extends ResolveDevelopmentCardRequest{

    private final String resource;
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
    public ResolveDevelopmentCardMonopolyRequest(String devCard, UserDTO user, String gameName, String resource) {
        super(devCard, user, gameName);
        this.resource = resource;
    }

    /**
     * Getter for the name of the first resource
     *
     * @return the String name of the first resource
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public String getResource() {
        return resource;
    }
}
