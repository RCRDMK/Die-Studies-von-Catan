package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.response.AllThisLobbyUsersResponse;

/**
 * Request for initialising the user list in the lobby
 *
 * This message is sent during the initialization of the user list. The server will
 * respond with a AllThisLobbyUsersResponse.
 *
 * @see AllThisLobbyUsersResponse
 * @author Ricardo Mook
 * @since 2020-12-02
 */

public class RetrieveAllThisLobbyUsersRequest extends AbstractLobbyRequest {
    private String name;

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2020-12-02
     */
    public RetrieveAllThisLobbyUsersRequest() {
    }

    /**
     * Constructor
     *
     * @param lobbyName name of the lobby
     * @since 2020-12-02
     */
    public RetrieveAllThisLobbyUsersRequest(String lobbyName) {
        this.name = lobbyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
