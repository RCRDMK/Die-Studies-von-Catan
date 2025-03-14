package de.uol.swp.common.user.response;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A message containing the session (typically for a new logged in user)
 * <p>
 * This response is sent to the Client whose LoginRequest was successful
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-08-07
 */
public class LoginSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -9107206137706636541L;

    private final User user;

    /**
     * Constructor
     * <p>
     *
     * @param user the user who successfully logged in
     * @author Marco Grawunder
     * @since 2019-08-07
     */
    public LoginSuccessfulResponse(User user) {
        this.user = user;
    }

    /**
     * Getter for the User variable
     * <p>
     *
     * @return User object of the user who successfully logged in
     * @author Marco Grawunder
     * @since 2019-08-07
     */
    public User getUser() {
        return user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    /**
     * getter for hash of User user
     * returns int
     *
     * @return hash of User user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LoginSuccessfulResponse that = (LoginSuccessfulResponse) o;
        return Objects.equals(user, that.user);
    }
}
