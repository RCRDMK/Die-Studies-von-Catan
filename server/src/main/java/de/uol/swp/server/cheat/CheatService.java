package de.uol.swp.server.cheat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service that handles all cheats.
 * <p>
 * <h1>Cheatcodes with Examples:</h1>
 *
 * <h2><u>Cheatcode "givemecard"</u> </h2>
 * <b>Usage:</b> givemecard [string] [int]<p>
 * <b> e.g. givemecard knight 1</b><p>
 * <b>possible strings:</b>
 * lumber,
 * brick,
 * grain,
 * wool,
 * ore,
 * monopoly,
 * knight,
 * roadbuilding,
 * yearofplenty,
 * victory
 * <p><b>Gives the user [int] amount of the provided cards.</b></p>
 *
 *
 * <p><h2><u>Cheatcode "givemeall"</u> </h2>
 * <b>Usage:</b> givemeall [int]<p>
 * <b>e.g. givemeall 15</b>
 * <p><b>Gives user [int] ressources of each card and 1 of each development cards</b></p>
 *
 *
 * <p><h2><u>Cheatcode "endgame 1"</u> </h2>
 * <b>Usage:</b> endgame 1
 * <p><b>Gives user 10 victory points and ends the game</b></p>
 *
 *
 * <p><h2><u>Cheatcode "roll"</u> </h2>
 * <b>Usage:</b> roll [int]<p>
 * <b>e.g. roll 2</b>
 * <p><b>Rolls the dice with the provided [int]</b></p>
 *
 * @author René Meyer, Sergej Tulnev
 * @see AbstractService
 * @since 2021-04-17
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class CheatService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);
    private final GameService gameService;

    /**
     * Constructor for CheatService
     * <p>
     *
     * @param gameService
     * @param bus         eventbus
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     */
    @Inject
    public CheatService(GameService gameService, EventBus bus) {
        super(bus);
        this.gameService = gameService;
    }

    private enum Cheat {
        endgame, givemeall, givemecard, roll
    }

    /**
     * Function that parses and executes the cheat
     * <p>
     * Check for different cheats and execute.<p>
     * For the roll Cheat we call the onRollDiceRequest with the cheatEyesArgument<p>
     * For the endgame Cheat we give the user who sent the cheat 10 victory points to trigger a win <p>
     * For the givemeall and givemecard Cheat we manipulate the private Inventory and update the Inventories in the gameService
     *
     * @param cheatMessage
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     */
    public void parseExecuteCheat(RequestChatMessage cheatMessage) {
        // parse CheatPrefix
        var cheatMessageSplit = cheatMessage.getMessage().split("\\s");
        var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];
        var gameNameArray = cheatMessage.getChat().split("_");

        // Cheats only get executed when they are typed in a game chat. For that we check for the game_ prefix
        if (gameNameArray.length > 1) {
            var gameName = gameNameArray[1];
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
                        // Update Inventory to trigger the Win
                        gameService.updateInventory(game.get());
                    } else {
                        LOG.debug("Wrong endgame command! Make sure to use endgame 1!");
                    }
                }
            }

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
                        gameService.updateInventory(game.get());
                        LOG.debug(inventory.getPrivateView());
                    } else {
                        LOG.debug("Game not present!");
                    }
                }
            }
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
                        gameService.updateInventory(game.get());
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
     * If the prefix is equal to an existing cheatcommand in the Cheat Enum return true
     * else false.
     * If the cheatcommand is without argument return false
     *
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     * @param cheatMessage chatMessage
     * @return true or false
     */
    public boolean isCheat(RequestChatMessage cheatMessage) {
        for (Cheat cheatCode : Cheat.values()) {
            try {
                var cheatPrefix = cheatMessage.getMessage().split("\\s")[0];
                var gameNameArray = cheatMessage.getChat().split("_");
                // Just accept cheatcodes entered in game
                if (cheatPrefix.equals(cheatCode.name()) && gameNameArray.length > 1) {
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
