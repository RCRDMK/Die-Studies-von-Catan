package de.uol.swp.common.game.trade;

/**
 *This class is used to save information about an item to be traded
 *
 * @author Alexander Losse, Ricardo Mook
 * @since 2021-04-07
 */
public class TradeItem {
    //name of the item e.g. "Lumber"
    private String name;
    private int count;

    /**
     * Constructor
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     * @param count how many items
     * @param name the name of the item
     */
    public TradeItem(String name, int count){
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
}
