package de.uol.swp.common.user.response;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Response message for the RetrieveUserInformationResponse
 * <p>
 * This message gets sent to the client that sent an RetrieveUserInformationRequest.
 * It contains a UserDTO with username, the mail and an empty password String.
 * This response is only sent to clients that previously sent a RetrieveUserInformationRequest.
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.user.request.RetrieveUserInformationRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-03-12
 */
public class RetrieveUserInformationResponse extends AbstractResponseMessage {

    private final User toMail;

    /**
     * Constructor with User
     *
     * @param user the User
     */
    public RetrieveUserInformationResponse(User user) {
        this.toMail = user;
    }

    /**
     * getter for User toMail
     *
     * @return User toMail
     */
    public User getUser() {
        return toMail;
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
    public int hashCode() {
        return Objects.hash(toMail);
    }

    /**
     * getter for hash of User toMail
     * returns int
     *
     * @return hash of User toMail
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RetrieveUserInformationResponse that = (RetrieveUserInformationResponse) o;
        return Objects.equals(toMail, that.toMail);
    }
}
