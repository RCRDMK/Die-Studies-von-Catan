package de.uol.swp.common.game.message;
import de.uol.swp.common.user.UserDTO;

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
     *
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, UserDTO user) {
        this.name = name;
        this.user = user;
    }

}
