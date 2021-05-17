package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Response send to the user, that rolled a seven.
 *
 * @author Marius Birk
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-04-07
 */
public class MoveRobberMessage extends AbstractGameMessage {
    public MoveRobberMessage(String name, UserDTO user) {
        super(name, user);
    }
}
