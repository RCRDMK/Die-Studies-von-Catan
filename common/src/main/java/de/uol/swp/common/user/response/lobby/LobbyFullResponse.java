package de.uol.swp.common.user.response.lobby;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * LobbyFullResponse
 * <p>
 * Response that is sent to the User who requested to join a full Lobby
 * Contains the lobbyName, so the User can get a chat feedback which lobby is full
 *
 * @author René Meyer
 * @since 2020-12-17
 */

public class LobbyFullResponse extends AbstractResponseMessage {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName the name of the lobby that is already full
     * @author René Meyer
     * @since 2020-12-17
     */
    public LobbyFullResponse(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Getter for LobbyName
     *
     * @return the lobbyName the user tried to join
     * @author René Meyer
     * @since 2020-12-17
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * getter for hash of String lobbyname
     * returns int
     *
     * @return hash of String lobbyname
     */
    @Override
    public int hashCode() {
        return Objects.hash(lobbyName);
    }
}
