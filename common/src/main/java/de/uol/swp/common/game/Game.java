package de.uol.swp.common.game;

import de.uol.swp.common.game.inventory.DevelopmentCardDeck;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * Interface to unify game objects
 * <p>
 * This is an Interface to allow for multiple types of game objects since it is possible that not every client has to
 * have every information of the game.
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
     *
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
     *
     * @since 2021-01-25
     */
    void joinUser(User user);

    /**
     * Removes an user from the game
     *
     * @param user The user to remove from the game
     *
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

    /**
     * Getter for the games users in a List.
     *
     * <p>Returns the users in the game as a list so we can look for users at certain indices.</p>
     *
     * @return List of Users
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    ArrayList<User> getUsersList();

    /**
     * Getter for a single user.
     *
     * <p>This getter returns a single User specified by an index.</p>
     *
     * @return A User object
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    User getUser(int index);

    /**
     * Getter for the GameField of this game
     *
     * @return The GameField of this game
     * @author Pieter Vogt, Marc Hermes
     * @see de.uol.swp.common.game.GameField
     * @since 2021-03-13
     */
    GameField getGameField();

    /**
     * Setter for the GameField of this game
     *
     * @param gameField the GameField to be set as the gameField of this Game
     *
     * @author Pieter Vogt, Marc Hermes
     * @see de.uol.swp.common.game.GameField
     * @since 2021-03-13
     */
    void setGameField(GameField gameField);

    /**
     * Sets up the Arraylist containing the users.
     *
     * <p>This is used to enable the server to adress users with indices. This was not possible with the Set-structure
     * of the Users.</p>
     */
    void setUpUserArrayList();

    /**
     * Returns a number, indicating whos turn it is.
     *
     * <p>The number represents the index inside the playersList, pointing to a certain Player.</p>
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    int getTurn();

    /**
     * Returns the number of total turns played so far.
     *
     * <p>This can be used for the summaryscreen at the end of a game.</p>
     *
     * @return Number of overall turns played so far
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    int getOverallTurns();

    /**
     * Increments the round number to the next player.
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    void nextRound();

    /**
     * Runs the initial Turn for every player.
     * <p>
     * If the game has 4 players, it lets the players do their turn in the following order: 1, 2, 3, 4, 4, 3, 2, 1.
     * </p>
     *
     * @author Pieter Vogt
     * @since 2021-03-30
     */
    void openingPhase();

    /**
     * Gives the inventory 1-4 a User
     *
     * @author Anton Nikiforov
     * @since 2021-04-01
     */
    void setUpInventories();

    /**
     * Getter for the Inventory from user
     *
     * @param user
     * @return The Inventory from user
     * @author Anton Nikiforov
     * @see de.uol.swp.common.game.inventory.Inventory
     * @since 2021-04-01
     */
    Inventory getInventory(User user);

    DevelopmentCardDeck getDevelopmentCardDeck();
}
