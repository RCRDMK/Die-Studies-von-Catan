package de.uol.swp.common.lobby.dto;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;

/**
 * Object to transfer the information of a game lobby
 * <p>
 * This object is used to communicate the current state of game lobbies between
 * the server and clients. It contains information about the Name of the lobby,
 * who owns the lobby and who joined the lobby.
 * <p>
 * enhanced by Marc Hermes 2021-03-25
 * enhanced by René Meyer 2021-06-05 for protected lobby support
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class LobbyDTO implements Lobby {

    private final String name;
    private int passwordHash = 0;
    private User owner;
    private final Set<User> users = new TreeSet<>();
    private final Set<User> playersReady = new TreeSet<>();
    private int rdyResponsesReceived = 0;
    private String gameFieldVariant;
    private boolean gameStarted = false;
    private transient final Timer timerForGameStart = new Timer();
    private boolean timerStarted = false;
    private boolean isUsedForTest = false;
    private int minimumAmountOfPlayers;

    /**
     * Constructor
     *
     * @param name    The name the lobby should have
     * @param creator The user who created the lobby and therefore shall be the
     *                owner
     * @author Marco Grawunder
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public LobbyDTO(String name, User creator) {
        this.name = name;
        this.owner = creator;
        this.users.add(creator);
    }

    /**
     * Getter for the lobby's name
     *
     * @return A String containing the name of the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Getter for passwordHash
     * <p>
     *
     * @return passwordHash integer
     * @author René Meyer
     * @since 2021-06-05
     */
    @Override
    public int getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Setter for password
     * <p>
     *
     * @return hash of the password
     * @author René Meyer
     * @see String
     * @since 2021-06-05
     */
    @Override
    public void setPassword(String password) {
        this.passwordHash = password.hashCode();
    }

    /**
     * Setter for passwordHash. Needed for tempLobby reload in AllCreatedLobbiesResponse.java
     *
     * @param passwordHash the password to set this lobby's password to
     * @author René Meyer
     * @since 2021-06-05
     */
    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Getter for the gameFieldVariant
     *
     * @return the String description of the gameFieldVariant
     * @author Marc Hermes
     * @since 2021-05-18
     */
    @Override
    public String getGameFieldVariant() {
        return gameFieldVariant;
    }

    /**
     * Adds a new user to the lobby
     *
     * @param user The new user to add to the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Override
    public void joinUser(User user) {
        this.users.add(user);
    }

    /**
     * Adds a new user to the "ready to start the game" players list
     *
     * @param user The new user that is ready to start
     * @author Marco Grawunder
     * @since 2021-01-24
     */
    @Override
    public void joinPlayerReady(User user) {
        this.playersReady.add(user);
    }

    /**
     * Removes an user from the lobby
     * Number of users must be bigger than 1.
     * If lobby owner left lobby, lobby owner will be updated.
     *
     * @param user The user to remove from the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Override
    public void leaveUser(User user) {
        if (users.size() == 1) {
            throw new IllegalArgumentException("Lobby must contain at least one user!");
        }
        if (users.contains(user)) {
            this.users.remove(user);
            if (this.owner.equals(user)) {
                updateOwner(users.iterator().next());
            }
        }
    }

    /**
     * Changes the owner of the lobby
     *
     * @param user The user who should be the new owner
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Override
    public void updateOwner(User user) {
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException("User " + user.getUsername() + "not found. Owner must be member of lobby!");
        }
        this.owner = user;
    }

    /**
     * Getter for the current owner of the lobby
     *
     * @return A User object containing the owner of the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Override
    public User getOwner() {
        return owner;
    }

    /**
     * Getter for all users in the lobby
     *
     * @return A Set containing all user in this lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    /**
     * Getter for all user in this lobby which are ready to start the game
     *
     * @return A Set containing all user in this lobby which are ready to start the game
     * @author Marco Grawunder
     * @since 2021-01-24
     */
    @Override
    public Set<User> getPlayersReady() {
        return Collections.unmodifiableSet(playersReady);
    }

    /**
     * Clears the playersReady Set
     *
     * @author Marc Hermes
     * @since 2021-05-18
     */
    @Override
    public void setPlayersReadyToNull() {
        this.playersReady.clear();
    }

    /**
     * Sets the ready-responses for this lobby to a certain value, usually 0.
     *
     * @param responsesReceived the ready-responses received in this lobby
     * @author Marc Hermes
     * @since 2021-03-23
     */
    @Override
    public void setRdyResponsesReceived(int responsesReceived) {
        this.rdyResponsesReceived = responsesReceived;
    }

    /**
     * Returns the amount ready-responses received for this lobby
     *
     * @return an int Value representing the amount of ready-responses received
     * @author Marc Hermes
     * @since 2021-03-23
     */
    @Override
    public int getRdyResponsesReceived() {
        return this.rdyResponsesReceived;
    }

    /**
     * Increases the amount of the received ready-responses of this lobby by 1.
     *
     * @author Marc Hermes
     * @since 2021-03-23
     */
    @Override
    public void incrementRdyResponsesReceived() {
        this.rdyResponsesReceived++;
    }

    /**
     * Setter for the gameFieldVariant
     *
     * @param gfv the String description of the gameFieldVariant
     */
    @Override
    public void setGameFieldVariant(String gfv) {
        this.gameFieldVariant = gfv;
    }

    /**
     * Returns a boolean value saying whether the game of this lobby started or not
     *
     * @return True if the game started, false if not (users in the lobby still waiting)
     * @author Carsten Dekker
     * @since 2021-04-08
     */
    @Override
    public boolean getGameStarted() {
        return this.gameStarted;
    }

    /**
     * Sets the value of the gameStarted variable
     *
     * @param value True if the game started, false if not
     * @author Carsten Dekker
     * @since 2021-04-08
     */
    @Override
    public void setGameStarted(boolean value) {
        this.gameStarted = value;
    }

    /**
     * Method used to start the timer of the lobby for the gameStart
     *
     * @return the timer of this lobby
     * @author Marc Hermes
     * @since 2021-05-18
     */
    @Override
    public Timer startTimerForGameStart() {
        timerStarted = true;
        return this.timerForGameStart;
    }

    /**
     * Method used to stop the timer of the lobby for the gameStart
     *
     * @author Marc Hermes
     * @since 2021-05-18
     */
    @Override
    public void stopTimerForGameStart() {
        if (timerStarted) {
            timerForGameStart.cancel();
        }
        timerStarted = false;
    }

    /**
     * Setter for the minimum amount of players for the game corresponding to this lobby
     *
     * @param minimumAmountOfPlayers the minimum amount of users to play in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Override
    public void setMinimumAmountOfPlayers(int minimumAmountOfPlayers) {
        this.minimumAmountOfPlayers = minimumAmountOfPlayers;
    }

    /**
     * Getter for the minimum amount of players for the game corresponding to this lobby
     *
     * @return the minimum amount of users to play in the game
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Override
    public int getMinimumAmountOfPlayers() {
        return minimumAmountOfPlayers;
    }

    /**
     * Getter for the boolean value if the lobby will be used for test purposes
     *
     * @return true if used for test, false if not
     * @author Marc Hermes
     * @since 2021-06-06
     */
    @Override
    public boolean isUsedForTest() {
        return this.isUsedForTest;
    }

    /**
     * Setter for the boolean value isUsedForTest
     *
     * @param value the value to set isUsedForTest to
     * @author Marc Hermes
     * @since 2021-06-06
     */
    @Override
    public void setUsedForTest(boolean value) {
        this.isUsedForTest = value;
    }
}
