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
    //@Todo: Maybe add in Future onPrivateInventoryChangeMessage from Ticket 169 to trigger a win or make inventory change visible?
    public void parseExecuteCheat(RequestChatMessage cheatMessage) {
        // parse CheatPrefix
        var cheatMessageSplit = cheatMessage.getMessage().split("\\s");
        var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];
        var gameNameArray = cheatMessage.getChat().split("_");

        // Cheats only get executed when they are typed in a game chat. For that we check for the game_ prefix
        if (gameNameArray.length > 1) {
            var gameName = gameNameArray[1];
            // Cheatcode "roll"
            // Usage: roll [int]
            //   e.g. roll 2

            // Check that roll cheatMessage has correct 2 arguments
            if (cheatPrefix.equals("roll") && cheatMessageSplit.length == 2) {
                var cheatEyesArgument = cheatMessage.getMessage().split("\\s")[1];
                if (cheatMessage.getSession().isPresent()) {
                    var session = cheatMessage.getSession().get();
                    var user = session.getUser();
                    var rollDiceRequest = new RollDiceRequest(gameName, user, Integer.parseInt(cheatEyesArgument));
                    gameService.onRollDiceRequest(rollDiceRequest);
                }
            }
            // Cheatcode "endgame 1"
            // Usage: endgame [int]
            //   e.g. endgame 1

            // Check that endgame cheatMessage has correct 2 arguments
            else if (cheatPrefix.equals("endgame") && cheatMessageSplit.length == 2) {
                if (cheatMessage.getSession().isPresent()) {
                    var bool = Integer.parseInt(cheatMessage.getMessage().split("\\s")[1]);
                    var session = cheatMessage.getSession().get();
                    var user = session.getUser();
                    var game = gameService.getGameManagement().getGame(gameName);
                    if (game.isPresent() && bool == 1) {
                        var inventory = game.get().getInventory(user);
                        // Set User Victory Points to 10
                        inventory.setVictoryPoints(10);
                        // End Turn to trigger the Win
                        var endTurnRequest = new EndTurnRequest(gameName, (UserDTO) user);
                        //@Todo: Maybe change onEndTurnRequest in Future to onPrivateInventoryChangeMessage from Ticket 169?
                        gameService.onEndTurnRequest(endTurnRequest);
                    } else {
                        LOG.debug("Wrong endgame command! Make sure to use endgame 1!");
                    }
                }
            }
            // Cheatcode "givemeall"
            // Usage: givemeall [int]
            //   e.g. givemeall 15
            // Gives user [int] ressources of each card and 1 of each development cards

            // Check that givemeall cheatMessage has correct 2 arguments
            else if (cheatPrefix.equals("givemeall") && cheatMessageSplit.length == 2) {
                var resourceAmount = Integer.parseInt(cheatMessage.getMessage().split("\\s")[1]);
                if (cheatMessage.getSession().isPresent()) {
                    var session = cheatMessage.getSession().get();
                    var user = session.getUser();
                    var game = gameService.getGameManagement().getGame(gameName);
                    if (game.isPresent()) {
                        var inventory = game.get().getInventory(user);
                        // Increase ressources by resourceAmount
                        inventory.incCard("Lumber", resourceAmount);
                        inventory.incCard("Brick", resourceAmount);
                        inventory.incCard("Wool", resourceAmount);
                        inventory.incCard("Grain", resourceAmount);
                        inventory.incCard("Ore", resourceAmount);
                        // Increase development cards
                        inventory.cardKnight.incNumber();
                        inventory.cardMonopoly.incNumber();
                        inventory.cardRoadBuilding.incNumber();
                        inventory.cardYearOfPlenty.incNumber();
                        LOG.debug(inventory.getPrivateView());
                    } else {
                        LOG.debug("Game not present!");
                    }
                }
            }
            // Cheatcode "givemecard"
            // Usage: givemecard [string] [int]
            //   e.g. givemecard knight 1
            // Gives the user [int] of the provided cards.

            // Check that givemecard cheatMessage has correct 3 arguments
            else if (cheatPrefix.equals("givemecard") && cheatMessageSplit.length == 3) {
                var cardName = cheatMessage.getMessage().split("\\s")[1];
                var cardAmount = Integer.parseInt(cheatMessage.getMessage().split("\\s")[2]);
                if (cheatMessage.getSession().isPresent()) {
                    var session = cheatMessage.getSession().get();
                    var user = session.getUser();
                    var game = gameService.getGameManagement().getGame(gameName);
                    if (game.isPresent()) {
                        var inventory = game.get().getInventory(user);
                        switch (cardName) {
                            case "lumber":
                                inventory.incCard("Lumber", cardAmount);
                                break;
                            case "brick":
                                inventory.incCard("Brick", cardAmount);
                                break;
                            case "grain":
                                inventory.incCard("Grain", cardAmount);
                                break;
                            case "wool":
                                inventory.incCard("Wool", cardAmount);
                                break;
                            case "ore":
                                inventory.incCard("Ore", cardAmount);
                                break;
                            case "monopoly":
                                inventory.cardMonopoly.incNumber(cardAmount);
                                break;
                            case "knight":
                                inventory.cardKnight.incNumber(cardAmount);
                                break;
                            case "roadbuilding":
                                inventory.cardRoadBuilding.incNumber(cardAmount);
                                break;
                            case "yearofplenty":
                                inventory.cardYearOfPlenty.incNumber(cardAmount);
                                break;
                            case "victory":
                                inventory.incCardVictoryPoint(cardAmount);
                                break;
                        }
                        LOG.debug(inventory.getPrivateView());
                    } else {
                        LOG.debug("Game not present!");
                    }
                }
            }
        } else {
            LOG.debug("Cheat not entered in an active game!");
        }
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
