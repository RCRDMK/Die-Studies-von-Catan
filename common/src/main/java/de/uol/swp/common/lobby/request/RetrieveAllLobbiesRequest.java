package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the lobby list in the client
 *
 * This message is sent during the initialization of the lobby list. The server will
 * respond with a AllCreatedLobbiesResponse.
 *
 * @see de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse
 * @author Carsten Dekker and Marius Birk
 * @since 2020-04-12
 */

public class RetrieveAllLobbiesRequest extends AbstractRequestMessage {
}
