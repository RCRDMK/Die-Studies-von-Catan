package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.UserDTO;

import java.util.Objects;

/**
 * Base class of all game request messages. Basic handling of game data.
 *
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class AbstractGameRequest extends AbstractRequestMessage {

    String name;
    UserDTO user;

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2021-01-15
     */
    public AbstractGameRequest() {
    }

    /**
     * Constructor
     *
     * @param name name of the game
     * @param user user responsible for the creation of this message
     * @since 2019-10-08
     */
    public AbstractGameRequest(String name, UserDTO user) {
        this.name = name;
        this.user = user;
    }

    /**
     * Getter for the name variable
     *
     * @return String containing the game's name
     * @since 2021-01-15
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name variable
     *
     * @param name  String containing the game's name
     * @since 2021-01-15
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the user variable
     *
     * @return User responsible for the creation of this message
     * @since 2021-01-15
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Setter for the user variable
     *
     * @param user  User responsible for the creation of this message
     * @since 2021-01-15
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        de.uol.swp.common.game.request.AbstractGameRequest that = (de.uol.swp.common.game.request.AbstractGameRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user);
    }
}
