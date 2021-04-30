package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message sent by the server when a game is finished
 * <p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractGameMessage
 * @since 2021-04-18
 */
public class SummaryConfirmedMessage extends AbstractGameMessage {
    private String gameName;
    private User user;

    /**
     * Constructor
     * <p>
     *
     * @param gameName name of the game
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public SummaryConfirmedMessage(String gameName, User user) {
        this.user = user;
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

    public User GetUser() {
        return this.user;
    }
}