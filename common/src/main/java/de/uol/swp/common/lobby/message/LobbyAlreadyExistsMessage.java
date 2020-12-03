package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Message sent to the client when a lobby already exists.
 *
 * @see de.uol.swp.common.lobby.message.AbstractLobbyRequest
 * @see de.uol.swp.common.message.ResponseMessage
 * @see de.uol.swp.common.user.User
 * @author Marius Birk and Carsten Dekker
 * @since 2020-12-02
 */
public class LobbyAlreadyExistsMessage extends AbstractLobbyMessage implements ResponseMessage, ServerMessage {

    /**
     * Default constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public LobbyAlreadyExistsMessage() {
    }

    /**
     * Constructor
     *
     * @param name name of the lobby
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public LobbyAlreadyExistsMessage(String name, UserDTO owner) {
        super(name, owner);
    }

    /**
     * Setter for the user variable
     *
     * @param owner  User trying to create the lobby
     * @since 2020-12-02
     */
    public void setOwner(UserDTO owner) {
        setUser(owner);
    }

    /**
     * Getter for the user variable
     *
     * @return User trying to create the lobby
     * @since 2020-12-02
     */
    public User getOwner() {
        return getUser();
    }

}

