package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

/**
 * Message sent by the server when a player is kicked
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-06-25
 */

public class PlayerKickedMessage extends AbstractGameMessage {
    private final boolean toBan;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-06-25
     */
    public PlayerKickedMessage() {
        toBan = false;
    }

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @param user     lobby/game owner
     * @param toBan    boolean for true or false (if true, player will be banned from the game, is false player will be kicked.)
     * @author Iskander Yusupov
     * @since 2021-06-25
     */
    public PlayerKickedMessage(String gameName, UserDTO user, boolean toBan) {
        super(gameName, user);
        this.toBan = toBan;
    }

    /**
     * Getter for the toBan
     * <p>
     *
     * @return boolean toBan defines whether the player will be kicked or banned from the game
     * @author Iskander Yusupov
     * @since 2021-06-25
     */
    public boolean isToBan() {
        return toBan;
    }
}
