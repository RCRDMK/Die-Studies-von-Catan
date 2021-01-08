package de.uol.swp.client.lobby;

/**
 * Exception thrown in LobbyPresenter
 * <p>
 * This exception is thrown if the LeaveLobbyButton is activated and Variables are missing
 *
 * @author Alexander Losse, Iskander Yusupov
 */
class LobbyPresenterException extends RuntimeException {
    LobbyPresenterException(String s) {
        super(s);
    }
}
