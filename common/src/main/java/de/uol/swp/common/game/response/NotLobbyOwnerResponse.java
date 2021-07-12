package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;


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

    /**
     * Getter
     *
     * @return the lobby name
     * @author Marius Birk and Carsten Dekker
     * @since 2020-12-02
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * The default hashCode method to get the objects hash code
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(lobbyName);
    }

}

