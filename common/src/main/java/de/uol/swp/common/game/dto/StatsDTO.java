package de.uol.swp.common.game.dto;

import de.uol.swp.common.game.Inventory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The type Stats dto.
 */
public class StatsDTO implements Serializable {

    private final String winner;
    private final String gameName;
    private final int overallTurns;
    private final int overallTrades;
    private final ArrayList<Inventory> inventoryArrayList;

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
     * Gets inventory array list.
     *
     * @return the inventory array list
     * @author René Meyer
     * @since 2021 -05-08
     */
    public ArrayList<Inventory> getInventoryArrayList() {
        return inventoryArrayList;
    }

}
