package de.uol.swp.common.game.request;

import de.uol.swp.common.game.response.AllThisGameUsersResponse;

/**
 * Request for initialising the user list in the game
 * <p>
 * This message is sent during the initialization of the user list. The server will
 * respond with a AllThisGameUsersResponse.
 *
 * @author Iskander Yusupov
 * @see AllThisGameUsersResponse
 * @since 2021-01-15
 */

public class RetrieveAllThisGameUsersRequest extends AbstractGameRequest {
    private String name;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public RetrieveAllThisGameUsersRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public RetrieveAllThisGameUsersRequest(String gameName) {
        this.name = gameName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

