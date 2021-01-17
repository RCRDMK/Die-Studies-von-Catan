package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LobbiesForSpecificUserResponse extends AbstractResponseMessage {
    final private ArrayList<LobbyDTO> lobbies = new ArrayList<>();
    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2020-04-12
     */

    public  LobbiesForSpecificUserResponse(){
        // needed for serialization
    }

    /**
     * Constructor
     *
     * This constructor generates a new List of currently existing lobbies in LobbyDTO from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the LobbyDTO objects.
     *
     * @param lobbyCollection Collection of all lobbies currently existing
     * @since 2020-04-12
     */
    public LobbiesForSpecificUserResponse(User user , Collection<Lobby> lobbyCollection) {
        for (Lobby lobby : lobbyCollection) {
            if(lobby.getUsers().contains(user) || lobby.getOwner().equals(user)){
                this.lobbies.add(new LobbyDTO(lobby.getName(), lobby.getOwner()));
            }
        }
    }

    /**
     * Getter for the list of the LobbyDTO
     *
     * @return list of lobbies
     * @since 2020-04-12
     */
    public List<LobbyDTO> getLobbyDTOs() {
        return lobbies;
    }
}
