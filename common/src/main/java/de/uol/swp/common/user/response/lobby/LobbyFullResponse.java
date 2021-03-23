package de.uol.swp.common.user.response.lobby;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to join a full Lobby
 * Contains the lobbyName, so the User can get a chat feedback which lobby is full
 *
 * @author René
 * @since 2020-12-17
 */

public class LobbyFullResponse extends AbstractResponseMessage {

    private String lobbyName;

    public LobbyFullResponse(String lobbyName) {
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
