package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;


/**
 * Message sent to the client when a lobby already exists.
 *
 * @author Marius Birk and Carsten Dekker
 * @see de.uol.swp.common.message.ResponseMessage
 * @since 2020-12-02
 */
public class GameAlreadyExistsResponse extends AbstractResponseMessage {


    private final String lobbyName;

    /**
     * Constructor
     *
     * @param lobbyName Name of the Lobby
     * @author Marius Birk and Carsten Dekker
     * @since 2020-12-02
     */
    public GameAlreadyExistsResponse(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    /**
     * Getter for the lobbyname
     *
     * @return the lobbyname as a string
     * @author Marius Birk and Carsten Dekker
     * @since 2020-12-02
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * The default hashCode method to get the objects hash code
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(lobbyName);
    }

}

