package de.uol.swp.common.game;

import java.io.Serializable;

public class Gamefield implements Serializable {

private TerrainFieldContainer[] tFCs = new TerrainFieldContainer[37];

public Gamefield(){}

public Gamefield(int variant) {
    if (variant == 0) {
        TerrainFieldContainer[] tempArray = new TerrainFieldContainer[37];
        //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;

        //beginning of oceans
        for (int i = 0; i <= 17; i++) {
            tempArray[i] = new TerrainFieldContainer(0,0);
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
    }

    else if (variant > 0) {
        //TODO implementiere andere Spielfelder
    }

}

    public TerrainFieldContainer[] gettFCs() {
        return tFCs;
    }

}
