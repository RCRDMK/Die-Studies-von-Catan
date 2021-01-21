package de.uol.swp.common.game.request;


import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to create a new game
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-15
 */
public class CreateGameRequest extends AbstractGameRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public CreateGameRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param name  name of the game
     * @param owner User trying to create the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public CreateGameRequest(String name, UserDTO owner) {
        super(name, owner);
    }

    /**
     * Setter for the user variable
     * <p>
     *
     * @param owner User trying to create the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public void setOwner(UserDTO owner) {
        setUser(owner);
    }

    /**
     * Getter for the user variable
     * <p>
     *
     * @return User trying to create the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public User getOwner() {
        return getUser();
    }

}
