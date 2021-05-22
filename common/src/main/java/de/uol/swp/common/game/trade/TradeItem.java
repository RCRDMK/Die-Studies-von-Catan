package de.uol.swp.common.game.trade;

import java.io.Serializable;

/**
 * This class is used to save information about an item to be traded
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 */
public class TradeItem implements Serializable {
    //name of the item e.g. "Lumber"
    private final String name;
    private int count;

    /**
     * Constructor
     *
     * @param count how many items
     * @param name  the name of the item
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */
    public TradeItem(String name, int count) {
        this.name = name;
        this.count = count;
    }

    /**
     * getter
     *
     * @return String name of the item
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */
    public String getName() {
        return name;
    }

    /**
     * getter
     *
     * @return int count
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */
    public int getCount() {
        return count;
    }

    /**
     * decrements Count
     *
     * @param i the amount to decrease
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-22
     */
    public void decCount(int i) {
        count = count - i;
    }
}
