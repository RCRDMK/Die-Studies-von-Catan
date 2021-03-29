package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameField;
import de.uol.swp.common.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * Object to transfer the information of a game
 * <p>
 * This object is used to communicate the current state of games between the server and clients. It contains information
 * about the Name of the game, who owns the game.
 * <p>
 * enhanced by Pieter Vogt 2021-03-26
 *
 * @author Iskander Yusupov
 * @since 2021-01-15
 */
public class GameDTO implements Game {

    private final String name;
    private User owner;
    private final Set<User> users = new TreeSet<>();
    private GameField gameField;
    private int overallTurns = 0; //This just counts +1 every time a player ends his turn. (good for Summaryscreen for example)
    private int turn = 0; //this points to the index of the user who now makes his turn.
    private ArrayList<User> userArrayList = new ArrayList<User>();


    /**
     * Constructor
     *
     * @param name    The name the game should have
     * @param creator The user who created the game and therefore shall be the owner
     *
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

    /**
     * Converts the Set of Users into an Arraylist
     *
     * <p>Without conversion of user-set into user-Arraylist, we have no tool to adress a specific user with
     * gamelogic.</p>
     *
     * @return Arraylist of users
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public ArrayList<User> getUsersList() {
        return userArrayList;
    }

    @Override
    public User getUser(int index) {
        return userArrayList.get(index);
    }

    @Override
    public GameField getGameField() {
        return gameField;
    }

    @Override
    public void setGameField(GameField gameField) {
        this.gameField = gameField;
    }

    @Override
    public void setUpUserArrayList() {
        userArrayList.addAll(users);
    }

    /**
     * This returns the index of the player who has the current turn.
     *
     * <p>This can be used to get the currently turning user.</p>
     *
     * @return The user that currently makes his turn.
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public int getTurn() {
        return overallTurns % users.size();
    }

    /**
     * Returns the number of turnes played in the current game as a whole.
     *
     * <p>Returns the overall turns played. It is primarily used to determine the current active player. This can e.g.
     * also be useful for the summaryscreen after a game in an informative manner.</p>
     *
     * @return Overall number of turnes played in the current game.
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public int getOverallTurns() {
        return overallTurns;
    }

    /**
     * Incrementing the counter of turns made in the game.
     *
     * <p>This method increments the counter that keeps track of how many turns were made in the game overall.</p>
     *
     * @author Pieter Vogt
     * @since 2021-03-26
     */
    @Override
    public void nextRound() {
        overallTurns++;
    }
}
