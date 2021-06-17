package de.uol.swp.client.game.HelperObjects;

/**
 * The type Inventory table stats.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.game.SummaryPresenter
 * @since 2021-05-08
 */
public class InventoryTableStats {
    private final String user;
    private final int lumber;
    private final int brick;
    private final int grain;
    private final int wool;
    private final int ore;

    /**
     * Instantiates a new Inventory table stats.
     *
     * @param user   the user
     * @param lumber the lumber
     * @param brick  the brick
     * @param grain  the grain
     * @param wool   the wool
     * @param ore    the ore
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public InventoryTableStats(String user, int lumber, int brick, int grain, int wool, int ore) {
        this.user = user;
        this.lumber = lumber;
        this.brick = brick;
        this.grain = grain;
        this.wool = wool;
        this.ore = ore;
    }

    /**
     * Gets user.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public String getUser() {
        return user;
    }

    /**
     * Gets lumber.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the lumber
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getLumber() {
        return lumber;
    }

    /**
     * Gets brick.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the brick
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getBrick() {
        return brick;
    }

    /**
     * Gets grain.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the grain
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getGrain() {
        return grain;
    }

    /**
     * Gets wool.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the wool
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getWool() {
        return wool;
    }

    /**
     * Gets ore.
     * <p>
     * Used for the PropertyValueFactory!
     *
     * @return the ore
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getOre() {
        return ore;
    }
}
