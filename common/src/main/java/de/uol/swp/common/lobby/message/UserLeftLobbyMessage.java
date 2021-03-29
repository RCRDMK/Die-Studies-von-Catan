package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;

/**
 * Message sent by the server when a user successfully leaves a lobby
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.message.AbstractLobbyMessage
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class UserLeftLobbyMessage extends AbstractLobbyMessage {

    private ArrayList<UserDTO> users = new ArrayList<>();

    final private String lobbyOwner;

    /**
     * Default constructor
     * <p>
     *
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
     *
     * @param lobbyName name of the lobby
     * @param user      user who left the lobby
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    public UserLeftLobbyMessage(String lobbyName, UserDTO user, ArrayList<UserDTO> lobbyUsers, String lobbyOwner) {
        super(lobbyName, user);
        this.users = lobbyUsers;
        this.lobbyOwner=lobbyOwner;
    }

    public ArrayList<UserDTO> getUsers() {
        return users;
    }

    public String getLobbyOwner(){
        return this.lobbyOwner;
    }
}
