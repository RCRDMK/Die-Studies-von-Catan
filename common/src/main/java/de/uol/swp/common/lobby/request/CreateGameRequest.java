package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to create a new game
 * <p>
 *
 * @author Kirstin Beyer, Iskander Yusupov
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-24
 */
public class CreateGameRequest extends AbstractLobbyRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public CreateGameRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param name  name of the lobby
     * @param owner User trying to create the game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public CreateGameRequest(String name, UserDTO owner) {
        super(name, owner);
    }

    /**
     * Setter for the user variable
     * <p>
     *
     * @param owner User trying to create the game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public void setOwner(UserDTO owner) {
        setUser(owner);
    }

    /**
     * Getter for the user variable
     * <p>
     *
     * @return User trying to create the game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public User getOwner() {
        return getUser();
    }

}
