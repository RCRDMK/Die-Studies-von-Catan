package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 *
 *
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