package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Message send to an specific user, if the user doesn't have enough ressources to afford
 * a development card.
 *
 * @author Marius Birk
 * @since 2021-04-03
 */
public class NotEnoughRessourcesMessage extends AbstractGameMessage {

    /**
     * Default constructor
     *
     * @author Marius Birk
     * @since 2021-04-03
     */
    public NotEnoughRessourcesMessage() {
    }

    public NotEnoughRessourcesMessage(String name, UserDTO user) {
        super(name, user);
    }
}
