package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * This exception is thrown if something went wrong during the retrieve user mail process.
 * e.g.: The username is not in the database
 *
 * @author Carsten Dekker
 * @since 2021-03-12
 */

public class RetrieveUserMailExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the process failed
     * @since 2021-03-12
     */
    public RetrieveUserMailExceptionMessage(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return "RetrieveUserMailExceptionMessage "+message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RetrieveUserMailExceptionMessage that = (RetrieveUserMailExceptionMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
