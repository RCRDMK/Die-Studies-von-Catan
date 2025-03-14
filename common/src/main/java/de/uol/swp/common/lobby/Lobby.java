package de.uol.swp.common.lobby;

import java.io.Serializable;
import java.util.Set;
import java.util.Timer;

import de.uol.swp.common.user.User;


/**
 * Interface to unify lobby objects
 * <p>
 * This is an Interface to allow for multiple types of lobby objects since it is possible that not every client has to
 * have every information of the lobby.
 * <p>
 * Enhanced by Carsten Dekker and Marius Birk
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.dto.LobbyDTO
 * @since 2020-12-04 Now the Lobby class extends Serializable
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
     * Getter for password hash
     *
     * @author René Meyer
     * @since 2021-06-05
     */
    int getPasswordHash();

    /**
     * Setter for the lobby password
     *
     * @author René Meyer
     * @since 2021-06-05
     */
    void setPassword(String password);

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

    /**
     * Getter for the gameFieldVariant
     *
     * @return the String description of the gameFieldVariant
     * @author Marc Hermes
     * @since 2021-05-18
     */
    String getGameFieldVariant();

    /**
     * Setter for the gameFieldVariant
     *
     * @param s the String description of the gameFieldVariant
     */
    void setGameFieldVariant(String s);

    /**
     * Clears the playersReady Set
     *
     * @author Marc Hermes
     * @since 2021-05-18
     */
    void setPlayersReadyToNull();

    /**
     * Increases the amount of the received ready-responses of this lobby by 1.
     *
     * @author Marc Hermes
     * @since 2021-03-23
     */
    void incrementRdyResponsesReceived();

    /**
     * Returns the amount ready-responses received for this lobby
     *
     * @return an int Value representing the amount of ready-responses received
     * @author Marc Hermes
     * @since 2021-03-23
     */
    int getRdyResponsesReceived();

    /**
     * Sets the ready-responses for this lobby to a certain value, usually 0.
     *
     * @param responsesReceived the ready-responses received in this lobby
     * @author Marc Hermes
     * @since 2021-03-23
     */
    void setRdyResponsesReceived(int responsesReceived);

    /**
     * Returns a boolean value saying whether the game of this lobby started or not
     *
     * @return True if the game started, false if not (users in the lobby still waiting)
     * @author Carsten Dekker
     * @since 2021-04-08
     */
    boolean getGameStarted();

    /**
     * Sets the value of the gameStarted variable
     *
     * @param value True if the game started, false if not
     * @author Carsten Dekker
     * @since 2021-04-08
     */
    void setGameStarted(boolean value);

    /**
     * Method used to start the timer of the lobby for the gameStart
     *
     * @return the timer of this lobby
     * @author Marc Hermes
     * @since 2021-05-18
     */
    Timer startTimerForGameStart();

    /**
     * Method used to stop the timer of the lobby for the gameStart
     *
     * @author Marc Hermes
     * @since 2021-05-18
     */
    void stopTimerForGameStart();

    /**
     * Getter for the minimum amount of players for the game corresponding to this lobby
     *
     * @return the minimum amount of users to play in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    int getMinimumAmountOfPlayers();

    /**
     * Setter for the minimum amount of players for the game corresponding to this lobby
     *
     * @param amount the minimum amount of users to play in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    void setMinimumAmountOfPlayers(int amount);

    /**
     * Getter for the boolean value if the lobby will be used for test purposes
     *
     * @return true if used for test, false if not
     * @author Marc Hermes
     * @since 2021-06-06
     */
    boolean isUsedForTest();

    /**
     * Setter for the boolean value isUsedForTest
     *
     * @param value the value to set isUsedForTest to
     * @author Marc Hermes
     * @since 2021-06-06
     */
    void setUsedForTest(boolean value);
}
