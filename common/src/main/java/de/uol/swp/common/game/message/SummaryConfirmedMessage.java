package de.uol.swp.common.game.message;

/**
 * Message sent when a summary screen is confirmed
 * <p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractGameMessage
 * @since 2021-05-01
 */
public class SummaryConfirmedMessage extends AbstractGameMessage {
    private String gameName;

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public SummaryConfirmedMessage(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Getter for the gameName
     * <p>
     *
     * @return gameName
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public String getGameName() {
        return this.gameName;
    }
}