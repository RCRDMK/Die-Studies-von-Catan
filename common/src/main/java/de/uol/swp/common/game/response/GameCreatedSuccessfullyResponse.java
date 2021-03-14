package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.UserDTO;


/**
 * Response sent to the client when a game is successfully created.
 * <p>
 * This Response is needed to get the current joinedUser and lobbyName in the gameScene
 *
 * @see de.uol.swp.common.message.ResponseMessage
 * @author René Meyer
 * @since 2020-03-13
 */
public class GameCreatedSuccessfullyResponse extends AbstractResponseMessage {

    public UserDTO joinedUser;
    public String lobbyName;

    public GameCreatedSuccessfullyResponse(UserDTO joinedUser, String lobbyName){
        this.joinedUser = joinedUser;
        this.lobbyName = lobbyName;
    }

    public UserDTO getJoinedUser(){
        return this.joinedUser;
    }

    public String getLobbyName(){
        return this.lobbyName;
    }
}
