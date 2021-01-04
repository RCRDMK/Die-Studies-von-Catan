package de.uol.swp.common.lobby.message;

/**
 * Message sent by the server when the size of a lobby changes, i.e. through joining/leaving a lobby
 * <p>
 * @author Ricardo Mook, Marc Hermes
 * @since 2020-12-18
 */

public class LobbySizeChangedMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     * <p>
     * @param name name of the lobby
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-12-18
     */
    public LobbySizeChangedMessage(String name){
        this.name = name;
    }

}
