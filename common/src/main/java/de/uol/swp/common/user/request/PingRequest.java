package de.uol.swp.common.user.request;

import java.util.Objects;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * A request send from client to server, trying to send
 * a ping with the user
 *
 * @author Philip
 * @since 2021-01-25
 */

public class PingRequest extends AbstractRequestMessage {

    private static final long serialVersionUID = -6032175459878192061L;
    private User user;
    private Long time;

    /**
     * Constructor
     *
     * @param user User the user tries to send Ping
     * @author Philip
     * @since 2021-01-22
     */
    public PingRequest(User user, Long time) {
        this.user = user;
        this.time = time;
    }

    @Override
    public boolean authorizationNeeded() {
        return false;
    }

    /**
     * Getter for the user variable
     *
     * @return User tries to send Ping
     * @author Philip
     * @since 2021-01-22
     */
    public User getUser() {
        return user;
    }

    /**
     * Setter for the user variable
     *
     * @param user containing the new user
     * @author Philip
     * @since 2021-01-22
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Getter for the time variable
     *
     * @return Long tries to send Ping
     * @author Philip
     * @since 2021-01-22
     */
    public Long getTime() {
        return time;
    }

    /**
     * Setter for the time variable
     *
     * @param time Long containing the new user
     * @author Philip
     * @since 2021-01-22
     */
    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PingRequest that = (PingRequest) o;
        return Objects.equals(user, that.user) && Objects.equals(time, that.time);
    }

}
