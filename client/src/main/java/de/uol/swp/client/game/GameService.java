package de.uol.swp.client.game;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.game.message.RollDiceRequest;
import de.uol.swp.common.game.request.RetrieveAllGamesRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that manages games
 * <p>
 *
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
     *
     * @param name Name of the lobby where the user wants to roll the dice
     * @param user User who wants to roll the dice
     * @author Kirstin, Pieter
     * @see de.uol.swp.common.game.message.RollDiceRequest
     * @since 2021-01-07
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     * <p>
     * I have changed the place of the method to the new GameService.
     * It is a temporary method.
     */
    public void rollDiceTest(String name, User user) {
        RollDiceRequest rollDiceRequest = new RollDiceRequest(name, user);
        eventBus.post(rollDiceRequest);
    }

    /**
     * Posts a request to get a list of all existing games on the EventBus
     *
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.request.RetrieveAllGamesRequest
     * @since 2020-04-12
     */
    public void retrieveAllGames() {
        RetrieveAllGamesRequest cmd = new RetrieveAllGamesRequest();
        eventBus.post(cmd);
    }

    /**
     * Creates a new RetrieveAllThisGameUsersRequest and puts it on the Eventbus
     * <p>
     *
     * @param gameName Name of the game of which the User list was requested
     * @author Iskander Yusupov
     * @see de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest
     * @since 2020-03-14
     */
    public void retrieveAllThisGameUsers(String gameName) {
        RetrieveAllThisGameUsersRequest gameUsersRequest = new RetrieveAllThisGameUsersRequest(gameName);
        eventBus.post(gameUsersRequest);
    }

}
