package de.uol.swp.common.game.trade;

import java.io.Serializable;

/**
 * This class is used to save information about an item to be traded
 * <p>
 * enhanced by Anton Nikiforov 'boolean canBuy from the bank'
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 * @since 2021-05-28
 */
public class TradeItem implements Serializable {
    //name of the item e.g. "Lumber"
    private String name;
    private int count;
    private boolean canBuy;

    /**
     * Constructor
     *
     * @param count how many items
     * @param name the name of the item
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */
    public TradeItem(String name, int count){
        this.name = name;
        this.count = count;
        this.canBuy = true;
    }

    /**
     * getter
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     * @return String name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * getter
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     * @return int count
     */
    public int getCount() {
        return count;
    }

    /**
     * getter
     *
     * @author Anton Nikiforov
     * @since 2021-05-28
     * @return boolean canBuy
     */
    public boolean isCanBuy() {
        return canBuy;
    }

    /**
     * setter
     *
     + @param boolean canBuy from the bank
     *
     * @author Anton Nikiforov
     * @since 2021-05-28
     */
    public void setCanBuy(boolean canBuy) {
        this.canBuy = canBuy;
    }
}
