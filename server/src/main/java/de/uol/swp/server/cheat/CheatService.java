package de.uol.swp.server.cheat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * Service that handles all cheats.
 * <p>
 * It also has a function to detect a chatmessage as cheat.
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractService
 * @since 2021-04-17
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class CheatService extends AbstractService {
    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final ArrayList<String> cheatList = new CheatList().get();
    private final GameService gameService;

    /**
     * Constructor for CheatService
     * <p>
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     * @param gameService
     * @param bus eventbus
     */
    @Inject
    public CheatService(GameService gameService, EventBus bus) {
        super(bus);
        this.gameService = gameService;
    }

    /**
     * Function that parses and executes the cheat
     * <p>
     * Check for different cheats and execute.
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     * @param cheatMessage
     */
    public void parseExecuteCheat(RequestChatMessage cheatMessage) {
        // parse CheatPrefix
        var cheatMessageSplit = cheatMessage.getMessage().split("\\s");
        var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];

        // Cheatcode "rollx"
        // Usage: roll [int] [string]
        //   e.g. roll 2 testGameName

        // Check that roll cheatMessage has correct 3 arguments
        if (cheatPrefix.equals("roll") && cheatMessageSplit.length == 3) {
            var cheatEyesArgument = cheatMessage.getMessage().split("\\s")[1];
            var gameNameArgument = cheatMessage.getMessage().split("\\s")[2];
            if (cheatMessage.getSession().isPresent()) {
                var session = cheatMessage.getSession().get();
                var user = session.getUser();
                var rollDiceRequest = new RollDiceRequest(gameNameArgument, user, Integer.parseInt(cheatEyesArgument));
                gameService.onRollDiceRequest(rollDiceRequest);
            }
        }
        // Cheatcode "endgame"
        // Usage: endgame [string]
        //   e.g. endgame testGameName

        // Check that endgame cheatMessage has correct 2 arguments
        if (cheatPrefix.equals("endgame") && cheatMessageSplit.length == 2) {
            var cheatGameName = cheatMessage.getMessage().split("\\s")[1];
            if (cheatMessage.getSession().isPresent()) {
                var session = cheatMessage.getSession().get();
                var user = session.getUser();
                var game = gameService.getGameManagement().getGame(cheatGameName);
                if (game.isPresent()) {
                    var inventory = game.get().getInventory(user);
                    // Set User Victory Points to 10
                    inventory.setVictoryPoints(10);
                    // End Turn to trigger the Win
                    var endTurnRequest = new EndTurnRequest(cheatGameName, (UserDTO) user);
                    gameService.onEndTurnRequest(endTurnRequest);
                } else {
                    LOG.debug("Game not present!");
                }
            }
        }
        // @TODO: Endgame Scene still missing
        // Cheatcode "givememoney"
        //@Todo: give me x resource cards, warte auf Pieters Ticket, da terrainFieldContainer entfernt wurde?
        // Cheatcode "givemecardx"
        //@Todo: give me the resource card X, warte auf Pieters Ticket, da terrainFieldContainer entfernt wurde?
        // Cheatcode "letmebuild"
        //@Todo: let me free build
        // Cheatcode "moverobber"
        //@TODO: move the robber
    }

    /**
     * Checks if the chatmessage is a cheat.
     * <p>
     * Parses the chatmessage and checks the prefix before the space.
     * If the prefix is equal to an existing cheatcommand in the cheatList return true
     * else false.
     * If the cheatcommand is without argument return false
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     * @param cheatMessage chatMessage
     * @return true or false
     */
    public boolean isCheat(RequestChatMessage cheatMessage) {
        for (String cheatCode : cheatList) {
            try {
                var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];
                if (cheatPrefix.equals(cheatCode)) {
                    var cheatArgument = cheatMessage.getMessage().split("\\s")[1];
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                LOG.debug("Cheatcode invalid, argument missing");
                return false;
            }
        }
        return false;
    }
}
