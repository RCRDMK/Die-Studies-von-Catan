package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * The message that is used to signal the client that he did not resolve the DevelopmentCard successfully.
 *
 * @author Marc Hermes
 * @since 2021-05-03
 */
public class ResolveDevelopmentCardNotSuccessfulResponse extends AbstractResponseMessage {
    final private String devCard;
    final private String userName;
    final private String gameName;
    private String errorDescription;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public ResolveDevelopmentCardNotSuccessfulResponse() {
        this.devCard = null;
        this.userName = null;
        this.gameName = null;
    }

    /**
     * Constructor
     *
     * @param devCard  the name of the DevelopmentCard that was not resolved successfully
     * @param userName the name of the user who attempted to play the DevelopmentCard
     * @param gameName the name of the game in which the card was played
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public ResolveDevelopmentCardNotSuccessfulResponse(String devCard, String userName, String gameName) {
        this.devCard = devCard;
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
     * Getter for the name of the user who played the card
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

    /**
     * Getter for the error description.
     *
     * @return the String description of the error.
     * @author Marc Hermes
     * @since 2021-05-04
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Setter for the error description
     *
     * @param errorDescription the errorDescription describing what the server would except from the user
     * @author Marc Hermes
     * @since 2021-05-04
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
