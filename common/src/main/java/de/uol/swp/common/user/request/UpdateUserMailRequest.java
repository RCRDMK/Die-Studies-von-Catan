package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Request to update the users mail address
 * <p>
 * @see de.uol.swp.common.user.User
 * @author Carsten Dekker
 * @since 2021-03-12
 */
public class UpdateUserMailRequest extends AbstractRequestMessage {

    final private User toUpdateMail;

    /**
     * Constructor
     * <p>
     * @param user the user object that shall be updated
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public UpdateUserMailRequest(User user){
        this.toUpdateMail = user;
    }

    /**
     * Getter for the updated user object
     * <p>
     * @return the updated user object
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public User getUser() {
        return toUpdateMail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUserMailRequest that = (UpdateUserMailRequest) o;
        return Objects.equals(toUpdateMail, that.toUpdateMail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toUpdateMail);
    }
}
