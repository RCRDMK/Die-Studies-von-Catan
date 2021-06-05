package de.uol.swp.common.user.response.lobby;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

public class WrongLobbyPasswordResponse extends AbstractResponseMessage {

    private String lobbyName;

    public WrongLobbyPasswordResponse(String lobbyName) {
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
