package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully leaves a game
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @see de.uol.swp.common.user.User
 * @since 2021-01-15
 */
public class UserLeftGameMessage extends AbstractGameMessage {

    private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public UserLeftGameMessage() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @param user     user who left the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public UserLeftGameMessage(String gameName, UserDTO user, ArrayList<UserDTO> users) {
        super(gameName, user);
        this.users = users;
    }

    public List<UserDTO> getUsers() {
        return users;
    }
}
