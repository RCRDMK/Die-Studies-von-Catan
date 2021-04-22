package de.uol.swp.common.game.inventory;

import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and manages the Inventory
 *
 * @author Anton
 * @since 2021-02-01
 */
public class Inventory {

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

    public int getVictoryPoints() { return victoryPoints; }

    public int getPlayedKnights() { return playedKnights; }

    public int getContinuousRoad() { return continuousRoad; }

    public boolean isLargestArmy() { return largestArmy; }

    public boolean isLongestRoad() { return longestRoad; }

    public void setLargestArmy(boolean largestArmy) { this.largestArmy = largestArmy; }

    public void setLongestRoad(boolean longestRoad) { this.longestRoad = longestRoad; }

    public void setVictoryPoints(int victoryPoints) { this.victoryPoints = Math.max(victoryPoints, 0); }

    public void setPlayedKnights(int playedKnights) { this.playedKnights = Math.max(playedKnights, 0); }

    public void setContinuousRoad(int continuousRoad) { this.continuousRoad = Math.max(continuousRoad, 0); }


    //Increment the Victory Point Card and increase the victoryPoints by one
    public void incCardVictoryPoint() {
        this.cardVictoryPoint++;
        this.victoryPoints++;
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
     * Getter for Cards
     * <p>
     * It gets the right card for the entered name.
     *
     * @param cardName to get
     *
     * @return Card with entered cardName
     * @author Anton Nikiforov
     * @see de.uol.swp.common.game.inventory.Card
     * @since 2021-04-06
     */
    public Card getCard(String cardName) {
        switch (cardName) {
            case "Lumber" : return lumber;
            case "Brick" : return brick;
            case "Grain" : return grain;
            case "Wool" : return wool;
            case "Ore" : return ore;

            case "Knight" : return cardKnight;
            case "Monopoly" : return cardMonopoly;
            case "Road Building" : return cardRoadBuilding;
            case "Year of Plenty" : return cardYearOfPlenty;

            default : return null;
        }
    }
    /**
     * Getter for the number of Cards
     * <p>
     * It gets the number of Cards for the entered cardName.
     *
     * @param cardName to get the number from
     *
     * @return number for Card with entered cardName
     * @author Anton Nikiforov
     * @see de.uol.swp.common.game.inventory.Card
     * @since
     */
    public int getNumberFromCard(String cardName) {
        return getCard(cardName).getNumber();
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
        privateInventory.put("Citys", city.getNumber());
        privateInventory.put("Roads", road.getNumber());
        privateInventory.put("Settlements", settlement.getNumber());

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

        publicInventory.put("Resource", sumResource());
        publicInventory.put("Development Cards", sumDevelopmentCards());

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