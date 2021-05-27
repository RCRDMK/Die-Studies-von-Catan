package de.uol.swp.common.lobby.response;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.Set;

public class JoinOnGoingGameResponse extends AbstractResponseMessage {

    private final MapGraph mapGraph;
    private final ArrayList<User> users;
    private final Set<User> humans;
    private final String gameFieldVariant;
    private final String gameName;
    private final UserDTO user;
    private final boolean joinedSuccessful;

    public JoinOnGoingGameResponse(String gameName, UserDTO user, boolean joinedSuccessful, MapGraph mapGraph, ArrayList<User> users, Set<User> humans, String gameFieldVariant) {
        this.gameName = gameName;
        this.user = user;
        this.joinedSuccessful = joinedSuccessful;
        this.mapGraph = mapGraph;
        this.users = users;
        this.humans = humans;
        this.gameFieldVariant = gameFieldVariant;
    }

    public MapGraph getMapGraph() {
        return mapGraph;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public Set<User> getHumans() {
        return humans;
    }

    public String getGameFieldVariant() {
        return gameFieldVariant;
    }

    public String getGameName() {
        return gameName;
    }

    public UserDTO getUser() {
        return user;
    }

    public boolean isJoinedSuccessful() {
        return joinedSuccessful;
    }
}
