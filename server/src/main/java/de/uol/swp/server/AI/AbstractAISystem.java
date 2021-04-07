package de.uol.swp.server.AI;

import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;

public abstract class AbstractAISystem implements AISystem{

    GameDTO game;

    Inventory inventory;

    ArrayList<AIAction> aiActions;

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

}
