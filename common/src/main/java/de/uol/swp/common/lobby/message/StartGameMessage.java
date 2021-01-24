package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when there are enough players in the lobby and game can be started.
 * <p>
 * @see AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @author Kirstin Beyer, Iskander Yusupov
 * @since 2021-01-24
 */
public class StartGameMessage extends AbstractLobbyMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Default constructor
     * <p>
     * @implNote this constructor is needed for serialization
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public StartGameMessage() {
    }

    /**
     * Constructor
     * <p>
     * @param lobbyName name of the lobby
     * @param user user sent startGameRequest
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public StartGameMessage(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

    public List<UserDTO> getUsers() {
        return users;
    }

}
