package de.uol.swp.common.lobby.response;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Set;

/**
 * Response used for communicating to the client whether or not he was able to join the ongoing game and if he was able to, some information about the game
 *
 * @author Marc Hermes
 * @since 2021-05-27
 */
public class JoinOnGoingGameResponse extends AbstractResponseMessage {

    private final MapGraph mapGraph;
    private final ArrayList<User> users;
    private final Set<User> humans;
    private final String gameFieldVariant;
    private final String gameName;
    private final UserDTO user;
    private final boolean joinedSuccessful;
    private final String reasonForFailedJoin;
    private final User gameOwner;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public JoinOnGoingGameResponse() {
        this.mapGraph = null;
        this.users = null;
        this.humans = null;
        this.gameFieldVariant = null;
        this.gameName = null;
        this.user = null;
        this.joinedSuccessful = false;
        this.reasonForFailedJoin = null;
        this.gameOwner = null;
    }

    /**
     * Constructor
     *
     * @param gameName         the name of the game the user wanted to join
     * @param user             the user who wanted to join the game
     * @param joinedSuccessful true if he successfully joined, false if not
     * @param mapGraph         the mapGraph of the game
     * @param users            all the users currently in the game
     * @param humans           the human users currently in the game
     * @param gameFieldVariant the gameFieldVariant of the mapGraph
     * @param reasonForFailedJoin the reason why the user wasn't able to join, empty if he was able to
     */
    public JoinOnGoingGameResponse(String gameName, UserDTO user, boolean joinedSuccessful, MapGraph mapGraph, ArrayList<User> users, Set<User> humans, String gameFieldVariant, String reasonForFailedJoin, User gameOwner) {
        this.gameName = gameName;
        this.user = user;
        this.joinedSuccessful = joinedSuccessful;
        this.mapGraph = mapGraph;
        this.users = users;
        this.humans = humans;
        this.gameFieldVariant = gameFieldVariant;
        this.reasonForFailedJoin = reasonForFailedJoin;
        this.gameOwner = gameOwner;
    }

    /**
     * Getter for the mapGraph
     *
     * @return the mapGraph of the game the user joined
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public MapGraph getMapGraph() {
        return mapGraph;
    }

    /**
     * Getter for the users ArrayList
     *
     * @return the ArrayList of all users of the game the user joined
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Getter for the human users ArrayList
     *
     * @return the ArrayList containing all human users of the game the user joined
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public Set<User> getHumans() {
        return humans;
    }

    /**
     * Getter for the gameFieldVariant of the mapGraph
     *
     * @return the gameFieldVariant of the game the user joined
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public String getGameFieldVariant() {
        return gameFieldVariant;
    }

    /**
     * Getter for the game name
     *
     * @return the String name of the game the user joined
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Getter for the user
     *
     * @return the user who wanted to join the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Getter for the boolean value representing whether or not the joining was successful
     *
     * @return true if successfully joined, false if not
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public boolean isJoinedSuccessful() {
        return joinedSuccessful;
    }

    /**
     * Getter for the reason why the user wasn't able to join the game
     *
     * @return the String reason for the failure
     * @author Marc Hermes
     * @since 2021-06-01
     */
    public String getReasonForFailedJoin() {
        return reasonForFailedJoin;
    }

    /**
     * Getter for the User, who is the game owner
     *
     * @return User, who is the game owner
     * @author Iskander Yusupov
     * @since 2021-06-21
     */
    public User getGameOwner() { return gameOwner; }
}
