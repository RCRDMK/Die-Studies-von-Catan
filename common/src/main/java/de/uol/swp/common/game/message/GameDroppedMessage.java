package de.uol.swp.common.game.message;

/**
 * Message sent by the server when a game is dropped
 * <p>
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameDroppedMessage extends AbstractGameMessage {

    /**
     * Constructor
     * <p>
     * @param name name of the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public GameDroppedMessage(String name) {
        this.name = name;
    }

}
