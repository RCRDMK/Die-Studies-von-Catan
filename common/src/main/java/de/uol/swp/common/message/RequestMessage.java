package de.uol.swp.common.message;

/**
 * A base interface for all messages from client to server
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.Message
 * @since 2019-08-07
 */

public interface RequestMessage extends Message {

    /**
     * State, if this request can only be used, if
     * the user is authorized (typically has a valid auth)
     *
     * @return true if valid authorization is needed
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @since 2019-08-07
     */
    boolean authorizationNeeded();

}
