package de.uol.swp.common.lobby.message;

import java.util.ArrayList;
import java.util.List;

import de.uol.swp.common.user.UserDTO;

/**
 * Message sent by the server when there are enough players in the lobby and game can be started.
 * <p>
 *
 * @author Kirstin Beyer, Iskander Yusupov
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-24
 */
public class StartGameMessage extends AbstractLobbyMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public StartGameMessage() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param lobbyName name of the lobby
     * @param user      user sent startGameRequest
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public StartGameMessage(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

    /**
     * Getter for the User, who are in the Game
     *
     * @return User, who are in the Game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public List<UserDTO> getUsers() {
        return users;
    }

}
