package de.uol.swp.common.lobby.dto;

import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TreeSet;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

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
    private final Set<User> users = new TreeSet<>();
    private final Set<User> playersReady = new TreeSet<>();
    private transient final Timer timerForGameStart = new Timer();
    private int passwordHash = 0;
    private User owner;
    private int rdyResponsesReceived = 0;
    private String gameFieldVariant;
    private boolean gameStarted = false;
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
     * @author René Meyer
     * @see String
     * @since 2021-06-05
     */
    @Override
    public void setPassword(String password) {
        this.passwordHash = password.hashCode();
    }

    @Override
    public void updateOwner(User user) {
        if (!this.users.contains(user)) {
            throw new IllegalArgumentException(
                    "User " + user.getUsername() + "not found. Owner must be member of lobby!");
        }
        this.owner = user;
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    public void joinUser(User user) {
        this.users.add(user);
    }

    @Override
    public void joinPlayerReady(User user) {
        this.playersReady.add(user);
    }

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

    @Override
    public Set<User> getUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public Set<User> getPlayersReady() {
        return Collections.unmodifiableSet(playersReady);
    }

    @Override
    public String getGameFieldVariant() {
        return gameFieldVariant;
    }

    @Override
    public void setGameFieldVariant(String gfv) {
        this.gameFieldVariant = gfv;
    }

    @Override
    public void setPlayersReadyToNull() {
        this.playersReady.clear();
    }

    @Override
    public void incrementRdyResponsesReceived() {
        this.rdyResponsesReceived++;
    }

    @Override
    public int getRdyResponsesReceived() {
        return this.rdyResponsesReceived;
    }

    @Override
    public void setRdyResponsesReceived(int responsesReceived) {
        this.rdyResponsesReceived = responsesReceived;
    }

    @Override
    public boolean getGameStarted() {
        return this.gameStarted;
    }

    @Override
    public void setGameStarted(boolean value) {
        this.gameStarted = value;
    }

    @Override
    public Timer startTimerForGameStart() {
        timerStarted = true;
        return this.timerForGameStart;
    }

    @Override
    public void stopTimerForGameStart() {
        if (timerStarted) {
            timerForGameStart.cancel();
        }
        timerStarted = false;
    }

    @Override
    public int getMinimumAmountOfPlayers() {
        return minimumAmountOfPlayers;
    }

    @Override
    public void setMinimumAmountOfPlayers(int minimumAmountOfPlayers) {
        this.minimumAmountOfPlayers = minimumAmountOfPlayers;
    }

    @Override
    public boolean isUsedForTest() {
        return this.isUsedForTest;
    }

    @Override
    public void setUsedForTest(boolean value) {
        this.isUsedForTest = value;
    }

    /**
     * Setter for passwordHash. Needed for tempLobby reload in AllCreatedLobbiesResponse.java
     *
     * @param passwordHash the password to set this lobby's password to
     */
    public void setPasswordHash(int passwordHash) {
        this.passwordHash = passwordHash;
    }
}
