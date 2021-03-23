package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully joins a lobby
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class UserJoinedLobbyMessage extends AbstractLobbyMessage {

    private ArrayList<UserDTO> users;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param lobbyName name of the lobby
     * @param user      user who joined the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public UserJoinedLobbyMessage(String lobbyName, UserDTO user, ArrayList<UserDTO> users) {
        super(lobbyName, user);
        this.users = users;
    }

    public ArrayList<UserDTO> getUsers() {
        return users;
    }

}
