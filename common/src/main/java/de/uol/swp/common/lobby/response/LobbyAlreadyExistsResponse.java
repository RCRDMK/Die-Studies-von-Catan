package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.lobby.request.AbstractLobbyRequest;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Message sent to the client when a lobby already exists.
 *
 * @see AbstractLobbyRequest
 * @see de.uol.swp.common.message.ResponseMessage
 * @see de.uol.swp.common.user.User
 * @author Marius Birk and Carsten Dekker
 * @since 2020-12-02
 */
public class LobbyAlreadyExistsResponse extends AbstractResponseMessage {

    private String name;
    private UserDTO owner;

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public LobbyAlreadyExistsResponse() {
    }

    /**
     * Constructor
     *
     * @param newName name of the lobby
     * @param newOwner User trying to create the lobby
     * @since 2019-10-08
     */
    public LobbyAlreadyExistsResponse(String newName, UserDTO newOwner) {
        this.name = newName;
        this.owner = newOwner;
    }

    /**
     * Getter for the user variable
     *
     * @return User trying to create the lobby
     * @since 2020-12-02
     */
    public User getOwner() {
        return owner;
    }

}

