package de.uol.swp.common.user.response.game;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.ArrayList;
import java.util.Set;

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
    private final ArrayList<User> users;
    private final Set<User> humanUsers;
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
        // needed for serialization
        this.users = null;
        this.humanUsers = null;
    }

    /**
     * Constructor
     * <p>
     * This constructor generates a new List of the users in this game from the given
     * List of sessions. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param humanUsers the set of users containing all human users of this game
     * @param users      List of all users currently in the game
     * @param gameName   the name of the game for this request
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public AllThisGameUsersResponse(Set<User> humanUsers, ArrayList<User> users, String gameName) {
        this.game = gameName;
        this.users = users;
        this.humanUsers = humanUsers;
    }

    /**
     * Getter for the list of users currently in the game
     * <p>
     *
     * @return list of users currently in the game
     * @author Iskander Yusupov
     * @since 2021-01-15
     */
    public ArrayList<User> getUsers() {
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

    /**
     * Getter for the set of human users in the game
     *
     * @return the set of human users in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public Set<User> getHumanUsers() {
        return humanUsers;
    }
}

