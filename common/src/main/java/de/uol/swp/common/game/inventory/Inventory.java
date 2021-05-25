package de.uol.swp.common.game.inventory;

import de.uol.swp.common.user.User;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Creates and manages the Inventory
 *
 * @author Anton
 * @since 2021-02-01
 */
public class Inventory implements Serializable {

    private User user;

    public Inventory(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Resource Cards
    public Card lumber = new Card();
    public Card brick = new Card();
    public Card grain = new Card();
    public Card wool = new Card();
    public Card ore = new Card();

    // Development Cards
    public Card cardKnight = new Card();
    public Card cardMonopoly = new Card();
    public Card cardRoadBuilding = new Card();
    public Card cardYearOfPlenty = new Card();

    // Building Units
    public Unit city = new Unit(4);
    public Unit road = new Unit(15);
    public Unit settlement = new Unit(5);

    // Achievements
    private int victoryPoints = 0;
    private int playedKnights = 0;
    private int continuousRoad = 0;
    private int cardVictoryPoint = 0;
    private boolean largestArmy = false;
    private boolean longestRoad = false;


    //Getter and Setter for Achievements

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = Math.max(victoryPoints, 0);
    }

    public int getPlayedKnights() {
        return playedKnights;
    }

    public void setPlayedKnights(int playedKnights) {
        this.playedKnights = Math.max(playedKnights, 0);
    }

    public int getContinuousRoad() {
        return continuousRoad;
    }

    public void setContinuousRoad(int continuousRoad) {
        this.continuousRoad = Math.max(continuousRoad, 0);
    }

    public boolean isLargestArmy() {
        return largestArmy;
    }

    public void setLargestArmy(boolean largestArmy) {
        this.largestArmy = largestArmy;
    }

    public boolean isLongestRoad() {
        return longestRoad;
    }

    public void setLongestRoad(boolean longestRoad) {
        this.longestRoad = longestRoad;
    }


    //Increment the Victory Point Card
    public void incCardVictoryPoint() {
        this.cardVictoryPoint++;
        this.victoryPoints++;
    }

    //This method add the Resource Cards
    public int getResource() {
        return lumber.getNumber() +
                brick.getNumber() +
                grain.getNumber() +
                wool.getNumber() +
                ore.getNumber();
    }

    //This method gets the Development Cards
    public int getDevelopmentCards() {
        return cardVictoryPoint +
                cardKnight.getNumber() +
                cardMonopoly.getNumber() +
                cardRoadBuilding.getNumber() +
                cardYearOfPlenty.getNumber();
    }

    /**
     * Creates Private Inventory View
     * <p>
     * This method creates a HashMap with all the information about the Inventory
     * for the private view to send to client
     *
     * @return privateInventory
     */
    public HashMap<String, Integer> getPrivateView() {

        HashMap<String, Integer> privateInventory = new HashMap<>();

        // Resource Cards
        privateInventory.put("Lumber", lumber.getNumber());
        privateInventory.put("Brick", brick.getNumber());
        privateInventory.put("Grain", grain.getNumber());
        privateInventory.put("Wool", wool.getNumber());
        privateInventory.put("Ore", ore.getNumber());

        // Development Cards
        privateInventory.put("Knight", cardKnight.getNumber());
        privateInventory.put("Monopoly", cardMonopoly.getNumber());
        privateInventory.put("Road Building", cardRoadBuilding.getNumber());
        privateInventory.put("Year of Plenty", cardYearOfPlenty.getNumber());
        privateInventory.put("Victory Point Card", cardVictoryPoint);

        // Building Units
        privateInventory.put("Cities", city.getNumber());
        privateInventory.put("Roads", road.getNumber());
        privateInventory.put("Settlements", settlement.getNumber());

        // Achievement
        privateInventory.put("Victory Points", victoryPoints);

        return privateInventory;
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
    public HashMap<String, Integer> getPublicView() {

        HashMap<String, Integer> publicInventory = new HashMap<>();

        publicInventory.put("Resource", getResource());
        publicInventory.put("Development Cards", getDevelopmentCards());

        publicInventory.put("Played Knights", playedKnights);
        publicInventory.put("Continuous Road", continuousRoad);

        if (largestArmy) publicInventory.put("Largest Army", 1);
        else publicInventory.put("Largest Army", 0);

        if (longestRoad) publicInventory.put("Longest Road", 1);
        else publicInventory.put("Longest Road", 0);

        if (victoryPoints < cardVictoryPoint) publicInventory.put("Public Victory Points", 0);
        else publicInventory.put("Public Victory Points", victoryPoints - cardVictoryPoint);

        return publicInventory;
    }

    /**
     * increases a specific Ressource Card by a specific amount
     * <p>
     * this method calls the Method incNumber(int) of the class Card
     * String Card specifies the Ressource Card and Development Cards
     * valid Strings: Lumber, Brick, Grain, Wool, Ore, Knight, Monopoly, Road Building,
     * Year of Plenty, Victory Point Card.
     *
     * enhanced by Anton Nikiforov, Alexander Losse, Iskander Yusupov
     * @since 2021-05-16
     * @param card   the name of the Ressource Card
     * @param amount how much of the Card should be increased
     * @return true if valid resource name, false if not
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-08
     */
    public boolean incCard(String card, int amount) {
        switch (card) {
            case "Lumber":
                lumber.incNumber(amount);
                return true;
            case "Brick":
                brick.incNumber(amount);
                return true;
            case "Grain":
                grain.incNumber(amount);
                return true;
            case "Wool":
                wool.incNumber(amount);
                return true;
            case "Ore":
                ore.incNumber(amount);
                return true;
            case "Knight":
                cardKnight.incNumber(amount);
                return true;
            case "Monopoly":
                cardMonopoly.incNumber(amount);
                return true;
            case "Road Building":
                cardRoadBuilding.incNumber(amount);
                return true;
            case "Year of Plenty":
                cardYearOfPlenty.incNumber(amount);
                return true;
            case "Victory Point Card":
                incCardVictoryPoint();
                return true;
            default:
                return false;
        }
    }

    /**
     * Decreases a specific ressource card by a specific amount
     * <p>
     * This method calls the method decNumber(int) of the class Card
     * String Card specifies the ressource card
     * valid Strings: Lumber, Brick, Grain, Wool, Ore
     *
     * @param card   the name of the Ressource Card
     * @param amount how much of the Card should be decreased
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-08
     */
    public void decCard(String card, int amount) {
        switch (card) {
            case "Lumber":
                lumber.decNumber(amount);
                break;
            case "Brick":
                brick.decNumber(amount);
                break;
            case "Grain":
                grain.decNumber(amount);
                break;
            case "Wool":
                wool.decNumber(amount);
                break;
            case "Ore":
                ore.decNumber(amount);
                break;
        }
    }

}