package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * The AIAction used to play and resolve the Year of Plenty developmentCard
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class PlayDevelopmentCardYearOfPlentyAction extends PlayDevelopmentCardAction {

    private final String resource1;
    private final String resource2;

    /**
     * Constructor
     *
     * @param user      the user who wants to play and resolve the developmentCard
     * @param gameName  the name of the game in which the developmentCard is to be played and resolved
     * @param devCard   the name of the developmentCard
     * @param resource1 the name of the first resource
     * @param resource2 the name of the second resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public PlayDevelopmentCardYearOfPlentyAction(User user, String gameName, String devCard, String resource1, String resource2) {
        super(user, gameName, devCard);
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    /**
     * Getter for the first resource
     *
     * @return the String name of the first resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getResource1() {
        return resource1;
    }

    /**
     * Getter for the second resource
     *
     * @return the String name of the second resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public String getResource2() {
        return resource2;
    }
}
