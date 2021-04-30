package de.uol.swp.common.game.message;

import de.uol.swp.common.game.dto.GameDTO;

/**
 * Message sent by the server when a game is finished
 * <p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractGameMessage
 * @since 2021-04-18
 */
public class GameFinishedMessage extends AbstractGameMessage {
    private GameDTO game;

    /**
     * Constructor
     * <p>
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public GameFinishedMessage(GameDTO game) {
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
    public GameDTO GetGame() {
        return this.game;
    }
}
