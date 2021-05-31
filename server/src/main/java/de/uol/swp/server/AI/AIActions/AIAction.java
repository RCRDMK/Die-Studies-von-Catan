package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * Abstract class for AIActions which represent Actions the AI can do and which the server will have to interpret accordingly
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public abstract class AIAction {

    private final String actionType;
    private final User user;
    private final String gameName;

    /**
     * Constructor
     *
     * @param actionType the String name of the ActionType
     * @param user       the User who this AI represents
     * @param gameName   the name of the Game for which this Action is to be done
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public AIAction(String actionType, User user, String gameName) {
        this.actionType = actionType;
        this.user = user;
        this.gameName = gameName;
    }

    /**
     * Getter for the ActionType
     *
     * @return the String name of the ActionType
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * Getter for the User
     *
     * @return the User who this AI represents
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public User getUser() {
        return user;
    }

    /**
     * Getter for the gameName
     *
     * @return the String name of the Game
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public String getGameName() {
        return gameName;
    }
}
