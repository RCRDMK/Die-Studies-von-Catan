package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request to update a user profile picture
 * <p>
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.user.User
 * @since 2021-04-15
 */
public class UpdateUserProfilePictureRequest extends AbstractRequestMessage {

    private final User toUpdatePicture;

    /**
     * Constructor
     * <p>
     *
     * @param user the user object that shall be updated
     * @author Carsten Dekker
     * @since 2021-04-15
     */
    public UpdateUserProfilePictureRequest(User user) {
        this.toUpdatePicture = user;
    }

    /**
     * Getter for the updated user object
     * <p>
     *
     * @return the updated user object
     * @author Carsten Dekker
     * @since 2021-04-15
     */
    public User getUser() {
        return toUpdatePicture;
    }

    /**
     * getter for hash of User toUpdatePicture
     * returns int
     *
     * @return hash of User toUpdatePicture
     */
    @Override
    public int hashCode() {
        return Objects.hash(toUpdatePicture);
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
        UpdateUserProfilePictureRequest that = (UpdateUserProfilePictureRequest) o;
        return Objects.equals(toUpdatePicture, that.toUpdatePicture);
    }
}
