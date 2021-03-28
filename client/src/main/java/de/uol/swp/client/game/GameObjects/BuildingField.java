package de.uol.swp.client.game.GameObjects;

import de.uol.swp.client.game.HelperObjects.Vector;
import javafx.scene.paint.Color;

/**
 * This Class holds all information needed to handle the buildings, their placement and type
 * This class is used for the Client-side interpretation of the BuildingFields
 *
 * @author Kirstin Beyer
 * @since 2021-03-28
 */
public class BuildingField {

    //attributes
    private String name; //Name of the building-type this field represents.
    private Vector position; //the absolute position in x and y on the playfield.
    private boolean used; //boolean to describe whether the buildingField is already used or not


    /**
     * Constructor
     *
     * @author Kirstin
     * @param position
     * @since 2021-03-28
     */
    public BuildingField(Vector position) {
        this.position = position;
    }


    /**
     * Getter for the name variable
     *
     * @author Kirstin Beyer
     * @return String containing the name of the BuildingField
     * @since 2021-03-28
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name variable
     *
     * @author Kirstin Beyer
     * @param name String containing the new name of the BuildingField
     * @since 2021-03-28
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter for the position variable
     *
     * @author Kirstin Beyer
     * @return Vector containing the absolute position of the BuildingField
     * @since 2021-03-28
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Setter for the position variable
     *
     * @author Kirstin Beyer
     * @param position Vector containing the new absolute position
     * @since 2021-03-28
     */
    public void setPosition(Vector position) {
        this.position = position;
    }

    /**
     * Getter for the boolean used
     *
     * @author Kirstin Beyer
     * @return boolean to describe whether the buildingField is already used or not
     * @since 2021-03-28
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Setter for the boolean used
     *
     * @author Kirstin Beyer
     * @param used boolean to describe whether the buildingField is already used or not
     * @since 2021-03-28
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

}
