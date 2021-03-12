package de.uol.swp.common.game;

import java.io.Serializable;

public class TerrainFieldContainer implements Serializable {

    public int fieldType = 0;
    public int diceTokens = 0;
    public BuildingSpot[] buildingSpots = new BuildingSpot[12];

    public TerrainFieldContainer(int fieldType, int diceTokens) {
        this.fieldType = fieldType;
        this.diceTokens = diceTokens;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getDiceTokens() {
        return diceTokens;
    }

    public void setDiceTokens(int diceTokens) {
        this.diceTokens = diceTokens;
    }
}
