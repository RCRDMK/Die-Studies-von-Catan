package de.uol.swp.client.game.HelperObjects;

/**
 * This class is used to for the statsTable in the SummaryScreen
 * <p>
 * <p>
 * This DTO is needed so we can add items to the summary statsTable in an easy way.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.game.SummaryPresenter
 * @since 2021-05-01
 */
public class DetailedTableStats {
    private final String user;
    private final int roadAmount;
    private final int knightAmount;
    private final int victoryPoints;

    /**
     * Constructor for StatsDTO
     * <p>
     * The passed parameters are equal to the table columns.
     *
     * @param user          username as String
     * @param roadAmount    biggest road as int
     * @param knightAmount  biggest knight amount as int
     * @param victoryPoints victory points as int
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public DetailedTableStats(String user, int roadAmount, int knightAmount, int victoryPoints) {
        this.user = user;
        this.roadAmount = roadAmount;
        this.knightAmount = knightAmount;
        this.victoryPoints = victoryPoints;
    }

    /**
     * Getter for the Victory Points
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return victoryPoints
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Getter for the Knights
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return knightAmount
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public int getKnights() {
        return knightAmount;
    }

    /**
     * Getter for the Roads
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return roadAmount
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public int getRoads() {
        return roadAmount;
    }

    /**
     * Getter for the username as String
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public String getUser() {
        return user;
    }

}
