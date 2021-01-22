package de.uol.swp.common.game.request;


import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to create a new game
 * <p>
 *
 * @author Iskander Yusupov
 * @see AbstractGameRequest
 * @see User
 * @since 2021-01-15
 */
public class PlayerReadyRequest extends AbstractGameRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public PlayerReadyRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param name  name of the game
     * @param user User trying to create the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public PlayerReadyRequest(String name, UserDTO user) {
        super(name, user);
    }

}
