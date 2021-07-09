package de.uol.swp.common.game.message;

/**
 * Message sent by the server when a game started
 * <p>
 *
 * @author Carsten Dekker
 * @since 2021-04-08
 */

public class GameStartedMessage extends AbstractGameMessage {

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public GameStartedMessage() {

    }

    /**
     * Constructor
     * <p>
     *
     * @param name name of the lobby/game
     * @author Carsten Dekker
     * @since 2021-04-08
     */
    public GameStartedMessage(String name) {
        this.name = name;
    }

    /**
     * getter for the lobbyName.
     *
     * @return returns the name of the lobby
     */
    public String getLobbyName() {
        return name;
    }
}
