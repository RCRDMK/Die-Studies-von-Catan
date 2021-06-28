package de.uol.swp.common.user.response.lobby;

import java.util.Objects;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Response that is sent to the User who tried to join a lobby with the wrong password
 * <p>
 * Contains the lobby name
 *
 * @author René Meyer
 * @since 2021-06-05
 */
public class WrongLobbyPasswordResponse extends AbstractResponseMessage {

    private final String lobbyName;

    /**
     * Constructor
     * <p>
     *
     * @param lobbyName the name of the lobby whose password was wrong
     * @author René Meyer
     * @since 2021-06-05
     */
    public WrongLobbyPasswordResponse(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Getter for the lobbyName
     *
     * @return the name of the lobby
     * @author René Meyer
     * @since 2021-06-05
     */
    public String getLobbyName() {
        return lobbyName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lobbyName);
    }
}
