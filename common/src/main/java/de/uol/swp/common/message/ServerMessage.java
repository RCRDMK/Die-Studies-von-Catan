package de.uol.swp.common.message;

import java.util.List;

import de.uol.swp.common.user.Session;

/**
 * A message from server to a number of clients that
 * is not necessary a response to a request (aka server push)
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.Message
 * @since 2019-08-07
 */
public interface ServerMessage extends Message {

    /**
     * Getter for the receiver list
     *
     * @return List of Session objects defining the receivers
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    List<Session> getReceiver();

    /**
     * Sets the receivers of this message
     *
     * @param receiver List of Session objects defining the receivers
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    void setReceiver(List<Session> receiver);
}
