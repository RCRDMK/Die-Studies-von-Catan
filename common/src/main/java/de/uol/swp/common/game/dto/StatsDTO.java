package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.inventory.Inventory;

import java.io.Serializable;
import java.util.ArrayList;

public class StatsDTO implements Serializable {

    private String winner;
    private String gameName;
    private int overallTurns;
    private int overallTrades;
    private ArrayList<Inventory> inventoryArrayList;

    public StatsDTO(String gameName, String winner, int overallTrades, int overallTurns, ArrayList<Inventory> inventoryArrayList) {
        this.gameName = gameName;
        this.winner = winner;
        this.overallTrades = overallTrades;
        this.overallTurns = overallTurns;
        this.inventoryArrayList = inventoryArrayList;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getOverallTurns() {
        return overallTurns;
    }

    public void setOverallTurns(int overallTurns) {
        this.overallTurns = overallTurns;
    }

    public int getOverallTrades() {
        return overallTrades;
    }

    public void setOverallTrades(int overallTrades) {
        this.overallTrades = overallTrades;
    }

    public ArrayList<Inventory> getInventoryArrayList() {
        return inventoryArrayList;
    }

    public void setInventoryArrayList(ArrayList<Inventory> inventoryArrayList) {
        this.inventoryArrayList = inventoryArrayList;
    }

}
