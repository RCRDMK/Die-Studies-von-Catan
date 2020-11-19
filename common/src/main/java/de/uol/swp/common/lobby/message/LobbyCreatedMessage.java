package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class LobbyCreatedMessage extends AbstractLobbyMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    public LobbyCreatedMessage(String name, UserDTO user) {
        super(name, user);
    }

    public List<UserDTO> getUsers() {
        return users;
    }

}
