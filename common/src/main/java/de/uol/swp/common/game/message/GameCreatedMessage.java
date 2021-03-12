package de.uol.swp.common.game.message;


import de.uol.swp.common.game.GameField;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {
    private final GameField gamefield;

    /**
     * Constructor
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, GameField gamefield) {
        this.name = name;
        this.gamefield = gamefield;
    }

    public GameField getGamefield() {
        return gamefield;
    }
}
