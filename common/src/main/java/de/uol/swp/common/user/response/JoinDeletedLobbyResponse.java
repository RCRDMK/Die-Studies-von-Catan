package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * Response that is to sent to the User who requested to join a full Lobby
 * Contains the lobbyName, so the User can get a chat feedback, which lobby is full.
 *
 * @author Sergej
 * @since 2020-12-19
 */

public class JoinDeletedLobbyResponse extends AbstractResponseMessage {

    private String lobbyName;

    public JoinDeletedLobbyResponse(String lobbyName){
        this.lobbyName = lobbyName;
    }

    public String getLobbyName(){
        return lobbyName;
    }

    @Override
    public int hashCode(){
        return Objects.hash(lobbyName);
    }

}