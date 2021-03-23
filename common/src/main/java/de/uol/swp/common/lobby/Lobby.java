package de.uol.swp.common.lobby;

import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * Interface to unify lobby objects
 * <p>
 * This is an Interface to allow for multiple types of lobby objects since it is
 * possible that not every client has to have every information of the lobby.
 * <p>
 * Enhanced by Carsten Dekker and Marius Birk
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.dto.LobbyDTO
 * @since 2020-12-04
 * Now the Lobby class extends Serializable
 * @since 2019-10-08
 */
public interface Lobby extends Serializable {

    /**
     * Getter for the lobby's name
     *
     * @return A String containing the name of the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    String getName();

    /**
     * Changes the owner of the lobby
     *
     * @param user The user who should be the new owner
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    void updateOwner(User user);

    /**
     * Getter for the current owner of the lobby
     *
     * @return A User object containing the owner of the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    User getOwner();

    /**
     * Adds a new user to the lobby
     *
     * @param user The new user to add to the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    void joinUser(User user);

    /**
     * Adds a new user to the "ready to start the game" players list
     *
     * @param user The new user that is ready to start
     * @author Marco Grawunder
     * @since 2021-01-24
     */

    void joinPlayerReady(User user);

    /**
     * Removes an user from the lobby
     *
     * @param user The user to remove from the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    void leaveUser(User user);

    /**
     * Getter for all users in the lobby
     *
     * @return A Set containing all user in this lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    Set<User> getUsers();

    /**
     * Getter for all user in this lobby which are ready to start the game
     *
     * @return A Set containing all user in this lobby which are ready to start the game
     * @author Marco Grawunder
     * @since 2021-01-24
     */

    Set<User> getPlayersReady();

    void setPlayersReadyToNull();
}
