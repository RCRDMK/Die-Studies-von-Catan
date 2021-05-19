package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.TooMuchResourceCardsMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AI.AIActions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
    }

    // https://www.journaldev.com/17129/java-deep-copy-object
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

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public GameDTO getGame() {
        return game;
    }

    public void setGame(GameDTO game) {
        this.game = game;
    }

    public ArrayList<AIAction> getAiActions() {
        return aiActions;
    }

    public void setAiActions(ArrayList<AIAction> aiActions) {
        this.aiActions = aiActions;
    }

    public MapGraph getMapGraph() {
        return mapGraph;
    }

    public void setMapGraph(MapGraph mapGraph) {
        this.mapGraph = mapGraph;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void endTurn() {
        AIAction aiAction = new EndTurnAction(user, game.getName());
        aiActions.add(aiAction);
    }

    @Override
    public void buyDevelopmentCard() {
        BuyDevelopmentCardAction bdca = new BuyDevelopmentCardAction(user, game.getName());
        inventory.decCard("Ore", 1);
        inventory.decCard("Grain", 1);
        inventory.decCard("Wool", 1);
        System.out.println("Buy card");
        aiActions.add(bdca);

    }

    @Override
    public void buildStreet(UUID field) {
        BuildAction pa = new BuildAction("BuildStreet", user, game.getName(), field);
        inventory.decCard("Brick", 1);
        inventory.decCard("Lumber", 1);
        System.out.println("Build street");
        aiActions.add(pa);

    }

    @Override
    public void buildTown(UUID field) {
        BuildAction pa = new BuildAction("BuildTown", user, game.getName(), field);
        inventory.decCard("Brick", 1);
        inventory.decCard("Lumber", 1);
        inventory.decCard("Grain", 1);
        inventory.decCard("Wool", 1);
        aiActions.add(pa);

    }

    @Override
    public void buildCity(UUID field) {
        BuildAction pa = new BuildAction("BuildCity", user, game.getName(), field);
        inventory.decCard("Ore", 3);
        inventory.decCard("Grain", 2);
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
        aiActions.add(pa);
    }

    @Override
    public void playDevelopmentCardMonopoly(String resource) {
        PlayDevelopmentCardMonopolyAction pa = new PlayDevelopmentCardMonopolyAction(user, game.getName(), "Monopoly", resource);
        aiActions.add(pa);
    }

    @Override
    public void playDevelopmentCardRoadBuilding(UUID street1, UUID street2) {
        PlayDevelopmentCardRoadBuildingAction pa = new PlayDevelopmentCardRoadBuildingAction(user, game.getName(), "Road Building", street1, street2);
        aiActions.add(pa);
    }

    @Override
    public void playDevelopmentCardYearOfPlenty(String resource1, String resource2) {
        PlayDevelopmentCardYearOfPlentyAction pa = new PlayDevelopmentCardYearOfPlentyAction(user, game.getName(), "Year of Plenty", resource1, resource2);
        aiActions.add(pa);
    }

    @Override
    public void moveBandit(UUID field) {
        MoveBanditAction mba = new MoveBanditAction(user, game.getName(), field);
        aiActions.add(mba);
    }

    @Override
    public void discardResources(HashMap<String, Integer> resourcesToDiscard) {
        DiscardResourcesAction dra = new DiscardResourcesAction(user, game.getName(), resourcesToDiscard);
        aiActions.add(dra);
    }

    @Override
    public ArrayList<AIAction> discardResourcesOrder(TooMuchResourceCardsMessage tmrcm) {
        aiActions.clear();

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> tradeBidOrder(TradeOfferInformBiddersMessage toibm) {
        aiActions.clear();

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> startTurnOrder() {

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> continueTurnOrder(TradeInformSellerAboutBidsMessage tisabm) {
        aiActions.clear();

        return this.aiActions;
    }

    public boolean canBuildStreet() {
        return inventory.brick.getNumber() > 0 && inventory.lumber.getNumber() > 0;
    }

    public boolean canBuildTown() {
        return inventory.brick.getNumber() > 0 && inventory.lumber.getNumber() > 0 && inventory.grain.getNumber() > 0 && inventory.wool.getNumber() > 0;
    }

    public boolean canBuildCity() {
        return inventory.ore.getNumber() > 2 && inventory.grain.getNumber() > 1;
    }

    public boolean canBuyDevelopmentCard() {
        return inventory.ore.getNumber() > 0 && inventory.grain.getNumber() > 0 && inventory.wool.getNumber() > 0;
    }

    public ArrayList<String> canPlayDevelopmentCard() {
        // TODO: check if the card was bought this turn
        ArrayList<String> playableCards = new ArrayList<>();
        if(inventory.cardYearOfPlenty.getNumber() > 0) playableCards.add("Year of Plenty");
        if(inventory.cardMonopoly.getNumber() > 0) playableCards.add("Monopoly");
        if(inventory.cardRoadBuilding.getNumber() > 0) playableCards.add("Road Building");
        if(inventory.cardKnight.getNumber() > 0) playableCards.add("Knight");
        return playableCards;
    }
}
