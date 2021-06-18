package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.UserDTO;

import java.util.Objects;

/**
 * Base class of all lobby request messages. Basic handling of lobby data.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2019-10-08
 */
public class AbstractLobbyRequest extends AbstractRequestMessage {

    String name;
    UserDTO user;
    String password;

    /**
     * Default constructor
     *
     * @author Marco Grawunder
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public AbstractLobbyRequest() {
    }

    /**
     * Constructor
     *
     * @param name name of the lobby
     * @param user user responsible for the creation of this message
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public AbstractLobbyRequest(String name, UserDTO user) {
        this.name = name;
        this.user = user;
    }

    /**
     * Constructor
     *
     * @param name     name of the lobby
     * @param user     user responsible for the creation of this message
     * @param password password for the lobby
     * @author René Meyer
     * @since 2021-06-05
     */
    public AbstractLobbyRequest(String name, UserDTO user, String password) {
        this.name = name;
        this.user = user;
        this.password = password;
    }

    /**
     * Getter for Password
     *
     * @return the password for this lobby
     * @author René Meyer
     * @since 2021-06-05
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Setter for Password
     *
     * @param password the password to set this lobby's password to
     * @author René Meyer
     * @since 2021-06-05
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the name variable
     *
     * @return String containing the lobby's name
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name variable
     *
     * @param name String containing the lobby's name
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the user variable
     *
     * @return User responsible for the creation of this message
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Setter for the user variable
     *
     * @param user User responsible for the creation of this message
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLobbyRequest that = (AbstractLobbyRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user);
    }
}
