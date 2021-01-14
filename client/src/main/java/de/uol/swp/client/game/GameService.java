package de.uol.swp.client.game;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.game.message.RollDiceRequest;
import de.uol.swp.common.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that manages games
 * <p>
 * @author Carsten Dekker
 * @since 2021-01-13
 */

@SuppressWarnings("UnstableApiUsage")
public class GameService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);

    private final EventBus eventBus;

    @Inject
    public GameService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Creates a new RollDiceRequest and puts it on the Eventbus
     * <p>
     * @param name Name of the lobby where the user wants to roll the dice
     * @param user User who wants to roll the dice
     * @see de.uol.swp.common.game.message.RollDiceRequest
     * @author Kirstin, Pieter
     * @since 2021-01-07
     *
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     *
     * I have changed the place of the method to the new GameService.
     * It is a temporary method.
     */
    public void rollDiceTest(String name, User user) {
        RollDiceRequest rollDiceRequest = new RollDiceRequest(name, user);
        eventBus.post(rollDiceRequest);
    }
}
