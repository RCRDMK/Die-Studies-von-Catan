package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.message.RollDiceRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import de.uol.swp.server.lobby.*;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/**
 * Handles the game requests send by the users
 *
 * @author Kirstin, Pieter
 * @since 2021-01-07
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class GameService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);
    private LobbyManagement lobbyManagement;
    private AuthenticationService authenticationService;

    @Inject
    public GameService(EventBus bus) {
        super(bus);
    }

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
     * Handles RollDiceRequests found on the EventBus
     * <p>
     * If a RollDiceRequest is detected on the EventBus, this method is called.
     * It rolls the dices and sends a ResponseChatMessage containing the user who roll the dice
     * and the result to every user in the lobby.
     * @see de.uol.swp.common.game.message.RollDiceRequest
     * @param rollDiceRequest The RollDiceRequest found on the EventBus
     * @author Kirstin, Pieter
     * @since 2021-01-07
     */
    @Subscribe
    private void onRollDiceRequest (RollDiceRequest rollDiceRequest) {
        LOG.debug("Got new RollDiceRequest from user: " + rollDiceRequest.getUser());

        Dice dice = new Dice();
        dice.rollDice();
        Integer eyestostring = dice.getEyes();
        String eyes = eyestostring.toString();
        if (eyestostring == 8 || eyestostring == 11){
            ResponseChatMessage msg = new ResponseChatMessage("Player " + rollDiceRequest.getUser().getUsername() + " rolled an " + eyes, rollDiceRequest.getName(), "Dice", System.currentTimeMillis());
            post(msg);
        } else {
            ResponseChatMessage msg = new ResponseChatMessage("Player " + rollDiceRequest.getUser().getUsername() + " rolled a " + eyes, rollDiceRequest.getName(), "Dice", System.currentTimeMillis());
            post(msg);
        }

        LOG.debug("Posted ResponseChatMessage on eventBus");
    }

}
