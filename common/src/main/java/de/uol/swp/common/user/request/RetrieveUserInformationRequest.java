package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request to get the user information
 * <p>
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.user.User
 * @since 2021-03-12
 */
public class RetrieveUserInformationRequest extends AbstractRequestMessage {
    private final User toGetInformation;

    /**
     * Constructor
     * <p>
     *
     * @param user the user object the sender shall be updated to unchanged fields
     *             being empty
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public RetrieveUserInformationRequest(User user) {
        this.toGetInformation = user;
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
        return toGetInformation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toGetInformation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RetrieveUserInformationRequest that = (RetrieveUserInformationRequest) o;
        return Objects.equals(toGetInformation, that.toGetInformation);
    }
}
