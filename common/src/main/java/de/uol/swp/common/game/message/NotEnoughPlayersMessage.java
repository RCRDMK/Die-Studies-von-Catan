package de.uol.swp.common.game.message;


/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-25
 */

public class NotEnoughPlayersMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @since 2021-01-25
     * @author Iskander Yusupov
     */
    public NotEnoughPlayersMessage(String name) {
        this.name = name;
    }

}
