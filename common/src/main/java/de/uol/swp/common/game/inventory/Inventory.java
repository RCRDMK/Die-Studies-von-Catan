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
    public CardStack lumber = new CardStack();
    public CardStack brick = new CardStack();
    public CardStack grain = new CardStack();
    public CardStack wool = new CardStack();
    public CardStack ore = new CardStack();

    // Development Cards
    public CardStack cardKnight = new CardStack();
    public CardStack cardMonopoly = new CardStack();
    public CardStack cardRoadBuilding = new CardStack();
    public CardStack cardYearOfPlenty = new CardStack();

    // Building Units
    public UnitStack city = new UnitStack(4);
    public UnitStack road = new UnitStack(15);
    public UnitStack settlement = new UnitStack(5);

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

    public int getCardVictoryPoint() {
        return this.cardVictoryPoint;
    }

    //Increment the Victory Point Card and increase the victoryPoints by one
    public void incCardStackVictoryPoint() {
        this.cardVictoryPoint++;
        this.victoryPoints++;
    }

    //Increment the Victory Point Card by amount
    public void incCardVictoryPoint(int amount) {
        this.cardVictoryPoint += amount;
        this.victoryPoints += amount;
    }

    /**
     * Summed all Resource Cards together
     * <p>
     * This method summed all Resource Cards together and gives their sum
     *
     * @return sum of the Resource Cards
     * @author Anton Nikiforov
     * @since 2021-02-01
     */
    public int sumResource() {
        return  lumber.getNumber() +
                brick.getNumber() +
                grain.getNumber() +
                wool.getNumber() +
                ore.getNumber();
    }

    /**
     * Summed all Development Cards together
     * <p>
     * This method summed all Development Cards together and gives their sum
     *
     * @return sum of the Development Cards
     * @author Anton Nikiforov
     * @since 2021-02-01
     */
    public int sumDevelopmentCards() {
        return  cardVictoryPoint +
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

        if (victoryPoints < cardVictoryPoint) publicInventory.put("Public Victory Points", 0);
        else publicInventory.put("Public Victory Points", victoryPoints - cardVictoryPoint);

        publicInventory.put("Resource", sumResource());
        publicInventory.put("Development Cards", sumDevelopmentCards());

        publicInventory.put("Played Knights", playedKnights);
        publicInventory.put("Continuous Road", continuousRoad);

        if (largestArmy) publicInventory.put("Largest Army", 1);
        else publicInventory.put("Largest Army", 0);

        if (longestRoad) publicInventory.put("Longest Road", 1);
        else publicInventory.put("Longest Road", 0);



        return publicInventory;
    }

    /**
     * Getter for Cards
     * <p>
     * It gets the right card for the entered name.
     *
     * @param cardName to get the CardStack from
     *
     * @return Card with entered cardName
     * @author Anton Nikiforov
     * @see CardStack
     * @since 2021-04-06
     */
    public CardStack getCardStack(String cardName) {
        switch (cardName) {
            case "Lumber":
                return lumber;
            case "Brick":
                return brick;
            case "Grain":
                return grain;
            case "Wool":
                return wool;
            case "Ore":
                return ore;
            case "Knight":
                return cardKnight;
            case "Monopoly":
                return cardMonopoly;
            case "Road Building":
                return cardRoadBuilding;
            case "Year of Plenty":
                return cardYearOfPlenty;
            default:
                return null;
        }
    }

    /**
     * Increases a specific Ressource Card by a specific amount
     * <p>
     * this method calls the Method incNumber(int) of the class Card
     * String Card specifies the Ressource Card and Development Cards
     * valid Strings: Lumber, Brick, Grain, Wool, Ore, Knight, Monopoly, Road Building,
     * Year of Plenty, Victory Point Card.
     * <p>
     * enhanced by Anton Nikiforov, Alexander Losse, Iskander Yusupov
     *
     * @param cardName   the name of the Ressource Card
     * @param amount how much of the Card should be increased
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-05-16
     * @since 2021-04-08
     */
    public void incCardStack(String cardName, int amount) {
        switch (cardName) {
            case "Lumber":
                lumber.incNumber(amount);
                break;
            case "Brick":
                brick.incNumber(amount);
                break;
            case "Grain":
                grain.incNumber(amount);
                break;
            case "Wool":
                wool.incNumber(amount);
                break;
            case "Ore":
                ore.incNumber(amount);
                break;
            case "Knight":
                cardKnight.incNumber(amount);
                break;
            case "Monopoly":
                cardMonopoly.incNumber(amount);
                break;
            case "Road Building":
                cardRoadBuilding.incNumber(amount);
                break;
            case "Year of Plenty":
                cardYearOfPlenty.incNumber(amount);
                break;
            case "Victory Point Card":
                incCardStackVictoryPoint();
                break;
        }
    }

    /**
     * Decreases a specific ressource card by a specific amount
     * <p>
     * This method calls the method decNumber(int) of the class Card
     * String Card specifies the ressource card
     * valid Strings: Lumber, Brick, Grain, Wool, Ore
     *
     * @param cardName the name of the Ressource Card
     * @param amount   how much of the Card should be decreased
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-08
     */
    public void decCardStack(String cardName, int amount) {
        switch (cardName) {
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
            case "Knight":
                cardKnight.decNumber(amount);
                break;
            case "Monopoly":
                cardMonopoly.decNumber(amount);
                break;
            case "Road Building":
                cardRoadBuilding.decNumber(amount);
                break;
            case "Year of Plenty":
                cardYearOfPlenty.decNumber(amount);
                break;
        }
    }

    /**
     * Method used to return the amount of a certain resource
     *
     * @param resource the String name of the resource
     * @return the int amount of the resource in this inventory
     * @author Marc Hermes
     * @since 2021-05-19
     */
    public int getSpecificResourceAmount(String resource) {
        switch (resource) {
            case "Lumber":
                return lumber.getNumber();
            case "Brick":
                return brick.getNumber();
            case "Grain":
                return grain.getNumber();
            case "Wool":
                return wool.getNumber();
            case "Ore":
                return ore.getNumber();
            case "Knight":
                return cardKnight.getNumber();
            case "Monopoly":
                return cardMonopoly.getNumber();
            case "Road Building":
                return cardRoadBuilding.getNumber();
            case "Year of Plenty":
                return cardYearOfPlenty.getNumber();
            case "Victory Point Card":
                return cardVictoryPoint;
            default:
                return 0;
        }
    }

}