package de.uol.swp.common.user.response.lobby;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Response message for the RetrieveAllThisLobbyUsersRequest
 * <p>
 * This message gets sent to the client that sent an RetrieveAllThisLobbyUsersRequest.
 * It contains a List with User objects of every user currently in the lobby.
 *
 * @author Marc Hermes
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest
 * @see de.uol.swp.common.user.UserDTO
 * @since 2020-12-02
 */

public class AllThisLobbyUsersResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -7113321823425212173L;
    final private ArrayList<UserDTO> users = new ArrayList<>();

    /**
     * Default Constructor
     * <p>
     * @implNote this constructor is needed for serialization
     * @author Marc Hermes
     * @since 2020-12-02
     */
    public AllThisLobbyUsersResponse(){
        // needed for serialization
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the users in this lobby from the given
     * List of sessions. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users List of all sessions of the users currently in the lobby
     * @author Marc Hermes
     * @since 2020-12-02
     */
    public AllThisLobbyUsersResponse(List<Session> users) {
        for (Session user : users) {
            this.users.add(UserDTO.createWithoutPassword(user.getUser()));
        }
    }

    /**
     * Getter for the list of users currently in the lobby
     * <p>
     * @return list of users currently in the lobby
     * @author Marc Hermes
     * @since 2020-12-02
     */
    public List<UserDTO> getUsers() {
        return users;
    }

}
