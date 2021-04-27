package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to join a lobby
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class LobbyJoinUserRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public LobbyJoinUserRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param lobbyName name of the lobby
     * @param user      user who wants to join the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public LobbyJoinUserRequest(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

}
