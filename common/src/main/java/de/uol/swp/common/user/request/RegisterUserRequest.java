package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request to register a new user
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @since 2019-09-02
 */
public class RegisterUserRequest extends AbstractRequestMessage {

    final private User toCreate;

    /**
     * Constructor
     *
     * @param user the new User to create
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    public RegisterUserRequest(User user) {
        this.toCreate = user;
    }

    @Override
    public boolean authorizationNeeded() {
        return false;
    }

    /**
     * Getter for the user variable
     *
     * @return the new user to create
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    public User getUser() {
        return toCreate;
    }

    /**
     * getter for hash of User toCreate
     * returns int
     *
     * @return hash of User toCreate
     */
    @Override
    public int hashCode() {
        return Objects.hash(toCreate);
    }

    /**
     * compares an Object with this object and returns boolean
     * returns true if this object equals the parameter object
     * returns false if parameter is null or if this object does not equals the parameter object
     * returns true or false if the user equals user of parameter object
     *
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RegisterUserRequest that = (RegisterUserRequest) o;
        return Objects.equals(toCreate, that.toCreate);
    }
}
