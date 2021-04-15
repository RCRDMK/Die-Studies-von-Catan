package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;


/**
 * Request sent to the server when a user wants to roll the dice
 * <p>
 *
 * @author Kirstin, Pieter
 * @see AbstractRequestMessage
 * @see de.uol.swp.common.user.User
 * @since 2021-01-07
 */

public class RollDiceRequest extends AbstractRequestMessage {

    private final String name;
    private final User user;
    private final int cheatEyes;

    /**
     * Constructor
     *
     * @param name Lobby - Gamename
     * @param user User who is sending the RollDiceRequest
     * @since 2021-01-07
     */

    public RollDiceRequest(String name, User user, int... cheatEyes) {
        this.name = name;
        this.user = user;
        this.cheatEyes = (cheatEyes.length >= 1) ? cheatEyes[0] : 0;
    }

    public int getCheatEyes() {
        return cheatEyes;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
}
