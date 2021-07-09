package de.uol.swp.common.game.response;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;


/**
 * Message sent to the client when a lobby already exists.
 *
 * @author Marius Birk and Carsten Dekker
 * @see de.uol.swp.common.message.ResponseMessage
 * @since 2020-12-02
 */
public class GameAlreadyExistsResponse extends AbstractResponseMessage {


    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Name of the Lobby
     */

    public GameAlreadyExistsResponse(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobbyName);
    }

}

