package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Gamefield;
import de.uol.swp.common.user.User;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Object to transfer the information of a game
 * <p>
 * This object is used to communicate the current state of games between
 * the server and clients. It contains information about the Name of the game,
 * who owns the game.
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class GameDTO implements Game {

    private final String name;
    private User owner;
    private final Set<User> users = new TreeSet<>();
    private Gamefield gamefield;


    /**
     * Constructor
     *
     * @param name    The name the game should have
     * @param creator The user who created the game and therefore shall be the
     *                owner
     * @since 2021-01-15
     */
    public GameDTO(String name, User creator) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void joinUser(User user) {
        this.users.add(user);
    }

    @Override
    public void leaveUser(User user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Game must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            if (this.owner.equals(user)) {
                updateOwner(users.iterator().next());
            }
        }
    }

    @Override
    public void updateOwner(User user) {
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException("User " + user.getUsername() + "not found. Owner must be member of game!");
        }
        this.owner = user;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public Gamefield getGamefield() {
        return gamefield;
    }

    @Override
    public void setGamefield(Gamefield gamefield) {
        this.gamefield = gamefield;
    }
}
