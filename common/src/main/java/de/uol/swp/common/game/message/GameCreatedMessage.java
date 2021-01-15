package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {

    private String username;

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Constructor
     *<p>
     * @param name name of the game
     * @param user user who joined the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */

    public GameCreatedMessage(String name, UserDTO user) {
        super(name, user);
    }

    public GameCreatedMessage(String username){
        this.username = username;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public String getUsername() {
        return username;
    }

}
