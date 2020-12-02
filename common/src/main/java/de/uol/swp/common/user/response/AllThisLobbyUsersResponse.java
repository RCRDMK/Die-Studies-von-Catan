package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllThisLobbyUsersResponse extends AbstractServerMessage {

    private static final long serialVersionUID = -7113321823425212173L;
    final private ArrayList<UserDTO> users = new ArrayList<>();
    private List<Session> receiver;

    /**
     * Default Constructor
     *
     * @implNote this constructor is needed for serialization
     * @since 2019-08-13
     */
    public AllThisLobbyUsersResponse(){
        // needed for serialization
    }

    /**
     * Constructor
     *
     * This constructor generates a new List of the logged in users from the given
     * Collection. The significant difference between the two being that the new
     * List contains copies of the User objects. These copies have their password
     * variable set to an empty String.
     *
     * @param users Collection of all users currently logged in
     * @since 2019-08-13
     */
    public AllThisLobbyUsersResponse(List<Session> users) {
        for (Session user : users) {
            this.users.add(UserDTO.createWithoutPassword(user.getUser()));
        }
    }

    /**
     * Getter for the list of users currently logged in
     *
     * @return list of users currently logged in
     * @since 2019-08-13
     */
    public List<UserDTO> getUsers() {
        return users;
    }

   /* @Override
    public List<Session> getReceiver() {
        return receiver;
    }

    @Override
    public void setReceiver(List<Session> receiver) {
        this.receiver = receiver;
    }*/
}
