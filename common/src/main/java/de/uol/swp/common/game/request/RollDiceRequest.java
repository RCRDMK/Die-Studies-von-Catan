package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;


/**
 * Request sent to the server when a user wants to roll the dice
 * or when the roll cheat is executed
 * <p>
 * enhanced by René Meyer, Sergej Tulnev
 *
 * @author Kirstin, Pieter
 * @see AbstractRequestMessage
 * @see de.uol.swp.common.user.User
 * @since 2021-04-17
 * @since 2021-01-07
 */

public class RollDiceRequest extends AbstractRequestMessage {

    private final String name;
    private final User user;
    private final int cheatEyes;

    /**
     * Constructor
     * <p>
     * Normal RollDiceRequest Constructor overloading
     * <p>
     * enhanced by René Meyer, Sergej Tulnev
     *
     * @param name Lobby - Gamename
     * @param user User who is sending the RollDiceRequest
     * @since 2021-04-17
     * @since 2021-01-07
     */
    public RollDiceRequest(String name, User user) {
        this(name, user, 0);
    }

    /**
     * Constructor
     * <p>
     * CheatEyes RollDiceRequest Constructor overloading.
     * We call this constructor for the roll cheat.
     *
     * @param name      Lobby - Gamename
     * @param user      User who is sending the RollDiceRequest
     * @param cheatEyes Integer for roll Cheat
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     */
    public RollDiceRequest(String name, User user, int cheatEyes) {
        this.name = name;
        this.user = user;
        this.cheatEyes = cheatEyes;
    }

    /**
     * Getter for cheatEyes
     * <p>
     *
     * @return cheatEyes
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     */
    public int getCheatEyes() {
        return cheatEyes;
    }

    /**
     * Getter for name
     * <p>
     *
     * @return name
     * @since 2021-01-07
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for user
     * <p>
     *
     * @return user
     * @since 2021-01-07
     */
    public User getUser() {
        return user;
    }
}
