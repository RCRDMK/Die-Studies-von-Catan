package de.uol.swp.common.game;


import org.checkerframework.checker.units.qual.A;

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
    private final ArrayList<ArrayList<ArrayList<UUID>>> pathArrayList = new ArrayList<>();

    // Constructor which creates a new ArrayList of the adjacency matrices for each player in the first dimension.
    public LongestStreetPathCalculator(HashSet<MapGraph.StreetNode> streetNodeHashSet) {
        this.streetNodeHashSet = streetNodeHashSet;
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        adjacencyMatrixForAllPlayers.add(new ArrayList<>());
        pathArrayList.add(new ArrayList<>());
        pathArrayList.add(new ArrayList<>());
        pathArrayList.add(new ArrayList<>());
        pathArrayList.add(new ArrayList<>());
    }

    // Called when a new street is placed on the map graph
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
        playerMatrix.get(playerMatrix.size() - 1).add(new ConnectionClassIntegerWithUUID(streetUUID, streetUUID, 0));

        reCalculatePaths(playerIndex);
    }

    public void updateMatrixWithNewBuilding(MapGraph.BuildingNode buildingNode, int playerIndex) {
        HashSet<MapGraph.StreetNode> streetsOfBuilding = buildingNode.getConnectedStreetNodes();
        ArrayList<UUID>[] playerArray = new ArrayList[4];
        playerArray[0] = new ArrayList<>();
        playerArray[1] = new ArrayList<>();
        playerArray[2] = new ArrayList<>();
        playerArray[3] = new ArrayList<>();
        for (MapGraph.StreetNode streetNode : streetsOfBuilding) {
            if (streetNode.getOccupiedByPlayer() != 666) {
                playerArray[streetNode.getOccupiedByPlayer()].add(streetNode.getUuid());
            }
        }
        for (int p = 0; p < playerArray.length; p++) {

            if (playerArray[p].size() >= 2 && p != playerIndex) {
                ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers.get(p);

                for (int i = 0; i < playerMatrix.size(); i++) {
                    for (int j = 0; j < playerMatrix.get(i).size(); j++) {
                        UUID rowUUID = playerMatrix.get(i).get(j).getUuidForRow();
                        UUID columnUUID = playerMatrix.get(i).get(j).getUuidForColumn();
                        if (playerArray[p].contains(rowUUID) && playerArray[p].contains(columnUUID)) {
                            reCalculatePathsAtNewBuilding(p, rowUUID, columnUUID);
                            playerMatrix.get(i).get(j).setInteger(0);
                            playerMatrix.get(j).get(i).setInteger(0);
                        }

                    }

                }
                printAdjacencyMatrix(p);
                System.out.println("Amount of paths found " + pathArrayList.get(p).size());
                System.out.println("Longest path " + getLongestPath(p));
            }

        }
    }

    public void reCalculatePathsAtNewBuilding(int playerIndex, UUID street1, UUID street2){
        ArrayList<ArrayList<UUID>> pathListForPlayer = pathArrayList.get(playerIndex);
        int size = pathListForPlayer.size();
        for( int i = 0; i < size; i++) {
            ArrayList<UUID> path = pathListForPlayer.get(i);
            if (Math.abs(path.indexOf(street1) - path.indexOf(street2)) == 1 && path.contains(street1) && path.contains(street2)) {
                int index = Math.min(path.indexOf(street1), path.indexOf(street2));
                ArrayList<UUID> newPath = new ArrayList<>();
                while (path.size()-1 > index) {
                    newPath.add(path.remove(index+1));
                }
                pathListForPlayer.add(newPath);
            }
        }

    }

    public void reCalculatePaths(int playerIndex) {
        ConnectionClassIntegerWithUUID[][] matrix = toArrayMatrix(playerIndex);
        ArrayList<UUID> neighborStreets = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            if(matrix[matrix.length-1][i].getInteger()==1) {
                neighborStreets.add(matrix[matrix.length-1][i].getUuidForColumn());
            }
        }
        if(neighborStreets.size()>1) {
            calculateAllPathsFromStart(matrix, playerIndex);
        }
        else {
            calculateAllPathsStartingAtNewStreet(matrix, playerIndex);
        }

    }

    // called when the street that was added is only connected to 1 other street
    public void calculateAllPathsStartingAtNewStreet(ConnectionClassIntegerWithUUID[][] matrixOfPlayer, int playerIndex) {
        ArrayList<UUID> path = new ArrayList<>();
        horizontal(path, matrixOfPlayer.length - 1, 666, matrixOfPlayer, playerIndex);
        System.out.println("Amount of paths found " + pathArrayList.get(playerIndex).size());
        System.out.println("Longest path " + getLongestPath(playerIndex));
    }

    // called when the paths from all streets are to be calculated
    public void calculateAllPathsFromStart(ConnectionClassIntegerWithUUID[][] matrixOfPlayer, int playerIndex) {
        pathArrayList.get(playerIndex).clear();
        for (int row = 0; row < matrixOfPlayer.length; row++) {
            ArrayList<UUID> path = new ArrayList<>();
            horizontal(path, row, 666, matrixOfPlayer, playerIndex);
        }
        System.out.println("Amount of paths found " + pathArrayList.get(playerIndex).size());
        System.out.println("Longest path " + getLongestPath(playerIndex));
    }

    public ArrayList<UUID> horizontal(ArrayList<UUID> walkedPath, int row, int lookUpColumn, ConnectionClassIntegerWithUUID[][] matrix, int playerIndex) {
        for (int j = 0; j < matrix.length; j++) {
            ConnectionClassIntegerWithUUID currentMatrixEntry = matrix[row][j];
            boolean cond = true;

            if (currentMatrixEntry.getInteger() == 1 && !walkedPath.contains(currentMatrixEntry.getUuidForColumn())) {
                if (lookUpColumn != 666) {
                    for (int y = 0; y < matrix.length; y++) {
                        if (matrix[y][lookUpColumn].getUuidForRow().equals(currentMatrixEntry.getUuidForColumn()) && matrix[y][lookUpColumn].getInteger() == 1) {
                            cond = false;
                            break;
                        }
                    }
                }
                if (cond) {
                    if (!walkedPath.contains(currentMatrixEntry.getUuidForRow())) {
                        walkedPath.add(currentMatrixEntry.getUuidForRow());
                    }
                    walkedPath.add(currentMatrixEntry.getUuidForColumn());
                    ArrayList<UUID> result = new ArrayList<>(vertical(walkedPath, j, row, matrix, playerIndex));
                    pathArrayList.get(playerIndex).add(result);
                    walkedPath.remove(walkedPath.get(walkedPath.size() - 1));
                }
            }
        }
        return walkedPath;
    }

    public ArrayList<UUID> vertical(ArrayList<UUID> walkedPath, int column, int lookUpRow, ConnectionClassIntegerWithUUID[][] matrix, int playerIndex) {
        for (int i = 0; i < matrix.length; i++) {
            ConnectionClassIntegerWithUUID currentMatrixEntry = matrix[i][column];
            boolean cond = true;

            if (currentMatrixEntry.getInteger() == 1 && !walkedPath.contains(currentMatrixEntry.getUuidForRow())) {
                for (int x = 0; x < matrix.length; x++) {
                    if (matrix[lookUpRow][x].getUuidForColumn().equals(currentMatrixEntry.getUuidForRow()) && matrix[lookUpRow][x].getInteger() == 1) {
                        cond = false;
                        break;
                    }
                }
                if (cond) {
                    if (!walkedPath.contains(currentMatrixEntry.getUuidForColumn())) {
                        walkedPath.add(currentMatrixEntry.getUuidForColumn());
                    }
                    walkedPath.add(currentMatrixEntry.getUuidForRow());
                    ArrayList<UUID> result = new ArrayList<>(horizontal(walkedPath, i, column, matrix, playerIndex));
                    pathArrayList.get(playerIndex).add(result);
                    walkedPath.remove(walkedPath.get(walkedPath.size() - 1));
                }
            }
        }
        return walkedPath;
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

    public ConnectionClassIntegerWithUUID[][] toArrayMatrix(int playerIndex) {
        ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers.get(playerIndex);
        int size = playerMatrix.size();
        ConnectionClassIntegerWithUUID[][] matrix = new ConnectionClassIntegerWithUUID[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = playerMatrix.get(i).get(j);
            }
        }
        return matrix;
    }

    public int getLongestPath(int playerIndex) {
        int size = 0;
        for (ArrayList<UUID> list : pathArrayList.get(playerIndex)) {
            if (size < list.size()) {
                size = list.size();
            }
            if (size == list.size()) {
                System.out.println(list);
            }
        }
        return size;
    }
}
