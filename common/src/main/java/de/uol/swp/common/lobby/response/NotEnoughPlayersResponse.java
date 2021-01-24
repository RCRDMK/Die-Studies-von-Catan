package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;


/**
 * Message sent to the client when a lobby has not enough players to start the game.
 *
 * @author Kirstin Beyer, Iskander Yusupov
 * @see de.uol.swp.common.message.ResponseMessage
 * @since 2021-01-24
 */
public class NotEnoughPlayersResponse extends AbstractResponseMessage {
    /**
     * Default constructor
     *
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public NotEnoughPlayersResponse() {
    }

}

