package de.uol.swp.server.AI;

import com.google.gson.Gson;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.server.AI.AIActions.*;


import java.util.ArrayList;

public class RandomAI extends AbstractAISystem {

    public RandomAI(GameDTO thatGame) {
        Gson gson = new Gson();
        game = gson.fromJson(gson.toJson(thatGame), GameDTO.class);
        inventory = game.getInventory(game.getUser(game.getTurn()));
        mapGraph = game.getMapGraph();
        user = game.getUser(game.getTurn());

    }

    @Override
    public void endTurn() {
        AIAction aiAction = new EndTurnAction(user, game.getName());
        aiActions.add(aiAction);
    }

    @Override
    public void buyDevelopmentCard() {
        BuyDevelopmentCardAction bdca = new BuyDevelopmentCardAction();
        aiActions.add(bdca);

    }

    @Override
    public void placeStreet() {
        PlaceAction pa = new PlaceAction();
        aiActions.add(pa);

    }

    @Override
    public void placeTown() {
        PlaceAction pa = new PlaceAction();
        aiActions.add(pa);

    }

    @Override
    public void placeCity() {
        PlaceAction pa = new PlaceAction();
        aiActions.add(pa);

    }

    @Override
    public void trade() {
        TradeAction ta = new TradeAction();
        aiActions.add(ta);

    }

    @Override
    public void moveBandit() {
        MoveBanditAction mba = new MoveBanditAction();
        aiActions.add(mba);
    }

    @Override
    public ArrayList<AIAction> doAction(int eyes) {
        trade();
        buyDevelopmentCard();
        placeStreet();
        placeCity();
        placeTown();
        if (eyes == 7) moveBandit();
        endTurn();

        return this.aiActions;
    }
}
