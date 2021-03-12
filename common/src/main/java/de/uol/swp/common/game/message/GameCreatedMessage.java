package de.uol.swp.common.game.message;


import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Gamefield;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {
    private final Gamefield gamefield;

    /**
     * Constructor
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, Gamefield gamefield) {
        this.name = name;
        this.gamefield = gamefield;
    }

    public Gamefield getGamefield() {
        return gamefield;
    }
}
