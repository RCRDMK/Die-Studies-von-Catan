package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.message.TooMuchResourceCardsMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class is only used for tests.
 * <p>
 * Changing parameters / function calls in this class may result in certain tests failing
 *
 * @author Marc Hermes
 * @since 2021-05-12
 */
public class TestAI extends AbstractAISystem {
    /**
     * Constructor
     *
     * @param thatGame the game that this AISystem will have to work with and of which a deep copy will be created
     * @author Marc Hermes
     * @since 2021-05-12
     */
    public TestAI(GameDTO thatGame) {
        super(thatGame);
    }

    /**
     * This method will try to make use of every possible action the AI may do for test purposes
     *
     * @return the List of AIActions
     * @author Marc Hermes
     * @since 2021-05-12
     */
    public ArrayList<AIAction> startTurnOrder() {

        if (game.isStartingTurns()) {
            startingTurnLogic();
            endTurn();
        } else {

            if (canBuyDevelopmentCard())
                buyDevelopmentCard();

            ArrayList<String> cardsToPlay = canPlayDevelopmentCard();

            if (cardsToPlay.contains("Monopoly"))
                playDevelopmentCardMonopoly("Grain");

            if (cardsToPlay.contains("Year of Plenty"))
                playDevelopmentCardYearOfPlenty("Lumber", "Brick");

            int i = 0;
            UUID street1 = null;
            UUID street2 = null;
            for (MapGraph.StreetNode street : game.getMapGraph().getStreetNodeHashSet()) {
                if (i == 0 && street.getOccupiedByPlayer() == 666) {
                    street1 = street.getUuid();

                }
                if (i == 1 && street.getOccupiedByPlayer() == 666) {
                    street2 = street.getUuid();
                }
                if (i == 2) {
                    if (canBuildStreet())
                        buildStreet(street.getUuid());
                    break;
                }
                i++;
            }

            if (cardsToPlay.contains("Road Building"))
                playDevelopmentCardRoadBuilding(street1, street2);

            for (MapGraph.BuildingNode building : game.getMapGraph().getBuildingNodeHashSet()) {
                if (canBuildTown())
                    buildTown(building.getUuid());
                if (canBuildCity())
                    buildCity(building.getUuid());
                break;
            }

            i = 0;
            for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
                if (i == 0) {
                    moveBandit(hexagon.getUuid());
                }
                if (i == 1) {
                    playDevelopmentCardKnight(hexagon.getUuid());
                    //Try to just draw a resource from a player
                    drawRandomResourceFromPlayer(game.getUser(3).getUsername(), "Lumber");
                    break;
                }
                i++;
            }

            trade();
        }

        return this.aiActions;
    }

    public ArrayList<AIAction> continueTurnOrder(TradeInformSellerAboutBidsMessage tisabm, ArrayList<TradeItem> wishList) {
        tradeOfferAccept(tisabm.getTradeCode(), false, user);
        endTurn();
        return this.aiActions;
    }

    public ArrayList<AIAction> tradeBidOrder(TradeOfferInformBiddersMessage toibm) {
        TradeItem ti1 = new TradeItem("Lumber", 0);
        TradeItem ti2 = new TradeItem("Brick", 0);
        TradeItem ti3 = new TradeItem("Ore", 0);
        TradeItem ti4 = new TradeItem("Grain", 0);
        TradeItem ti5 = new TradeItem("Wool", 0);
        ArrayList<TradeItem> offerList = new ArrayList<>(Arrays.asList(ti1, ti2, ti3, ti4, ti5));

        tradeBid(offerList, toibm.getTradeCode());
        return this.aiActions;
    }

    public ArrayList<AIAction> discardResourcesOrder(TooMuchResourceCardsMessage tmrcm) {
        this.user = tmrcm.getUser();
        this.inventory = game.getInventory(user);
        int amountOfResourcesToBeDiscarded = tmrcm.getCards();

        HashMap<String, Integer> resourcesToDiscard = new HashMap<>();
        resourcesToDiscard.put("Wool", 0);
        resourcesToDiscard.put("Brick", 0);
        resourcesToDiscard.put("Grain", 0);
        resourcesToDiscard.put("Lumber", 0);
        resourcesToDiscard.put("Ore", 0);

        while (amountOfResourcesToBeDiscarded > 0) {
            String randomResource = returnRandomResource();
            if (inventory.getSpecificResourceAmount(randomResource) > 0) {
                inventory.decCardStack(randomResource, 1);
                resourcesToDiscard.put(randomResource, resourcesToDiscard.getOrDefault(randomResource, 0) + 1);
                amountOfResourcesToBeDiscarded--;
            }
        }
        discardResources(resourcesToDiscard);
        return this.aiActions;
    }

    public void trade() {

        TradeItem ti1 = new TradeItem("Lumber", 1);
        TradeItem ti2 = new TradeItem("Brick", 0);
        TradeItem ti3 = new TradeItem("Ore", 0);
        TradeItem ti4 = new TradeItem("Grain", 0);
        TradeItem ti5 = new TradeItem("Wool", 0);

        TradeItem ti6 = new TradeItem("Lumber", 0);
        TradeItem ti7 = new TradeItem("Grain", 0);

        ArrayList<TradeItem> wishList = new ArrayList<>(Arrays.asList(ti1, ti2, ti3, ti4, ti5));
        ArrayList<TradeItem> offerList = new ArrayList<>(Arrays.asList(ti2, ti3, ti5, ti6, ti7));

        tradeStart(wishList, offerList);
    }

    /**
     * Performs a number of actions during the opening turns of the game
     * <p>
     * Primarily 1 street and 1 building will be placed on the gameField.
     *
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private void startingTurnLogic() {
        boolean doneBuilding = false;
        for (MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if (doneBuilding) {
                break;
            }
            if (bn.getOccupiedByPlayer() == 666 && bn.getParent().getHexagons().size() == 6) {
                for (MapGraph.StreetNode sn : bn.getConnectedStreetNodes()) {
                    if (sn.getOccupiedByPlayer() == 666) {
                        buildTown(bn.getUuid());
                        buildStreet(sn.getUuid());
                        doneBuilding = true;
                        break;
                    }
                }
            }
        }
    }

    /**
     * This method returns a random resource as a string.
     *
     * @return the String name of a random resource
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private String returnRandomResource() {
        String resource;
        int rand = randomInt(0, 4);
        switch (rand) {
            case 0:
                resource = "Ore";
                break;
            case 1:
                resource = "Brick";
                break;
            case 2:
                resource = "Lumber";
                break;
            case 3:
                resource = "Wool";
                break;
            case 4:
                resource = "Grain";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + rand);
        }
        return resource;
    }

    /**
     * Returns a random (uniform distribution) int value between (including) two values
     *
     * @param min the min value for the random number
     * @param max the max value for the random number
     * @return the random number
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }
}
