package de.uol.swp.client.game.HelperObjects;

public class StatsDTO {
    private String user;
    private int roadAmount;
    private int knightAmount;
    private int victoryPoints;

    public StatsDTO(String user, int roadAmount, int knightAmount, int victoryPoints) {
        this.user = user;
        this.roadAmount = roadAmount;
        this.knightAmount = knightAmount;
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public int getKnights() {
        return knightAmount;
    }

    public void setKnights(int knightAmount) {
        this.knightAmount = knightAmount;
    }

    public int getRoads() {
        return roadAmount;
    }

    public void setRoads(int roadAmount) {
        this.roadAmount = roadAmount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
