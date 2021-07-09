package de.uol.swp.common.user.exception;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * This exception is thrown if something went wrong during the registration process.
 * e.g.: The username is already taken
 *
 * @author Marco Grawunder
 * @since 2019-09-02
 */
public class RegistrationExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the registration failed
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    public RegistrationExceptionMessage(String message) {
        this.message = message;
    }

    /**
     * Converts the message to a string.
     *
     * @return String of the message
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    @Override
    public String toString() {
        return "RegistrationExceptionMessage " + message;
    }

    /**
     * Give the hash of the message back
     *
     * @return Int of the hash from the message
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RegistrationExceptionMessage that = (RegistrationExceptionMessage) o;
        return Objects.equals(message, that.message);
    }
}
