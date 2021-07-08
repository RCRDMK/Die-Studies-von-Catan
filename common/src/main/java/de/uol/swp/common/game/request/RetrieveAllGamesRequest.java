package de.uol.swp.common.game.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request for initialising the game list in the client
 * <p>
 * This message is sent during the initialization of the game list. The server will
 * respond with a AllCreatedGamesResponse.
 * <p>
 * Enhanced by Marc Hermes on 2021-07-01
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.response.AllCreatedGamesResponse
 * @since 2021-01-15
 */
public class RetrieveAllGamesRequest extends AbstractRequestMessage {

    /**
     * method to disable that user are being listed in the game before they actually joined the game
     *
     * @return the boolean value false
     * @author Marc Hermes
     */
    @Override
    public boolean authorizationNeeded() {
        return false;
    }
}

