package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Request to update an user
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @since 2019-09-02
 */
public class UpdateUserRequest extends AbstractRequestMessage {

    final private User toUpdate;

    /**
     * Constructor
     * <p>
     *
     * @param user the user object the sender shall be updated to unchanged fields
     *             being empty
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    public UpdateUserRequest(User user) {
        this.toUpdate = user;
    }

    /**
     * Getter for the updated user object
     * <p>
     *
     * @return the updated user object
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    public User getUser() {
        return toUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUserRequest that = (UpdateUserRequest) o;
        return Objects.equals(toUpdate, that.toUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toUpdate);
    }
}
