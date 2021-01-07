package de.uol.swp.common.game.message;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Message sent by the client to request dice roll
 *
 * @author Kirstin Beyer, Pieter Vogt
 * @since 2020-12-29
 */

public class RollDiceRequest extends AbstractRequestMessage{

    private final String name;
    private final User user;


    /**
     * Constructor
     * @since 2019-10-08
     */
    public RollDiceRequest(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
}
