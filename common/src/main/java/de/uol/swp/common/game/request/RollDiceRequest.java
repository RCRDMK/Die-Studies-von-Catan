package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;


/**
 * Request sent to the server when a user wants to roll the dice
 * <p>
 * @see AbstractRequestMessage
 * @see de.uol.swp.common.user.User
 * @author Kirstin, Pieter
 * @since 2021-01-07
 */

public class RollDiceRequest extends AbstractRequestMessage{

    private final String name;
    private final User user;


    /**
     * Constructor
     * @since 2021-01-07
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
