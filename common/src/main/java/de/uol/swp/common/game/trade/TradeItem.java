package de.uol.swp.common.game.trade;

public class TradeItem {
    private String name;
    private int count;

    public TradeItem(String name, int count){
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
