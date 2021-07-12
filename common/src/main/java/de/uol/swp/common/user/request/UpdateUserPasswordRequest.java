package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request to update the users password
 * <p>
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.user.User
 * @since 2021-03-12
 */
public class UpdateUserPasswordRequest extends AbstractRequestMessage {

    private final User toUpdatePassword;
    private final String currentPassword;

    /**
     * Constructor
     * <p>
     *
     * @param user the user object the sender shall be updated to unchanged fields
     *             being empty
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public UpdateUserPasswordRequest(User user, String currentPassword) {
        this.toUpdatePassword = user;
        this.currentPassword = currentPassword;
    }

    /**
     * Getter for the updated user object
     * <p>
     *
     * @return the updated user object
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public User getUser() {
        return toUpdatePassword;
    }

    /**
     * Getter for the entered password
     * <p>
     *
     * @return currently password in use
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * getter for hash of User toUpdatePassword
     * returns int
     *
     * @return hash of User toUpdatePassword
     */
    @Override
    public int hashCode() {
        return Objects.hash(toUpdatePassword);
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
        UpdateUserPasswordRequest that = (UpdateUserPasswordRequest) o;
        return Objects.equals(toUpdatePassword, that.toUpdatePassword);
    }

}
