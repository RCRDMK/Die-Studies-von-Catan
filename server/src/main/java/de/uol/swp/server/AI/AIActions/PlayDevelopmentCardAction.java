package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * Abstract Class for AIActions that are used to play and resolve certain developmentCards
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public abstract class PlayDevelopmentCardAction extends AIAction {

    private final String devCard;

    /**
     * Constructor
     *
     * @param user     the user who wants to play and resolve the developmentCard
     * @param gameName the name in which the developmentCard is to be played and resolved
     * @param devCard  the String name of the developmentCard
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public PlayDevelopmentCardAction(User user, String gameName, String devCard) {
        super("PlayDevelopmentCard", user, gameName);
        this.devCard = devCard;
    }

    /**
     * Getter for the name of the developmentCard
     *
     * @return the String name of the developmentCard
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getDevCard() {
        return devCard;
    }


}
