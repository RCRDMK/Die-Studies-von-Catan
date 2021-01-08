package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the lobby list in the client
 * <p>
 * This message is sent during the initialization of the lobby list. The server will
 * respond with a AllCreatedLobbiesResponse.
 *
 * Enhanced by Marc Hermes on 2021-07-01
 *
 * @see de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse
 * @author Carsten Dekker and Marius Birk
 * @since 2020-04-12
 */
public class RetrieveAllLobbiesRequest extends AbstractRequestMessage {

    @Override
    public boolean authorizationNeeded() {
        return false;
    }
}
