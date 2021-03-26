package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Message sent by the server when a user successfully leaves a lobby
 * <p>
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @author Marco Grawunder
 * @since 2019-10-08
 */
public class UserLeftLobbyMessage extends AbstractLobbyMessage {

    //users Wird nirgends benutzt. Kann das weg? -Pieter
    final private ArrayList<UserDTO> users = new ArrayList<>();

    final private String lobbyOwner;

    /**
     * Default constructor
     * <p>
     * @implNote this constructor is needed for serialization
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public UserLeftLobbyMessage() {
        lobbyOwner="";
    }

    /**
     * Constructor
     * <p>
     * @param lobbyName name of the lobby
     * @param user user who left the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public UserLeftLobbyMessage(String lobbyName, UserDTO user, String lobbyOwner) {
        super(lobbyName, user);
        this.lobbyOwner=lobbyOwner;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public String getLobbyOwner(){
        return this.lobbyOwner;
    }
}
