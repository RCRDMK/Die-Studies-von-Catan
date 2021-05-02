package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * The response used when the server registered the un/successful playing of a DevelopmentCard
 *
 * @author Marc Hermes
 * @since 2021-05-03
 */
public class PlayDevelopmentCardResponse extends AbstractResponseMessage {
    final private String devCard;
    final private boolean canPlayCard;
    final private String userName;
    final private String gameName;

    /**
     * Constructor
     *
     * @param devCard     the name of the DevelopmentCard
     * @param canPlayCard the boolean value indicating whether or not the card can be played
     * @param userName    the name of the user who attempted to play the card
     * @param gameName    the name of the game in which the card was played
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public PlayDevelopmentCardResponse(String devCard, boolean canPlayCard, String userName, String gameName) {
        this.devCard = devCard;
        this.canPlayCard = canPlayCard;
        this.userName = userName;
        this.gameName = gameName;
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
     * Getter for the boolean value of whether or not the card was able to be played
     *
     * @return the boolean value, true when the card can be played, false when not
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public boolean isCanPlayCard() {
        return canPlayCard;
    }

    /**
     * Getter for the name of the user that played the card
     *
     * @return the String name of the user who played the card
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Getter for the name of the game in which the card was played
     *
     * @return the String name of the game in which the card was played
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public String getGameName() {
        return gameName;
    }
}
