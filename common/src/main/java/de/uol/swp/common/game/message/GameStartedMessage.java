package de.uol.swp.common.game.message;

/**
 * Message sent by the server when a game started
 * <p>
 *
 * @author Carsten Dekker
 * @since 2021-04-08
 */

public class GameStartedMessage extends AbstractGameMessage {

    private final String lobbyName;

    /**
     * Constructor
     * <p>
     *
     * @param name name of the lobby/game
     * @author Carsten Dekker
     * @since 2021-04-08
     */
    public GameStartedMessage(String name) {
        this.lobbyName = name;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}
