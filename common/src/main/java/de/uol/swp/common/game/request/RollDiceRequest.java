package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;


/**
 * Request sent to the server when a user wants to roll the dice
 * <p>
 * @see AbstractRequestMessage
 * @see de.uol.swp.common.user.User
 * @author Kirstin, Pieter
 * @since 2021-01-07
 */

public class RollDiceRequest extends AbstractGameRequest{

    /**
     * Constructor
     * @since 2021-01-07
     */
    public RollDiceRequest() {
    }

    public RollDiceRequest(String gameName, UserDTO user) {
        setUser(user);
        setName(gameName);
    }
}
