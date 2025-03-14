package de.uol.swp.common.user.response.lobby;

import java.util.ArrayList;
import java.util.List;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;

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
    private final String lobbyOwnerName;
    private String lobby;

    /**
     * Default Constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Marc Hermes
     * @since 2020-12-02
     */
    public AllThisLobbyUsersResponse() {
        // needed for serialization
        this.lobbyOwnerName = "";
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the users in this lobby from the given
     * List of sessions. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users     List of all sessions of the users currently in the lobby
     * @param lobbyName String of the name of the Lobby
     * @author Marc Hermes
     * @since 2020-12-02
     */
    public AllThisLobbyUsersResponse(List<Session> users, String lobbyName, String lobbyOwnerName) {
        this.lobby = lobbyName;
        this.lobbyOwnerName = lobbyOwnerName;
        for (Session user : users) {
            this.users.add(UserDTO.createWithoutPassword(user.getUser()));
        }
    }

    /**
     * Getter for the list of users currently in the lobby
     * <p>
     *
     * @return list of users currently in the lobby
     * @author Marc Hermes
     * @since 2020-12-02
     */
    public List<UserDTO> getUsers() {
        return users;
    }

    /**
     * Getter for the name of the lobby
     * <p>
     *
     * @return string name of the lobby
     * @author Marc Hermes
     * @since 2021-01-20
     */
    public String getName() {
        return lobby;
    }

    /**
     * Getter for the name of the lobby owner
     *
     * @return the String name of the lobby owner
     * @author Marc Hermes
     * @since 2021-06-01
     */
    public String getLobbyOwnerName() {
        return lobbyOwnerName;
    }
}
