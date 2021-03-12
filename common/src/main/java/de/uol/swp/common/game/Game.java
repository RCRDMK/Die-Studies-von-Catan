package de.uol.swp.common.game;

import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * Interface to unify game objects
 * <p>
 * This is an Interface to allow for multiple types of game objects since it is
 * possible that not every client has to have every information of the game.
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.common.game.dto.GameDTO
 * @since 2021-01-15
 */
public interface Game extends Serializable {

    /**
     * Getter for the game's name
     *
     * @return A String containing the name of the game
     * @since 2021-01-15
     */
    String getName();

    /**
     * Changes the owner of the game
     *
     * @param user The user who should be the new owner
     * @since 2021-01-15
     */
    void updateOwner(User user);

    /**
     * Getter for the current owner of the game
     *
     * @return A User object containing the owner of the game
     * @since 2021-01-15
     */
    User getOwner();

    /**
     * Adds a new user to the game
     *
     * @param user The new user to add to the game
     * @since 2021-01-25
     */
    void joinUser(User user);

    /**
     * Removes an user from the game
     *
     * @param user The user to remove from the game
     * @since 2021-01-15
     */
    void leaveUser(User user);

    /**
     * Getter for all users in the game
     *
     * @return A Set containing all user in this game
     * @since 2021-01-15
     */
    Set<User> getUsers();

    Gamefield getGamefield();

    void setGamefield(Gamefield gamefield);

}
