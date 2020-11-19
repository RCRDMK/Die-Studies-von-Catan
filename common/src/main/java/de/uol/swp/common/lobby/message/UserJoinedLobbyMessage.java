package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully joins a lobby
 *
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class UserJoinedLobbyMessage extends AbstractLobbyMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage() {
    }

    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     * @param user user who joined the lobby
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

    public List<UserDTO> getUsers() {
        return users;
    }
    //TODO: Genauso wie bei UserLoginMessage implementieren
}
