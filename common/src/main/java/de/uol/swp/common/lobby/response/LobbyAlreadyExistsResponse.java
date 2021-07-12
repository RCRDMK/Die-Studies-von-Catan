package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;


/**
 * Message sent to the client when a lobby already exists.
 *
 * @author Marius Birk and Carsten Dekker
 * @see de.uol.swp.common.message.ResponseMessage
 * @since 2020-12-02
 */
public class LobbyAlreadyExistsResponse extends AbstractResponseMessage {

    /**
     * Default constructor
     *
     * @author Marco Grawunder
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public LobbyAlreadyExistsResponse() {
    }

}

