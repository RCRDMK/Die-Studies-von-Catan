package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;

public abstract class AbstractAISystem implements AISystem{

    GameDTO game;

    Inventory inventory;

    MapGraph mapGraph;

    User user;

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
}
