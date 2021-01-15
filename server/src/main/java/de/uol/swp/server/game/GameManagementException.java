package de.uol.swp.server.game;
/**
 * Exception thrown in LobbyManagement
 *
 *
 *
 */

public class GameManagementException extends RuntimeException {
    /**
     * Constructor
     *
     * @param s String containing the cause for the exception.
     * @since 2021-01-15
     */
    public GameManagementException(String s) {
        super(s);
    }
}
