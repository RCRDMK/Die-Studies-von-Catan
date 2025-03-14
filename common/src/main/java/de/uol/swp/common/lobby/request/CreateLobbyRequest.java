package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to create a new lobby
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class CreateLobbyRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public CreateLobbyRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param name  name of the lobby
     * @param owner User trying to create the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public CreateLobbyRequest(String name, UserDTO owner) {
        super(name, owner);
    }

    /**
     * Constructor
     * <p>
     *
     * @param name     name of the lobby
     * @param owner    User trying to create the lobby
     * @param password password for the lobby
     * @author René Meyer
     * @since 2021-06-05
     */
    public CreateLobbyRequest(String name, UserDTO owner, String password) {
        super(name, owner, password);
    }

    /**
     * Getter for the user variable
     * <p>
     *
     * @return User trying to create the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public User getOwner() {
        return getUser();
    }

    /**
     * Setter for the user variable
     * <p>
     *
     * @param owner User trying to create the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public void setOwner(UserDTO owner) {
        setUser(owner);
    }

}
