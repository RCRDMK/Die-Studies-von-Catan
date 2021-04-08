package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Response message for the RetrieveAllLobbiesRequest
 * <p>
 * This message gets sent to the client that sent an RetrieveAllLobbiesRequest.
 * It contains a List with Lobby objects of every lobby currently existing on the
 * server.
 *
 * @author Carsten Dekker and Marius Birk
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
 * @see de.uol.swp.common.lobby.Lobby
 * @since 2020-04-12
 */


public class AllCreatedLobbiesResponse extends AbstractResponseMessage {


    final private ArrayList<LobbyDTO> lobbies = new ArrayList<>();

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2020-04-12
     */

    public AllCreatedLobbiesResponse() {
        // needed for serialization
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of currently existing lobbies in LobbyDTO from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the LobbyDTO objects.
     *
     * @param lobbyCollection Collection of all lobbies currently existing
     * @since 2020-04-12
     */
    public AllCreatedLobbiesResponse(Collection<Lobby> lobbyCollection) {
        for (Lobby lobby : lobbyCollection) {
            LobbyDTO tempLobby = new LobbyDTO(lobby.getName(), lobby.getOwner());
            tempLobby.setGameStarted(lobby.getGameStarted());
            for (User user : lobby.getUsers()) {
                tempLobby.joinUser(new UserDTO(user.getUsername(), "", ""));

            }
            this.lobbies.add(tempLobby);
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