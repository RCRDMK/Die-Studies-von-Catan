package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to create a new lobby
 * <p>
 * @see AbstractLobbyRequest
 * @see User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class CreateGameRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     * <p>
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public CreateGameRequest() {
    }

    /**
     * Constructor
     * <p>
     * @param name name of the lobby
     * @param owner User trying to create the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public CreateGameRequest(String name, UserDTO owner) {
        super(name, owner);
    }

    /**
     * Setter for the user variable
     * <p>
     * @param owner  User trying to create the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public void setOwner(UserDTO owner) {
        setUser(owner);
    }

    /**
     * Getter for the user variable
     * <p>
     * @return User trying to create the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public User getOwner() {
        return getUser();
    }

}
