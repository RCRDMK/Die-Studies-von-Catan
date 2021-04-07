package de.uol.swp.server.AI;

import com.google.gson.Gson;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.server.AI.AIActions.AIAction;
import de.uol.swp.server.AI.AIActions.EndTurnAction;
import de.uol.swp.server.AI.AIActions.MoveBanditAction;


import java.util.ArrayList;

public class RandomAI extends AbstractAISystem {

    public RandomAI(GameDTO thatGame) {
        Gson gson = new Gson();
        game = gson.fromJson(gson.toJson(thatGame), GameDTO.class);
        inventory = game.getInventory(game.getUser(game.getTurn()));

    }

    @Override
    public void endTurn() {
        AIAction aiAction = new EndTurnAction();
        aiActions.add(aiAction);
    }

    @Override
    public void buyDevelopmentCard() {

    }

    @Override
    public void placeStreet() {

    }

    @Override
    public void placeTown() {

    }

    @Override
    public void placeCity() {

    }

    @Override
    public void trade() {

    }

    @Override
    public void moveBandit() {
        AIAction aiAction = new MoveBanditAction();
        aiActions.add(aiAction);
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
