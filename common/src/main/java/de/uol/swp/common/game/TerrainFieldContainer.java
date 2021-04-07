package de.uol.swp.common.game;

import java.io.Serializable;

/**
 * Class used to store information about the TerrainFields of the GameField
 *
 * @author Pieter Vogt, Marc Hermes
 * @since 2021-03-13
 */
public class TerrainFieldContainer implements Serializable {

    public int fieldType;
    public int diceTokens;
    public BuildingSpot[] buildingSpots = new BuildingSpot[12];
    public boolean occupiedByRobber;

    /**
     * Constructor
     *
     * @param fieldType  the int value representing a certain fieldType
     * @param diceTokens the int value representing the amount of diceTokens on this TerrainField
     * @author Pieter Vogt, Marc Hermes
     * @since 2021-03-13
     * <p>
     * Enhanced by Marius Birk
     * @since 2021-04-07
     * <p>
     * Added the occupied by Robber attribute to check if the field is occupied by the robber.
     */
    public TerrainFieldContainer(int fieldType, int diceTokens) {
        this.fieldType = fieldType;
        this.diceTokens = diceTokens;
        if (this.fieldType == 6) {
            this.occupiedByRobber = true;
        } else {
            this.occupiedByRobber = false;
        }
    }

    /**
     * Getter for the fieldType variable
     *
     * @return int value representation of the fieldType
     * @author Pieter Vogt, Marc Hermes
     * @since 2021-03-13
     */
    public int getFieldType() {
        return fieldType;
    }

    /**
     * Setter for the fieldType variable
     *
     * @param fieldType value of the new fieldType
     * @author Pieter Vogt, Marc Hermes
     * @since 2021-03-13
     */
    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Getter for the diceTokens variable
     *
     * @return int value of the amount of diceTokens
     * @author Pieter Vogt, Marc Hermes
     * @since 2021-03-13
     */
    public int getDiceTokens() {
        return diceTokens;
    }

    /**
     * Setter for the diceTokens variable
     *
     * @param diceTokens value of the new diceTokens
     * @author Pieter Vogt, Marc Hermes
     * @since 2021-03-13
     */
    public void setDiceTokens(int diceTokens) {
        this.diceTokens = diceTokens;
    }

    /**
     * Getter for the occupiedByRobber variable
     *
     * @return boolean value of the occupiedByRobber attribute
     * @author Marius Birk
     * @since 2021-04-07
     */
    public boolean isOccupiedByRobber() {
        return occupiedByRobber;
    }

    /**
     * Setter for the occupiedByRobber variable
     *
     * @param occupiedByRobber
     * @author Marius Birk
     * @since 2021-04-07
     */
    public void setOccupiedByRobber(boolean occupiedByRobber) {
        this.occupiedByRobber = occupiedByRobber;
    }

    /**
     * Getter for the buildingSpots variable
     *
     * @return the buildingSpots Array
     * @author Marius Birk
     * @since 2021-04-07
     */
    public BuildingSpot[] getBuildingSpots() {
        return buildingSpots;
    }

    /**
     * Setter for the buildingSpots variable
     *
     * @param buildingSpots
     * @author Marius Birk
     * @since 2021-04-07
     */
    public void setBuildingSpots(BuildingSpot[] buildingSpots) {
        this.buildingSpots = buildingSpots;
    }
}
