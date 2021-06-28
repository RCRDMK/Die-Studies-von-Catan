package de.uol.swp.common.game.message;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Set;

/**
 * Message sent by the server when a user successfully creates a game
 * <p>
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class GameCreatedMessage extends AbstractGameMessage {
    private final MapGraph mapGraph;
    private final ArrayList<User> users;
    private final Set<User> humans;
    private final String gameFieldVariant;
    private final User gameOwner;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public GameCreatedMessage() {
        this.mapGraph = null;
        this.users = null;
        this.humans = null;
        this.gameFieldVariant = null;
        this.gameOwner = null;
    }

    /**
     * Constructor
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook 2021-03-05
     *
     * @param name
     * @param user
     * @param mapGraph
     * @param users
     * @param humans
     * @param gameFieldVariant
     * @param gameOwner
     * @since 2021-01-07
     */
    public GameCreatedMessage(String name, UserDTO user, MapGraph mapGraph, ArrayList<User> users, Set<User> humans, String gameFieldVariant, User gameOwner) {
        super(name, user);
        this.mapGraph = mapGraph;
        this.users = users;
        this.humans = humans;
        this.gameFieldVariant = gameFieldVariant;
        this.gameOwner = gameOwner;
    }

    public MapGraph getMapGraph() {
        return mapGraph;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Getter for the Set of human users in the game
     *
     * @return the Set containing all human users in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public Set<User> getHumans() { return humans; }

    public String getGameFieldVariant() {
        return gameFieldVariant;
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
