package de.uol.swp.common.game.message;

import de.uol.swp.common.game.Game;

/**
 * Message sent by the server when a game is finished
 * <p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractGameMessage
 * @since 2021-04-18
 */
public class GameFinishedMessage extends AbstractGameMessage {
    private Game game;

    /**
     * Constructor
     * <p>
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public GameFinishedMessage(Game game) {
        this.game = game;
    }

    /**
     * Getter for the gameName
     * <p>
     *
     * @return gameName
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public Game GetGame() {
        return this.game;
    }
}
