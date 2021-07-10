package de.uol.swp.server.AI.AIActions;

import java.util.UUID;

import de.uol.swp.common.user.User;

/**
 * The AIAction used to play and resolve the Road Building developmentCard
 *
 * @author Alexander Losse, Marc Hermes
 * @since 2021-05-08
 */
public class PlayDevelopmentCardRoadBuildingAction extends PlayDevelopmentCardAction {

    private final UUID street1;
    private final UUID street2;

    /**
     * Constructor
     *
     * @param user     the user who wants to play and resolve the developmentCard
     * @param gameName the name of the game in which the developmentCard is to be played and resolved
     * @param devCard  the name of developmentCard
     * @param street1  the UUID of the first streetNode
     * @param street2  the UUID of the second streetNode
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public PlayDevelopmentCardRoadBuildingAction(User user, String gameName, String devCard, UUID street1,
                                                 UUID street2) {
        super(user, gameName, devCard);
        this.street1 = street1;
        this.street2 = street2;
    }

    /**
     * Getter for the UUID of the first street
     *
     * @return the UUID of the first street
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public UUID getStreet1() {
        return street1;
    }

    /**
     * Getter for the UUID of the second street
     *
     * @return the UUID of the second street
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    public UUID getStreet2() {
        return street2;
    }
}
