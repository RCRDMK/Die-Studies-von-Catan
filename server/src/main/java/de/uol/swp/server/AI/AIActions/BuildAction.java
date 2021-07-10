package de.uol.swp.server.AI.AIActions;

import java.util.UUID;

import de.uol.swp.common.user.User;

/**
 * The AIAction used for building streets, towns and cities
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class BuildAction extends AIAction {

    private final UUID field;

    /**
     * Constructor
     *
     * @param actionType the String describing what is to be built "BuildStreet", "BuildTown" or "BuildCity"
     * @param user       the user who wants to build the building
     * @param gameName   the name of the game in which the building is to be built
     * @param field      the UUID of the field where the building is to be built
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public BuildAction(String actionType, User user, String gameName, UUID field) {
        super(actionType, user, gameName);
        this.field = field;
    }

    /**
     * Getter for the field
     *
     * @return the UUID of the field where the building is to be built
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public UUID getField() {
        return field;
    }
}
