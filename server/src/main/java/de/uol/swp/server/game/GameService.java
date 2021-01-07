package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.message.RollDiceRequest;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.dice.Dice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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

    @Inject
    public GameService(EventBus bus) {
        super(bus);
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
        String eyes = Integer.toString(dice.getEyes());
        if (dice.getEyes() == 8 || dice.getEyes() == 11){
            ResponseChatMessage msg = new ResponseChatMessage("Player " + rollDiceRequest.getUser().getUsername() + " rolled an " + eyes, rollDiceRequest.getName(), "Dice", System.currentTimeMillis());
            post(msg);
        } else {
            ResponseChatMessage msg = new ResponseChatMessage("Player " + rollDiceRequest.getUser().getUsername() + " rolled a " + eyes, rollDiceRequest.getName(), "Dice", System.currentTimeMillis());
            post(msg);
        }

        LOG.debug("Posted ResponseChatMessage on eventBus");
    }

}
