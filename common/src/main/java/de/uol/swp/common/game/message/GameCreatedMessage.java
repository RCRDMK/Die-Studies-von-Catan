package de.uol.swp.common.game.message;
import de.uol.swp.common.user.UserDTO;

import de.uol.swp.common.game.GameField;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {
    private final GameField gameField;

    /**
     * Constructor
     *
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, UserDTO user, GameField gameField) {
        this.name = name;
        this.user = user;
        this.gameField = gameField;
    }

    public GameField getGameField() {
        return gameField;
    }
}
