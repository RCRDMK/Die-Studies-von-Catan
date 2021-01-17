package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllLobbiesForSpecificUserResponse extends AbstractResponseMessage {
    final private ArrayList<LobbyDTO> lobbies = new ArrayList<>();
    private User user;

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2020-01-17
     */
    public  AllLobbiesForSpecificUserResponse(){
        // needed for serialization
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of currently existing lobbies in LobbyDTO from the given
     * Collection. The significant difference between the two being that the new
     * List only contains the lobbies where the user is currently part of.
     *
     * @param user User you want to retrieve all lobbies he currently is part of
     * @param lobbyCollection Collection of all lobbies currently existing
     * @since 2020-01-17
     */
    public AllLobbiesForSpecificUserResponse(User user, Collection<Lobby> lobbyCollection) {
        this.user = user;
        for (Lobby lobby : lobbyCollection) {
            if(lobby.getOwner().equals(user) || lobby.getUsers().contains(user.getWithoutPassword())){
                this.lobbies.add(new LobbyDTO(lobby.getName(), lobby.getOwner()));
            }
        }
    }

    /**
     * Getter for the user
     *
     * @return user
     * @since 2020-01-17
     */
    public User getUser(){
        return user;
    }

    /**
     * Getter for the list of the LobbyDTO
     *
     * @return list of lobbies
     * @since 2020-01-17
     */
    public List<LobbyDTO> getLobbyDTOs() {
        return lobbies;
    }
}
