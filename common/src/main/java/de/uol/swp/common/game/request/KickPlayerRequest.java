package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a lobby owner wants to kick player
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-06-24
 */
public class KickPlayerRequest extends AbstractGameRequest {

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-06-24
     */
    public KickPlayerRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @param user     user who will be kicked from the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public KickPlayerRequest(String gameName, UserDTO user) {
        super(gameName, user);
    }

}
