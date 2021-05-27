package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserDTO;

public class JoinOnGoingGameRequest extends AbstractLobbyRequest{

    public JoinOnGoingGameRequest(String lobbyName, UserDTO user) {
        super(lobbyName, user);
    }

}
