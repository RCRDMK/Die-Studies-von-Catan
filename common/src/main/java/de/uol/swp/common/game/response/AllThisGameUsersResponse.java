package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Response message for the RetrieveAllThisGameUsersRequest
 * <p>
 * This message gets sent to the client that sent an RetrieveAllThisGameUsersRequest.
 * It contains a List with User objects of every user currently in the game.
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest
 * @see de.uol.swp.common.user.UserDTO
 * @since 2020-12-02
 */

public class AllThisGameUsersResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -7113321823425212173L;
    final private ArrayList<UserDTO> users = new ArrayList<>();
    private String game;

    /**
     * Default Constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public AllThisGameUsersResponse() {
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the users in this game from the given
     * List of sessions. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users List of all sessions of the users currently in the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public AllThisGameUsersResponse(List<Session> users, String gameName) {
        this.game = gameName;
        for (Session user : users) {
            this.users.add(UserDTO.createWithoutPassword(user.getUser()));
        }
    }

    /**
     * Getter for the list of users currently in the game
     * <p>
     *
     * @return list of users currently in the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public List<UserDTO> getUsers() {
        return users;
    }

    /**
     * Getter for the name of the game
     * <p>
     *
     * @return string name of the game
     * @author Iskander Yusupov
     * @since 2021-03-14
     */
    public String getName() {
        return game;
    }

}

