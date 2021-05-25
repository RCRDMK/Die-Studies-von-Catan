package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.TooMuchResourceCardsMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.*;

/**
 * AI which makes choices randomly
 * <p>
 * //TODO: Work in progress
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public class RandomAI extends AbstractAISystem {

    /**
     * Constructor
     *
     * @param thatGame the game of this AI
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public RandomAI(GameDTO thatGame) {
        super(thatGame);
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


    /**
     * When the turn starts for the AI the server will call this function.
     * <p>
     * The AI will then put various random AIActions in his aiAction arrayList.
     * Finally the AI will either end its turn, or start a trade.
     *
     * @return the ArrayList of AIActions the AI wishes to do.
     * @author Marc Hermes
     * @since 2021-05-19
     */
    public ArrayList<AIAction> startTurnOrder() {

        if (game.isStartingTurns()) {
            startingTurnLogic();
        } else {
            // if a 7 was rolled, move the robber to a random hexagon
            if (game.getLastRolledDiceValue() == 7) {
                for (MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
                    if (!hx.isOccupiedByRobber()) {
                        moveBanditLogic(hx.getUuid());
                        break;
                    }
                }
            }
            // do some random actions
            makeRandomActionsLogic();
            startedTrade = makeRandomTradeLogic();
        }
        if (!startedTrade) {
            // try to play a developmentCard
            playDevelopmentCardLogic();

            endTurn();
        }
        return this.aiActions;
    }

    /**
     * When the turn continues for the AI (after the players in the game bid on the ongoing trade) the Server will call this function
     *
     * @param tisabm   the TradeInformSellerAboutBidsMessage containing information about the bids of the trade
     * @param wishList the original wishList of the AI
     * @return the ArrayList of AIActions the AI wishes to do
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-22
     */
    public ArrayList<AIAction> continueTurnOrder(TradeInformSellerAboutBidsMessage tisabm, ArrayList<TradeItem> wishList) {
        startedTrade = true;
        chooseTradeBidLogic(tisabm, wishList);
        makeRandomActionsLogic();
        playDevelopmentCardLogic();
        endTurn();
        return this.aiActions;
    }

    /**
     * When a different Player initiates a trade the server will call this Method for the AI so that it may
     * participate in the trade
     *
     * @param toibm the TradeOfferInformBiddersMessage containing the information about the new trade
     * @return the ArrayList of AIActions the AI wishes to do (should only include the tradeBidAction)
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-22
     */
    public ArrayList<AIAction> tradeBidOrder(TradeOfferInformBiddersMessage toibm) {
        this.user = toibm.getBidder();
        this.inventory = game.getInventory(user);
        bidOnItemLogic(toibm);

        return this.aiActions;
    }

    /**
     * When the robber was moved and the AI needs to discard resources the Server will call this method for the AI
     *
     * @param tmrcm the TooMuchResourceCardsMessage containing the information about the amount of cards to discard
     * @return the ArrayList of AIActions the AI wishes to do (should only include the discardResourcesAction)
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-22
     */
    public ArrayList<AIAction> discardResourcesOrder(TooMuchResourceCardsMessage tmrcm) {
        this.user = tmrcm.getUser();
        this.inventory = game.getInventory(user);
        discardResourcesLogic(tmrcm.getCards());
        return this.aiActions;
    }

    /**
     * Method called when the bandit is to be moved
     * <p>
     * When invoked this method will move the bandit as well as discard resources should it be necessary.
     *
     * @param hx the UUID of the field the robber should be moved to
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-22
     */
    private void moveBanditLogic(UUID hx) {
        moveBandit(hx);
        if (inventory.sumResource() > 7 && !playedCardThisTurn.equals("Knight")) {
            if (inventory.sumResource() % 2 != 0) {
                discardResourcesLogic((inventory.sumResource() - 1) / 2);
            } else {
                discardResourcesLogic(inventory.sumResource() / 2);
            }
        }

        // draw a random resource from a player
        ArrayList<User> usersNearTheRobber = new ArrayList<>();
        for (MapGraph.Hexagon hexagon : mapGraph.getHexagonHashSet()) {
            if (hexagon.isOccupiedByRobber()) {
                for (MapGraph.BuildingNode bn : hexagon.getBuildingNodes()) {
                    int playerIndex = bn.getOccupiedByPlayer();
                    if (playerIndex != 666 && playerIndex != game.getTurn()) {
                        if (!usersNearTheRobber.contains(game.getUser(game.getTurn()))) {
                            usersNearTheRobber.add(game.getUser(playerIndex));
                        }
                    }
                }
                break;
            }
        }
        if(usersNearTheRobber.size() > 0) {
            Inventory randomInventory = game.getInventory(usersNearTheRobber.get(randomInt(0, usersNearTheRobber.size() - 1)));
            HashMap<String, Integer> privateView = randomInventory.getPrivateView();
            ArrayList<String> resources = new ArrayList<>();
            if (privateView.get("Lumber") > 0) resources.add("Lumber");
            if (privateView.get("Brick") > 0) resources.add("Brick");
            if (privateView.get("Grain") > 0) resources.add("Grain");
            if (privateView.get("Wool") > 0) resources.add("Wool");
            if (privateView.get("Ore") > 0) resources.add("Ore");
            if(resources.size() > 0) {
                String randomResource = resources.get(randomInt(0, resources.size() - 1));
                inventory.incCardStack(randomResource, 1);
                drawRandomResourceFromPlayer(randomInventory.getUser().getUsername(), randomResource);
            } else {
                drawRandomResourceFromPlayer(randomInventory.getUser().getUsername(), "");
            }
        }
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
     * Using this method will result in the AI playing 1 random developmentCard that it currently can play.
     *
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private void playDevelopmentCardLogic() {
        ArrayList<String> cards = canPlayDevelopmentCard();
        if (cards.size() > 0) {
            String cardToPlay = cards.get(randomInt(0, cards.size() - 1));
            switch (cardToPlay) {
                case "Year of Plenty":
                    playDevelopmentCardYearOfPlenty(returnRandomResource(), returnRandomResource());
                    break;
                case "Knight":
                    UUID hexagon = null;
                    for (MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
                        if (!hx.isOccupiedByRobber()) {
                            hexagon = hx.getUuid();
                            break;
                        }
                    }
                    playDevelopmentCardKnight(hexagon);
                    moveBanditLogic(hexagon);
                    break;
                case "Monopoly":
                    playDevelopmentCardMonopoly(returnRandomResource());
                    break;
                case "Road Building":
                    UUID street1 = null;
                    UUID street2 = null;
                    int streets = 0;
                    for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
                        if (sn.getOccupiedByPlayer() == 666) {
                            //if(sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                            if (street1 == null) {
                                street1 = sn.getUuid();
                                streets = streets + 1;
                            } else if (streets == 1) {
                                street2 = sn.getUuid();
                                break;
                            }
                            //}
                        }
                    }
                    if (street1 != null & street2 != null) {
                        playDevelopmentCardRoadBuilding(street1, street2);
                    }
                    break;
            }
        }
    }

    /**
     * Using this method will result in the AI doing a random amount of random actions.
     * <p>
     * The AI can try to build streets, towns and cities or buy developmentCards.
     * Inherently the same kind of action may be done twice, if the AI has the resources to do so.
     *
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private void makeRandomActionsLogic() {
        int amountOfActions = randomInt(0, 3);
        for (int i = 0; i <= amountOfActions; i++) {
            int actionType = randomInt(0, 3);
            switch (actionType) {
                case 0:
                    if (canBuildStreet()) {
                        Optional<UUID> streetUUID = returnPossibleStreet();
                        streetUUID.ifPresent(this::buildStreet);
                        break;
                    }
                case 1:
                    if (canBuildTown()) {
                        Optional<UUID> townUUID = returnPossibleTown();
                        townUUID.ifPresent(this::buildTown);
                        break;
                    }
                case 2:
                    if (canBuildCity()) {
                        Optional<UUID> cityUUID = returnPossibleCity();
                        cityUUID.ifPresent(this::buildCity);
                        break;
                    }
                case 3:
                    if (canBuyDevelopmentCard()) {
                        buyDevelopmentCard();
                        break;
                    }
            }
        }
    }


    /**
     * This method is used when the AI wants to trade
     * <p>
     * Firstly it is randomly decided whether the AI wants to trade or not.
     * Then the method createWishAndOfferList() is invoked.
     * If there are any entries in the wishList with numbers larger than 0 the trade is started.
     *
     * @return true if the AI traded, false if not
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-22
     */
    private boolean makeRandomTradeLogic() {
        boolean startATradeTest = randomInt(0, 9) >= 5;
        if (startATradeTest) {
            ArrayList<ArrayList<TradeItem>> wishAndOfferList = createWishAndOfferList();
            ArrayList<TradeItem> wishList = wishAndOfferList.get(0);
            ArrayList<TradeItem> offerList = wishAndOfferList.get(1);
            int amountOfWishes = 0;
            for (TradeItem ti : wishList) {
                amountOfWishes += ti.getCount();
            }
            if (amountOfWishes > 0) {
                tradeStart(wishList, offerList);
            } else return false;
        }
        return startATradeTest;
    }

    /**
     * Method used to choose a certain bid after the AI was reactivated by the server during a trade that the AI initiated
     *
     * @param tisabm the TradeInformSellerAboutBidsMessage that the Server would usually send to a User
     * @param wishList the original wishList of the AI
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-25
     */
    private void chooseTradeBidLogic(TradeInformSellerAboutBidsMessage tisabm, ArrayList<TradeItem> wishList) {
        HashMap<UserDTO, Integer> usersWithAcceptableOffer = new HashMap<>();

        int lumberWish = 0;
        int grainWish = 0;
        int oreWish = 0;
        int woolWish = 0;
        int brickWish = 0;

        for (TradeItem ti : wishList) {
            switch (ti.getName()) {
                case "Lumber":
                    lumberWish = ti.getCount();
                    break;
                case "Ore":
                    oreWish = ti.getCount();
                    break;
                case "Wool":
                    woolWish = ti.getCount();
                    break;
                case "Brick":
                    brickWish = ti.getCount();
                    break;
                case "Grain":
                    grainWish = ti.getCount();
                    break;
                default:
                    break;
            }
        }
        for (UserDTO user : tisabm.getBidders()) {
            int goodTradeItemAmount = 5;
            int amountOfItems = 0;
            for (TradeItem offerItem : tisabm.getBids().get(user)) {
                int offerItemCount = offerItem.getCount();
                amountOfItems += offerItemCount;
                switch (offerItem.getName()) {
                    case "Lumber":
                        if (!(offerItemCount >= lumberWish)) {
                            goodTradeItemAmount--;
                        }
                        break;
                    case "Ore":
                        if (!(offerItemCount >= oreWish)) {
                            goodTradeItemAmount--;
                        }
                        break;
                    case "Brick":
                        if (!(offerItemCount >= brickWish)) {
                            goodTradeItemAmount--;
                        }
                        break;
                    case "Grain":
                        if (!(offerItemCount >= grainWish)) {
                            goodTradeItemAmount--;
                        }
                        break;
                    case "Wool":
                        if (!(offerItemCount >= woolWish)) {
                            goodTradeItemAmount--;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (goodTradeItemAmount >= 4) {
                usersWithAcceptableOffer.put(user, amountOfItems);
            }
        }

        UserDTO userWithMostItems = null;
        int mostItems = 0;
        for (UserDTO user : usersWithAcceptableOffer.keySet()) {
            if (usersWithAcceptableOffer.get(user) > mostItems) {
                mostItems = usersWithAcceptableOffer.get(user);
                userWithMostItems = user;
            } else if (usersWithAcceptableOffer.get(user) == mostItems && randomInt(0, 1) > 0) {
                mostItems = usersWithAcceptableOffer.get(user);
                userWithMostItems = user;
            }
        }
        if (userWithMostItems != null) {
            for (TradeItem ti : tisabm.getBids().get(userWithMostItems)) {
                inventory.incCardStack(ti.getName(), ti.getCount());
            }
            tradeOfferAccept(tisabm.getTradeCode(), true, userWithMostItems);
        } else {
            tradeOfferAccept(tisabm.getTradeCode(), false, this.user);
        }
    }

    /**
     * Method used for the AI in order to participate in an ongoing trade through bidding
     *
     * @param toibm the TradeInformBiddersMessage sent that would be sent to a client
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-25
     */
    private void bidOnItemLogic(TradeOfferInformBiddersMessage toibm) {
        ArrayList<ArrayList<TradeItem>> wishAndOfferListAI = createWishAndOfferList();
        ArrayList<TradeItem> wishListAI = wishAndOfferListAI.get(0);
        ArrayList<TradeItem> offerListAI = wishAndOfferListAI.get(1);
        ArrayList<TradeItem> offerListSeller = toibm.getSellingItems();

        int notAcceptableTradeItems = 0;
        for (TradeItem tradeItemSeller : offerListSeller) {
            for (TradeItem tradeItemAI : wishListAI) {
                if (tradeItemAI.getName().equals(tradeItemSeller.getName())) {
                    if (tradeItemAI.getCount() > tradeItemSeller.getCount()) {
                        notAcceptableTradeItems++;
                    }
                    break;
                }
            }
        }
        if (notAcceptableTradeItems > 1 + randomInt(0, 1)) {
            offerListAI.clear();
        } else if (notAcceptableTradeItems <= 1 + randomInt(0, 1)) {
            int tries = 0;
            while (notAcceptableTradeItems > 0 && tries < 50) {
                tries++;
                String randomResource = returnRandomResource();
                for (TradeItem tradeItemOffer : offerListAI) {
                    if (tradeItemOffer.getCount() > 0 && randomResource.equals(tradeItemOffer.getName())) {
                        notAcceptableTradeItems--;
                        tradeItemOffer.decCount(1);
                        if (notAcceptableTradeItems == 0) {
                            break;
                        }
                    }
                }
            }
        }
        tradeBid(offerListAI, toibm.getTradeCode());

    }

    /**
     * Method used for discarding resources when the robber is activated
     *
     * @param amountOfResourcesToBeDiscarded the int amount indicating how many resources need to be discarded
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-25
     */
    private void discardResourcesLogic(int amountOfResourcesToBeDiscarded) {

        HashMap<String, Integer> resourcesToDiscard = new HashMap<>();
        ArrayList<ArrayList<TradeItem>> wishAndOfferList = createWishAndOfferList();
        ArrayList<TradeItem> wishItems = wishAndOfferList.get(0);
        ArrayList<TradeItem> offerItems = wishAndOfferList.get(1);

        boolean discardedSomething = true;
        //discard items the AI would offer in a trade
        while (discardedSomething) {
            discardedSomething = false;
            String randomResource = returnRandomResource();
            for (TradeItem offerItem : offerItems) {
                if (offerItem.getName().equals(randomResource) && offerItem.getCount() > 0) {
                    offerItem.decCount(1);
                    amountOfResourcesToBeDiscarded--;
                    discardedSomething = true;
                    inventory.decCardStack(offerItem.getName(), 1);
                    resourcesToDiscard.put(offerItem.getName(), resourcesToDiscard.getOrDefault(offerItem.getName(), 0) + 1);
                    if (amountOfResourcesToBeDiscarded == 0) {
                        break;
                    }
                }
            }
            if (amountOfResourcesToBeDiscarded == 0) {
                break;
            }
        }
        //discard items AI wouldn't want to use
        if (amountOfResourcesToBeDiscarded > 0) {
            ArrayList<String> resourceList = new ArrayList<>();
            resourceList.add("Grain");
            resourceList.add("Wool");
            resourceList.add("Lumber");
            resourceList.add("Brick");
            resourceList.add("Ore");
            Iterator<String> it = resourceList.iterator();
            while (it.hasNext()) {
                String value = it.next();
                for (TradeItem wishItem : wishItems) {
                    if (value.equals(wishItem.getName())) {
                        it.remove();
                        break;
                    }
                }
            }
            discardedSomething = true;
            while (discardedSomething) {
                discardedSomething = false;
                String randomResource = returnRandomResource();
                Iterator<String> it1 = resourceList.iterator();
                while (it.hasNext()) {
                    String value1 = it1.next();
                    if (value1.equals(randomResource) && inventory.getSpecificResourceAmount(value1) > 0) {
                        inventory.decCardStack(value1, 1);
                        amountOfResourcesToBeDiscarded = amountOfResourcesToBeDiscarded - 1;
                        resourcesToDiscard.put(value1, resourcesToDiscard.getOrDefault(value1, 0) + 1);
                        discardedSomething = true;
                    } else if (value1.equals(randomResource) && inventory.getSpecificResourceAmount(value1) == 0) {
                        it1.remove();
                    }
                    if (amountOfResourcesToBeDiscarded == 0) {
                        break;
                    }
                }
                if (amountOfResourcesToBeDiscarded == 0) {
                    break;
                }
            }
            //discard rest if necessary
            while (amountOfResourcesToBeDiscarded > 0) {
                String randomResource = returnRandomResource();
                if (inventory.getSpecificResourceAmount(randomResource) > 0) {
                    inventory.decCardStack(randomResource, 1);
                    resourcesToDiscard.put(randomResource, resourcesToDiscard.getOrDefault(randomResource, 0) + 1);
                    amountOfResourcesToBeDiscarded--;
                }
            }
        }
        discardResources(resourcesToDiscard);
    }

    /**
     * This method will check the streetNodeHashSet of the mapGraph for a streetNode which might be built for this AI.
     *
     * @return an Optional UUID of the streetNode. Will be empty if there is no legal building spot for streets currently.
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private Optional<UUID> returnPossibleStreet() {
        for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
            if (sn.getOccupiedByPlayer() == 666) {
                // TODO: when the rules for building streets is done
                //if(sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(sn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    /**
     * This method will check the buildingNodeHashSet of the mapGraph for a buildingNode which might be built as a town for this AI.
     *
     * @return an Optional UUID of the buildingNode. Will be empty if there is no legal building spot for towns.
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private Optional<UUID> returnPossibleTown() {
        for (MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if (bn.getOccupiedByPlayer() == 666) {
                //if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(bn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    /**
     * This method will check the buildingNodeHashSet of the mapGraph for a buildingNode which might be built as a city for this AI.
     *
     * @return an Optional UUID of the buildingNode. Will be empty if there is no legal building spot for cities.
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private Optional<UUID> returnPossibleCity() {
        for (MapGraph.BuildingNode bn : mapGraph.getBuiltBuildings()) {
            if (bn.getOccupiedByPlayer() == game.getTurn()) {
                //if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(bn.getUuid());
                //}
            }
        }
        return Optional.empty();
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
     * Method used to create a wish and an offer list for the AI so that it may make decisions accordingly
     *
     * @return An arrayList holding 2 arrayLists of TradeItems, the first arrayList is used as the wishList, the second as the offerList
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-25
     */
    private ArrayList<ArrayList<TradeItem>> createWishAndOfferList() {
        ArrayList<ArrayList<TradeItem>> wishAndOfferList = new ArrayList<>();
        ArrayList<TradeItem> wishList = new ArrayList<>();
        ArrayList<TradeItem> offerList = new ArrayList<>();
        wishAndOfferList.add(wishList);
        wishAndOfferList.add(offerList);
        int lumber = inventory.lumber.getNumber();
        int grain = inventory.grain.getNumber();
        int brick = inventory.brick.getNumber();
        int wool = inventory.wool.getNumber();
        int ore = inventory.ore.getNumber();
        String brickString = "Brick";
        String lumberString = "Lumber";
        String oreString = "Ore";
        String grainString = "Grain";
        String woolString = "Wool";

        int lumberAllowedToBeTraded = lumber;
        int brickAllowedToBeTraded = brick;
        int grainAllowedToBeTraded = grain;
        int woolAllowedToBeTraded = wool;
        int oreAllowedToBeTraded = ore;

        ArrayList<String> cantDo = new ArrayList<>();
        if (!canBuildStreet()) {
            cantDo.add("Street");
        }
        if (!canBuildTown()) {
            cantDo.add("Town");
        }
        if (!canBuildCity()) {
            cantDo.add("City");
        }
        if (!canBuyDevelopmentCard()) {
            cantDo.add("DevCard");
        }
        if (cantDo.size() > 0)
            switch (cantDo.get(randomInt(0, cantDo.size() - 1))) {
                case "Street":
                    wishList.add(new TradeItem(lumberString, Math.max(1 - lumber, 0)));
                    lumberAllowedToBeTraded = Math.max(lumber - 1, 0);
                    wishList.add(new TradeItem(brickString, Math.max(1 - brick, 0)));
                    brickAllowedToBeTraded = Math.max(brick - 1, 0);
                    break;
                case "Town":
                    wishList.add(new TradeItem(brickString, Math.max(1 - brick, 0)));
                    brickAllowedToBeTraded = Math.max(brick - 1, 0);
                    wishList.add(new TradeItem(lumberString, Math.max(1 - lumber, 0)));
                    lumberAllowedToBeTraded = Math.max(lumber - 1, 0);
                    wishList.add(new TradeItem(grainString, Math.max(1 - grain, 0)));
                    grainAllowedToBeTraded = Math.max(grain - 1, 0);
                    wishList.add(new TradeItem(woolString, Math.max(1 - wool, 0)));
                    woolAllowedToBeTraded = Math.max(wool - 1, 0);
                case "City":
                    wishList.add(new TradeItem(oreString, Math.max(3 - ore, 0)));
                    oreAllowedToBeTraded = Math.max(ore - 3, 0);
                    wishList.add(new TradeItem(grainString, Math.max(2 - grain, 0)));
                    grainAllowedToBeTraded = Math.max(grain - 2, 0);
                    break;
                case "DevCard":
                    wishList.add(new TradeItem(oreString, Math.max(1 - ore, 0)));
                    oreAllowedToBeTraded = Math.max(ore - 1, 0);
                    wishList.add(new TradeItem(grainString, Math.max(1 - grain, 0)));
                    grainAllowedToBeTraded = Math.max(grain - 1, 0);
                    wishList.add(new TradeItem(woolString, Math.max(1 - wool, 0)));
                    woolAllowedToBeTraded = Math.max(wool - 1, 0);
                    break;
            }
        int amountOfWishes = 0;
        for (TradeItem ti : wishList) {
            amountOfWishes += ti.getCount();
        }
        int amountOfOffers = amountOfWishes + randomInt(0, 3) - 2;

        if (canBuildStreet()) {
            lumberAllowedToBeTraded = lumber - 1;
            brickAllowedToBeTraded = brick - 1;
        }
        if (canBuildTown()) {
            brickAllowedToBeTraded = brick - 1;
            lumberAllowedToBeTraded = lumber - 1;
            grainAllowedToBeTraded = grain - 1;
            woolAllowedToBeTraded = wool - 1;
        }
        if (canBuyDevelopmentCard()) {
            oreAllowedToBeTraded = ore - 1;
            grainAllowedToBeTraded = grain - 1;
            woolAllowedToBeTraded = wool - 1;
        }
        //last in block, because it has the highest amount of necessary resources(overwrites others, if true)
        if (canBuildCity()) {
            oreAllowedToBeTraded = ore - 3;
            grainAllowedToBeTraded = grain - 2;
        }

        int tries = 0;
        int offerLumber = 0;
        int offerOre = 0;
        int offerBrick = 0;
        int offerGrain = 0;
        int offerWool = 0;
        while (amountOfOffers > 0 && tries < 30) {
            switch (randomInt(0, 4)) {
                case 0:
                    if (oreAllowedToBeTraded > 0) {
                        offerOre += 1;
                        oreAllowedToBeTraded -= 1;
                    }
                    break;
                case 1:
                    if (brickAllowedToBeTraded > 0) {
                        offerBrick += 1;
                        brickAllowedToBeTraded -= 1;
                    }
                    break;
                case 2:
                    if (lumberAllowedToBeTraded > 0) {
                        offerLumber += 1;
                        lumberAllowedToBeTraded -= 1;
                    }
                    break;
                case 3:
                    if (grainAllowedToBeTraded > 0) {
                        offerGrain += 1;
                        grainAllowedToBeTraded -= 1;
                    }
                    break;
                case 4:
                    if (woolAllowedToBeTraded > 0) {
                        offerWool += 1;
                        woolAllowedToBeTraded -= 1;
                    }
                    break;
            }
            tries++;
        }
        offerList.add(new TradeItem(brickString, offerBrick));
        offerList.add(new TradeItem(lumberString, offerLumber));
        offerList.add(new TradeItem(oreString, offerOre));
        offerList.add(new TradeItem(woolString, offerWool));
        offerList.add(new TradeItem(grainString, offerGrain));

        return wishAndOfferList;
    }

}
