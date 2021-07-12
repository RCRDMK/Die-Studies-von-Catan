package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request to update the users mail address
 * <p>
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.user.User
 * @since 2021-03-12
 */
public class UpdateUserMailRequest extends AbstractRequestMessage {

    private final User toUpdateMail;

    /**
     * Constructor
     * <p>
     *
     * @param user the user object that shall be updated
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public UpdateUserMailRequest(User user) {
        this.toUpdateMail = user;
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
        return toUpdateMail;
    }

    /**
     * getter for hash of inUsert toUpdateMail
     * returns int
     *
     * @return hash of User toUpdateMail
     */
    @Override
    public int hashCode() {
        return Objects.hash(toUpdateMail);
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
        UpdateUserMailRequest that = (UpdateUserMailRequest) o;
        return Objects.equals(toUpdateMail, that.toUpdateMail);
    }
}
