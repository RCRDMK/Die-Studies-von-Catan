package de.uol.swp.common.game.message;

/**
 * Message sent by the server when a game is finished
 * <p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractGameMessage
 * @since 2021-04-18
 */
public class GameFinishedMessage extends AbstractGameMessage {
    private String gameName;

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public GameFinishedMessage(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Getter for the gameName
     * <p>
     *
     * @return gameName
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public String GetGameName() {
        return this.gameName;
    }
}
