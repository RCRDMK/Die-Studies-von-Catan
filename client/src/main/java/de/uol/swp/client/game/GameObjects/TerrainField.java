package de.uol.swp.client.game.GameObjects;

import de.uol.swp.client.game.HelperObjects.Vector;
import javafx.scene.paint.Color;

/**
 * This Class holds all information needed to handle the terrain-cards, their placement and their dice-value
 * This class is used for the Client-side interpretation of the TerrainFields
 *
 * @author Pieter Vogt
 * @since 24-01-2021
 */
public class TerrainField {

    //attributes
    private String name; //Name of the terrain-type this card represents.
    private int diceToken; //you need to roll this with the dice to generate the resources in this terrainfield.
    private Vector position; //the absolute postion in x and y on the playfield.
    private final Vector placementVector; //this is a relative vector that determines the direction to the card wich was placed before this one.


    /**
     * Constructor
     *
     * @author Pieter Vogt
     * @param placementVector
     * @since 2021-01-24
     */
    public TerrainField(Vector placementVector) {
        this.placementVector = placementVector;
    }


    /**
     * Getter for the name variable
     *
     * @author Pieter Vogt
     * @return String containing the name of the TerrainField
     * @since 2021-01-24
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name variable
     *
     * @author Pieter Vogt
     * @param name String containing the new name of the TerrainField
     * @since 2021-01-24
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the diceToken variable
     *
     * @author Pieter Vogt
     * @return int containing the value of the diceTokens of the TerrainField
     * @since 2021-01-24
     */
    public int getDiceToken() {
        return diceToken;
    }

    /**
     * Setter for the diceTokens variable
     *
     * @author Pieter Vogt
     * @param diceToken int containing the value of the diceTokens of this Terrainfield
     * @since 2021-01-24
     */
    public void setDiceToken(int diceToken) {this.diceToken = diceToken;}

    /**
     * Getter for the position variable
     *
     * @author Pieter Vogt
     * @return Vector containing the absolute position of the TerrainField
     * @since 2021-01-24
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Setter for the position variable
     *
     * @author Pieter Vogt
     * @param position Vector containing the new absolute position
     * @since 2021-01-24
     */
    public void setPosition(Vector position) {
        this.position = position;
    }

    /**
     * Getter for the placementVector variable
     *
     * @author Pieter Vogt
     * @return Vector containing the (relative position) placementVector of the TerrainField
     * @since 2021-01-24
     */
    public Vector getPlacementVector() {
        return placementVector;
    }

    /**
     * determines the color to draw its host-object
     * @return the color for the host-object
     * @author pieter vogt
     * @since 24-01-2021
     */
    public Color determineColorOfTerrain() {
        Color c;
        switch (this.name) {
            case "Forest":
                c = Color.OLIVEDRAB;
                break;
            case "Farmland":
                c = Color.GOLDENROD;
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
                c = Color.BLANCHEDALMOND;
                break;
        }
        return c;
    }
}
