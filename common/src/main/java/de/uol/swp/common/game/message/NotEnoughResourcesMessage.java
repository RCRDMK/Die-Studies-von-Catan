package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Message send to an specific user, if the user doesn't have enough resources to afford
 * a development card.
 *
 * @author Marius Birk
 * @since 2021-04-03
 */
public class NotEnoughResourcesMessage extends AbstractGameMessage {

    /**
     * Default constructor
     *
     * @author Marius Birk
     * @since 2021-04-03
     */
    public NotEnoughResourcesMessage() {

    }

    /**
     * Constructor
     *
     * @param gameName the name of the game
     * @param user     the user who didn't have enough resources
     * @author Marc Hermes
     * @since 2021-06-07
     */
    public NotEnoughResourcesMessage(String gameName, UserDTO user) {
        super(gameName, user);
    }
}
