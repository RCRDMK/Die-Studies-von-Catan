package de.uol.swp.common.game;

import java.io.Serializable;
import java.util.LinkedList;
//TODO: Diese Klasse ist obsolet! Es müssen alle Uses überarbeitet werden, sodass diese Klasse schnellstmöglich wieder entfernt werden kann!

/**
 * The GameField class used to store information about the GameField
 *
 * @author Pieter Vogt, Marc Hermes
 * @see de.uol.swp.common.game.TerrainFieldContainer
 * @since 2021-03-13
 */
public class GameField implements Serializable {

    private TerrainFieldContainer[] tFCs;

    /**
     * The constructor of the class
     * <p>
     * Fills the tFCs array with the TerrainFieldContainers. The TerrainFieldContainers contain the information for the
     * standard playing field.
     *
     * @author Pieter Vogt, Marc Hermes
     * @see de.uol.swp.common.game.TerrainFieldContainer
     * @since 2021-03-13
     */
    public GameField(String gameFieldVariant) {
        TerrainFieldContainer[] tempArray = new TerrainFieldContainer[37];

        if (gameFieldVariant.equals("Standard")) {

            //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;

            //beginning of oceans
            for (int i = 0; i <= 17; i++) {
                tempArray[i] = new TerrainFieldContainer(0, 0);
            }

            //beginning of landmasses
            tempArray[18] = new TerrainFieldContainer(1, 5);
            tempArray[19] = new TerrainFieldContainer(2, 2);
            tempArray[20] = new TerrainFieldContainer(1, 6);
            tempArray[21] = new TerrainFieldContainer(3, 3);
            tempArray[22] = new TerrainFieldContainer(3, 8);
            tempArray[23] = new TerrainFieldContainer(1, 10);
            tempArray[24] = new TerrainFieldContainer(2, 9);
            tempArray[25] = new TerrainFieldContainer(3, 12);
            tempArray[26] = new TerrainFieldContainer(4, 11);
            tempArray[27] = new TerrainFieldContainer(3, 4);
            tempArray[28] = new TerrainFieldContainer(4, 8);
            tempArray[29] = new TerrainFieldContainer(2, 10);
            tempArray[30] = new TerrainFieldContainer(4, 9);
            tempArray[31] = new TerrainFieldContainer(5, 4);
            tempArray[32] = new TerrainFieldContainer(2, 5);
            tempArray[33] = new TerrainFieldContainer(5, 6);
            tempArray[34] = new TerrainFieldContainer(1, 3);
            tempArray[35] = new TerrainFieldContainer(5, 3);
            tempArray[36] = new TerrainFieldContainer(6, 0);
            this.tFCs = tempArray;

        } else if (gameFieldVariant.equals("Random")){

            //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;

            //beginning of oceans
            for (int i = 0; i <= 17; i++) {
                tempArray[i] = new TerrainFieldContainer(0, 0);
            }

            //beginning of landmasses

            LinkedList<Integer> diceTokenList = new LinkedList<>();
            diceTokenList.add(5);
            diceTokenList.add(2);
            diceTokenList.add(6);
            diceTokenList.add(3);
            diceTokenList.add(8);
            diceTokenList.add(10);
            diceTokenList.add(9);
            diceTokenList.add(12);
            diceTokenList.add(11);
            diceTokenList.add(4);
            diceTokenList.add(8);
            diceTokenList.add(10);
            diceTokenList.add(9);
            diceTokenList.add(4);
            diceTokenList.add(5);
            diceTokenList.add(6);
            diceTokenList.add(3);
            diceTokenList.add(3);


            LinkedList<Integer> terrainType = new LinkedList<>();
            terrainType.add(1);
            terrainType.add(2);
            terrainType.add(1);
            terrainType.add(3);
            terrainType.add(3);
            terrainType.add(1);
            terrainType.add(2);
            terrainType.add(3);
            terrainType.add(4);
            terrainType.add(3);
            terrainType.add(4);
            terrainType.add(2);
            terrainType.add(4);
            terrainType.add(5);
            terrainType.add(2);
            terrainType.add(5);
            terrainType.add(1);
            terrainType.add(5);


            for (int i = 0; i < 18; i++) {
                int rand1 = randomInt(0, 17 - i);
                int rand2 = randomInt(0, 17 - i);
                tempArray[i+18] = new TerrainFieldContainer(terrainType.get(rand1), diceTokenList.get(rand2));
                terrainType.remove(rand1);
                diceTokenList.remove(rand2);
            }
            tempArray[36] = new TerrainFieldContainer(6, 0);
            tFCs = tempArray;

        }
    }

    /**
     * Getter tFCs variable
     *
     * @return the TerrainFieldContainer array stored in this GameField
     * @author Pieter Vogt, Marc Hermes
     * @since 2021-03-13
     */
    public TerrainFieldContainer[] getTFCs() {
        return tFCs;
    }

    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }


}
