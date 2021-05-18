package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * Request used for resolving of the DevelopmentCard Knight
 *
 * @author Marc Hermes
 * @since 2021-05-14
 */
public class ResolveDevelopmentCardKnightRequest extends ResolveDevelopmentCardRequest{

    private final UUID field;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public ResolveDevelopmentCardKnightRequest() {
        this.field = null;
    }

    /**
     * Constructor used for the resolution of the Monopoly DevelopmentCard
     *
     * @param devCard  the name of the DevelopmentCard, should be "Monopoly"
     * @param user     the user who wants to resolve the card
     * @param gameName the name of the game in which the card is to be resolved
     * @param field the UUID of the field the robber is to be moved to
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public ResolveDevelopmentCardKnightRequest(String devCard, UserDTO user, String gameName, UUID field) {
        super(devCard, user, gameName);
        this.field = field;
    }

    /**
     * Getter for the name of the first resource
     *
     * @return the String name of the first resource
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public UUID getField() {
        return field;
    }
}
