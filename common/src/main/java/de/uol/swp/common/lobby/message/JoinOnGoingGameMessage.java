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
    private final int numberOfDevCards;

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
        this.numberOfDevCards = 0;
    }

    /**
     * Constructor
     *
     * @param gameName         the name of the game in which the user joined
     * @param user             the user who joined the game
     * @param users            the current list of players in the game
     * @param humans           the current list of actual human players in the game
     * @param gameOwner        the current owner of the game
     * @param numberOfDevCards number of dev cards in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public JoinOnGoingGameMessage(String gameName, UserDTO user, ArrayList<User> users, Set<User> humans,
                                  User gameOwner, int numberOfDevCards) {
        super(gameName, user);
        this.users = users;
        this.humans = humans;
        this.gameOwner = gameOwner;
        this.numberOfDevCards = numberOfDevCards;
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

    /**
     * Getter for the number of dev cards in the game
     *
     * @return number of dev cards in the game
     * @author Anton Nikiforov
     * @since 2021-07-05
     */
    public int getNumberOfDevCards() {
        return numberOfDevCards;
    }
}
