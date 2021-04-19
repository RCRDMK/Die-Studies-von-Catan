package de.uol.swp.common.game.message;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */

public class GameCreatedMessage extends AbstractGameMessage {
    private final MapGraph mapGraph;
    private final ArrayList<UserDTO> users;

    /**
     * Constructor
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     *
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, UserDTO user, MapGraph mapGraph, ArrayList<UserDTO> users) {
        this.name = name;
        this.user = user;
        this.mapGraph = mapGraph;
        this.users = users;
    }

    public MapGraph getMapGraph() {
        return mapGraph;
    }

    public ArrayList<UserDTO> getUsers() {
        return users;
    }
}
