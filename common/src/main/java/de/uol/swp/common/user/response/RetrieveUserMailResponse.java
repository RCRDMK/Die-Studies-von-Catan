package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * Response message for the RetrieveUserMailResponse
 * <p>
 * This message gets sent to the client that sent an RetrieveUserMailRequest.
 * It contains a UserDTO with username, the mail and an empty password String.
 * This response is only sent to clients that previously sent a RetrieveUserMailRequest.
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.user.request.RetrieveUserMailRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-03-12
 */
public class RetrieveUserMailResponse extends AbstractResponseMessage {

    private final User toMail;

    /**
     * Constructor
     *
     * @param user User who gets the mail
     */

    public RetrieveUserMailResponse(User user) {
        this.toMail = user;
    }

    public User getUser() {
        return toMail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetrieveUserMailResponse that = (RetrieveUserMailResponse) o;
        return Objects.equals(toMail, that.toMail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toMail);
    }
}
