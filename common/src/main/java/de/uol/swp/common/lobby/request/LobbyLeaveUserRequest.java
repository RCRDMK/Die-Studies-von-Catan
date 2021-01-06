package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to leave a lobby
 * <p>
 * @see AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class LobbyLeaveUserRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     * <p>
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public LobbyLeaveUserRequest() {
    }

    /**
     * Constructor
     * <p>
     * @param lobbyName name of the lobby
     * @param user user who wants to leave the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public LobbyLeaveUserRequest(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

}
