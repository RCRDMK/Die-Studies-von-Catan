package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import de.uol.swp.common.game.GameField;

import java.util.ArrayList;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {
    private final GameField gameField;
    private final ArrayList<UserDTO> users;

    /**
     * Constructor
     *
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, UserDTO user, GameField gameField, ArrayList<UserDTO> users) {
        this.name = name;
        this.user = user;
        this.gameField = gameField;
        this.users = users;
    }

    public GameField getGameField() {
        return gameField;
    }

    public ArrayList<UserDTO> getUsers() {return users;}
}
