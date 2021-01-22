package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * Response that is sent to the User who requested to join a lobby where he already
 * joined before.
 * The ResponseMessage contains the name of the lobby.
 *
 * @author Carsten Dekker
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-01-22
 */

public class AlreadyJoinedThisLobbyResponse extends AbstractResponseMessage {

    private String lobbyName;

    public AlreadyJoinedThisLobbyResponse(String lobbyName){
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
