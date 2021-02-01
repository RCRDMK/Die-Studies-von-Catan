package de.uol.swp.server.game.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and manages the Inventory
 *
 * @author Anton
 * @since 2021-02-01
 */
public class Inventory {

    // Resource
    private int lumber = 0;
    private int brick = 0;
    private int grain = 0;
    private int wool = 0;
    private int ore = 0;

    // Development Cards
    private int cardKnight = 0;
    private int cardMonopoly = 0;
    private int cardRoadBuilding = 0;
    private int cardYearOfPlenty = 0;
    private int cardVictoryPoint = 0;

    // Achievements
    private int victoryPoints = 0;
    private int playedKnights = 0;
    private int continuousRoad = 0;
    private boolean largestArmy = false;
    private boolean longestRoad = false;

    // Building Units
    private int unitCity = 4;
    private int unitSettlement = 5;
    private int unitRoad = 15;

    /**
     * Getter and Setter for Resource
     * <p>
     * Setter are restricted
     */
    public int getLumber() { return lumber; }

    public int getBrick() { return brick; }

    public int getGrain() { return grain; }

    public int getWool() { return wool; }

    public int getOre() { return ore; }

    public void setLumber(int lumber) { this.lumber = Math.max(lumber, 0); }

    public void setBrick(int brick) { this.brick = Math.max(brick, 0); }

    public void setGrain(int grain) { this.grain = Math.max(grain, 0); }

    public void setWool(int wool) { this.wool = Math.max(wool, 0); }

    public void setOre(int ore) { this.ore = Math.max(ore, 0); }


    /**
     * Getter and Setter for Development Cards
     * <p>
     * Setter are restricted
     */
    public int getCardKnight() { return cardKnight; }

    public int getCardMonopoly() { return cardMonopoly; }

    public int getCardRoadBuilding() { return cardRoadBuilding; }

    public int getCardYearOfPlenty() { return cardYearOfPlenty; }

    public int getCardVictoryPoint() { return cardVictoryPoint; }

    public void setCardKnight(int cardKnight) { this.cardKnight = Math.max(cardKnight, 0); }

    public void setCardMonopoly(int cardMonopoly) { this.cardMonopoly = Math.max(cardMonopoly, 0); }

    public void setCardRoadBuilding(int cardRoadBuilding) { this.cardRoadBuilding = Math.max(cardRoadBuilding, 0); }

    public void setCardYearOfPlenty(int cardYearOfPlenty) { this.cardYearOfPlenty = Math.max(cardYearOfPlenty, 0); }

    public void setCardVictoryPoint(int cardVictoryPoint) { this.cardVictoryPoint = Math.max(cardVictoryPoint, 0); }


    /**
     * Getter and Setter for Achievements
     * <p>
     * Setter are restricted
     */
    public int getVictoryPoints() { return victoryPoints; }

    public int getPlayedKnights() { return playedKnights; }

    public int getNetworkRoad() { return continuousRoad; }

    public void setVictoryPoints(int victoryPoints) { this.victoryPoints = Math.max(victoryPoints, 0); }

    public void setPlayedKnights(int playedKnights) { this.playedKnights = Math.max(playedKnights, 0); }

    public void setNetworkRoad(int networkRoad) { this.continuousRoad = Math.max(networkRoad, 0); }

    public boolean isLargestArmy() { return largestArmy; }

    public boolean isLongestRoad() { return longestRoad; }

    public void setLargestArmy(boolean largestArmy) { this.largestArmy = largestArmy; }

    public void setLongestRoad(boolean longestRoad) { this.longestRoad = longestRoad; }

    /**
     * Getter and Setter for Building Units
     * <p>
     * Setter are restricted
     */
    public int getUnitCity() { return unitCity; }

    public int getUnitSettlement() { return unitSettlement; }

    public int getUnitRoad() { return unitRoad; }

    public void setUnitCity(int unitCity) {
        if (unitCity < 0) this.unitCity = 0;
        else this.unitCity = Math.min(unitCity, 4);
    }

    public void setUnitSettlement(int unitSettlement) {
        if (unitSettlement < 0) this.unitSettlement = 0;
        else this.unitSettlement = Math.min(unitSettlement, 5);
    }

    public void setUnitRoad(int unitRoad) {
        if (unitRoad < 0) this.unitRoad = 0;
        else this.unitRoad = Math.min(unitRoad, 15);
    }


    // This method add the Resource
    public int getResource() { return lumber + brick + grain + wool + ore; }

    // This method add the Development Cards
    public int getDevelopmentCards() {
        return cardVictoryPoint + cardKnight + cardMonopoly + cardRoadBuilding + cardYearOfPlenty;
    }


    /**
     * Creates Private Inventory View
     * <p>
     * This method creates a HashMap with all the information about the Inventory
     * for the private view to send to client
     *
     * @return privateInventory
     */
    public HashMap getPrivateView() {

        Map<String, Integer> privateInventory = new HashMap<>();

        // Resource
        privateInventory.put("Lumber", lumber);
        privateInventory.put("Brick", brick);
        privateInventory.put("Grain", grain);
        privateInventory.put("Wool", wool);
        privateInventory.put("Ore", ore);

        // Development Cards
        privateInventory.put("Knight", cardKnight);
        privateInventory.put("Monopoly", cardMonopoly);
        privateInventory.put("Road Building", cardRoadBuilding);
        privateInventory.put("Year of Plenty", cardYearOfPlenty);
        privateInventory.put("Victory Point Card", cardVictoryPoint);

        // Building Units
        privateInventory.put("Citys", unitCity);
        privateInventory.put("Settlements", unitSettlement);
        privateInventory.put("Roads", unitRoad);

        // Achievement
        privateInventory.put("Victory Points", victoryPoints);

        return (HashMap) privateInventory;
    }

    /**
     * Creates Public Inventory View
     * <p>
     * This method creates a HashMap with necessary information about the Inventory
     * for the public view to send to all clients.
     * "Public Victory Points" are without the Victory Point Cards
     *
     * @return publicInventory
     */
    public HashMap getPublicView() {

        Map<String, Integer> publicInventory = new HashMap<>();

        publicInventory.put("Resource", getResource());
        publicInventory.put("Development Cards", getDevelopmentCards());

        publicInventory.put("Played Knights", playedKnights);
        publicInventory.put("Continuous Road", continuousRoad);

        if (largestArmy) publicInventory.put("Largest Army", 1);
        else publicInventory.put("Largest Army", 0);

        if (longestRoad) publicInventory.put("Longest Road", 1);
        else publicInventory.put("Longest Road", 0);

        if(victoryPoints < cardVictoryPoint) publicInventory.put("Public Victory Points", 0);
        else publicInventory.put("Public Victory Points", victoryPoints - cardVictoryPoint);

        return (HashMap) publicInventory;
    }
}