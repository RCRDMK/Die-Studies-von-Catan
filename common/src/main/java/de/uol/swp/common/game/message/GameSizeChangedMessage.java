package de.uol.swp.common.game.message;


/**
 * Message sent by the server when the size of a game changes, i.e. through leaving a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameSizeChangedMessage extends AbstractGameMessage {

    /**
     * Constructor
     * <p>
     *
     * @param name name of the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public GameSizeChangedMessage(String name) {
        this.name = name;
    }

}
