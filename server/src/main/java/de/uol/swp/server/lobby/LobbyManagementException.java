package de.uol.swp.server.lobby;

/**
 * Exception thrown in LobbyManagement
 * <p>
 * This exception is thrown if someone wants to leave a Lobby and is not in this Lobby.
 * This exception is thrown if someone wants to Join a Lobby and is already in this Lobby.
 * This exception is thrown if someone wants to leave a Lobby and the Lobby does not exists.
 */
public class LobbyManagementException extends RuntimeException {

    /**
     * Constructor
     *
     * @author Marco Grawunder
     * @param s String containing the cause for the exception.
     * @since 2019-07-08
     */
    public LobbyManagementException(String s) {
        super(s);
    }
}
