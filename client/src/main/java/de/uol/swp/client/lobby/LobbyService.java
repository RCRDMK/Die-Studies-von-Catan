package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.message.LobbyLeaveUserRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;

/**
 * Classes that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Posts a request to create a lobby on the EventBus.
     * Returns a boolean. If the Request is posted on the eventbus it returns true. Is the String name blank or empty it returns false.
     * If the name is null, the exception is caught and posted on the bus. Therefore we return also false, cause no lobby was created.
     *
     * @param name Name chosen for the new lobby
     * @param user User who wants to create the new lobby
     * @see de.uol.swp.common.lobby.message.CreateLobbyRequest
     * @since 2019-11-20
     */
    public boolean createNewLobby(String name, UserDTO user) {
        try {
            if (name.equals(null) || name.trim().isEmpty() || name.trim().isBlank() || name.startsWith(" ") || name.endsWith(" ")) {
                return false;
            } else {
                CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(name, user);
                eventBus.post(createLobbyRequest);
                return true;
            }
        }catch(NullPointerException e){
            eventBus.post(e);
            return false;
        }

    }

    /**
     * Posts a request to join a specified lobby on the EventBus
     *
     * @param name Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     * @see de.uol.swp.common.lobby.message.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    public void joinLobby(String name, UserDTO user) {
        LobbyJoinUserRequest joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    public void leaveLobby(String name, UserDTO user) {
        LobbyLeaveUserRequest leaveUserRequest = new LobbyLeaveUserRequest(name, user);
        eventBus.post(leaveUserRequest);
    }

    /**
     * Posts a request to get a list of all existing lobbies on the EventBus
     *
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-04-12
     * @author Carsten Dekker and Marius Birk
     */

    public void retrieveAllLobbies() {
        RetrieveAllLobbiesRequest cmd = new RetrieveAllLobbiesRequest();
        eventBus.post(cmd);
    }
}
