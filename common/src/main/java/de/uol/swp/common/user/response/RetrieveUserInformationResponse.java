package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;

import java.util.Objects;

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


    public RetrieveUserInformationResponse(User user) {
        this.toMail = user;
    }

    public User getUser() {
        return toMail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetrieveUserInformationResponse that = (RetrieveUserInformationResponse) o;
        return Objects.equals(toMail, that.toMail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toMail);
    }
}
