package de.uol.swp.client.game;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.game.request.GameLeaveUserRequest;
import de.uol.swp.common.game.request.RetrieveAllGamesRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.request.RetrieveAllThisLobbyUsersRequest;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
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
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @author Kirstin, Pieter
     * @since 2021-01-07
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     */
    public void rollDice(String name, User user) {
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

    /**
     * Posts a request to leave the game on the Eventbus
     *
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.GameLeaveUserRequest
     * @since 2021-03-04
     */
    public void leaveGame(String game, User user) {
        GameLeaveUserRequest leaveRequest = new GameLeaveUserRequest(game, (UserDTO) user);
        eventBus.post(leaveRequest);
    }
}
