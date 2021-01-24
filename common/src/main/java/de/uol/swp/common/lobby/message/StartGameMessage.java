package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully joins a lobby
 * <p>
 * @see AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class StartGameMessage extends AbstractLobbyMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Default constructor
     * <p>
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public StartGameMessage() {
    }

    /**
     * Constructor
     * <p>
     * @param lobbyName name of the lobby
     * @param user user sent startGameRequest
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public StartGameMessage(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

    public List<UserDTO> getUsers() {
        return users;
    }

}
