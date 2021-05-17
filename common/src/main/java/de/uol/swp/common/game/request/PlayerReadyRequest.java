package de.uol.swp.common.game.request;


import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when the Player is ready to start the game
 * <p>
 *
 * @author Kirstin Beyer, Iskander Yusupov
 * @see AbstractGameRequest
 * @see User
 * @since 2021-01-24
 */
public class PlayerReadyRequest extends AbstractGameRequest {

    boolean ready;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public PlayerReadyRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param name  name of the lobby
     * @param user  User which is ready to start the game
     * @param ready boolean if player is ready (true) or not ready (false) to start game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public PlayerReadyRequest(String name, UserDTO user, boolean ready) {
        super(name, user);
        this.ready = ready;
    }

    /**
     * Getter for the boolean value
     *
     * @return boolean if player is ready to start
     * @since 2021-01-24
     */
    public boolean getBoolean() {
        return this.ready;
    }
}