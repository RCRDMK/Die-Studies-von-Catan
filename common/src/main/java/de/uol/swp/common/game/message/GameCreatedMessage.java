package de.uol.swp.common.game.message;


/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {

    /**
     * Constructor
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name) {
        this.name = name;
    }

}
