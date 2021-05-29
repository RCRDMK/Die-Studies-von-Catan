package de.uol.swp.common.game.trade;

import java.io.Serializable;

/**
 * This class is used to save information about an item to be traded
 * <p>
 * enhanced by Anton Nikiforov 'boolean enoughInWallet from user'
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 * @since 2021-05-28
 */
public class TradeItem implements Serializable {
    //name of the item e.g. "Lumber"
    private String name;
    private int count;
    private boolean enoughInWallet;

    /**
     * Constructor
     *
     * @param count how many items
     * @param name the name of the item
     *
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
     * @return boolean enoughInWallet
     */
    public boolean isEnoughInWallet() {
        return enoughInWallet;
    }

    /**
     * setter
     *
     + @param boolean enoughInWallet from user
     *
     * @author Anton Nikiforov
     * @since 2021-05-28
     */
    public void setEnoughInWallet(boolean enoughInWallet) {
        this.enoughInWallet = enoughInWallet;
    }
}
