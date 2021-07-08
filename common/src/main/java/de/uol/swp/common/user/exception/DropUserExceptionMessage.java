package de.uol.swp.common.user.exception;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * This exception is thrown if something went wrong during the dropUser process.
 *
 * @author Carsten Dekker
 * @since 2020-12-15
 */

public class DropUserExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the registration failed
     * @author Carsten Dekker
     * @since 2020-12-15
     */
    public DropUserExceptionMessage(String message) {
        this.message = message;
    }

    /**
     * Converts the message to a string.
     *
     * @return String of the message
     * @author Carsten Dekker
     * @since 2020-12-15
     */
    @Override
    public String toString() {
        return "DropUserExceptionMessage " + message;
    }

    /**
     * Give the hash of the message back
     *
     * @return Int of the hash from the message
     * @author Carsten Dekker
     * @since 2020-12-15
     */
    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        DropUserExceptionMessage that = (DropUserExceptionMessage) o;
        return Objects.equals(message, that.message);
    }
}

