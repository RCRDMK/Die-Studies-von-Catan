package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.inventory.Inventory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Stats dto.
 */
public class StatsDTO implements Serializable {

    private String winner;
    private String gameName;
    private int overallTurns;
    private int overallTrades;
    private ArrayList<Inventory> inventoryArrayList;

    /**
     * Constructor StatsDTO
     * <p>
     * The Constructor for the StatsDTO
     *
     * @param gameName           name of the game
     * @param winner             name of the user who won
     * @param overallTrades      overall trades made in the game
     * @param overallTurns       overall turns done in the game
     * @param inventoryArrayList all game inventories as array list
     * @author René Meyer
     * @since 2021 -05-08
     */
    public StatsDTO(String gameName, String winner, int overallTrades, int overallTurns, ArrayList<Inventory> inventoryArrayList) {
        this.gameName = gameName;
        this.winner = winner;
        this.overallTrades = overallTrades;
        this.overallTurns = overallTurns;
        this.inventoryArrayList = inventoryArrayList;
    }

    /**
     * Gets winner.
     *
     * @return the winner
     * @author René Meyer
     * @since 2021 -05-08
     */
    public String getWinner() {
        return winner;
    }

    /**
     * Sets winner.
     *
     * @param winner the winner
     * @author René Meyer
     * @since 2021 -05-08
     */
    public void setWinner(String winner) {
        this.winner = winner;
    }

    /**
     * Gets game name.
     *
     * @return the game name
     * @author René Meyer
     * @since 2021 -05-08
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Sets game name.
     *
     * @param gameName the game name
     * @author René Meyer
     * @since 2021 -05-08
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Gets overall turns.
     *
     * @return the overall turns
     * @author René Meyer
     * @since 2021 -05-08
     */
    public int getOverallTurns() {
        return overallTurns;
    }

    /**
     * Sets overall turns.
     *
     * @param overallTurns the overall turns
     * @author René Meyer
     * @since 2021 -05-08
     */
    public void setOverallTurns(int overallTurns) {
        this.overallTurns = overallTurns;
    }

    /**
     * Gets overall trades.
     *
     * @return the overall trades
     * @author René Meyer
     * @since 2021 -05-08
     */
    public int getOverallTrades() {
        return overallTrades;
    }

    /**
     * Sets overall trades.
     *
     * @param overallTrades the overall trades
     * @author René Meyer
     * @since 2021 -05-08
     */
    public void setOverallTrades(int overallTrades) {
        this.overallTrades = overallTrades;
    }

    /**
     * Gets inventory array list.
     *
     * @return the inventory array list
     * @author René Meyer
     * @since 2021 -05-08
     */
    public ArrayList<Inventory> getInventoryArrayList() {
        return inventoryArrayList;
    }

    /**
     * Sets inventory array list.
     *
     * @param inventoryArrayList the inventory array list
     * @author René Meyer
     * @since 2021 -05-08
     */
    public void setInventoryArrayList(ArrayList<Inventory> inventoryArrayList) {
        this.inventoryArrayList = inventoryArrayList;
    }

}
