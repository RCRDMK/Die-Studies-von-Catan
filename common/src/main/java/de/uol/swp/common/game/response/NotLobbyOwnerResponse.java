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
public class NotLobbyOwnerResponse extends AbstractResponseMessage {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Name from the Lobby
     */

    public NotLobbyOwnerResponse(String lobbyName) {
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

