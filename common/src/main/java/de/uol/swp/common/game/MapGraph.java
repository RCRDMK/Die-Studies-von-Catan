package de.uol.swp.common.game;

import de.uol.swp.common.game.exception.ListFullException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages the logic behind the playfield.
 * <p>This Class holds and processes the data about the buildable playfield. It can return the longest road, and
 * potentially the most settlements, the player with the most cities, overall number of buildings built, length of
 * combined roads and so on (especially interesting for endscreen, maybe?). This class should be read as the following:
 * Every attempt to find the longest Traderoute will be called a Route. A Route is made out of distinct RouteSegments.
 * RouteSegments are stretches of road that are not interrupted by crossroads or settlements. Every little piece of road
 * inside a RouteSegment will be called a StreetNode and every possible or build settlement will be called a
 * BuildingNode. So StreetNodes form RouteSegments, and RouteSegments form Routes. The Longest Route will be called the
 * Longest TradeRoute.
 * </p>
 *
 * @author Pieter Vogt
 * @since 2021-04-02
 */
public class MapGraph implements Serializable {
    //FIELDS
    private final HashSet<StreetNode> streetNodeSet = new HashSet<>();
    private final HashSet<BuildingNode> buildingNodeSet = new HashSet<>();
    private final HashSet<Hexagon> hexagonSet = new HashSet<>();

    //CONSTRUCTOR

    /**
     * Creates the interconnected Grid of StreetNodes and BuildingNodes.
     *
     * @author Pieter Vogt
     * @since 2021-04-02
     */
    public MapGraph(String mapTypeToGenerate) {
        initializeMapGraph(mapTypeToGenerate);
    }

    public static void main(String[] args) {
        MapGraph mapGraph = new MapGraph("");
    }
    //GETTER SETTER

    public Set<StreetNode> getStreetNodeSet() {
        return streetNodeSet;
    }

    public Set<BuildingNode> getBuildingNodeSet() {
        return buildingNodeSet;
    }

    public Set<Hexagon> getHexagonSet() {
        return hexagonSet;
    }

    //METHODS

    /**
     * Initializes MapGraph
     * <p>Creates the Hexagons, BuildingNodes and StreetNodes, interconnects them and updates the Lists to store
     * them.</p>
     *
     * @param mapTypeToGenerate The standard-case is to generate a MapGraph for a standard-playfield. So if you wish to
     *                          generate one, just parse "".
     *
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
            default: {
                //Generating the first Hexagon in the middle.
                Hexagon middle = new Hexagon("middle");

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


                //einfgügen der dicetoken und terraintypes

                //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;

                middle.configureTerrainTypeAndDiceToken(6, 0);
                middle.getHexLeft().configureTerrainTypeAndDiceToken(4, 9);
                middle.getHexBottomLeft().configureTerrainTypeAndDiceToken(5, 11);
                middle.getHexBottomRight().configureTerrainTypeAndDiceToken(1, 3);
                middle.getHexRight().configureTerrainTypeAndDiceToken(5, 6);
                middle.getHexTopRight().configureTerrainTypeAndDiceToken(2, 5);
                middle.getHexTopLeft().configureTerrainTypeAndDiceToken(5, 4);

                middle.getHexLeft().getHexTopLeft().configureTerrainTypeAndDiceToken(2, 10);
                middle.getHexLeft().getHexLeft().configureTerrainTypeAndDiceToken(4, 8);

                middle.getHexBottomLeft().getHexLeft().configureTerrainTypeAndDiceToken(3, 4);
                middle.getHexBottomLeft().getHexBottomLeft().configureTerrainTypeAndDiceToken(4, 11);

                middle.getHexBottomRight().getHexBottomLeft().configureTerrainTypeAndDiceToken(3, 12);
                middle.getHexBottomRight().getHexBottomRight().configureTerrainTypeAndDiceToken(2, 9);

                middle.getHexRight().getHexBottomRight().configureTerrainTypeAndDiceToken(1, 10);
                middle.getHexRight().getHexRight().configureTerrainTypeAndDiceToken(3, 8);

                middle.getHexTopRight().getHexRight().configureTerrainTypeAndDiceToken(3, 3);
                middle.getHexTopRight().getHexTopRight().configureTerrainTypeAndDiceToken(1, 6);

                middle.getHexTopLeft().getHexTopRight().configureTerrainTypeAndDiceToken(2, 2);
                middle.getHexTopLeft().getHexTopLeft().configureTerrainTypeAndDiceToken(1, 5);


            }
        }
    }

    /**
     * Returns the index of the player with the longest road.
     * <p>The first player, that has a Route of at least 5 StreetNode-objects, gets awarded the "Longest
     * Traderoute"-Flag.
     * </p>
     *
     * @return The integer representing the index of the player with the longest road, inside the ArrayList of players
     * in the GameDTO.
     * @author Pieter Vogt
     * @see de.uol.swp.common.game.dto.GameDTO
     * @since 2021-04-02
     */
    public int returnPlayerWithLongestRoad() {
        //TODO:This needs to be implemented in a separate ticket some time soon.
        return 666; //nonsense-value
    }

    /**
     * Enables direct, encapsulated (blackbox-like) access to every StreetNode.
     * <p>This is used to directly access a StreetNode to e.g. build a road. This will return null if the StreetNode
     * you are looking for does not exist.</p>
     *
     * @param hexagon        The list of relative vectors in String-form to reach the desired hexagon.
     * @param positionOfNode The relative position of the desired node relative to the argument-hexagon.
     *
     * @return A StreetNode at a position, determined by the parsed arguments.
     * @author Pieter Vogt
     * @since 2021-04-09
     */
    public StreetNode accessStreetNode(ArrayList<String> hexagon, String positionOfNode) {
        for (Hexagon h : hexagonSet) {
            if (h.getSelfPosition().equals(hexagon)) { //If the argument List of Strings matches the List of Strings of a given hexagon, we found the right object.
                switch (positionOfNode) {
                    case "topLeft":
                        return h.getStreetTopLeft();
                    case "topRight":
                        return h.getStreetTopRight();
                    case "left":
                        return h.getStreetLeft();
                    case "right":
                        return h.getStreetRight();
                    case "bottomLeft":
                        return h.getStreetBottomLeft();
                    case "bottomRight":
                        return h.getStreetBottomRight();
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    /**
     * Enables direct, encapsulated (blackbox-like) access to every BuildingNode.
     * <p>This is used to directly access a BuildingNode to e.g. build a road. This will return null if the
     * BuildingNode you are looking for does not exist. </p>
     *
     * @param hexagon        The List of relative vectors in String-form to reach the desired hexagon.
     * @param positionOfNode The relative position of the desired node relative to the argument-hexagon.
     *
     * @return A BuildingNode at a position, determined by the parsed arguments.
     * @author Pieter Vogt
     * @since 2021-04-09
     */
    public BuildingNode accessBuildingNode(ArrayList<String> hexagon, String positionOfNode) {
        for (Hexagon h : hexagonSet) {
            if (h.getSelfPosition().equals(hexagon)) { //If the argument List of Strings matches the List of Strings of a given hexagon, we found the right object.
                switch (positionOfNode) {
                    case "topLeft":
                        return h.getBuildingTopLeft();
                    case "topRight":
                        return h.getBuildingTopRight();
                    case "top":
                        return h.getBuildingTop();
                    case "bottom":
                        return h.getBuildingBottom();
                    case "bottomLeft":
                        return h.getBuildingBottomLeft();
                    case "bottomRight":
                        return h.getBuildingBottomRight();
                    default:
                        return null;
                }
            }
        }
        return null;
    }

    //NESTED CLASSES

    public abstract class MapGraphNode implements Serializable {

    }

    /**
     * Holds all the data needed to represent streets and the interactions made with them.
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    public class StreetNode extends MapGraphNode {

        //FIELDS

        private String positionToParent;
        private final HashSet<BuildingNode> connectedBuildingNodes = new HashSet<>();
        private int occupiedByPlayer;
        private Hexagon parent;

        //CONSTRUCTOR

        public StreetNode(String position, Hexagon h) {
            this.positionToParent = position;
            this.parent = h;
        }

        //GETTER SETTER

        public int getOccupiedByPlayer() {
            return occupiedByPlayer;
        }

        public void setOccupiedByPlayer(int occupiedByPlayer) {
            this.occupiedByPlayer = occupiedByPlayer;
        }

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

        public Hexagon getParent() {
            return parent;
        }

        public void setParent(Hexagon parent) {
            this.parent = parent;
        }

        public String getPositionToParent() {
            return positionToParent;
        }
    }

    /**
     * Holds all the data needed to represent Buildingspots and the interactions made with them.
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    public class BuildingNode extends MapGraphNode {

        //FIELDS

        private String positionToParent;
        private final HashSet<StreetNode> connectedStreetNodes = new HashSet<>();
        private int typeOfHarbor;
        private int occupiedByPlayer;
        private Hexagon parent;
        //CONSTRUCTOR

        /**
         * Creates a new BuildingNode
         *
         * <p>The type of harbor refers to this:
         * 0 = no harbor, 1 = 2:1 Sheep, 2 = 2:1 Clay, 3 = 2:1 Wood, 4 = 2:1 Grain, 5 = 2:1 Ore, 6 = 3:1 Any
         * </p>
         */
        public BuildingNode(String position, Hexagon h) {
            this.positionToParent = position;
            this.parent = h;

        }

        //GETTER SETTER

        public HashSet<StreetNode> getConnectedStreetNodes() {
            return connectedStreetNodes;
        }

        public int getOccupiedByPlayer() {
            return occupiedByPlayer;
        }

        public void setOccupiedByPlayer(int occupiedByPlayer) {
            this.occupiedByPlayer = occupiedByPlayer;
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

        public Hexagon getParent() {
            return parent;
        }

        public void setParent(Hexagon parent) {
            this.parent = parent;
        }

        public String getPositionToParent() {
            return positionToParent;
        }
    }

    /**
     * Represents the logical structure of one hexagonal cardboard-piece to build the Playfield of.
     * <p>This class represents the logic of the pathfinding- and the building-system. It houses the pointers to the
     * building-spots and is aware of its neighbour-hexagonals. With this, we are able to send specific commands to
     * specific places of the playfield. In the future it may hold all the data representing a TerrainField, not just
     * the logical aspect.</p>
     *
     * @author Pieter Vogt
     * @since 2021-04-09
     */
    public class Hexagon implements Serializable {

        //FIELDS

        private final List<String> selfPosition = new ArrayList<>(); //IMPORTANT! If fiddled with in the future: This must never become any sort of Set,because we need to be able to store duplicates!
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

        private List<BuildingNode> buildingNodes = new ArrayList<>();
        private List<StreetNode> streetNodes = new ArrayList<>();
        private List<Hexagon> hexagons = new ArrayList<>();

        //CONSTRUCTOR

        /**
         * Constructor for the first Hexagon.
         * <p>This Constructor does not parse any argument about ancestor-Hexagons. Therefore it can only be used for
         * the first Hexagon to be placed.</p>
         *
         * @param position The directional vector from the ancestor-Hexagon to this one.
         *
         * @author Pieter Vogt
         * @since 2021-04-10
         */
        public Hexagon(String position) {
            selfPosition.add(position);
            hexagonSet.add(this);
        }

        /**
         * Constructor for all but the first Hexagon.
         * <p>This Constructor parses the information about where the ancestor-Hexagon is located, relative to the
         * first Hexagon. Therefore it must be used for all but the first Hexagon to be placed, because every Hexagon
         * but the first has an ancestor.</p>
         *
         * @param position     The directional vector from the ancestor-Hexagon to this one.
         * @param positionList The List of positional vectors that describes the position of the ancestor-Hexagon.
         *
         * @author Pieter Vogt
         * @since 2021-04-10
         */
        public Hexagon(String position, List<String> positionList) {
            this.selfPosition.addAll(positionList);
            this.selfPosition.add(position);
            hexagonSet.add(this);
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

        public void setStreetLeft(StreetNode streetLeft) {
            this.streetLeft = streetLeft;
        }

        public StreetNode getStreetBottomLeft() {
            return streetBottomLeft;
        }

        public void setStreetBottomLeft(StreetNode streetBottomLeft) {
            this.streetBottomLeft = streetBottomLeft;
        }

        public StreetNode getStreetBottomRight() {
            return streetBottomRight;
        }

        public void setStreetBottomRight(StreetNode streetBottomRight) {
            this.streetBottomRight = streetBottomRight;
        }

        public StreetNode getStreetRight() {
            return streetRight;
        }

        public void setStreetRight(StreetNode streetRight) {
            this.streetRight = streetRight;
        }

        public StreetNode getStreetTopRight() {
            return streetTopRight;
        }

        public void setStreetTopRight(StreetNode streetTopRight) {
            this.streetTopRight = streetTopRight;
        }

        public StreetNode getStreetTopLeft() {
            return streetTopLeft;
        }

        public void setStreetTopLeft(StreetNode streetTopLeft) {
            this.streetTopLeft = streetTopLeft;
        }

        public BuildingNode getBuildingTopLeft() {
            return buildingTopLeft;
        }

        public void setBuildingTopLeft(BuildingNode buildingTopLeft) {
            this.buildingTopLeft = buildingTopLeft;
        }

        public BuildingNode getBuildingBottomLeft() {
            return buildingBottomLeft;
        }

        public void setBuildingBottomLeft(BuildingNode buildingBottomLeft) {
            this.buildingBottomLeft = buildingBottomLeft;
        }

        public BuildingNode getBuildingBottom() {
            return buildingBottom;
        }

        public void setBuildingBottom(BuildingNode buildingBottom) {
            this.buildingBottom = buildingBottom;
        }

        public BuildingNode getBuildingBottomRight() {
            return buildingBottomRight;
        }

        public void setBuildingBottomRight(BuildingNode buildingBottomRight) {
            this.buildingBottomRight = buildingBottomRight;
        }

        public BuildingNode getBuildingTopRight() {
            return buildingTopRight;
        }

        public void setBuildingTopRight(BuildingNode buildingTopRight) {
            this.buildingTopRight = buildingTopRight;
        }

        public BuildingNode getBuildingTop() {
            return buildingTop;
        }

        public void setBuildingTop(BuildingNode buildingTop) {
            this.buildingTop = buildingTop;
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

        public List<BuildingNode> getBuildingNodes() {
            return buildingNodes;
        }

        public void setBuildingNodes(List<BuildingNode> buildingNodes) {
            this.buildingNodes = buildingNodes;
        }

        public List<StreetNode> getStreetNodes() {
            return streetNodes;
        }

        public void setStreetNodes(List<StreetNode> streetNodes) {
            this.streetNodes = streetNodes;
        }

        public List<Hexagon> getHexagons() {
            return hexagons;
        }

        public void setHexagons(List<Hexagon> hexagons) {
            this.hexagons = hexagons;
        }

        public List<String> getSelfPosition() {
            return selfPosition;
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
            hexagonSet.addAll(hexagons);
        }

        /**
         * Fills the empty slots with nodes.
         * <p>This is used, to quickly generate a fully occupied hexagon to dock to. First it checks if the respective
         * NodeSpot is empty. If so, it fills it with a new one. Because of that, we can call this function with already
         * partially occupied Hexagons without overwriting Nodes that might already been shared between multiple
         * Hexagons. This is especially important when expanding the inner ring of Hexagons a second time to get the
         * full Standard-Playfield.</p>
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void generateNodes() {
            //First checking streetnodes...
            if (this.streetTopLeft == null && hexTopLeft != null) {
                if (hexTopLeft.getStreetBottomRight() == null) {
                    this.streetTopLeft = new StreetNode("topLeft", this);
                } else {
                    this.streetTopLeft = hexTopLeft.getStreetBottomRight();
                }
            } else {
                this.streetTopLeft = new StreetNode("topLeft", this);
            }


            if (this.streetTopRight == null && hexTopRight != null) {

                if (hexTopRight.getStreetBottomLeft() == null) {
                    this.streetTopRight = new StreetNode("topRight", this);
                } else {
                    this.streetTopRight = hexTopRight.getStreetBottomLeft();
                }
            } else {
                this.streetTopRight = new StreetNode("topRight", this);
            }


            if (streetLeft == null && hexLeft != null) {

                if (hexLeft.getStreetRight() == null) {
                    this.streetLeft = new StreetNode("left", this);
                } else {
                    this.streetLeft = hexLeft.getStreetRight();
                }
            } else {
                this.streetLeft = new StreetNode("left", this);
            }


            if (streetRight == null && hexRight != null) {

                if (hexRight.getStreetLeft() == null) {
                    this.streetRight = new StreetNode("right", this);
                } else {
                    this.streetRight = hexRight.getStreetLeft();
                }
            } else {
                this.streetRight = new StreetNode("right", this);
            }


            if (streetBottomLeft == null && hexBottomLeft != null) {

                if (hexBottomLeft.getStreetTopRight() == null) {
                    this.streetBottomLeft = new StreetNode("bottomLeft", this);
                } else {
                    this.streetBottomLeft = hexBottomLeft.getStreetTopRight();
                }
            } else {
                this.streetBottomLeft = new StreetNode("bottomLeft", this);
            }


            if (streetBottomRight == null && hexBottomRight != null) {

                if (hexBottomRight.getStreetTopLeft() == null) {
                    this.streetBottomRight = new StreetNode("bottomRight", this);
                } else {
                    this.streetBottomRight = hexBottomRight.getStreetTopLeft();
                }
            } else {
                this.streetBottomRight = new StreetNode("bottomRight", this);
            }


            //... then checking BuildingNodes.
            if (buildingTop == null && (hexTopLeft != null || hexTopRight != null)) {
                if (hexTopLeft != null && hexTopRight == null) {
                    if (hexTopLeft.getBuildingBottomRight() == null) {
                        this.buildingTop = new BuildingNode("top", this);
                    } else {
                        this.buildingTop = hexTopLeft.getBuildingBottomRight();
                    }
                } else if (hexTopLeft == null && hexTopRight != null) {
                    if (hexTopRight.getBuildingBottomLeft() == null) {
                        this.buildingTop = new BuildingNode("top", this);
                    } else {
                        this.buildingTop = hexTopRight.getBuildingBottomLeft();
                    }
                } else if (hexTopLeft != null) {
                    if (hexTopLeft.getBuildingBottomRight() != null) {
                        this.buildingTop = hexTopLeft.getBuildingBottomRight();
                    } else if (hexTopRight.getBuildingBottomLeft() != null) {
                        this.buildingTop = hexTopRight.getBuildingBottomLeft();
                    } else {
                        this.buildingTop = new BuildingNode("top", this);
                    }
                }
            } else {
                this.buildingTop = new BuildingNode("top", this);
            }


            //Für buildingTopLeft, vergl. mit hexLeft und hexTopLeft
            if (buildingTopLeft == null && (hexLeft != null || hexTopLeft != null)) {
                if (hexLeft != null && hexTopLeft == null) {
                    if (hexLeft.getBuildingTopRight() == null) {
                        this.buildingTopLeft = new BuildingNode("topLeft", this);
                    } else {
                        this.buildingTopLeft = hexLeft.getBuildingTopRight();
                    }
                } else if (hexLeft == null && hexTopLeft != null) {
                    if (hexTopLeft.getBuildingBottom() == null) {
                        this.buildingTopLeft = new BuildingNode("topLeft", this);
                    } else {
                        this.buildingTopLeft = hexTopLeft.getBuildingBottom();
                    }
                } else if (hexLeft != null) {
                    if (hexLeft.getBuildingTopRight() != null) {
                        this.buildingTopLeft = hexLeft.getBuildingTopRight();
                    } else if (hexTopLeft.getBuildingBottom() != null) {
                        this.buildingTopLeft = hexTopLeft.getBuildingBottom();
                    } else {
                        this.buildingTopLeft = new BuildingNode("topLeft", this);
                    }
                }
            } else {
                this.buildingTopLeft = new BuildingNode("topLeft", this);
            }

            //Für buildingTopRight, vergl. mit hexRight und hexTopRight
            if (buildingTopRight == null && (hexRight != null || hexTopRight != null)) {
                if (hexRight != null && hexTopRight == null) {
                    if (hexRight.getBuildingTopLeft() == null) {
                        this.buildingTopRight = new BuildingNode("topRight", this);
                    } else {
                        this.buildingTopRight = hexRight.getBuildingTopLeft();
                    }
                } else if (hexRight == null && hexTopRight != null) {
                    if (hexTopRight.getBuildingBottom() == null) {
                        this.buildingTopRight = new BuildingNode("topRight", this);
                    } else {
                        this.buildingTopRight = hexTopRight.getBuildingBottom();
                    }
                } else if (hexRight != null) {
                    if (hexRight.getBuildingTopLeft() != null) {
                        this.buildingTopRight = hexRight.getBuildingTopLeft();
                    } else if (hexTopRight.getBuildingBottom() != null) {
                        this.buildingTopRight = hexTopRight.getBuildingBottom();
                    } else {
                        this.buildingTopRight = new BuildingNode("topRight", this);
                    }
                }
            } else {
                this.buildingTopRight = new BuildingNode("topRight", this);
            }

            //Für buildingBottom, vergl. mit hexBottomLeft und hexBottomRight
            if (buildingBottom == null && (hexBottomLeft != null || hexBottomRight != null)) {
                if (hexBottomLeft != null && hexBottomRight == null) {
                    if (hexBottomLeft.getBuildingTopRight() == null) {
                        this.buildingBottom = new BuildingNode("bottom", this);
                    } else {
                        this.buildingBottom = hexBottomLeft.getBuildingTopRight();
                    }
                } else if (hexBottomLeft == null && hexBottomRight != null) {
                    if (hexBottomRight.getBuildingTopLeft() == null) {
                        this.buildingBottom = new BuildingNode("bottom", this);
                    } else {
                        this.buildingBottom = hexBottomRight.getBuildingTopLeft();
                    }
                } else if (hexBottomLeft != null) {
                    if (hexBottomLeft.getBuildingTopRight() != null) {
                        this.buildingBottom = hexBottomLeft.getBuildingTopRight();
                    } else if (hexBottomRight.getBuildingTopLeft() != null) {
                        this.buildingBottom = hexBottomRight.getBuildingTopLeft();
                    } else {
                        this.buildingBottom = new BuildingNode("bottom", this);
                    }
                }
            } else {
                this.buildingBottom = new BuildingNode("bottom", this);
            }

            //Für buildingBottomLeft, vergl. mit hexLeft und hexBottomLeft
            if (buildingBottomLeft == null && (hexLeft != null || hexBottomLeft != null)) {
                if (hexLeft != null && hexBottomLeft == null) {
                    if (hexLeft.getBuildingBottomRight() == null) {
                        this.buildingBottomLeft = new BuildingNode("bottomLeft", this);
                    } else {
                        this.buildingBottomLeft = hexLeft.getBuildingBottomRight();
                    }
                } else if (hexLeft == null && hexBottomLeft != null) {
                    if (hexBottomLeft.getBuildingTop() == null) {
                        this.buildingBottomLeft = new BuildingNode("bottomLeft", this);
                    } else {
                        this.buildingBottomLeft = hexBottomLeft.getBuildingTop();
                    }
                } else if (hexLeft != null) {
                    if (hexLeft.getBuildingBottomRight() != null) {
                        this.buildingBottomLeft = hexLeft.getBuildingBottomRight();
                    } else if (hexBottomLeft.getBuildingTop() != null) {
                        this.buildingBottomLeft = hexBottomLeft.getBuildingTop();
                    } else {
                        this.buildingBottomLeft = new BuildingNode("bottomLeft", this);
                    }
                }
            } else {
                this.buildingBottomLeft = new BuildingNode("bottomLeft", this);
            }

            //Für buildingBottomRight, vergl. mit hexBottomRight und hexRight
            if (buildingBottomRight == null && (hexBottomRight != null || hexRight != null)) {
                if (hexBottomRight != null && hexRight == null) {
                    if (hexBottomRight.getBuildingTop() == null) {
                        this.buildingBottomRight = new BuildingNode("bottomRight", this);
                    } else {
                        this.buildingBottomRight = hexBottomRight.getBuildingTop();
                    }
                } else if (hexBottomRight == null && hexRight != null) {
                    if (hexRight.getBuildingBottomLeft() == null) {
                        this.buildingBottomRight = new BuildingNode("bottomRight", this);
                    } else {
                        this.buildingBottomRight = hexRight.getBuildingBottomLeft();
                    }
                } else if (hexBottomRight != null) {
                    if (hexBottomRight.getBuildingTop() != null) {
                        this.buildingBottomRight = hexBottomRight.getBuildingTop();
                    } else if (hexRight.getBuildingBottomLeft() != null) {
                        this.buildingBottomRight = hexRight.getBuildingBottomLeft();
                    } else {
                        this.buildingBottomRight = new BuildingNode("bottomRight", this);
                    }
                }
            } else {
                this.buildingBottomRight = new BuildingNode("bottomRight", this);
            }

            updateNodeLists();
            streetNodeSet.addAll(streetNodes);
            buildingNodeSet.addAll(buildingNodes);
        }

        public void generateNodesMiddle() {
            this.streetTopLeft = new StreetNode("topLeft", this);
            this.streetBottomLeft = new StreetNode("bottomLeft", this);
            this.streetTopRight = new StreetNode("topRight", this);
            this.streetLeft = new StreetNode("left", this);
            this.streetRight = new StreetNode("right", this);
            this.streetBottomRight = new StreetNode("bottomRight", this);

            this.buildingTopLeft = new BuildingNode("topLeft", this);
            this.buildingTopRight = new BuildingNode("topRight", this);
            this.buildingBottomLeft = new BuildingNode("bottomLeft", this);
            this.buildingBottomRight = new BuildingNode("bottomRight", this);
            this.buildingTop = new BuildingNode("top", this);
            this.buildingBottom = new BuildingNode("bottom", this);

            updateNodeLists();
            streetNodeSet.addAll(streetNodes);
            buildingNodeSet.addAll(buildingNodes);
        }

        /**
         * Generates nodes for all neighbour-Hexagons.
         *
         * @author Pieter Vogt
         * @since 2021-04-10
         */
        private void generateNeighbourNodes() {
            for (Hexagon h : hexagons) {
                h.generateNodes();
            }
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

                streetRight.addBuildingNode(buildingTopLeft);
                streetRight.addBuildingNode(buildingTopRight);

                streetBottomLeft.addBuildingNode(buildingBottom);
                streetBottomLeft.addBuildingNode(buildingBottomLeft);

                streetBottomRight.addBuildingNode(buildingBottom);
                streetBottomRight.addBuildingNode(buildingBottomRight);

            } catch (ListFullException e) {
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
            } catch (ListFullException e) {
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
        public void dockRight() {
            if (hexRight == null) {
                this.hexRight = new Hexagon("right", selfPosition);
                hexRight.setHexLeft(this);
            }
        }

        /**
         * Docks calling hexagon to its left Hexagon. If the left Hexagon is still null, the method generates a new one
         * there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void dockLeft() {
            if (hexLeft == null) {
                this.hexLeft = new Hexagon("left", selfPosition);
                hexLeft.setHexRight(this);
            }
        }

        /**
         * Docks calling hexagon to its top-right Hexagon. If the top-right Hexagon is still null, the method generates
         * a new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void dockTopRight() {
            if (hexTopRight == null) {
                this.hexTopRight = new Hexagon("topRight", selfPosition);
                hexTopRight.setHexBottomLeft(this);
            }
        }

        /**
         * Docks calling hexagon to its bottom-right Hexagon. If the bottom-right Hexagon is still null, the method
         * generates a new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void dockBottomRight() {
            if (hexBottomRight == null) {
                this.hexBottomRight = new Hexagon("bottomRight", selfPosition);
                hexBottomRight.setHexTopLeft(this);
            }
        }

        /**
         * Docks calling hexagon to its top-left Hexagon. If the top-left Hexagon is still null, the method generates a
         * new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void dockTopLeft() {
            if (hexTopLeft == null) {
                this.hexTopLeft = new Hexagon("topLeft", selfPosition);
                hexTopLeft.setHexBottomRight(this);
            }
        }

        /**
         * Docks calling hexagon to its bottom-left Hexagon. If the bottom-left Hexagon is still null, the method
         * generates a new one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void dockBottomLeft() {
            if (hexBottomLeft == null) {
                this.hexBottomLeft = new Hexagon("bottomLeft", selfPosition);
                hexBottomLeft.setHexTopRight(this);
            }
        }
    }
}


/*        public void dock(Hexagon h1, Hexagon h2, String position, ArrayList<String> selfPosition){
            if(h1==null){
                h2 = new Hexagon(position,selfPosition);
                h1.set
            }
        }*/