package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * The request sent by the User when we wants to play a DevelopmentCard
 *
 * @author Marc Hermes
 * @since 2021-05-03
 */
public class PlayDevelopmentCardRequest extends AbstractGameRequest {

    final private String devCard;

    /**
     * Constructor
     *
     * @param devCard  the name of the DevelopmentCard the user wishes to play
     * @param gameName the name of the game in which the card is to be played
     * @param user     the user who wants to play the card
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public PlayDevelopmentCardRequest(String devCard, String gameName, UserDTO user) {
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
