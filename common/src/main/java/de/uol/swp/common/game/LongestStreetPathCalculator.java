package de.uol.swp.common.game;

import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class LongestStreetPathCalculator implements Serializable {
    private final HashSet<MapGraph.StreetNode> streetNodeHashSet;
    // first dimension referencing the player
    // second dimension referencing the columns
    // third dimension referencing the rows, ultimately containing the UUIDs of the corresponding street nodes and the integer value indicating connectedness
    private final ArrayList<ArrayList<ArrayList<ConnectionClassIntegerWithUUID>>> adjacencyMatrixForAllPlayers = new ArrayList<>();

    // Constructor which creates a new ArrayList of the adjacency matrices for each player in the first dimension.
    public LongestStreetPathCalculator(HashSet<MapGraph.StreetNode> streetNodeHashSet) {
        this.streetNodeHashSet = streetNodeHashSet;
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());

    }

    // Called when a new street is placed on the mapgraph
    public void updateMatrixWithNewStreet(UUID streetUUID, int playerIndex) {
        // get the corresponding players matrix
        ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers.get(playerIndex);
        // create new ROW for the new street
        playerMatrix.add(new ArrayList<>());

        // iterates over the COLUMNS of the player matrix
        for (int i = 0; i < playerMatrix.size() - 1; i++) {
            // get the COLUMN ID -> UUID of the STREET of this COLUMN
            UUID uuidColumn = playerMatrix.get(i).get(0).getUuidForRow();
            // add new ROW to this COLUMN, the connection class indicates the coordinates of the connection. STREETUUID -> current COLUMN, UUIDCOLUMN -> current ROW
            playerMatrix.get(playerMatrix.size() - 1).add(new ConnectionClassIntegerWithUUID(streetUUID, uuidColumn, 0));
            // do the same as above BUT MIRRORED (possible because of symmetry of the matrix)
            playerMatrix.get(i).add(new ConnectionClassIntegerWithUUID(uuidColumn, streetUUID, 0));
            MapGraph.StreetNode correctNewStreetNode = null;
            MapGraph.StreetNode correctOldStreetNode = null;
            // find the to the UUIDs corresponding streetNodes
            for (MapGraph.StreetNode streetNode : streetNodeHashSet) {
                if (streetNode.getUuid().equals(streetUUID)) {
                    correctNewStreetNode = streetNode;
                } else if (streetNode.getUuid().equals(uuidColumn)) {
                    correctOldStreetNode = streetNode;
                }
            }
            // Check if the two streetNodes are CONNECTED by iterating over the to the newStreetNode connected buildingNodes
            for (MapGraph.BuildingNode buildingNode : correctNewStreetNode.getConnectedBuildingNodes()) {
                HashSet<MapGraph.StreetNode> newNeighborStreetNodes = buildingNode.getConnectedStreetNodes();
                // Check if buildingNode is connected to the oldStreetNode (the streetNode representing the current COLUMN)
                // also check whether or not the buildingNode is owned by a different player, thus disconnecting the two streets
                if (newNeighborStreetNodes.contains(correctOldStreetNode) && (buildingNode.getOccupiedByPlayer() == playerIndex || buildingNode.getOccupiedByPlayer() == 666)) {
                    // if actually connected put a 1 in the corresponding field of the matrix
                    playerMatrix.get(playerMatrix.size() - 1).get(i).setInteger(1);
                    // as well as the mirrored position (because of symmetry)
                    playerMatrix.get(i).get(playerMatrix.size() - 1).setInteger(1);
                    break;
                }
            }
        }
        // this is the entry of the matrix where the streetNode references itself (the diagonal of the matrix) which is always set to 0.
        playerMatrix.get(playerMatrix.size()-1).add(new ConnectionClassIntegerWithUUID(streetUUID, streetUUID, 0));

    }

    public void printAdjacencyMatrix(int playerIndex) {
        ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers.get(playerIndex);
        for (int i = 0; i < playerMatrix.size(); i++) {
            for (int j = 0; j < playerMatrix.get(i).size(); j++) {
                System.out.print(playerMatrix.get(i).get(j).getInteger() + "   ");
            }
            System.out.println("\n");
        }
    }
}
