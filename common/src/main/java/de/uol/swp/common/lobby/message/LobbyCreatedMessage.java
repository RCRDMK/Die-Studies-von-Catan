package de.uol.swp.common.lobby.message;

import java.util.ArrayList;
import java.util.List;

import de.uol.swp.common.user.UserDTO;

/**
 * Message sent by the server when a user successfully creates a lobby
 * <p>
 *
 * @author Ricardo Mook, Marc Hermes
 * @since 2020-11-19
 */

public class LobbyCreatedMessage extends AbstractLobbyMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Constructor
     * <p>
     *
     * @param name name of the lobby
     * @param user user who joined the lobby
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-11-19
     */
    public LobbyCreatedMessage(String name, UserDTO user) {
        super(name, user);
    }

    public List<UserDTO> getUsers() {
        return users;
    }

}
