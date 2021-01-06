package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;


/**
 * Message sent to the client when a lobby already exists.
 *
 * @see de.uol.swp.common.message.ResponseMessage
 * @author Marius Birk and Carsten Dekker
 * @since 2020-12-02
 */
public class LobbyAlreadyExistsResponse extends AbstractResponseMessage {
    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public LobbyAlreadyExistsResponse() {
    }

}

