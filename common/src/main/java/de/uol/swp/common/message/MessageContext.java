package de.uol.swp.common.message;

import java.io.Serializable;

/**
 * Interface to encapsulate different Types of MessageContexts
 * <p>
 * In the base project the only implementation of this interface is the NettyMessageContext
 * within the communication package of the server
 *
 * @author Marco Grawunder
 * @since 2019-08-13
 */
public interface MessageContext extends Serializable {

    /**
     * Send a ResponseMessage
     *
     * @author Marco Grawunder
     * @param message The message that should be sent
     * @author Marco Grawunder
     * @since 2019-11-20
     */
    void writeAndFlush(ResponseMessage message);

    /**
     * Send a ServerMessage
     *
     * @author Marco Grawunder
     * @param message The server message that should be sent
     * @author Marco Grawunder
     * @since 2019-11-20
     */
    void writeAndFlush(ServerMessage message);
}
