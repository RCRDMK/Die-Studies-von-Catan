package de.uol.swp.client.game;

/**
 * Exception thrown in GamePresenter
 * <p>
 * This exception is thrown if the onLeaveGame method is called and Variables are missing
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-03-05
 */
class GamePresenterException extends RuntimeException {
    GamePresenterException(String s) {
        super(s);
    }
}
