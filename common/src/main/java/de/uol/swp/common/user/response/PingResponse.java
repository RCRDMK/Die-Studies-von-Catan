package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * A message containing the session (typically for a new logged in user)
 * <p>
 * This response is sent to the Client whose LoginRequest was successful
 *
 * @author Philip Nitsche
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
     * @author Philip Nitsche
     * @since 2021-01-22
     */

    public PingResponse(String username, Long time) {
        this.username = username;
        this.time = time;
    }

    /**
     * Getter for String username
     * <p>
     *
     * @return String username
     * @author Philip Nitsche
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
     * @author Philip Nitsche
     * @since 2021-01-22
     */
    public Long getTime() {
        return time;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PingResponse that = (PingResponse) o;
        return Objects.equals(username, that.username) && Objects.equals(time, that.time);
    }

    /**
     * getter for hash of String username and long time
     * returns int
     *
     * @return hash of String username and long time
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, time);
    }
}
