package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.UserDTO;

/**
 * Request sent to the server when a user in the lobby wants to start the game
 * <p>
 *
 * @author Kirstin Beyer, Iskander Yusupov
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-01-24
 */
public class StartGameRequest extends AbstractLobbyRequest {
 private String gameFieldVariant;
 private int minimumAmountOfPlayers;
    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public StartGameRequest() {
    }

    /**
     * Constructor
     * <p>
     *
     * @param lobbyName name of the lobby
     * @param user      user trying to start the game
     * @author Kirstin Beyer, Iskander Yusupov
     * @since 2021-01-24
     */
    public StartGameRequest(String lobbyName, UserDTO user, String gameFieldVariant, int minimumAmountOfPlayers) {
        this.name=lobbyName;
        this.user=user;
        this.gameFieldVariant=gameFieldVariant;
        this.minimumAmountOfPlayers = minimumAmountOfPlayers;
    }

    public String getGameFieldVariant() {
        return gameFieldVariant;
    }

    public int getMinimumAmountOfPlayers() {
        return minimumAmountOfPlayers;
    }
}
