package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.Objects;

/**
 * Base class of all game messages. Basic handling of game data.
 *
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class AbstractGameMessage extends AbstractServerMessage {
    String name;
    UserDTO user;

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2021-01-15
     */
    public AbstractGameMessage() {
    }

    /**
     * Constructor
     *
     * @param name name of the game
     * @param user user responsible for the creation of this message
     * @since 2021-01-15
     */
    public AbstractGameMessage(String name, UserDTO user) {
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
    public User getUser() {
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
        AbstractGameMessage that = (AbstractGameMessage) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user);
    }
}
