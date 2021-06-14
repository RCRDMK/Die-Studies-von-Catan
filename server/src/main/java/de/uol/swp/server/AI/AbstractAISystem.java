package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.message.TooMuchResourceCardsMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AI.AIActions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Abstract class used for the AISystems
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public abstract class AbstractAISystem implements AISystem {

    GameDTO game;

    Inventory inventory;

    MapGraph mapGraph;

    User user;

    String playedCardThisTurn = "";

    boolean startedTrade = false;

    ArrayList<AIAction> aiActions;

    /**
     * Abstract constructor
     * <p>
     * Creates a Deep Copy of the game so that the values of the original game are not overwritten by the AI.
     *
     * @param thatGame the game that this AISystem will have to work with and of which a deep copy will be created
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public AbstractAISystem(GameDTO thatGame) {
        game = (GameDTO) deepCopy(thatGame);
        assert game != null;
        inventory = game.getInventory(game.getUser(game.getTurn()));
        mapGraph = game.getMapGraph();
        user = game.getUser(game.getTurn());
        aiActions = new ArrayList<>();
        playedCardThisTurn = game.getCurrentCard();
    }

    /**
     * Creates a deep copy of an object
     * <p>
     * By turning the object into an byteStream and then returning in into the object it originally was,
     * all of the information stored in the object will remain.
     * However the actual reference of the object in the program will be lost.
     * Thus we can use the deep copy of the object to analyze changes without actually changing the original object
     * <p>
     * Idea from: https://www.journaldev.com/17129/java-deep-copy-object
     *
     * @param object the object to create a deep copy of
     * @return the deep copy of the object
     * @author Marc Hermes
     * @since 2021-05-19
     */
    private static Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
            outputStrm.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
            return objInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void endTurn() {
        AIAction aiAction = new EndTurnAction(user, game.getName());
        aiActions.add(aiAction);
    }

    @Override
    public void buyDevelopmentCard() {
        BuyDevelopmentCardAction bdca = new BuyDevelopmentCardAction(user, game.getName());
        String devCard = game.getDevelopmentCardDeck().drawnCard();
        if (devCard != null) {
            inventory.incCardStack(devCard, 1);
            inventory.decCardStack("Ore", 1);
            inventory.decCardStack("Grain", 1);
            inventory.decCardStack("Wool", 1);
            game.getBankInventory().incCardStack("Ore", 1);
            game.getBankInventory().incCardStack("Grain", 1);
            game.getBankInventory().incCardStack("Wool", 1);
            aiActions.add(bdca);
        }

    }

    @Override
    public void buildStreet(MapGraph.StreetNode field) {
        BuildAction pa = new BuildAction("BuildStreet", user, game.getName(), field.getUuid());
        inventory.decCardStack("Brick", 1);
        inventory.decCardStack("Lumber", 1);
        game.getBankInventory().incCardStack("Brick", 1);
        game.getBankInventory().incCardStack("Lumber", 1);
        field.buildRoad(game.getTurn());
        aiActions.add(pa);

    }

    @Override
    public void buildTown(MapGraph.BuildingNode field) {
        BuildAction pa = new BuildAction("BuildTown", user, game.getName(), field.getUuid());
        inventory.decCardStack("Brick", 1);
        inventory.decCardStack("Lumber", 1);
        inventory.decCardStack("Grain", 1);
        inventory.decCardStack("Wool", 1);
        game.getBankInventory().incCardStack("Brick", 1);
        game.getBankInventory().incCardStack("Lumber", 1);
        game.getBankInventory().incCardStack("Grain", 1);
        game.getBankInventory().incCardStack("Wool", 1);
        field.buildOrDevelopSettlement(game.getTurn());
        game.getMapGraph().addBuiltBuilding(field);
        aiActions.add(pa);

    }

    @Override
    public void buildCity(MapGraph.BuildingNode field) {
        BuildAction pa = new BuildAction("BuildCity", user, game.getName(), field.getUuid());
        inventory.decCardStack("Ore", 3);
        inventory.decCardStack("Grain", 2);
        game.getBankInventory().incCardStack("Ore", 3);
        game.getBankInventory().incCardStack("Grain", 2);
        field.buildOrDevelopSettlement(game.getTurn());
        aiActions.add(pa);

    }

    @Override
    public void tradeStart(ArrayList<TradeItem> wishList, ArrayList<TradeItem> offerList) {
        TradeStartAction ta = new TradeStartAction(user, game.getName(), wishList, offerList);
        aiActions.add(ta);
    }

    @Override
    public void tradeBid(ArrayList<TradeItem> bidList, String tradeCode) {
        TradeBidAction ta = new TradeBidAction(user, game.getName(), bidList, tradeCode);
        aiActions.add(ta);
    }

    @Override
    public void tradeOfferAccept(String tradeCode, boolean tradeAccepted, User acceptedBidder) {
        TradeOfferAcceptAction ta = new TradeOfferAcceptAction(user, game.getName(), tradeCode, tradeAccepted, acceptedBidder);
        aiActions.add(ta);
    }

    @Override
    public void playDevelopmentCardKnight(UUID field) {
        PlayDevelopmentCardKnightAction pa = new PlayDevelopmentCardKnightAction(user, game.getName(), "Knight", field);
        inventory.cardKnight.decNumber();
        playedCardThisTurn = "Knight";
        for (MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
            if (hx.isOccupiedByRobber()) {
                hx.setOccupiedByRobber(false);
            }
            if (hx.getUuid().equals(field)) {
                hx.setOccupiedByRobber(true);
            }
        }
        aiActions.add(pa);
    }

    @Override
    public void playDevelopmentCardMonopoly(String resource) {
        PlayDevelopmentCardMonopolyAction pa = new PlayDevelopmentCardMonopolyAction(user, game.getName(), "Monopoly", resource);
        inventory.cardMonopoly.decNumber();
        playedCardThisTurn = "Monopoly";
        for (User player : game.getUsersList()) {
            if (!player.equals(this.user)) {
                game.getInventory(player).decCardStack(resource, game.getInventory(player).getSpecificResourceAmount(resource));
                game.getInventory(user).incCardStack(resource, game.getInventory(player).getSpecificResourceAmount(resource));
            }
        }
        aiActions.add(pa);
    }

    @Override
    public void playDevelopmentCardRoadBuilding(UUID street1, UUID street2) {
        PlayDevelopmentCardRoadBuildingAction pa = new PlayDevelopmentCardRoadBuildingAction(user, game.getName(), "Road Building", street1, street2);
        inventory.cardRoadBuilding.decNumber();
        for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
            if (sn.getUuid().equals(street1) || sn.getUuid().equals(street2)) {
                sn.buildRoad(game.getTurn());
            }
        }
        playedCardThisTurn = "Road Building";
        aiActions.add(pa);
    }

    @Override
    public void playDevelopmentCardYearOfPlenty(String resource1, String resource2) {
        PlayDevelopmentCardYearOfPlentyAction pa = new PlayDevelopmentCardYearOfPlentyAction(user, game.getName(), "Year of Plenty", resource1, resource2);
        inventory.cardRoadBuilding.decNumber();
        playedCardThisTurn = "Year of Plenty";
        inventory.incCardStack(resource1, 1);
        inventory.incCardStack(resource2, 1);
        game.getBankInventory().incCardStack(resource1, 1);
        game.getBankInventory().incCardStack(resource2, 1);
        aiActions.add(pa);
    }

    @Override
    public void moveBandit(UUID field) {
        MoveBanditAction mba = new MoveBanditAction(user, game.getName(), field);
        for (MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
            if (hx.isOccupiedByRobber()) {
                hx.setOccupiedByRobber(false);
            }
            if (hx.getUuid().equals(field)) {
                hx.setOccupiedByRobber(true);
            }
        }
        aiActions.add(mba);
    }

    @Override
    public void discardResources(HashMap<String, Integer> resourcesToDiscard) {
        DiscardResourcesAction dra = new DiscardResourcesAction(user, game.getName(), resourcesToDiscard);
        aiActions.add(dra);
    }

    @Override
    public void drawRandomResourceFromPlayer(String playerName, String resource) {
        DrawRandomResourceFromPlayerAction drrfpa = new DrawRandomResourceFromPlayerAction(user, game.getName(), playerName, resource);
        aiActions.add(drrfpa);
    }

    @Override
    public ArrayList<AIAction> discardResourcesOrder(TooMuchResourceCardsMessage tmrcm) {

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> tradeBidOrder(TradeOfferInformBiddersMessage toibm) {

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> startTurnOrder() {

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> continueTurnOrder(TradeInformSellerAboutBidsMessage tisabm, ArrayList<TradeItem> wishList) {

        return this.aiActions;
    }

    @Override
    public boolean canBuildStreet() {
        return inventory.brick.getNumber() > 0 && inventory.lumber.getNumber() > 0;
    }

    @Override
    public boolean canBuildTown() {
        return inventory.brick.getNumber() > 0 && inventory.lumber.getNumber() > 0 && inventory.grain.getNumber() > 0 && inventory.wool.getNumber() > 0;
    }

    @Override
    public boolean canBuildCity() {
        return inventory.ore.getNumber() > 2 && inventory.grain.getNumber() > 1;
    }

    @Override
    public boolean canBuyDevelopmentCard() {
        return inventory.ore.getNumber() > 0 && inventory.grain.getNumber() > 0 && inventory.wool.getNumber() > 0;
    }

    @Override
    public ArrayList<String> canPlayDevelopmentCard() {
        ArrayList<String> playableCards = new ArrayList<>();
        if(game.canUserPlayDevCard(user,"Year of Plenty")){
            playableCards.add("Year of Plenty");
        }
        if(game.canUserPlayDevCard(user,"Monopoly")){
            playableCards.add("Monopoly");
        }
        if(game.canUserPlayDevCard(user,"Road Building")){
            playableCards.add("Road Building");
        }
        if(game.canUserPlayDevCard(user,"Knight")){
            playableCards.add("Knight");
        }


        return playableCards;
    }

    /**
     * This method will check the streetNodeHashSet of the mapGraph for a streetNode which might be built for this AI.
     *
     * @return an Optional UUID of the streetNode. Will be empty if there is no legal building spot for streets currently.
     * @author Marc Hermes
     * @since 2021-05-19
     */
    public Optional<MapGraph.StreetNode> returnPossibleStreet() {
        for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
            if (sn.getOccupiedByPlayer() == 666) {
                if(sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(sn);
                }
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
    public Optional<MapGraph.BuildingNode> returnPossibleTown() {
        for (MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if (bn.getOccupiedByPlayer() == 666) {
                if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(bn);
                }
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
    public Optional<MapGraph.BuildingNode> returnPossibleCity() {
        for (MapGraph.BuildingNode bn : mapGraph.getBuiltBuildings()) {
            if (bn.getOccupiedByPlayer() == game.getTurn()) {
                if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(bn);
                }
            }
        }
        return Optional.empty();
    }
}
