package de.uol.swp.client.game.HelperObjects;

/**
 * The type Inventory table stats.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.game.SummaryPresenter
 * @since 2021-05-08
 */
public class InventoryTableStats {
    private String user;
    private int lumber;
    private int brick;
    private int grain;
    private int wool;
    private int ore;

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
     *
     * @return the user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets lumber.
     *
     * @return the lumber
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getLumber() {
        return lumber;
    }

    /**
     * Sets lumber.
     *
     * @param lumber the lumber
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setLumber(int lumber) {
        this.lumber = lumber;
    }

    /**
     * Gets brick.
     *
     * @return the brick
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getBrick() {
        return brick;
    }

    /**
     * Sets brick.
     *
     * @param brick the brick
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setBrick(int brick) {
        this.brick = brick;
    }

    /**
     * Gets grain.
     *
     * @return the grain
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getGrain() {
        return grain;
    }

    /**
     * Sets grain.
     *
     * @param grain the grain
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setGrain(int grain) {
        this.grain = grain;
    }

    /**
     * Gets wool.
     *
     * @return the wool
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getWool() {
        return wool;
    }

    /**
     * Sets wool.
     *
     * @param wool the wool
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setWool(int wool) {
        this.wool = wool;
    }

    /**
     * Gets ore.
     *
     * @return the ore
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public int getOre() {
        return ore;
    }

    /**
     * Sets ore.
     *
     * @param ore the ore
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-08
     */
    public void setOre(int ore) {
        this.ore = ore;
    }
}
