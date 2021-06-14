package de.uol.swp.common.game.inventory;

import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the Inventory
 *
 * @author Anton
 * @since 2021-03-08
 */
public class InventoryTest {
    User user = new UserDTO("test1", "", "");

    Inventory inventory = new Inventory(user);
    HashMap<String, Integer> privateInventory = inventory.getPrivateView();
    HashMap<String, Integer> publicInventory = inventory.getPublicView();

    /**
     * This test analyzes the Private Inventory View
     * <p>
     * It examines whether the Private Inventory View
     * is instance of a HashMap and analyzes their content.
     *
     * @author Anton
     * @since 2021-03-08
     */
    @Test
    void onGetPrivateView() {
        assertTrue(privateInventory instanceof HashMap);

        // Resource Cards
        assertEquals(privateInventory.get("Lumber"), 0);
        assertEquals(privateInventory.get("Brick"), 0);
        assertEquals(privateInventory.get("Grain"), 0);
        assertEquals(privateInventory.get("Wool"), 0);
        assertEquals(privateInventory.get("Ore"), 0);

        // Development Cards
        assertEquals(privateInventory.get("Knight"), 0);
        assertEquals(privateInventory.get("Monopoly"), 0);
        assertEquals(privateInventory.get("Road Building"), 0);
        assertEquals(privateInventory.get("Year of Plenty"), 0);
        assertEquals(privateInventory.get("Victory Point Card"), 0);

        // Building Units
        assertEquals(privateInventory.get("Cities"), 4);
        assertEquals(privateInventory.get("Roads"), 15);
        assertEquals(privateInventory.get("Settlements"), 5);

        // Achievement
        assertEquals(privateInventory.get("Victory Points"), 0);
    }

    /**
     * This test analyzes the Public Inventory View
     * <p>
     * It examines whether the Public Inventory View
     * is instance of a HashMap and analyzes their content.
     *
     * @author Anton
     * @since 2021-03-08
     */
    @Test
    void onGetPublicView() {
        assertTrue(publicInventory instanceof HashMap);

        assertEquals(publicInventory.get("Resource"), 0);
        assertEquals(publicInventory.get("Development Cards"), 0);
        assertEquals(publicInventory.get("Played Knights"), 0);
        assertEquals(publicInventory.get("Continuous Road"), 0);
        assertEquals(publicInventory.get("Largest Army"), 0);
        assertEquals(publicInventory.get("Longest Road"), 0);
        assertEquals(publicInventory.get("Public Victory Points"), 0);
    }

    /**
     * Incrementer Test
     * <p>
     * Increment the number of the cards and analyzes the HashMap
     *
     * @author Anton
     * @since 2021-03-08
     */
    @Test
    void onIncNumber() {

        // Resource Cards inc
        inventory.lumber.incNumber();
        inventory.brick.incNumber();
        inventory.grain.incNumber();
        inventory.wool.incNumber();
        inventory.ore.incNumber();

        // Development Cards inc
        inventory.cardKnight.incNumber();
        inventory.cardMonopoly.incNumber();
        inventory.cardRoadBuilding.incNumber();
        inventory.cardYearOfPlenty.incNumber();
        inventory.incCardStackVictoryPoint();

        // Inventory analysis
        privateInventory = inventory.getPrivateView();
        assertEquals(privateInventory.get("Lumber"), 1);
        assertEquals(privateInventory.get("Brick"), 1);
        assertEquals(privateInventory.get("Grain"), 1);
        assertEquals(privateInventory.get("Wool"), 1);
        assertEquals(privateInventory.get("Ore"), 1);
        assertEquals(privateInventory.get("Knight"), 1);
        assertEquals(privateInventory.get("Monopoly"), 1);
        assertEquals(privateInventory.get("Road Building"), 1);
        assertEquals(privateInventory.get("Year of Plenty"), 1);
        assertEquals(privateInventory.get("Victory Point Card"), 1);
        assertEquals(privateInventory.get("Cities"), 4);
        assertEquals(privateInventory.get("Roads"), 15);
        assertEquals(privateInventory.get("Settlements"), 5);
        assertEquals(privateInventory.get("Victory Points"), 1);

        publicInventory = inventory.getPublicView();
        assertEquals(publicInventory.get("Resource"), 5);
        assertEquals(publicInventory.get("Development Cards"), 5);
        assertEquals(publicInventory.get("Played Knights"), 0);
        assertEquals(publicInventory.get("Continuous Road"), 0);
        assertEquals(publicInventory.get("Largest Army"), 0);
        assertEquals(publicInventory.get("Longest Road"), 0);
        assertEquals(publicInventory.get("Public Victory Points"), 0);
    }

    /**
     * Incrementer with number Test
     * <p>
     * Increment the number of the cards than
     * increment they 5 times again and analyzes the HashMap
     *
     * @author Anton
     * @since 2021-03-08
     */
    @Test
    void onIncNumberWithNumber() {

        // Resource Cards inc
        inventory.lumber.incNumber();
        inventory.brick.incNumber();
        inventory.grain.incNumber();
        inventory.wool.incNumber();
        inventory.ore.incNumber();

        // Development Cards inc
        inventory.cardKnight.incNumber();
        inventory.cardMonopoly.incNumber();
        inventory.cardRoadBuilding.incNumber();
        inventory.cardYearOfPlenty.incNumber();

        // Resource Cards inc with number
        inventory.lumber.incNumber(5);
        inventory.brick.incNumber(5);
        inventory.grain.incNumber(5);
        inventory.wool.incNumber(5);
        inventory.ore.incNumber(5);

        // Development Cards inc with number
        inventory.cardKnight.incNumber(5);
        inventory.cardMonopoly.incNumber(5);
        inventory.cardRoadBuilding.incNumber(5);
        inventory.cardYearOfPlenty.incNumber(5);

        // Inventory analysis
        privateInventory = inventory.getPrivateView();
        assertEquals(privateInventory.get("Lumber"), 6);
        assertEquals(privateInventory.get("Brick"), 6);
        assertEquals(privateInventory.get("Grain"), 6);
        assertEquals(privateInventory.get("Wool"), 6);
        assertEquals(privateInventory.get("Ore"), 6);
        assertEquals(privateInventory.get("Knight"), 6);
        assertEquals(privateInventory.get("Monopoly"), 6);
        assertEquals(privateInventory.get("Road Building"), 6);
        assertEquals(privateInventory.get("Year of Plenty"), 6);
        assertEquals(privateInventory.get("Victory Point Card"), 0);
        assertEquals(privateInventory.get("Cities"), 4);
        assertEquals(privateInventory.get("Roads"), 15);
        assertEquals(privateInventory.get("Settlements"), 5);
        assertEquals(privateInventory.get("Victory Points"), 0);

        publicInventory = inventory.getPublicView();
        assertEquals(publicInventory.get("Resource"), 30);
        assertEquals(publicInventory.get("Development Cards"), 24);
        assertEquals(publicInventory.get("Played Knights"), 0);
        assertEquals(publicInventory.get("Continuous Road"), 0);
        assertEquals(publicInventory.get("Largest Army"), 0);
        assertEquals(publicInventory.get("Longest Road"), 0);
        assertEquals(publicInventory.get("Public Victory Points"), 0);
    }

    /**
     * Decrementer Test
     * <p>
     * Increment the number of the cards than
     * decrement they and the Building Units and analyzes the HashMap
     *
     * @author Anton
     * @since 2021-03-08
     */
    @Test
    void onDecNumber() {

        // Resource Cards inc
        inventory.lumber.incNumber();
        inventory.brick.incNumber();
        inventory.grain.incNumber();
        inventory.wool.incNumber();
        inventory.ore.incNumber();

        // Development Cards inc
        inventory.cardKnight.incNumber();
        inventory.cardMonopoly.incNumber();
        inventory.cardRoadBuilding.incNumber();
        inventory.cardYearOfPlenty.incNumber();

        // Resource Cards dec
        inventory.lumber.decNumber();
        inventory.brick.decNumber();
        inventory.grain.decNumber();
        inventory.wool.decNumber();
        inventory.ore.decNumber();

        // Development Cards dec
        inventory.cardKnight.decNumber();
        inventory.cardMonopoly.decNumber();
        inventory.cardRoadBuilding.decNumber();
        inventory.cardYearOfPlenty.decNumber();

        // Building Units dec
        inventory.city.decNumber();
        inventory.road.decNumber();
        inventory.settlement.decNumber();

        // Inventory analysis
        privateInventory = inventory.getPrivateView();
        assertEquals(privateInventory.get("Lumber"), 0);
        assertEquals(privateInventory.get("Brick"), 0);
        assertEquals(privateInventory.get("Grain"), 0);
        assertEquals(privateInventory.get("Wool"), 0);
        assertEquals(privateInventory.get("Ore"), 0);
        assertEquals(privateInventory.get("Knight"), 0);
        assertEquals(privateInventory.get("Monopoly"), 0);
        assertEquals(privateInventory.get("Road Building"), 0);
        assertEquals(privateInventory.get("Year of Plenty"), 0);
        assertEquals(privateInventory.get("Victory Point Card"), 0);
        assertEquals(privateInventory.get("Cities"), 3);
        assertEquals(privateInventory.get("Roads"), 14);
        assertEquals(privateInventory.get("Settlements"), 4);
        assertEquals(privateInventory.get("Victory Points"), 0);

        publicInventory = inventory.getPublicView();
        assertEquals(publicInventory.get("Resource"), 0);
        assertEquals(publicInventory.get("Development Cards"), 0);
        assertEquals(publicInventory.get("Played Knights"), 0);
        assertEquals(publicInventory.get("Continuous Road"), 0);
        assertEquals(publicInventory.get("Largest Army"), 0);
        assertEquals(publicInventory.get("Longest Road"), 0);
        assertEquals(publicInventory.get("Public Victory Points"), 0);
    }

    /**
     * Decrementer with number Test
     * <p>
     * Increment the number of the cards 5 times than
     * decrement they 3 times and analyzes the HashMap
     *
     * @author Anton
     * @since 2021-03-08
     */
    @Test
    void onDecNumberWithNumber() {

        // Resource Cards inc
        inventory.lumber.incNumber(5);
        inventory.brick.incNumber(5);
        inventory.grain.incNumber(5);
        inventory.wool.incNumber(5);
        inventory.ore.incNumber(5);

        // Development Cards inc
        inventory.cardKnight.incNumber(5);
        inventory.cardMonopoly.incNumber(5);
        inventory.cardRoadBuilding.incNumber(5);
        inventory.cardYearOfPlenty.incNumber(5);

        // Resource Cards dec
        inventory.lumber.decNumber(3);
        inventory.brick.decNumber(3);
        inventory.grain.decNumber(3);
        inventory.wool.decNumber(3);
        inventory.ore.decNumber(3);

        // Development Cards dec
        inventory.cardKnight.decNumber(3);
        inventory.cardMonopoly.decNumber(3);
        inventory.cardRoadBuilding.decNumber(3);
        inventory.cardYearOfPlenty.decNumber(3);

        // Inventory analysis
        privateInventory = inventory.getPrivateView();
        assertEquals(privateInventory.get("Lumber"), 2);
        assertEquals(privateInventory.get("Brick"), 2);
        assertEquals(privateInventory.get("Grain"), 2);
        assertEquals(privateInventory.get("Wool"), 2);
        assertEquals(privateInventory.get("Ore"), 2);
        assertEquals(privateInventory.get("Knight"), 2);
        assertEquals(privateInventory.get("Monopoly"), 2);
        assertEquals(privateInventory.get("Road Building"), 2);
        assertEquals(privateInventory.get("Year of Plenty"), 2);
        assertEquals(privateInventory.get("Victory Point Card"), 0);
        assertEquals(privateInventory.get("Cities"), 4);
        assertEquals(privateInventory.get("Roads"), 15);
        assertEquals(privateInventory.get("Settlements"), 5);
        assertEquals(privateInventory.get("Victory Points"), 0);

        publicInventory = inventory.getPublicView();
        assertEquals(publicInventory.get("Resource"), 10);
        assertEquals(publicInventory.get("Development Cards"), 8);
        assertEquals(publicInventory.get("Played Knights"), 0);
        assertEquals(publicInventory.get("Continuous Road"), 0);
        assertEquals(publicInventory.get("Largest Army"), 0);
        assertEquals(publicInventory.get("Longest Road"), 0);
        assertEquals(publicInventory.get("Public Victory Points"), 0);
    }

    @Test
    public void incCardTest(){
        Inventory inventory = new Inventory(user);

        inventory.incCardStack("Lumber", 10);
        inventory.incCardStack("Brick", 10);
        inventory.incCardStack("Grain", 10);
        inventory.incCardStack("Wool", 10);
        inventory.incCardStack("Ore", 10);
        inventory.incCardStack("Knight", 10);
        inventory.incCardStack("Monopoly", 10);
        inventory.incCardStack("Road Building", 10);
        inventory.incCardStack("Year of Plenty", 10);
        inventory.incCardStack("Victory Point Card", 1);
        inventory.incCardStack("Test", 1);

        assertEquals(10, inventory.lumber.getNumber());
        assertEquals(10, inventory.brick.getNumber());
        assertEquals(10, inventory.grain.getNumber());
        assertEquals(10, inventory.wool.getNumber());
        assertEquals(10, inventory.ore.getNumber());
        assertEquals(10, inventory.cardKnight.getNumber());
        assertEquals(10, inventory.cardMonopoly.getNumber());
        assertEquals(10, inventory.cardRoadBuilding.getNumber());
        assertEquals(10, inventory.cardYearOfPlenty.getNumber());
        assertEquals(1, inventory.getVictoryPoints());
    }

    @Test
    public void decCardTest(){
        Inventory inventory = new Inventory(user);

        inventory.incCardStack("Lumber", 10);
        inventory.incCardStack("Brick", 10);
        inventory.incCardStack("Grain", 10);
        inventory.incCardStack("Wool", 10);
        inventory.incCardStack("Ore", 10);
        inventory.incCardStack("Knight", 10);
        inventory.incCardStack("Monopoly", 10);
        inventory.incCardStack("Road Building", 10);
        inventory.incCardStack("Year of Plenty", 10);
        inventory.incCardStack("Victory Point Card", 1);

        inventory.decCardStack("Lumber", 2);
        inventory.decCardStack("Brick", 2);
        inventory.decCardStack("Grain", 2);
        inventory.decCardStack("Wool", 2);
        inventory.decCardStack("Ore", 2);
        inventory.decCardStack("Knight", 2);
        inventory.decCardStack("Monopoly", 2);
        inventory.decCardStack("Road Building", 2);
        inventory.decCardStack("Year of Plenty", 2);
        inventory.decCardStack("Victory Point Card", 1);
        inventory.decCardStack("Test", 1);

        assertEquals(8, inventory.lumber.getNumber());
        assertEquals(8, inventory.brick.getNumber());
        assertEquals(8, inventory.grain.getNumber());
        assertEquals(8, inventory.wool.getNumber());
        assertEquals(8, inventory.ore.getNumber());
    }
}