package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * The AIAction used to play and resolve the Monopoly developmentCard
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class PlayDevelopmentCardMonopolyAction extends PlayDevelopmentCardAction {

    private final String resource;

    /**
     * Constructor
     *
     * @param user     the user who wants to play the developmentCard
     * @param gameName the game in which the developmentCard is to be played
     * @param devCard  the String name of the developmentCard
     * @param resource the String name of the resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public PlayDevelopmentCardMonopolyAction(User user, String gameName, String devCard, String resource) {
        super(user, gameName, devCard);
        this.resource = resource;
    }

    /**
     * Getter for the name of the resource
     *
     * @return the String name of the resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getResource() {
        return resource;
    }
}
