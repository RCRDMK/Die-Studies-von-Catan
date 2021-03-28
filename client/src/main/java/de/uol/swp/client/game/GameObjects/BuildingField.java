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
public class BuildingField {

    //attributes
    private String name; //Name of the terrain-type this card represents.
    private Vector position; //the absolute postion in x and y on the playfield.
    private boolean used; //this is a relative vector that determines the direction to the card wich was placed before this one.


    /**
     * Constructor
     *
     * @author Pieter Vogt
     * @param position
     * @since 2021-01-24
     */
    public BuildingField(Vector position) {
        this.position = position;
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
    public boolean isUsed() {
        return used;
    }

    /**
     * Setter for the placementVector variable
     *
     * @author Pieter Vogt
     * @return Vector containing the (relative position) placementVector of the TerrainField
     * @since 2021-01-24
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

}
