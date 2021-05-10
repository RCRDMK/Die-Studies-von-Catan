package de.uol.swp.server.AI;

import com.google.gson.Gson;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AI.AIActions.*;

import java.util.ArrayList;
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
        Gson gson = new Gson();
        game = gson.fromJson(gson.toJson(thatGame), GameDTO.class);
        inventory = game.getInventory(game.getUser(game.getTurn()));
        mapGraph = game.getMapGraph();
        user = game.getUser(game.getTurn());
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
        aiActions.add(bdca);

    }

    @Override
    public void buildStreet(UUID field) {
        BuildAction pa = new BuildAction("BuildStreet", user, game.getName(), field);
        aiActions.add(pa);

    }

    @Override
    public void buildTown(UUID field) {
        BuildAction pa = new BuildAction("BuildTown", user, game.getName(), field);
        aiActions.add(pa);

    }

    @Override
    public void buildCity(UUID field) {
        BuildAction pa = new BuildAction("BuildCity", user, game.getName(), field);
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
        PlayDevelopmentCardYearOfPlentyAction pa = new PlayDevelopmentCardYearOfPlentyAction(user, game.getName(), "Year Of Plenty", resource1, resource2);
        aiActions.add(pa);
    }

    @Override
    public void moveBandit(UUID field) {
        MoveBanditAction mba = new MoveBanditAction(user, game.getName(), field);
        aiActions.add(mba);
    }

    @Override
    public ArrayList<AIAction> startTurnAction() {

        return this.aiActions;
    }

    @Override
    public ArrayList<AIAction> continueTurnAction(Trade trade, String tradeCode) {
        aiActions.clear();

        return this.aiActions;
    }
}
