package de.uol.swp.common.user.response.lobby;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to create a lobby.
 * <p>
 * Contains the user who sent the request initially (he is also the owner)
 * as well as the name of the lobby that was successfully created
 *
 * @author Marc Hermes
 * @since 2020-11-24
 */


public class LobbyCreatedSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -8113921823425219873L;

    private final User user;

    private String name;

    /**
     * Constructor with User
     *
     * @param user the User
     * @author Marc Hermes
     * @since 2020-11-24
     */
    public LobbyCreatedSuccessfulResponse(User user) {
        this.user = user;
    }

    /**
     * Constructor with User and name of the lobby
     *
     * @param name the name of the lobby
     * @param user the User
     */
    public LobbyCreatedSuccessfulResponse(String name, User user) {
        this.user = user;
        this.name = name;
    }

    /**
     * getter for User user
     *
     * @return User user
     */
    public User getUser() {
        return user;
    }

    /**
     * getter for String name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * compares and Object with this object and returns boolean
     * returns true if this object equals the parameter object
     * returns false if parameter is null or if this object does not equals the parameter object
     * returns true or false if the user equals user of parameter object
     *
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyCreatedSuccessfulResponse that = (LobbyCreatedSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }

    /**
     * getter for hash of User user
     * returns int
     *
     * @return hash of User user
     */
    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

}
