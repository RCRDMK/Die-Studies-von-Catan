package de.uol.swp.client.game.HelperObjects;

public class InventoryTableStats {
    private String user;
    private int lumber;
    private int brick;
    private int grain;
    private int wool;
    private int ore;

    public InventoryTableStats(String user, int lumber, int brick, int grain, int wool, int ore) {
        this.user = user;
        this.lumber = lumber;
        this.brick = brick;
        this.grain = grain;
        this.wool = wool;
        this.ore = ore;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getLumber() {
        return lumber;
    }

    public void setLumber(int lumber) {
        this.lumber = lumber;
    }

    public int getBrick() {
        return brick;
    }

    public void setBrick(int brick) {
        this.brick = brick;
    }

    public int getGrain() {
        return grain;
    }

    public void setGrain(int grain) {
        this.grain = grain;
    }

    public int getWool() {
        return wool;
    }

    public void setWool(int wool) {
        this.wool = wool;
    }

    public int getOre() {
        return ore;
    }

    public void setOre(int ore) {
        this.ore = ore;
    }
}
