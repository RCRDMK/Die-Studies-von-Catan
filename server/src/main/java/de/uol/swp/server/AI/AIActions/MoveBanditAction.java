package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

import java.util.UUID;

/**
 * The AIAction used for moving the robber when a 7 is rolled with the dice
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public class MoveBanditAction extends AIAction {

    private final UUID field;

    /**
     * Constructor
     *
     * @param user     the user who wants to move the robber
     * @param gameName the name of the game in which the robber is to be moved
     * @param field    the UUID of the field where the robber is to be moved
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public MoveBanditAction(User user, String gameName, UUID field) {
        super("MoveBandit", user, gameName);
        this.field = field;
    }

    /**
     * Getter for the field
     *
     * @return the UUID of the field
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public UUID getField() {
        return field;
    }
}
