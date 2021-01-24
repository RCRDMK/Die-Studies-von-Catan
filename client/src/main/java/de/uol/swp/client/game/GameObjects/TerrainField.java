package de.uol.swp.client.game.GameObjects;

import de.uol.swp.client.game.HelperObjects.Vector;
import javafx.scene.paint.Color;

/**
 * This Class holds all information needed to handle the terrain-cards, their placement and their dice-value
 *
 * @author pieter vogt
 * @since 24-01-2021
 */
public class TerrainField {

    //fields
    private String name; //Name of the terrain-type this card represents.
    private int diceToken; //you need to roll this with the dice to generate the resources in this terrainfield.
    private Vector position; //the absolute postion in x and y on the playfield.
    private final Vector placementVector; //this is a relative vector that determines the direction to the card wich was placed before this one.

    //constructor

    public TerrainField(String name, int diceToken, Vector placementVector) {
        this.name = name;
        this.diceToken = diceToken;
        this.placementVector = placementVector;
    }

    //getter + setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiceToken() {
        return diceToken;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getPlacementVector() {
        return placementVector;
    }

    //methods

    /**
     * determines the color to draw its hostobject
     * @return the color for the placementobject
     * @author pieter vogt
     * @since 24-01-2021
     */
    public Color determineColorOfTerrain() {
        Color c;
        switch (this.name) {
            case "Forrest":
                c = Color.OLIVEDRAB;
                break;
            case "Farmland":
                c = Color.WHEAT;
                break;
            case "Grassland":
                c = Color.LAWNGREEN;
                break;
            case "Hillside":
                c = Color.LIGHTCORAL;
                break;
            case "Mountain":
                c = Color.GREY;
                break;
            case "Ocean":
                c = Color.DODGERBLUE;
                break;
            default:
                c = Color.LIGHTYELLOW;
                break;
        }
        return c;
    }
}
