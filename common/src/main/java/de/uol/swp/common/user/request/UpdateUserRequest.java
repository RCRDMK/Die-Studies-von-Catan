package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request to update an user
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @since 2019-09-02
 */
public class UpdateUserRequest extends AbstractRequestMessage {

    private final User toUpdate;

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
    public int hashCode() {
        return Objects.hash(toUpdate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        UpdateUserRequest that = (UpdateUserRequest) o;
        return Objects.equals(toUpdate, that.toUpdate);
    }
}
