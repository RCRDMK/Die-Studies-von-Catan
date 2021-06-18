package de.uol.swp.common.game;

import de.uol.swp.common.game.exception.ListFullException;

import java.io.Serializable;
import java.util.*;

/**
 * Manages the logic behind the play field.
 * <p>This Class holds and processes the data about the play field. It can return the longest road, and
 * potentially the most settlements, the player with the most cities, overall number of buildings built, length of
 * combined roads and so on (especially interesting for end screen, maybe?).
 * </p>
 *
 * @author Pieter Vogt
 * @since 2021-04-02
 */
public class MapGraph implements Serializable {

    private final HashSet<StreetNode> streetNodeHashSet = new HashSet<>();
    private final HashSet<BuildingNode> buildingNodeHashSet = new HashSet<>();
    private final HashSet<Hexagon> hexagonHashSet = new HashSet<>();
    private final int[] numOfRoads = new int[]{0, 0, 0, 0};
    private final int[] numOfBuildings = new int[]{0, 0, 0, 0};
    private final ArrayList<BuildingNode> builtBuildings = new ArrayList<>();
    // middle hexagon for reference
    private final Hexagon middle = new Hexagon("middle");
    private final LongestStreetPathCalculator longestStreetPathCalculator;

    /**
     * Creates the interconnected Grid of StreetNodes and BuildingNodes.
     *
     * @author Pieter Vogt
     * @since 2021-04-02
     */
    public MapGraph(String mapTypeToGenerate) {
        initializeMapGraph(mapTypeToGenerate);
        this.longestStreetPathCalculator = new LongestStreetPathCalculator(streetNodeHashSet);
    }

    public HashSet<StreetNode> getStreetNodeHashSet() {
        return streetNodeHashSet;
    }

    public HashSet<BuildingNode> getBuildingNodeHashSet() {
        return buildingNodeHashSet;
    }

    public HashSet<Hexagon> getHexagonHashSet() {
        return hexagonHashSet;
    }

    public LongestStreetPathCalculator getLongestStreetPathCalculator() {
        return longestStreetPathCalculator;
    }

    public int[] getNumOfRoads() {
        return numOfRoads;
    }

    public int[] getNumOfBuildings() {
        return numOfBuildings;
    }

    /**
     * Initializes MapGraph
     * <p>Creates the Hexagons, BuildingNodes and StreetNodes, interconnects them and updates the Lists to store
     * them.</p>
     *
     * @param mapTypeToGenerate The standard-case is to generate a MapGraph for a standard-play field. So if you wish to
     *                          generate one, just parse "".
     * @author Pieter Vogt
     * @since 2021-04-10
     */
    private void initializeMapGraph(String mapTypeToGenerate) {
        switch (mapTypeToGenerate) {
            //                                      //
            //                                      //
            //Here is some space for future mapTypes//
            //                                      //
            //                                      //

            case "VeryRandom":
                generateRandomField();
                configureTerrainTypeAndDiceTokensForAllHexagonsRandomly();
                configureHarborsRandomly();
                break;


            case "Random":
                generateStandardField();
                configureTerrainTypeAndDiceTokensForAllHexagonsRandomly();
                configureHarborsStandard();
                break;

            default: {
                generateStandardField();
                //einfügen der dice token und terrain types und harbors
                configureTerrainTypeAndDiceTokensForAllHexagonsStandard();
                configureHarborsStandard();
            }
        }

    }

    /**
     * Generates the standard game field
     * <p>
     * When this function is called, the standard game field is created
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    private void generateStandardField() {
        //Generating the first Hexagon in the middle.
        middle.generateNodesMiddle();
        middle.expand();
        middle.interconnectOwnNodes();
        middle.interconnectNeighbourHexagons();

        middle.getHexTopLeft().expand();
        middle.getHexTopLeft().interconnectNeighbourHexagons();

        middle.getHexTopRight().expand();
        middle.getHexTopRight().interconnectNeighbourHexagons();

        middle.getHexLeft().expand();
        middle.getHexLeft().interconnectNeighbourHexagons();

        middle.getHexRight().expand();
        middle.getHexRight().interconnectNeighbourHexagons();

        middle.getHexBottomLeft().expand();
        middle.getHexBottomLeft().interconnectNeighbourHexagons();

        middle.getHexBottomRight().expand();
        middle.getHexBottomRight().interconnectNeighbourHexagons();


        middle.getHexTopLeft().generateNodes();
        middle.getHexTopRight().generateNodes();
        middle.getHexLeft().generateNodes();
        middle.getHexRight().generateNodes();
        middle.getHexBottomLeft().generateNodes();
        middle.getHexBottomRight().generateNodes();

        middle.getHexTopLeft().getHexTopLeft().generateNodes();
        middle.getHexTopLeft().getHexTopRight().generateNodes();

        middle.getHexTopRight().getHexTopRight().generateNodes();
        middle.getHexTopRight().getHexRight().generateNodes();

        middle.getHexRight().getHexRight().generateNodes();
        middle.getHexRight().getHexBottomRight().generateNodes();

        middle.getHexBottomRight().getHexBottomRight().generateNodes();
        middle.getHexBottomRight().getHexBottomLeft().generateNodes();

        middle.getHexBottomLeft().getHexBottomLeft().generateNodes();
        middle.getHexBottomLeft().getHexLeft().generateNodes();

        middle.getHexLeft().getHexLeft().generateNodes();
        middle.getHexLeft().getHexTopLeft().generateNodes();


        middle.getHexTopLeft().interconnectNeighbourNodes();
        middle.getHexTopRight().interconnectNeighbourNodes();
        middle.getHexLeft().interconnectNeighbourNodes();
        middle.getHexRight().interconnectNeighbourNodes();
        middle.getHexBottomLeft().interconnectNeighbourNodes();
        middle.getHexBottomRight().interconnectNeighbourNodes();


        middle.getHexTopLeft().getHexTopLeft().updateHexagonList();
        middle.getHexTopLeft().getHexTopRight().updateHexagonList();

        middle.getHexTopRight().getHexTopRight().updateHexagonList();
        middle.getHexTopRight().getHexRight().updateHexagonList();

        middle.getHexRight().getHexRight().updateHexagonList();
        middle.getHexRight().getHexBottomRight().updateHexagonList();

        middle.getHexBottomRight().getHexBottomRight().updateHexagonList();
        middle.getHexBottomRight().getHexBottomLeft().updateHexagonList();

        middle.getHexBottomLeft().getHexBottomLeft().updateHexagonList();
        middle.getHexBottomLeft().getHexLeft().updateHexagonList();

        middle.getHexLeft().getHexLeft().updateHexagonList();
        middle.getHexLeft().getHexTopLeft().updateHexagonList();

    }

    /**
     * Generates a random game field
     * <p>
     * When this method is called a game field is created in which the position of the hexagons is decided randomly
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    private void generateRandomField() {
        middle.generateNodesMiddle();
        middle.interconnectOwnNodes();

        ArrayList<Hexagon> placedHexagons = new ArrayList<>();
        placedHexagons.add(middle);
        while (hexagonHashSet.size() < 19) {
            expandRandomly(placedHexagons, randomInt(0, placedHexagons.size()), randomInt(0, 6));
            hexagonHashSet.forEach(Hexagon::updateHexagonList);
            hexagonHashSet.forEach(Hexagon::interconnectNeighbourHexagons);

        }
        for (Hexagon hexagon : placedHexagons) {
            if (!hexagon.equals(middle))
                hexagon.generateNodes();
        }
        for (Hexagon hexagon : placedHexagons) {
            if (!hexagon.equals(middle))
                hexagon.interconnectOwnNodes();
        }

    }

    /**
     * Function used for expanding randomly from a hexagon, in contrast to the usual 6-directional expanding
     * <p>
     * A random hexagon of the already generated ones gets selected and will then randomly expand in 1 direction
     * Furthermore the list containing the existing hexagons gets updated because a new one was created
     *
     * @param list      the ArrayList containing the existing hexagons
     * @param rand      the random number used to index the ArrayList of the hexagons
     * @param direction the random number used to decide the direction in which to expand
     * @author Marc Hermes
     * @since 2021-05-14
     */
    private void expandRandomly(ArrayList<Hexagon> list, int rand, int direction) {
        switch (direction) {
            case 0:
                if (list.get(rand).getSelfPosition().size() < 4) {
                    Hexagon hex = list.get(rand).dockTopLeft();
                    list.clear();
                    list.addAll(hexagonHashSet);
                    hex.updateHexagonList();

                }
                break;
            case 1:
                if (list.get(rand).getSelfPosition().size() < 4) {
                    Hexagon hex = list.get(rand).dockLeft();
                    list.clear();
                    list.addAll(hexagonHashSet);
                    hex.updateHexagonList();

                }
                break;
            case 2:
                if (list.get(rand).getSelfPosition().size() < 4) {
                    Hexagon hex = list.get(rand).dockRight();
                    list.clear();
                    list.addAll(hexagonHashSet);
                    hex.updateHexagonList();

                }
                break;
            case 3:
                if (list.get(rand).getSelfPosition().size() < 4) {
                    Hexagon hex = list.get(rand).dockBottomLeft();
                    list.clear();
                    list.addAll(hexagonHashSet);
                    hex.updateHexagonList();

                }
                break;
            case 4:
                if (list.get(rand).getSelfPosition().size() < 4) {
                    Hexagon hex = list.get(rand).dockBottomRight();
                    list.clear();
                    list.addAll(hexagonHashSet);
                    hex.updateHexagonList();

                }
                break;
            case 5:
                if (list.get(rand).getSelfPosition().size() < 4) {
                    Hexagon hex = list.get(rand).dockTopRight();
                    list.clear();
                    list.addAll(hexagonHashSet);
                    hex.updateHexagonList();
                }
                break;
        }

    }

    /**
     * Configures the dice tokens and field types as well as the harbors for the hexagons randomly
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public void configureTerrainTypeAndDiceTokensForAllHexagonsRandomly() {
        ArrayList<Hexagon> hexagons = new ArrayList<>(hexagonHashSet);

        ArrayList<Integer> diceTokenList = new ArrayList<>();
        //diceTokenList.add(0);
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


        ArrayList<Integer> terrainType = new ArrayList<>();
        terrainType.add(6);
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

        for (int i = 0; i < 19; i++) {
            int rand1 = randomInt(0, 19 - i);
            int rand2 = randomInt(0, 18 - i);
            if(terrainType.get(rand1) !=6) {
                hexagons.get(i).configureTerrainTypeAndDiceToken(terrainType.get(rand1), diceTokenList.get(rand2));

                terrainType.remove(rand1);
                diceTokenList.remove(rand2);
            } else {
                hexagons.get(i).configureTerrainTypeAndDiceToken(terrainType.get(rand1), 0);
                terrainType.remove(rand1);
            }
        }

    }

    /**
     * Configures the harbors of the hexagons/building nodes randomly
     * <p>
     * By using this method it can happen that not all 18 harbors are placed on the game field.
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public void configureHarborsRandomly() {

        ArrayList<Integer> harborList = new ArrayList<>();
        harborList.add(1);
        harborList.add(2);
        harborList.add(3);
        harborList.add(4);
        harborList.add(5);
        harborList.add(6);
        harborList.add(6);
        harborList.add(6);
        harborList.add(6);

        for (Hexagon hexagonToInspect : hexagonHashSet) {
            if (!hexagonToInspect.equals(middle)) {
                int rand3 = randomInt(0, harborList.size());

                // Check if the hexagon already has harbors. because then no harbor will be placed

                boolean alreadyHasHarbor = false;
                for (BuildingNode bn : hexagonToInspect.getBuildingNodes()) {
                    if (bn.getTypeOfHarbor() != 0) {
                        alreadyHasHarbor = true;
                        break;
                    }
                }

                // size() < 6 means that the hexagon has a connection to the ocean and thus a harbor may be placed
                if (hexagonToInspect.hexagons.size() < 6 && !alreadyHasHarbor) {
                    if (hexagonToInspect.hexLeft == null) {
                        hexagonToInspect.buildingTopLeft.setTypeOfHarbor(harborList.get(rand3));
                        hexagonToInspect.buildingBottomLeft.setTypeOfHarbor(harborList.get(rand3));
                        harborList.remove(rand3);
                    } else if (hexagonToInspect.hexRight == null) {
                        hexagonToInspect.buildingTopRight.setTypeOfHarbor(harborList.get(rand3));
                        hexagonToInspect.buildingBottomRight.setTypeOfHarbor(harborList.get(rand3));
                        harborList.remove(rand3);
                    } else if (hexagonToInspect.hexTopLeft == null) {
                        hexagonToInspect.buildingTopLeft.setTypeOfHarbor(harborList.get(rand3));
                        hexagonToInspect.buildingTop.setTypeOfHarbor(harborList.get(rand3));
                        harborList.remove(rand3);
                    } else if (hexagonToInspect.hexTopRight == null) {
                        hexagonToInspect.buildingTop.setTypeOfHarbor(harborList.get(rand3));
                        hexagonToInspect.buildingTopRight.setTypeOfHarbor(harborList.get(rand3));
                        harborList.remove(rand3);
                    } else if (hexagonToInspect.hexBottomLeft == null) {
                        hexagonToInspect.buildingBottom.setTypeOfHarbor(harborList.get(rand3));
                        hexagonToInspect.buildingBottomLeft.setTypeOfHarbor(harborList.get(rand3));
                        harborList.remove(rand3);
                    } else if (hexagonToInspect.hexBottomRight == null) {
                        hexagonToInspect.buildingBottomRight.setTypeOfHarbor(harborList.get(rand3));
                        hexagonToInspect.buildingBottom.setTypeOfHarbor(harborList.get(rand3));
                        harborList.remove(rand3);
                    }
                    if (harborList.isEmpty()) {
                        break;
                    }

                }
            }
        }
    }

    /**
     * Generates the standard configuration of the harbors of the game field
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public void configureHarborsStandard() {
        // 0 = no harbor, 1 = 2:1 Sheep, 2 = 2:1 Clay, 3 = 2:1 Wood, 4 = 2:1 Grain, 5 = 2:1 Ore, 6 = 3:1 Any*/

        middle.getHexLeft().getHexTopLeft().getBuildingTopLeft().setTypeOfHarbor(5);
        middle.getHexLeft().getHexTopLeft().getBuildingBottomLeft().setTypeOfHarbor(5);

        middle.getHexBottomLeft().getHexLeft().getBuildingTopLeft().setTypeOfHarbor(4);
        middle.getHexBottomLeft().getHexLeft().getBuildingBottomLeft().setTypeOfHarbor(4);

        middle.getHexBottomLeft().getHexBottomLeft().getBuildingBottomLeft().setTypeOfHarbor(6);
        middle.getHexBottomLeft().getHexBottomLeft().getBuildingBottom().setTypeOfHarbor(6);

        middle.getHexBottomRight().getHexBottomLeft().getBuildingBottom().setTypeOfHarbor(3);
        middle.getHexBottomRight().getHexBottomLeft().getBuildingBottomRight().setTypeOfHarbor(3);

        middle.getHexRight().getHexBottomRight().getBuildingBottom().setTypeOfHarbor(2);
        middle.getHexRight().getHexBottomRight().getBuildingBottomRight().setTypeOfHarbor(2);

        middle.getHexRight().getHexRight().getBuildingTopRight().setTypeOfHarbor(6);
        middle.getHexRight().getHexRight().getBuildingBottomRight().setTypeOfHarbor(6);

        middle.getHexRight().getHexTopRight().getBuildingTop().setTypeOfHarbor(6);
        middle.getHexRight().getHexTopRight().getBuildingTopRight().setTypeOfHarbor(6);

        middle.getHexTopRight().getHexTopLeft().getBuildingTop().setTypeOfHarbor(1);
        middle.getHexTopRight().getHexTopLeft().getBuildingTopRight().setTypeOfHarbor(1);

        middle.getHexTopLeft().getHexTopLeft().getBuildingTopLeft().setTypeOfHarbor(6);
        middle.getHexTopLeft().getHexTopLeft().getBuildingTop().setTypeOfHarbor(6);
    }

    /**
     * Configures the standard dice tokens and field types for the hexagons
     * <p>
     * IMPORTANT: only use this function if you have generated the standard game field type otherwise it may not work
     * because the references of the hexagons may not exist.
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public void configureTerrainTypeAndDiceTokensForAllHexagonsStandard() {
        //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;

        middle.configureTerrainTypeAndDiceToken(6, 0);
        middle.getHexLeft().configureTerrainTypeAndDiceToken(4, 9);
        middle.getHexBottomLeft().configureTerrainTypeAndDiceToken(5, 11);
        middle.getHexBottomRight().configureTerrainTypeAndDiceToken(1, 3);
        middle.getHexRight().configureTerrainTypeAndDiceToken(5, 6);
        middle.getHexTopRight().configureTerrainTypeAndDiceToken(2, 5);
        middle.getHexTopLeft().configureTerrainTypeAndDiceToken(5, 4);

        middle.getHexLeft().getHexTopLeft().configureTerrainTypeAndDiceToken(2, 2);
        middle.getHexLeft().getHexLeft().configureTerrainTypeAndDiceToken(1, 5);

        middle.getHexBottomLeft().getHexLeft().configureTerrainTypeAndDiceToken(2, 10);
        middle.getHexBottomLeft().getHexBottomLeft().configureTerrainTypeAndDiceToken(4, 8);

        middle.getHexBottomRight().getHexBottomLeft().configureTerrainTypeAndDiceToken(3, 4);
        middle.getHexBottomRight().getHexBottomRight().configureTerrainTypeAndDiceToken(4, 11);

        middle.getHexRight().getHexBottomRight().configureTerrainTypeAndDiceToken(3, 12);
        middle.getHexRight().getHexRight().configureTerrainTypeAndDiceToken(2, 9);

        middle.getHexTopRight().getHexRight().configureTerrainTypeAndDiceToken(1, 10);
        middle.getHexTopRight().getHexTopRight().configureTerrainTypeAndDiceToken(3, 8);

        middle.getHexTopLeft().getHexTopRight().configureTerrainTypeAndDiceToken(3, 3);
        middle.getHexTopLeft().getHexTopLeft().configureTerrainTypeAndDiceToken(1, 6);

    }

    /**
     * Returns a random integer in a given range with standard distribution
     *
     * @param min the (inclusive) lower bound for the random number
     * @param max the (exclusive) upper bound for the random number
     * @return the random number generated
     * @author Marc Hermes
     * @since 2021-05-14
     */
    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }

    public ArrayList<BuildingNode> getBuiltBuildings() {
        return builtBuildings;
    }

    /**
     * Adds built Buildings to a List.
     *
     * <p>Adds a new building to the list if it is not already part of the list.</p>
     *
     * @author Philip Nitsche
     * @since 2021-04-26
     */

    public void addBuiltBuilding(BuildingNode builtBuilding) {
        if (!builtBuildings.contains(builtBuilding)) {
            builtBuildings.add(builtBuilding);
        }
    }

    /**
     * Represents a buildable Node of the MapGraph.
     * <p>This class gives us the ability to put StreetNodes and BuildingNodes into the same List by putting in
     * MapGraphNodes.</p>
     *
     * @author Pieter Vogt
     * @since 2021-04-15
     */
    public abstract class MapGraphNode implements Serializable {

        //Fields

        public final UUID uuid;
        public final String positionToParent;
        public int occupiedByPlayer = 666;
        public final Hexagon parent;

        //Constructors
        public MapGraphNode(String positionToParent, Hexagon parent, UUID uuid) {
            this.positionToParent = positionToParent;
            this.uuid = uuid;
            this.parent = parent;
        }

        //Getter Setter

        public UUID getUuid() {
            return uuid;
        }

        public String getPositionToParent() {
            return positionToParent;
        }

        public int getOccupiedByPlayer() {
            return occupiedByPlayer;
        }

        public void setOccupiedByPlayer(int occupiedByPlayer) {
            this.occupiedByPlayer = occupiedByPlayer;
        }

        public Hexagon getParent() {
            return parent;
        }
    }

    /**
     * Holds all the data needed to represent places to build streets on, and the interactions made with them.
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    public class StreetNode extends MapGraphNode {

        //FIELDS

        private final HashSet<BuildingNode> connectedBuildingNodes = new HashSet<>();

        //CONSTRUCTOR

        public StreetNode(String position, Hexagon h, UUID uuid) {
            super(position, h, uuid);
        }

        //GETTER SETTER

        public HashSet<BuildingNode> getConnectedBuildingNodes() {
            return connectedBuildingNodes;
        }

        public void addBuildingNode(BuildingNode buildingNode) throws ListFullException {
            if (!connectedBuildingNodes.contains(buildingNode)) {
                if (connectedBuildingNodes.size() < 2) {
                    connectedBuildingNodes.add(buildingNode);
                } else throw new ListFullException("This StreetNode already has 2 BuildingNodes connected to it.");
            }
        }

        //METHODS

        /**
         * Builds a road for player with parsed index.
         * Calls the function to update the matrix with new Street.
         * <p>
         * enhanced by Marc, Kirstin, 2021-04-23
         *
         * @param playerIndex Index of the player who wants to build a road
         * @return True if construction was successful, false if not.
         * @author Pieter Vogt, enhanced by Kirstin Beyer
         * @since 2021-04-15
         */
        public Boolean tryBuildRoad(int playerIndex, int startingPhase) {

            boolean existingConnection = false;
            boolean buildingAllowed = false;
            boolean correctBuildingPhaseTwo = false;
            if (startingPhase > 0) {
                buildingAllowed = true;
            }

            for (MapGraph.BuildingNode connectedBuildingNode : this.getConnectedBuildingNodes()) {
                if (connectedBuildingNode.getOccupiedByPlayer() == playerIndex) {
                    existingConnection = true;
                    correctBuildingPhaseTwo = builtBuildings.get(builtBuildings.size() - 1).getUuid().equals(connectedBuildingNode.getUuid());
                }
                for (MapGraph.StreetNode connectedStreetNode : connectedBuildingNode.getConnectedStreetNodes()) {
                    if (connectedStreetNode.getOccupiedByPlayer() == playerIndex) {
                        existingConnection = true;
                        if (connectedBuildingNode.getOccupiedByPlayer() == 666 || connectedBuildingNode.getOccupiedByPlayer() == playerIndex) {
                            buildingAllowed = true;
                            break;
                        }
                    }
                }
            }

            if (this.occupiedByPlayer == 666 && existingConnection && buildingAllowed && (startingPhase == 0 ||
                    ((startingPhase == 1 || (startingPhase == 2 && correctBuildingPhaseTwo)) &&
                            numOfRoads[playerIndex] == startingPhase - 1 && numOfRoads[playerIndex] < numOfBuildings[playerIndex]))) {
                numOfRoads[playerIndex]++;
                return true;
            } else return false;
        }

        public boolean buildRoad(int playerIndex) {
            this.occupiedByPlayer = playerIndex;
            longestStreetPathCalculator.updateMatrixWithNewStreet(this.getUuid(), playerIndex);
            return true;
        }
    }

    /**
     * Holds all the data needed to represent places to build buildings on, and the interactions made with them.
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    public class BuildingNode extends MapGraphNode {

        //FIELDS

        private final HashSet<StreetNode> connectedStreetNodes = new HashSet<>();

        private int typeOfHarbor = 0;
        private int sizeOfSettlement = 0;
        //CONSTRUCTOR

        /**
         * Creates a new BuildingNode
         *
         * <p>The type of harbor refers to this:
         * 0 = no harbor, 1 = 2:1 Sheep, 2 = 2:1 Clay, 3 = 2:1 Wood, 4 = 2:1 Grain, 5 = 2:1 Ore, 6 = 3:1 Any
         * </p>
         */
        public BuildingNode(String position, Hexagon h, UUID uuid) {
            super(position, h, uuid);
        }

        //GETTER SETTER

        public HashSet<StreetNode> getConnectedStreetNodes() {
            return connectedStreetNodes;
        }

        public int getTypeOfHarbor() {
            return typeOfHarbor;
        }

        public void setTypeOfHarbor(int typeOfHarbor) {
            this.typeOfHarbor = typeOfHarbor;
        }

        public void addStreetNode(StreetNode streetNode) throws ListFullException {
            if (!connectedStreetNodes.contains(streetNode)) {
                if (connectedStreetNodes.size() < 3) {
                    connectedStreetNodes.add(streetNode);
                } else throw new ListFullException("This BuildingNode already has 3 StreetNodes connected to it.");
            }
        }

        public int getSizeOfSettlement() {
            return sizeOfSettlement;
        }

        public void incSizeOfSettlement() {
            this.sizeOfSettlement++;
        }

        //METHODS

        /**
         * Builds or upgrades a settlement for player with parsed index.
         * Calls the function to update the matrix with new building, if the building is not just a size increase.
         * <p>
         * enhanced by Marc, Kirstin, 2021-04-23
         *
         * @param playerIndex Index of the player who wants to build or upgrade a building.
         * @return True if construction was successful, false if not.
         * @author Pieter Vogt, enhanced by Kirstin Beyer
         * @since 2021-04-15
         */
        public Boolean tryBuildOrDevelopSettlement(int playerIndex, int startingPhase) {

            boolean existingStreet = false;
            boolean buildingAllowed = true;
            if (startingPhase > 0) {
                existingStreet = true;
            }

            for (MapGraph.StreetNode connectedStreetNode : this.getConnectedStreetNodes()) {
                if (connectedStreetNode.getOccupiedByPlayer() == playerIndex) {
                    existingStreet = true;
                }
                for (MapGraph.BuildingNode connectedBuildingNode : connectedStreetNode.getConnectedBuildingNodes()) {
                    if (connectedBuildingNode.getOccupiedByPlayer() != 666) {
                        buildingAllowed = false;
                        break;
                    }
                }
            }

            if (occupiedByPlayer == 666 || occupiedByPlayer == playerIndex) {
                if ((sizeOfSettlement < 1 && existingStreet && buildingAllowed &&
                        (startingPhase == 0 || numOfBuildings[playerIndex] == startingPhase - 1)) ||
                        (startingPhase == 0 && sizeOfSettlement == 1 && occupiedByPlayer == playerIndex)) {
                    numOfBuildings[playerIndex]++;
                    return true;
                } else return false;
            } else return false;
        }

        public void buildOrDevelopSettlement(int playerIndex) {
            this.occupiedByPlayer = playerIndex;
            if (sizeOfSettlement == 0) {
                longestStreetPathCalculator.updateMatrixWithNewBuilding(this, playerIndex);
            }
            sizeOfSettlement++;
        }
    }

    /**
     * Represents the logical structure of one hexagonal cardboard-piece to build the play field of.
     * <p>This class represents the logic of the pathfinding- and the building-system. It houses the pointers to the
     * building-spots and is aware of its neighbour-hexagons. With this, we are able to send specific commands to
     * specific places of the play field. Furthermore this has superseded the GameField-class and now also represents the
     * type of Terrain and the diceToken.</p>
     *
     * @author Pieter Vogt
     * @since 2021-04-09
     */
    public class Hexagon implements Serializable {

        //FIELDS

        private final List<String> selfPosition = new ArrayList<>(); //IMPORTANT! If fiddled with in the future: This must never become any sort of Set,because we need to be able to store duplicates!

        private final UUID uuid = UUID.randomUUID();

        private int diceToken;
        private int terrainType;
        private Hexagon hexTopLeft;
        private Hexagon hexTopRight;
        private Hexagon hexLeft;
        private Hexagon hexRight;
        private Hexagon hexBottomLeft;
        private Hexagon hexBottomRight;

        private StreetNode streetLeft;
        private StreetNode streetBottomLeft;
        private StreetNode streetBottomRight;
        private StreetNode streetRight;
        private StreetNode streetTopRight;
        private StreetNode streetTopLeft;

        private BuildingNode buildingTopLeft;
        private BuildingNode buildingBottomLeft;
        private BuildingNode buildingBottom;
        private BuildingNode buildingBottomRight;
        private BuildingNode buildingTopRight;
        private BuildingNode buildingTop;

        private final Set<BuildingNode> buildingNodes = new HashSet<>();
        private final Set<StreetNode> streetNodes = new HashSet<>();
        private final Set<Hexagon> hexagons = new HashSet<>();

        private boolean occupiedByRobber;

        //CONSTRUCTOR

        /**
         * Constructor for the first Hexagon.
         * <p>This Constructor does not parse any argument about ancestor-Hexagons. Therefore it can only be used for
         * the first Hexagon to be placed.</p>
         *
         * @param position The directional vector from the ancestor-Hexagon to this one.
         * @author Pieter Vogt
         * @since 2021-04-10
         */
        public Hexagon(String position) {
            selfPosition.add(position);
            hexagonHashSet.add(this);
        }

        /**
         * Constructor for all but the first Hexagon.
         * <p>This Constructor parses the information about where the ancestor-Hexagon is located, relative to the
         * first Hexagon. Therefore it must be used for all but the first Hexagon to be placed, because every Hexagon
         * but the first has an ancestor.</p>
         *
         * @param position     The directional vector from the ancestor-Hexagon to this one.
         * @param positionList The List of positional vectors that describes the position of the ancestor-Hexagon.
         * @author Pieter Vogt
         * @since 2021-04-10
         */
        public Hexagon(String position, List<String> positionList) {
            this.selfPosition.addAll(positionList);
            this.selfPosition.add(position);
            hexagonHashSet.add(this);
        }

        //GETTER SETTER

        public int getDiceToken() {
            return diceToken;
        }

        public int getTerrainType() {
            return terrainType;
        }

        public void configureTerrainTypeAndDiceToken(int terrainType, int diceToken) {
            this.terrainType = terrainType;
            this.diceToken = diceToken;
        }

        public StreetNode getStreetLeft() {
            return streetLeft;
        }

        public StreetNode getStreetBottomLeft() {
            return streetBottomLeft;
        }

        public StreetNode getStreetBottomRight() {
            return streetBottomRight;
        }

        public StreetNode getStreetRight() {
            return streetRight;
        }

        public StreetNode getStreetTopRight() {
            return streetTopRight;
        }

        public StreetNode getStreetTopLeft() {
            return streetTopLeft;
        }

        public BuildingNode getBuildingTopLeft() {
            return buildingTopLeft;
        }

        public BuildingNode getBuildingBottomLeft() {
            return buildingBottomLeft;
        }

        public BuildingNode getBuildingBottom() {
            return buildingBottom;
        }

        public BuildingNode getBuildingBottomRight() {
            return buildingBottomRight;
        }

        public BuildingNode getBuildingTopRight() {
            return buildingTopRight;
        }

        public BuildingNode getBuildingTop() {
            return buildingTop;
        }

        public Hexagon getHexTopLeft() {
            return hexTopLeft;
        }

        public void setHexTopLeft(Hexagon hexTopLeft) {
            this.hexTopLeft = hexTopLeft;
        }

        public Hexagon getHexTopRight() {
            return hexTopRight;
        }

        public void setHexTopRight(Hexagon hexTopRight) {
            this.hexTopRight = hexTopRight;
        }

        public Hexagon getHexLeft() {
            return hexLeft;
        }

        public void setHexLeft(Hexagon hexLeft) {
            this.hexLeft = hexLeft;
        }

        public Hexagon getHexRight() {
            return hexRight;
        }

        public void setHexRight(Hexagon hexRight) {
            this.hexRight = hexRight;
        }

        public Hexagon getHexBottomLeft() {
            return hexBottomLeft;
        }

        public void setHexBottomLeft(Hexagon hexBottomLeft) {
            this.hexBottomLeft = hexBottomLeft;
        }

        public Hexagon getHexBottomRight() {
            return hexBottomRight;
        }

        public void setHexBottomRight(Hexagon hexBottomRight) {
            this.hexBottomRight = hexBottomRight;
        }

        public Set<BuildingNode> getBuildingNodes() {
            return buildingNodes;
        }

        public List<String> getSelfPosition() {
            return selfPosition;
        }

        public UUID getUuid() {
            return uuid;
        }

        public boolean isOccupiedByRobber() {
            return occupiedByRobber;
        }

        public void setOccupiedByRobber(boolean occupiedByRobber) {
            this.occupiedByRobber = occupiedByRobber;
        }

        // METHODS

        /**
         * Adds all nodes to the corresponding nodeLists.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void updateNodeLists() {
            streetNodes.add(streetTopLeft);
            streetNodes.add(streetTopRight);
            streetNodes.add(streetLeft);
            streetNodes.add(streetRight);
            streetNodes.add(streetBottomLeft);
            streetNodes.add(streetBottomRight);

            buildingNodes.add(buildingTop);
            buildingNodes.add(buildingTopLeft);
            buildingNodes.add(buildingTopRight);
            buildingNodes.add(buildingBottomLeft);
            buildingNodes.add(buildingBottomRight);
            buildingNodes.add(buildingBottom);

        }

        /**
         * Adds all Hexagons to the HexagonList.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void updateHexagonList() {
            if (hexTopLeft != null) {
                hexagons.add(hexTopLeft);
            }
            if (hexTopRight != null) {
                hexagons.add(hexTopRight);
            }
            if (hexRight != null) {
                hexagons.add(hexRight);
            }
            if (hexLeft != null) {
                hexagons.add(hexLeft);
            }
            if (hexBottomLeft != null) {
                hexagons.add(hexBottomLeft);
            }
            if (hexBottomRight != null) {
                hexagons.add(hexBottomRight);
            }
        }

        /**
         * Fills the empty slots with nodes.
         * <p>This is used, to quickly generate a fully occupied hexagon to dock to. First it checks if the respective
         * NodeSpot is empty. If so, it fills it with a new one. Because of that, we can call this function with already
         * partially occupied Hexagons without overwriting Nodes that might already been shared between multiple
         * Hexagons. This is especially important when expanding the inner ring of Hexagons a second time to get the
         * full Standard-play field.</p>
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void generateNodes() {
            //First checking streetNodes...
            if (this.streetTopLeft == null && hexTopLeft != null) {
                if (hexTopLeft.getStreetBottomRight() == null) {
                    this.streetTopLeft = new StreetNode("topLeft", this, UUID.randomUUID());
                } else {
                    this.streetTopLeft = hexTopLeft.getStreetBottomRight();
                }
            } else {
                this.streetTopLeft = new StreetNode("topLeft", this, UUID.randomUUID());
            }


            if (this.streetTopRight == null && hexTopRight != null) {

                if (hexTopRight.getStreetBottomLeft() == null) {
                    this.streetTopRight = new StreetNode("topRight", this, UUID.randomUUID());
                } else {
                    this.streetTopRight = hexTopRight.getStreetBottomLeft();
                }
            } else {
                this.streetTopRight = new StreetNode("topRight", this, UUID.randomUUID());
            }


            if (streetLeft == null && hexLeft != null) {

                if (hexLeft.getStreetRight() == null) {
                    this.streetLeft = new StreetNode("left", this, UUID.randomUUID());
                } else {
                    this.streetLeft = hexLeft.getStreetRight();
                }
            } else {
                this.streetLeft = new StreetNode("left", this, UUID.randomUUID());
            }


            if (streetRight == null && hexRight != null) {

                if (hexRight.getStreetLeft() == null) {
                    this.streetRight = new StreetNode("right", this, UUID.randomUUID());
                } else {
                    this.streetRight = hexRight.getStreetLeft();
                }
            } else {
                this.streetRight = new StreetNode("right", this, UUID.randomUUID());
            }


            if (streetBottomLeft == null && hexBottomLeft != null) {

                if (hexBottomLeft.getStreetTopRight() == null) {
                    this.streetBottomLeft = new StreetNode("bottomLeft", this, UUID.randomUUID());
                } else {
                    this.streetBottomLeft = hexBottomLeft.getStreetTopRight();
                }
            } else {
                this.streetBottomLeft = new StreetNode("bottomLeft", this, UUID.randomUUID());
            }


            if (streetBottomRight == null && hexBottomRight != null) {

                if (hexBottomRight.getStreetTopLeft() == null) {
                    this.streetBottomRight = new StreetNode("bottomRight", this, UUID.randomUUID());
                } else {
                    this.streetBottomRight = hexBottomRight.getStreetTopLeft();
                }
            } else {
                this.streetBottomRight = new StreetNode("bottomRight", this, UUID.randomUUID());
            }


            //... then checking BuildingNodes.
            if (buildingTop == null && (hexTopLeft != null || hexTopRight != null)) {
                if (hexTopLeft != null && hexTopRight == null) {
                    if (hexTopLeft.getBuildingBottomRight() == null) {
                        this.buildingTop = new BuildingNode("top", this, UUID.randomUUID());
                    } else {
                        this.buildingTop = hexTopLeft.getBuildingBottomRight();
                    }
                } else if (hexTopLeft == null && hexTopRight != null) {
                    if (hexTopRight.getBuildingBottomLeft() == null) {
                        this.buildingTop = new BuildingNode("top", this, UUID.randomUUID());
                    } else {
                        this.buildingTop = hexTopRight.getBuildingBottomLeft();
                    }
                } else if (hexTopLeft != null) {
                    if (hexTopLeft.getBuildingBottomRight() != null) {
                        this.buildingTop = hexTopLeft.getBuildingBottomRight();
                    } else if (hexTopRight.getBuildingBottomLeft() != null) {
                        this.buildingTop = hexTopRight.getBuildingBottomLeft();
                    } else {
                        this.buildingTop = new BuildingNode("top", this, UUID.randomUUID());
                    }
                }
            } else {
                this.buildingTop = new BuildingNode("top", this, UUID.randomUUID());
            }


            //Für buildingTopLeft, vergl. mit hexLeft und hexTopLeft
            if (buildingTopLeft == null && (hexLeft != null || hexTopLeft != null)) {
                if (hexLeft != null && hexTopLeft == null) {
                    if (hexLeft.getBuildingTopRight() == null) {
                        this.buildingTopLeft = new BuildingNode("topLeft", this, UUID.randomUUID());
                    } else {
                        this.buildingTopLeft = hexLeft.getBuildingTopRight();
                    }
                } else if (hexLeft == null && hexTopLeft != null) {
                    if (hexTopLeft.getBuildingBottom() == null) {
                        this.buildingTopLeft = new BuildingNode("topLeft", this, UUID.randomUUID());
                    } else {
                        this.buildingTopLeft = hexTopLeft.getBuildingBottom();
                    }
                } else if (hexLeft != null) {
                    if (hexLeft.getBuildingTopRight() != null) {
                        this.buildingTopLeft = hexLeft.getBuildingTopRight();
                    } else if (hexTopLeft.getBuildingBottom() != null) {
                        this.buildingTopLeft = hexTopLeft.getBuildingBottom();
                    } else {
                        this.buildingTopLeft = new BuildingNode("topLeft", this, UUID.randomUUID());
                    }
                }
            } else {
                this.buildingTopLeft = new BuildingNode("topLeft", this, UUID.randomUUID());
            }

            //Für buildingTopRight, vergl. mit hexRight und hexTopRight
            if (buildingTopRight == null && (hexRight != null || hexTopRight != null)) {
                if (hexRight != null && hexTopRight == null) {
                    if (hexRight.getBuildingTopLeft() == null) {
                        this.buildingTopRight = new BuildingNode("topRight", this, UUID.randomUUID());
                    } else {
                        this.buildingTopRight = hexRight.getBuildingTopLeft();
                    }
                } else if (hexRight == null && hexTopRight != null) {
                    if (hexTopRight.getBuildingBottom() == null) {
                        this.buildingTopRight = new BuildingNode("topRight", this, UUID.randomUUID());
                    } else {
                        this.buildingTopRight = hexTopRight.getBuildingBottom();
                    }
                } else if (hexRight != null) {
                    if (hexRight.getBuildingTopLeft() != null) {
                        this.buildingTopRight = hexRight.getBuildingTopLeft();
                    } else if (hexTopRight.getBuildingBottom() != null) {
                        this.buildingTopRight = hexTopRight.getBuildingBottom();
                    } else {
                        this.buildingTopRight = new BuildingNode("topRight", this, UUID.randomUUID());
                    }
                }
            } else {
                this.buildingTopRight = new BuildingNode("topRight", this, UUID.randomUUID());
            }

            //Für buildingBottom, vergl. mit hexBottomLeft und hexBottomRight
            if (buildingBottom == null && (hexBottomLeft != null || hexBottomRight != null)) {
                if (hexBottomLeft != null && hexBottomRight == null) {
                    if (hexBottomLeft.getBuildingTopRight() == null) {
                        this.buildingBottom = new BuildingNode("bottom", this, UUID.randomUUID());
                    } else {
                        this.buildingBottom = hexBottomLeft.getBuildingTopRight();
                    }
                } else if (hexBottomLeft == null && hexBottomRight != null) {
                    if (hexBottomRight.getBuildingTopLeft() == null) {
                        this.buildingBottom = new BuildingNode("bottom", this, UUID.randomUUID());
                    } else {
                        this.buildingBottom = hexBottomRight.getBuildingTopLeft();
                    }
                } else if (hexBottomLeft != null) {
                    if (hexBottomLeft.getBuildingTopRight() != null) {
                        this.buildingBottom = hexBottomLeft.getBuildingTopRight();
                    } else if (hexBottomRight.getBuildingTopLeft() != null) {
                        this.buildingBottom = hexBottomRight.getBuildingTopLeft();
                    } else {
                        this.buildingBottom = new BuildingNode("bottom", this, UUID.randomUUID());
                    }
                }
            } else {
                this.buildingBottom = new BuildingNode("bottom", this, UUID.randomUUID());
            }

            //Für buildingBottomLeft, vergl. mit hexLeft und hexBottomLeft
            if (buildingBottomLeft == null && (hexLeft != null || hexBottomLeft != null)) {
                if (hexLeft != null && hexBottomLeft == null) {
                    if (hexLeft.getBuildingBottomRight() == null) {
                        this.buildingBottomLeft = new BuildingNode("bottomLeft", this, UUID.randomUUID());
                    } else {
                        this.buildingBottomLeft = hexLeft.getBuildingBottomRight();
                    }
                } else if (hexLeft == null && hexBottomLeft != null) {
                    if (hexBottomLeft.getBuildingTop() == null) {
                        this.buildingBottomLeft = new BuildingNode("bottomLeft", this, UUID.randomUUID());
                    } else {
                        this.buildingBottomLeft = hexBottomLeft.getBuildingTop();
                    }
                } else if (hexLeft != null) {
                    if (hexLeft.getBuildingBottomRight() != null) {
                        this.buildingBottomLeft = hexLeft.getBuildingBottomRight();
                    } else if (hexBottomLeft.getBuildingTop() != null) {
                        this.buildingBottomLeft = hexBottomLeft.getBuildingTop();
                    } else {
                        this.buildingBottomLeft = new BuildingNode("bottomLeft", this, UUID.randomUUID());
                    }
                }
            } else {
                this.buildingBottomLeft = new BuildingNode("bottomLeft", this, UUID.randomUUID());
            }

            //Für buildingBottomRight, vergl. mit hexBottomRight und hexRight
            if (buildingBottomRight == null && (hexBottomRight != null || hexRight != null)) {
                if (hexBottomRight != null && hexRight == null) {
                    if (hexBottomRight.getBuildingTop() == null) {
                        this.buildingBottomRight = new BuildingNode("bottomRight", this, UUID.randomUUID());
                    } else {
                        this.buildingBottomRight = hexBottomRight.getBuildingTop();
                    }
                } else if (hexBottomRight == null && hexRight != null) {
                    if (hexRight.getBuildingBottomLeft() == null) {
                        this.buildingBottomRight = new BuildingNode("bottomRight", this, UUID.randomUUID());
                    } else {
                        this.buildingBottomRight = hexRight.getBuildingBottomLeft();
                    }
                } else if (hexBottomRight != null) {
                    if (hexBottomRight.getBuildingTop() != null) {
                        this.buildingBottomRight = hexBottomRight.getBuildingTop();
                    } else if (hexRight.getBuildingBottomLeft() != null) {
                        this.buildingBottomRight = hexRight.getBuildingBottomLeft();
                    } else {
                        this.buildingBottomRight = new BuildingNode("bottomRight", this, UUID.randomUUID());
                    }
                }
            } else {
                this.buildingBottomRight = new BuildingNode("bottomRight", this, UUID.randomUUID());
            }

            updateAllLists();

        }

        public void generateNodesMiddle() {
            this.streetTopLeft = new StreetNode("topLeft", this, UUID.randomUUID());
            this.streetBottomLeft = new StreetNode("bottomLeft", this, UUID.randomUUID());
            this.streetTopRight = new StreetNode("topRight", this, UUID.randomUUID());
            this.streetLeft = new StreetNode("left", this, UUID.randomUUID());
            this.streetRight = new StreetNode("right", this, UUID.randomUUID());
            this.streetBottomRight = new StreetNode("bottomRight", this, UUID.randomUUID());

            this.buildingTopLeft = new BuildingNode("topLeft", this, UUID.randomUUID());
            this.buildingTopRight = new BuildingNode("topRight", this, UUID.randomUUID());
            this.buildingBottomLeft = new BuildingNode("bottomLeft", this, UUID.randomUUID());
            this.buildingBottomRight = new BuildingNode("bottomRight", this, UUID.randomUUID());
            this.buildingTop = new BuildingNode("top", this, UUID.randomUUID());
            this.buildingBottom = new BuildingNode("bottom", this, UUID.randomUUID());

            updateAllLists();
        }

        private void updateAllLists() {
            //Updating lists inside the calling Hexagon.
            updateNodeLists();

            //Updating Sets of the MapGraph.
            streetNodeHashSet.addAll(streetNodes);
            buildingNodeHashSet.addAll(buildingNodes);
        }

        /**
         * Calls all dock-functions for convenience
         * <p>This method expands the calling hexagon for 6 new surrounding hexagons and interconnects them.</p>
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void expand() {
            //If needed, generate - then dock other Hexagons to the corresponding sides of the method.
            dockLeft();
            dockBottomLeft();
            dockBottomRight();
            dockRight();
            dockTopRight();
            dockTopLeft();
            updateHexagonList();
        }

        /**
         * Interconnects the outer ring around the calling Hexagon.
         * <p>This is used, to interconnect the Hexagons that are around the calling Hexagon with themselves, NOT THE
         * CALLING HEXAGON ITSELF. After expanding a Hexagon, this method needs to be called. For example it takes the
         * Hexagon to the right and connects its upper-left Nodes to the Hexagon to its upper left side.</p>
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        private void interconnectNeighbourHexagons() {
            if (hexTopLeft != null) {
                if (hexLeft != null) {
                    hexTopLeft.setHexBottomLeft(hexLeft);
                }
                if (hexTopRight != null) {
                    hexTopLeft.setHexRight(hexTopRight);
                }
            }

            if (hexLeft != null) {
                if (hexTopLeft != null) {
                    hexLeft.setHexTopRight(hexTopLeft);
                }
                if (hexBottomLeft != null) {
                    hexLeft.setHexBottomRight(hexBottomLeft);
                }
            }

            if (hexBottomLeft != null) {
                if (hexLeft != null) {
                    hexBottomLeft.setHexTopLeft(hexLeft);
                }
                if (hexBottomRight != null) {
                    hexBottomLeft.setHexRight(hexBottomRight);
                }
            }

            if (hexBottomRight != null) {
                if (hexBottomLeft != null) {
                    hexBottomRight.setHexLeft(hexBottomLeft);
                }
                if (hexRight != null) {
                    hexBottomRight.setHexTopRight(hexRight);
                }
            }

            if (hexRight != null) {
                if (hexBottomRight != null) {
                    hexRight.setHexBottomLeft(hexBottomRight);
                }
                if (hexTopRight != null) {
                    hexRight.setHexTopLeft(hexTopRight);
                }
            }

            if (hexTopRight != null) {
                if (hexRight != null) {
                    hexTopRight.setHexBottomRight(hexRight);
                }
                if (hexTopLeft != null) {
                    hexTopRight.setHexLeft(hexTopLeft);
                }
            }
        }

        /**
         * Interconnects the nodes of the calling Hexagon.
         * <p>This is used, to interconnect the StreetNodes and BuildingNodes inside the calling Hexagon with
         * themselves. For example it links the left and the top-left BuildingNode to the top-left StreetNode.</p>
         *
         * @author Pieter Vogt
         * @since 2021-04-09
         */
        private void interconnectOwnNodes() {
            //First we try to introduce the BuildingNodes to the StreetNodes...
            try {
                streetTopLeft.addBuildingNode(buildingTop);
                streetTopLeft.addBuildingNode(buildingTopLeft);

                streetTopRight.addBuildingNode(buildingTop);
                streetTopRight.addBuildingNode(buildingTopRight);

                streetLeft.addBuildingNode(buildingTopLeft);
                streetLeft.addBuildingNode(buildingBottomLeft);

                streetRight.addBuildingNode(buildingBottomRight);
                streetRight.addBuildingNode(buildingTopRight);

                streetBottomLeft.addBuildingNode(buildingBottom);
                streetBottomLeft.addBuildingNode(buildingBottomLeft);

                streetBottomRight.addBuildingNode(buildingBottom);
                streetBottomRight.addBuildingNode(buildingBottomRight);

            } catch (ListFullException ignored) {
            }
            //... then we try to introduce the StreetNodes to the BuildingNodes.
            try {
                buildingTop.addStreetNode(streetTopLeft);
                buildingTop.addStreetNode(streetTopRight);

                buildingTopLeft.addStreetNode(streetTopLeft);
                buildingTopLeft.addStreetNode(streetLeft);

                buildingTopRight.addStreetNode(streetTopRight);
                buildingTopRight.addStreetNode(streetRight);

                buildingBottomLeft.addStreetNode(streetBottomLeft);
                buildingBottomLeft.addStreetNode(streetLeft);

                buildingBottomRight.addStreetNode(streetBottomRight);
                buildingBottomRight.addStreetNode(streetRight);

                buildingBottom.addStreetNode(streetBottomLeft);
                buildingBottom.addStreetNode(streetBottomRight);
            } catch (ListFullException ignored) {
            }
        }

        /**
         * Interconnects the Nodes of the neighbour-Hexagons
         *
         * @author Pieter Vogt
         * @since 2021-04-10
         */
        private void interconnectNeighbourNodes() {
            for (Hexagon h : hexagons) {
                h.interconnectOwnNodes();
            }
        }


        //DOCKER-METHODS

        /**
         * Docks calling hexagon to its right Hexagon. If the right Hexagon is still null, the method generates a new
         * one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public Hexagon dockRight() {
            if (hexRight == null) {
                this.hexRight = new Hexagon("right", selfPosition);
                hexRight.setHexLeft(this);
            }
            return hexRight;
        }

        /**
         * Docks calling hexagon to its left Hexagon. If the left Hexagon is still null, the method generates a new one
         * there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public Hexagon dockLeft() {
            if (hexLeft == null) {
                this.hexLeft = new Hexagon("left", selfPosition);
                hexLeft.setHexRight(this);
            }
            return hexLeft;
        }

        /**
         * Docks calling hexagon to its top-right Hexagon. If the top-right Hexagon is still null, the method generates
         * a new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public Hexagon dockTopRight() {
            if (hexTopRight == null) {
                this.hexTopRight = new Hexagon("topRight", selfPosition);
                hexTopRight.setHexBottomLeft(this);
            }
            return hexTopRight;
        }

        /**
         * Docks calling hexagon to its bottom-right Hexagon. If the bottom-right Hexagon is still null, the method
         * generates a new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public Hexagon dockBottomRight() {
            if (hexBottomRight == null) {
                this.hexBottomRight = new Hexagon("bottomRight", selfPosition);
                hexBottomRight.setHexTopLeft(this);
            }
            return hexBottomRight;
        }

        /**
         * Docks calling hexagon to its top-left Hexagon. If the top-left Hexagon is still null, the method generates a
         * new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public Hexagon dockTopLeft() {
            if (hexTopLeft == null) {
                this.hexTopLeft = new Hexagon("topLeft", selfPosition);
                hexTopLeft.setHexBottomRight(this);
            }
            return hexTopLeft;
        }

        /**
         * Docks calling hexagon to its bottom-left Hexagon. If the bottom-left Hexagon is still null, the method
         * generates a new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public Hexagon dockBottomLeft() {
            if (hexBottomLeft == null) {
                this.hexBottomLeft = new Hexagon("bottomLeft", selfPosition);
                hexBottomLeft.setHexTopRight(this);
            }
            return hexBottomLeft;
        }
    }
}