package de.uol.swp.common.game;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * Class that manages matrices of players and calculates street paths for each player.
 * <p>
 *
 * @author Marc Hermes, Kirstin Beyer
 * @since 2021-04-23
 */

public class LongestStreetPathCalculator implements Serializable {
    private final HashSet<MapGraph.StreetNode> streetNodeHashSet;
    // first dimension referencing the player
    // second dimension referencing the columns
    // third dimension referencing the rows, ultimately containing the UUIDs of the corresponding street nodes and the integer value indicating connectedness
    private final ArrayList<ArrayList<ArrayList<ConnectionClassIntegerWithUUID>>> adjacencyMatrixForAllPlayers = new ArrayList<>();
    // ArrayList containing all possible street paths for each player
    // first dimension referencing players
    // second dimension referencing street paths
    // third dimension referencing street UUIDs that make up the path
    private final ArrayList<ArrayList<ArrayList<UUID>>> pathArrayList = new ArrayList<>();

    /**
     * Constructor
     * <p>
     * Creates a new ArrayList of the adjacency matrices in the first dimension and a pathArrayList for each player.
     *
     * @param streetNodeHashSet HashSet containing all street nodes of the MapGraph
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
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

    /**
     * Method called when a new street is placed on the MapGraph
     * <p>
     * Creates a new row and a new column in the players matrix for the new street with a 1 if the streets are connected and a 0 if they are not connected.
     * Calls method recalculatePaths to recalculate all paths of this player at the end.
     *
     * @param streetUUID  UUID of the new street
     * @param playerIndex index of the corresponding player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public void updateMatrixWithNewStreet(UUID streetUUID, int playerIndex) {
        // get the corresponding players matrix
        ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers
                .get(playerIndex);
        // create new ROW for the new street
        playerMatrix.add(new ArrayList<>());

        // iterates over the COLUMNS of the player matrix
        for (int i = 0; i < playerMatrix.size() - 1; i++) {
            // get the COLUMN ID -> UUID of the STREET of this COLUMN
            UUID uuidColumn = playerMatrix.get(i).get(0).getUuidForRow();
            // add new ROW to this COLUMN, the connection class indicates the coordinates of the connection. STREET UUID -> current COLUMN, UUID COLUMN -> current ROW
            playerMatrix.get(playerMatrix.size() - 1)
                    .add(new ConnectionClassIntegerWithUUID(streetUUID, uuidColumn, 0));
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
                if (newNeighborStreetNodes.contains(correctOldStreetNode) && (buildingNode
                        .getOccupiedByPlayer() == playerIndex || buildingNode.getOccupiedByPlayer() == 666)) {
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

    /**
     * Method called when a new building is placed on the MapGraph
     * <p>
     * Checks if a player (not the player who built the building) has two or more streets connected to this building.
     * Updates players matrix accordingly and replaces 1 with 0, then calls method recalculatePathAtNewBuilding to recalculate paths for this player.
     *
     * @param buildingNode building node that was placed on the MapGraph
     * @param playerIndex index of the corresponding player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
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
            }
        }
    }

    /**
     * Method to recalculate paths when a new Building was placed on the MapGraph
     * <p>
     * Finds all paths in the ArrayList from the affected player, that are divided by the new building and splits these
     * paths between street1 and street2. The second part containing street2 is added to the ArrayList.
     *
     * @param playerIndex indicates the player who's path lengths are affected by the new building
     * @param street1 first street from player with playerIndex that is connected to the new building
     * @param street2 second street from player with playerIndex that is connected to the new building
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public void reCalculatePathsAtNewBuilding(int playerIndex, UUID street1, UUID street2) {
        ArrayList<ArrayList<UUID>> pathListForPlayer = pathArrayList.get(playerIndex);
        int size = pathListForPlayer.size();
        for (int i = 0; i < size; i++) {
            ArrayList<UUID> path = pathListForPlayer.get(i);
            if (Math.abs(path.indexOf(street1) - path.indexOf(street2)) == 1 && path.contains(street1) && path
                    .contains(street2)) {
                int index = Math.min(path.indexOf(street1), path.indexOf(street2));
                ArrayList<UUID> newPath = new ArrayList<>();
                while (path.size() - 1 > index) {
                    newPath.add(path.remove(index + 1));
                }
                pathListForPlayer.add(newPath);
            }
        }

    }

    /**
     * Method to recalculate all paths for one player
     * <p>
     * Calculates the number of the entries "1" in the last row of the matrix. When it contains only one "1" the method
     * calculateAllPathsStartingAtNewStreet is called, otherwise the method calculateAllPathsFromStart.
     *
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public void reCalculatePaths(int playerIndex) {
        ConnectionClassIntegerWithUUID[][] matrix = toArrayMatrix(playerIndex);
        ArrayList<UUID> neighborStreets = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[matrix.length - 1][i].getInteger() == 1) {
                neighborStreets.add(matrix[matrix.length - 1][i].getUuidForColumn());
            }
        }
        if (neighborStreets.size() > 1) {
            calculateAllPathsFromStart(matrix, playerIndex);
        } else {
            calculateAllPathsStartingAtNewStreet(matrix, playerIndex);
        }

    }

    /**
     * Method to calculate the paths when the new street is only connected to one street
     * <p>
     * Called when the street that was added is only connected to one other street. Calculates only the new paths by
     * calling the method horizontal in the last row of the matrix.
     *
     * @param matrixOfPlayer adjacency matrix containing the street connections for the player
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public void calculateAllPathsStartingAtNewStreet(ConnectionClassIntegerWithUUID[][] matrixOfPlayer,
                                                     int playerIndex) {
        ArrayList<UUID> path = new ArrayList<>();
        horizontal(path, matrixOfPlayer.length - 1, 666, matrixOfPlayer, playerIndex);
    }

    /**
     * Method to calculate all paths from one player from start
     * <p>
     * Called when all paths from the player have to be calculated. Calls the method horizontal in every row of the
     * matrix.
     *
     * @param matrixOfPlayer adjacency matrix containing the street connections for the player
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public void calculateAllPathsFromStart(ConnectionClassIntegerWithUUID[][] matrixOfPlayer, int playerIndex) {
        pathArrayList.get(playerIndex).clear();
        for (int row = 0; row < matrixOfPlayer.length; row++) {
            ArrayList<UUID> path = new ArrayList<>();
            horizontal(path, row, 666, matrixOfPlayer, playerIndex);
        }
    }

    /**
     * Method to go horizontal in the adjacency matrix
     * <p>
     * Goes through all row entries "1" for which the corresponding street in the column is not contained in the walked
     * path yet.
     * Then adds UUID of the corresponding street to the walked path and calls the method vertical in the column j and
     * the considered row.
     * Adds the walked path to the players path Array List and removes the last entry from walkedPath.
     * Returns the walked path.
     *
     * @param walkedPath   ArrayList that contains the UUIDs of the previous streets
     * @param row          corresponding row of the matrix
     * @param lookUpColumn previous considered column of the matrix
     * @param matrix adjacency matrix containing the street connections for the player
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public ArrayList<UUID> horizontal(ArrayList<UUID> walkedPath, int row, int lookUpColumn,
                                      ConnectionClassIntegerWithUUID[][] matrix, int playerIndex) {
        for (int j = 0; j < matrix.length; j++) {
            ConnectionClassIntegerWithUUID currentMatrixEntry = matrix[row][j];
            boolean cond = true;

            if (currentMatrixEntry.getInteger() == 1 && !walkedPath.contains(currentMatrixEntry.getUuidForColumn())) {
                if (lookUpColumn != 666) {
                    for (int y = 0; y < matrix.length; y++) {
                        if (matrix[y][lookUpColumn].getUuidForRow()
                                .equals(currentMatrixEntry.getUuidForColumn()) && matrix[y][lookUpColumn]
                                .getInteger() == 1) {
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

    /**
     * Method to go vertical in the adjacency matrix
     * <p>
     * Goes through all column entries "1" for which the corresponding street in the row is not contained in the walked
     * path yet.
     * Then adds UUID of the corresponding street to the walked path and calls the method horizontal in the row i and
     * the considered column.
     * Adds the walked path to the players path Array List and removes the last entry from walkedPath.
     * Returns the walked path.
     *
     * @param walkedPath  ArrayList that contains the UUIDs of the previous streets
     * @param column      corresponding column of the matrix
     * @param lookUpRow   previous considered row of the matrix
     * @param matrix      adjacency matrix containing the street connections for the player
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public ArrayList<UUID> vertical(ArrayList<UUID> walkedPath, int column, int lookUpRow,
                                    ConnectionClassIntegerWithUUID[][] matrix, int playerIndex) {
        for (int i = 0; i < matrix.length; i++) {
            ConnectionClassIntegerWithUUID currentMatrixEntry = matrix[i][column];
            boolean cond = true;

            if (currentMatrixEntry.getInteger() == 1 && !walkedPath.contains(currentMatrixEntry.getUuidForRow())) {
                for (int x = 0; x < matrix.length; x++) {
                    if (matrix[lookUpRow][x].getUuidForColumn()
                            .equals(currentMatrixEntry.getUuidForRow()) && matrix[lookUpRow][x].getInteger() == 1) {
                        cond = false;
                        break;
                    }
                }
                if (cond) {
                    walkedPath.add(currentMatrixEntry.getUuidForRow());
                    ArrayList<UUID> result = new ArrayList<>(horizontal(walkedPath, i, column, matrix, playerIndex));
                    pathArrayList.get(playerIndex).add(result);
                    walkedPath.remove(walkedPath.get(walkedPath.size() - 1));
                }
            }
        }
        return walkedPath;
    }

    /**
     * Method to print adjacency matrix for one player
     * <p>
     * Prints adjacency matrix of the corresponding player row by row.
     *
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public void printAdjacencyMatrix(int playerIndex) {
        ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers
                .get(playerIndex);
        for (int i = 0; i < playerMatrix.size(); i++) {
            for (int j = 0; j < playerMatrix.get(i).size(); j++) {
                System.out.print(playerMatrix.get(i).get(j).getInteger() + "   ");
            }
            System.out.println("\n");
        }
    }

    /**
     * Method to convert adjacency matrix from an ArrayList to an array
     * <p>
     * Converts adjacency matrix of the corresponding player from ArrayList tp array.
     *
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public ConnectionClassIntegerWithUUID[][] toArrayMatrix(int playerIndex) {
        ArrayList<ArrayList<ConnectionClassIntegerWithUUID>> playerMatrix = adjacencyMatrixForAllPlayers
                .get(playerIndex);
        int size = playerMatrix.size();
        ConnectionClassIntegerWithUUID[][] matrix = new ConnectionClassIntegerWithUUID[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = playerMatrix.get(i).get(j);
            }
        }
        return matrix;
    }

    /**
     * Method to return longest path from player
     * <p>
     * Gets longest path from the players path ArrayList and returns the size of this path.
     *
     * @param playerIndex indicates the player
     * @author Marc Hermes, Kirstin Beyer
     * @since 2021-04-23
     */
    public int getLongestPath(int playerIndex) {
        int size = 0;
        for (ArrayList<UUID> list : pathArrayList.get(playerIndex)) {
            if (size < list.size()) {
                size = list.size();
            }
        }
        return size;
    }
}
