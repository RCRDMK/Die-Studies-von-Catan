package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

import java.util.UUID;

/**
 * The AIAction used to play and resolve the Knight developmentCard
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class PlayDevelopmentCardKnightAction extends PlayDevelopmentCardAction {

    private final UUID field;

    /**
     * Constructor
     *
     * @param user     the user who wants to play and resolve the developmentCard
     * @param gameName the name of the game in which the developmentCard is to be played and resolved
     * @param devCard  the String name of the developmentCard
     * @param field    the UUID of the field to which the robber is to moved
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public PlayDevelopmentCardKnightAction(User user, String gameName, String devCard, UUID field) {
        super(user, gameName, devCard);
        this.field = field;
    }

    /**
     * Getter for the field
     *
     * @return the UUID of the field
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public UUID getField() {
        return field;
    }
}
