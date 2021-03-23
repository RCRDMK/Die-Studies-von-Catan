package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse;

/**
 * Request for initialising the user list in the lobby
 * <p>
 * This message is sent during the initialization of the user list. The server will
 * respond with a AllThisLobbyUsersResponse.
 *
 * @author Ricardo Mook
 * @see AllThisLobbyUsersResponse
 * @since 2020-12-02
 */

public class RetrieveAllThisLobbyUsersRequest extends AbstractLobbyRequest {
    private String name;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Ricardo Mook
     * @since 2020-12-02
     */
    public RetrieveAllThisLobbyUsersRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param lobbyName name of the lobby
     * @author Ricardo Mook
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
