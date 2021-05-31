package de.uol.swp.common.game.trade;

import java.io.Serializable;

/**
 * This class is used to save information about an item to be traded
 * <p>
 * enhanced by Anton Nikiforov 'boolean notEnough in the inventory from user'
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 * @since 2021-05-28
 */
public class TradeItem implements Serializable {
    //name of the item e.g. "Lumber"
    private final String name;
    private int count;
    private boolean notEnough = false;

    /**
     * Constructor
     *
     * @param count how many items
     * @param name the name of the item
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
     * setter
     *
     * @param count int
     * @author Anton Nikiforov
     * @since 2021-05-31
     */
    public void setCount(int count) {
        this.count = count;
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

    /**
     * getter
     *
     * @author Anton Nikiforov
     * @since 2021-05-28
     * @return boolean enoughInWallet
     */
    public boolean isNotEnough() {
        return notEnough;
    }

    /**
     * setter
     *
     + @param boolean enoughInWallet from user
     *
     * @author Anton Nikiforov
     * @since 2021-05-28
     */
    public void setNotEnough(boolean notEnough) {
        this.notEnough = notEnough;
    }
}
