package de.uol.swp.common.lobby.message;

import java.util.ArrayList;
import java.util.Set;

import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Method used to signal everyone in the game that a player joined
 *
 * @author Marc Hermes
 * @since 2021-05-27
 */
public class JoinOnGoingGameMessage extends AbstractGameMessage {

    private final ArrayList<User> users;
    private final Set<User> humans;
    private final User gameOwner;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public JoinOnGoingGameMessage() {
        this.users = null;
        this.humans = null;
        this.gameOwner = null;
    }

    /**
     * Constructor
     *
     * @param gameName  the name of the game in which the user joined
     * @param user      the user who joined the game
     * @param users     the current list of players in the game
     * @param humans    the current list of actual human players in the game
     * @param gameOwner the current owner of the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public JoinOnGoingGameMessage(String gameName, UserDTO user, ArrayList<User> users, Set<User> humans,
                                  User gameOwner) {
        super(gameName, user);
        this.users = users;
        this.humans = humans;
        this.gameOwner = gameOwner;
    }

    /**
     * Getter for the list of all the users in the game
     *
     * @return the ArrayList containing all users in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Getter for the list of human users in the game
     *
     * @return the ArrayList containing all users in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public Set<User> getHumans() {
        return humans;
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
