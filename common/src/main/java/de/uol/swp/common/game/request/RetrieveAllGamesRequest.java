package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the game list in the client
 * <p>
 * This message is sent during the initialization of the game list. The server will
 * respond with a AllCreatedGamesResponse.
 *
 * Enhanced by Marc Hermes on 2021-07-01
 *
 * @see de.uol.swp.common.game.response.AllCreatedGamesResponse
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class RetrieveAllGamesRequest extends AbstractRequestMessage {

    @Override
    public boolean authorizationNeeded() {
        return false;
    }
}

