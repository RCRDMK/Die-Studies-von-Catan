package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;


/**
 * Request sent to the server when a user wants to roll the dice
 * <p>
 * @see AbstractRequestMessage
 * @see User
 * @author Kirstin, Pieter
 * @since 2021-01-07
 */

public class StartGameRequest extends AbstractRequestMessage{

    private final String name;
    private final User user;


    /**
     * Constructor
     * @since 2021-01-07
     */
    public StartGameRequest(String name, User user) {
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
