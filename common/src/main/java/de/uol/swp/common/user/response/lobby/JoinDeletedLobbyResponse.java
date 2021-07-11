package de.uol.swp.common.user.response.lobby;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Response that is to sent to the User who requested to join a deletedLobby
 * Contains the lobbyName, so the User can get a chat feedback, which lobby is deleted.
 *
 * @author Sergej Tulnev
 * @since 2020-12-19
 */

public class JoinDeletedLobbyResponse extends AbstractResponseMessage {

    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Name form the Lobby
     */
    public JoinDeletedLobbyResponse(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * getter for String lobbyName
     *
     * @return String lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * getter for hash of String lobbyName
     * returns int
     *
     * @return hash of String lobbyName
     */
    @Override
    public int hashCode() {
        return Objects.hash(lobbyName);
    }

}