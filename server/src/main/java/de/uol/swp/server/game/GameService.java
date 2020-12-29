package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Singleton;
import de.uol.swp.common.game.message.RollDiceRequest;
import de.uol.swp.common.game.message.RollDiceResponse;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import de.uol.swp.server.lobby.*;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Handles the lobby requests send by the users
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class GameService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);
    private LobbyManagement lobbyManagement;
    private AuthenticationService authenticationService;

    @Inject
    public GameService(EventBus eventBus) {super(eventBus); }


    /**
     * Prepares a given ServerMessage to be send to all players in the lobby and
     * posts it on the EventBus
     *
     * @param lobbyName Name of the lobby the players are in
     * @param message   the message to be send to the users
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        } else {
            throw new LobbyManagementException("Lobby unknown!");

        }
    }

    /**
     * Handles LobbyJoinUserRequests found on the EventBus
     * <p>
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a Lobby stored in the LobbyManagement and sends a UserJoinedLobbyMessage
     * to every user in the lobby.
     * If a lobby already has 4 users, this method will return a LobbyFullResponse to the user
     * who requested to join the lobby
     * If a lobby is not present, this method will return a JoinDeletedLobbyResponse to the user.
     * @param rollDiceRequest The LobbyJoinUserRequest found on the EventBus
     * @since 2019-10-08
     */
    @Subscribe
    public void onRollDiceRequest (RollDiceRequest rollDiceRequest) {
        Dice dice = new Dice();
        dice.rollDice();
        int eyes = dice.getEyes();
        sendToAllInLobby(rollDiceRequest.getName(), new RollDiceResponse(rollDiceRequest.getName(), rollDiceRequest.getUser(), eyes));
    }

}
