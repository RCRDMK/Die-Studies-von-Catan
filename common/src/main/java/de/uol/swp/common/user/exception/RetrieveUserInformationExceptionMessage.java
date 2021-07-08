package de.uol.swp.common.user.exception;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * This exception is thrown if something went wrong during the retrieve user mail process.
 * e.g.: The username is not in the database
 *
 * @author Carsten Dekker
 * @since 2021-03-12
 */

public class RetrieveUserInformationExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the process failed
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    public RetrieveUserInformationExceptionMessage(String message) {
        this.message = message;
    }

    /**
     * Converts the message to a string.
     *
     * @return String of the message
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    @Override
    public String toString() {
        return "RetrieveUserMailExceptionMessage " + message;
    }

    /**
     * Give the hash of the message back
     *
     * @return Int of the hash from the message
     * @author Carsten Dekker
     * @since 2021-03-12
     */
    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        RetrieveUserInformationExceptionMessage that = (RetrieveUserInformationExceptionMessage) o;
        return Objects.equals(message, that.message);
    }
}
