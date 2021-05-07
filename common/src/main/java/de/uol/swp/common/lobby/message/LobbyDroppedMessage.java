package de.uol.swp.common.lobby.message;

/**
 * Message sent by the server when a lobby is dropped
 * <p>
 *
 * @author Ricardo Mook, Marc Hermes
 * @since 2020-12-17
 */

public class LobbyDroppedMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     * <p>
     *
     * @param name name of the lobby
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-12-17
     */
    public LobbyDroppedMessage(String name) {
        this.name = name;
    }

}
