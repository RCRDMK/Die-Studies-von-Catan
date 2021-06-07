package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Message send to an specific user, if the settlement was already upgraded to a city.
 *
 * @author Carsten Dekker
 * @since 2021-06-07
 */
public class SettlementFullyDevelopedMessage extends AbstractGameMessage{
    /**
     * Default Constructor
     *
     * @author Carsten Dekker
     * @since 2021-06-07
     */
    public SettlementFullyDevelopedMessage() {
    }

    /**
     * Constructor
     *
     * @param gameName the name of the game
     * @param user the user who didn't have enough resources
     * @author Marc Hermes
     * @since 2021-06-07
     */
    public SettlementFullyDevelopedMessage(String gameName, UserDTO user) {
        super(gameName, user);
    }
}
