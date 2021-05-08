package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

/**
 * The AIAction used for ending the turn
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public class EndTurnAction extends AIAction {

    /**
     * Constructor
     *
     * @param user     the User whose turn is to be ended
     * @param gameName the name of the game in which the turn is to be ended
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public EndTurnAction(User user, String gameName) {
        super("EndTurn", user, gameName);
    }
}
