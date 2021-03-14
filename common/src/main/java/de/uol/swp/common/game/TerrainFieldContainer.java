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

    /**
     * Constructor
     *
     * @author Pieter Vogt, Marc Hermes
     * @param fieldType the int value representing a certain fieldType
     * @param diceTokens the int value representing the amount of diceTokens on this TerrainField
     * @since 2021-03-13
     */
    public TerrainFieldContainer(int fieldType, int diceTokens) {
        this.fieldType = fieldType;
        this.diceTokens = diceTokens;
    }

    /**
     * Getter for the fieldType variable
     *
     * @author Pieter Vogt, Marc Hermes
     * @return int value representation of the fieldType
     * @since 2021-03-13
     */
    public int getFieldType() {
        return fieldType;
    }

    /**
     * Setter for the fieldType variable
     *
     * @author Pieter Vogt, Marc Hermes
     * @param fieldType value of the new fieldType
     * @since 2021-03-13
     */
    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Getter for the diceTokens variable
     *
     * @author Pieter Vogt, Marc Hermes
     * @return int value of the amount of diceTokens
     * @since 2021-03-13
     */
    public int getDiceTokens() {
        return diceTokens;
    }

    /**
     * Setter for the diceTokens variable
     *
     * @author Pieter Vogt, Marc Hermes
     * @param diceTokens value of the new diceTokens
     * @since 2021-03-13
     */
    public void setDiceTokens(int diceTokens) {
        this.diceTokens = diceTokens;
    }
}
