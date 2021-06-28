package de.uol.swp.common.user.response;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A message containing the session (typically for a new logged in user)
 * <p>
 * This response is sent to the Client whose LoginRequest was successful
 *
 * @author Philip
 * @see de.uol.swp.common.user.request.LoginRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-22
 */

public class PingResponse extends AbstractResponseMessage {

    private final String username;
    private final long time;

    /**
     * Constructor
     * <p>
     *
     * @param username the user who successfully logged in
     * @author Philip
     * @since 2021-01-22
     */

    public PingResponse(String username, Long time) {
        this.username = username;
        this.time = time;
    }

    /**
     * Getter for the User variable
     * <p>
     *
     * @return User object of the user who send the Ping Request
     * @author Philip
     * @since 2021-01-22
     */

    public String getUsername() {
        return username;
    }

    /**
     * Setter for the time variable
     * <p>
     *
     * @return Time object of the user who send the Ping Request
     * @author Philip
     * @since 2021-01-22
     */

    public Long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PingResponse that = (PingResponse) o;
        return Objects.equals(username, that.username) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, time);
    }
}
