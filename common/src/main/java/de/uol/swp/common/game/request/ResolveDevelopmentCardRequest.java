package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.UUID;

/**
 * The request sent from the client when he wants to resolve a certain DevelopmentCard
 *
 * @author Marc Hermes
 * @since 2021-05-03
 */
public abstract class ResolveDevelopmentCardRequest extends AbstractGameRequest {
    final private String devCard;

    // TODO: neue klasse? abstrakte Klasse ResolveDevelopmentCardRequest

    /**
     * Abstract Constructor used for the resolution of DevelopmentCards
     *
     * @param devCard  the name of the DevelopmentCard
     * @param user     the user who wants to resolve the card
     * @param gameName the name of the game in which the card is to be resolved
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public ResolveDevelopmentCardRequest(String devCard, UserDTO user, String gameName) {
        super(gameName, user);
        this.devCard = devCard;
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
}
