package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Request for leaving all lobbies and logging out after exiting the program via x Button
 * <p>
 * This message is sent after exiting the program via x Button. The server will
 * respond with a AllLobbiesForSpecificUserResponse.
 *
 * @see de.uol.swp.common.lobby.response.AllLobbiesForSpecificUserResponse
 * @author René Meyer, Sergej Tulnev
 * @since 2020-04-12
 */
public class RetrieveAllLobbiesForUserRequest extends AbstractRequestMessage {
    private User user;

    public RetrieveAllLobbiesForUserRequest(){
        // needed for serialization
    }

    public RetrieveAllLobbiesForUserRequest(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

}
