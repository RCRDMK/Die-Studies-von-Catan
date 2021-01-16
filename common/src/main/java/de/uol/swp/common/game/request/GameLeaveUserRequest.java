package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user wants to leave a game
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-15
 */
public class GameLeaveUserRequest extends AbstractGameRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public GameLeaveUserRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @param user     user who wants to leave the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public GameLeaveUserRequest(String gameName, UserDTO user) {
        super(gameName, user);
    }

}
