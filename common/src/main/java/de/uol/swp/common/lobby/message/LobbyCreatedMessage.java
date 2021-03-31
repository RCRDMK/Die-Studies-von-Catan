package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully creates a lobby
 * <p>
 * @author Ricardo Mook, Marc Hermes
 * @since 2020-11-19
 */

public class LobbyCreatedMessage extends AbstractLobbyMessage {

    private String username;

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Constructor
     *<p>
     * @param name name of the lobby
     * @param user user who joined the lobby
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-11-19
     */
    public LobbyCreatedMessage(String name, UserDTO user) {
        super(name, user);
    }

    public LobbyCreatedMessage(String username){
        this.username = username;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public String getUsername() {
        return username;
    }

}
