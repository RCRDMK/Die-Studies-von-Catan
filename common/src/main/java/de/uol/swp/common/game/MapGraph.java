package de.uol.swp.common.game;

import java.util.ArrayList;
import java.util.List;

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
 * @see RouteSegment
 * @see StreetNode
 * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
 * @since 2021-04-02
 */
public class MapGraph {

    //Constructor

    /**
     * Creates the interconnected Grid of StreetNodes and BuildingNodes.
     *
     * @author Pieter Vogt
     * @since 2021-04-02
     */


    //Methods

    /**
     * Initializes MapGraph
     * <p>Creates the BuildingNodes and StreetNodes and connects them.</p>
     */
    public void initializeMapGraph() {
        //TODO: Hier weiterarbeiten.
        Hexagon middle = new Hexagon();
        middle.expand();
    }

    /**
     * Returns the index of the player with the longest road.
     * <p>The first player, that has a Route of at least 5 StreetNode-objects, gets awarded the "Longest
     * Traderoute"-Flag.
     * </p>
     *
     * @return
     * @author Pieter Vogt
     * @since 2021-04-02
     */
    public int playerWithLongestRoad() {
        /*
         * Might be smart to make this work recursively like this:
         * if ( city or crossroad ) { create new RouteSegments for all unvisited StreetNodes }
         * else if ( end of road ahead ) { add RouteSegment to all Source-Segments and return value }
         * else ( no crossroad ahead ) { add StreetNode to RouteSegment }

         * Implement like this:
         * fetch every settlement of a player.
         * Create a RouteSegment for every StreetNode connected to a city.
         * go outwards for 1 StreetNode at a time.
         * if RouteSegment reaches city or crossroad, create new RouteSegment for each outgoing-, not marked as visited StreetNode.
         * if Streetnode was used by RouteSegment n, mark StreetNode as visited by n.
         * (could be possible for a StreetNode to be part of a longer Route than RouteSegment n.)
         * do not use StreetNodes marked as visited by RouteSegment n again, for n in the future.
         * if no unused, reachable StreetNode exists, stop and summarize Route.
         * if all branches stopped, compare branch-lengths and return highest value.
         * */
        return 0;
    }

    //Nested Classes


    /**
     * Holds all the data needed to represent streets and the interactions made with them.
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    private class StreetNode {

        //FIELDS

        private final BuildingNode[] connectedBuildingNodes = new BuildingNode[2];
        private int occupiedByPlayer;

        //CONSTRUCTOR

        public StreetNode() {

        }

        //GETTER SETTER

        public int getOccupiedByPlayer() {
            return occupiedByPlayer;
        }

        public void setOccupiedByPlayer(int occupiedByPlayer) {
            this.occupiedByPlayer = occupiedByPlayer;
        }

        public BuildingNode[] getConnectedBuildingNodes() {
            return connectedBuildingNodes;
        }
    }

    /**
     * Holds all the data needed to represent Buildingspots and the interactions made with them.
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    private class BuildingNode {

        //FIELDS

        private final StreetNode[] connectedStreetNodes = new StreetNode[3];
        private int typeOfHarbor;
        private int occupiedByPlayer;

        //CONSTRUCTOR

        /**
         * Creates a new BuildingNode
         *
         * <p>The type of harbor refers to this:
         * 0 = no harbor, 1 = 2:1 Sheep, 2 = 2:1 Clay, 3 = 2:1 Wood, 4 = 2:1 Grain, 5 = 2:1 Ore, 6 = 3:1 Any
         * </p>
         */
        public BuildingNode() {
        }

        //GETTER SETTER

        public StreetNode[] getConnectedStreetNodes() {
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
    }

    /**
     * Helper-Class for traversing the MapGraph recursively.
     * <p>Used for traversing the MapGraph and counting lengths and noting Member-StreetNodes of trade-routes while
     * calculating longest trade-routes. Think of it as "straight line" of roads between either a settlement or a
     * crossroad. A trade-route that crosses multiple crossroads and/or settlements is made up of consecutive
     * RouteSegments.</p>
     *
     * @author Pieter Vogt
     * @see <a href=>https://confluence.swl.informatik.uni-oldenburg.de/pages/editpage.action?pageId=263979012</a>
     * @since 2021-04-02
     */
    private class RouteSegment {
        //Fields
        private final List<StreetNode> StreetNodesVisited = new ArrayList<>();
        private final int identifier;

        //Constructor
        public RouteSegment(StreetNode streetNode, int identifier) {
            this.StreetNodesVisited.add(streetNode);
            this.identifier = identifier;
        }

        //GETTER SETTER

        public List<StreetNode> getStreetNodesVisited() {
            return StreetNodesVisited;
        }

        public int getIdentifier() {
            return identifier;
        }

        //METHODS

        public void addStreetNode(StreetNode streetNode) {
            this.StreetNodesVisited.add(streetNode);
        }

    }

    private class Hexagon {

        //FIELDS

        Hexagon hexTopLeft;
        Hexagon hexTopRight;
        Hexagon hexLeft;
        Hexagon hexRight;
        Hexagon hexBottomLeft;
        Hexagon hexBottomRight;

        StreetNode streetLeft;
        StreetNode streetBottomLeft;
        StreetNode streetBottomRight;
        StreetNode streetRight;
        StreetNode streetTopRight;
        StreetNode streetTopLeft;

        BuildingNode buildingTopLeft;
        BuildingNode buildingBottomLeft;
        BuildingNode buildingBottom;
        BuildingNode buildingBottomRight;
        BuildingNode buildingTopRight;
        BuildingNode buildingTop;

        //GETTER SETTER

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
            buildingBottomLeft = buildingBottomLeft;
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
// METHODS

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
            if (this.streetTopLeft == null) {
                this.streetTopLeft = new StreetNode();
            }
            if (this.streetTopRight == null) {
                this.streetTopRight = new StreetNode();
            }
            if (streetLeft == null) {
                this.streetLeft = new StreetNode();
            }
            if (streetRight == null) {
                this.streetRight = new StreetNode();
            }
            if (streetBottomLeft == null) {
                this.streetBottomLeft = new StreetNode();
            }
            if (streetBottomLeft == null) {
                this.streetBottomRight = new StreetNode();
            }

            //... then checking BuildingNodes.
            if (buildingTop == null) {
                this.buildingTop = new BuildingNode();
            }
            if (buildingTopLeft == null) {
                this.buildingTopLeft = new BuildingNode();
            }
            if (buildingTopRight == null) {
                this.buildingTopRight = new BuildingNode();
            }
            if (buildingBottom == null) {
                this.buildingBottom = new BuildingNode();
            }
            if (buildingBottomLeft == null) {
                this.buildingBottomLeft = new BuildingNode();
            }
            if (buildingBottomRight == null) {
                this.buildingBottomRight = new BuildingNode();
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

            //Fill all NodeSpots with its correct Nodes.
            generateNodes();

            //If needed, generate - then dock other Hexagons to the corresponding sides of the method.
            dockBottomLeft();
            dockLeft();
            dockRight();
            dockTopLeft();
            dockTopRight();
            dockBottomRight();
        }

        /**
         * Interconnects the outer ring around the calling Hexagon.
         * <p>This is used, to interconnect the Hexagons that are around the calling hexagon with themselves, NOT THE
         * CALLING HEXAGON ITSELF. After expanding a Hexagon, this method needs to be called. For example it takes the
         * Hexagon to the right and connects its upper-left Nodes to the Hexagon to its upper left side.</p>
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void interconnectNeighbours() {
            if (hexTopLeft != null) {
                hexTopLeft.setHexBottomLeft(hexLeft);
                hexTopLeft.setHexRight(hexTopRight);
            }

            if (hexLeft != null) {
                hexLeft.setHexTopRight(hexTopLeft);
                hexLeft.setHexBottomRight(hexBottomLeft);
            }

            if (hexBottomLeft != null) {
                hexBottomLeft.setHexTopLeft(hexLeft);
                hexBottomLeft.setHexRight(hexBottomRight);
            }

            if (hexBottomRight != null) {
                hexBottomRight.setHexLeft(hexBottomLeft);
                hexBottomRight.setHexTopRight(hexRight);
            }

            if (hexRight != null) {
                hexRight.setHexBottomLeft(hexBottomRight);
                hexRight.setHexTopLeft(hexTopRight);
            }

            if (hexTopRight != null) {
                hexTopRight.setHexBottomRight(hexRight);
                hexTopRight.setHexLeft(hexTopLeft);
            }
        }

        //GENERATOR-METHODS

        /**
         * Docks calling hexagon to its right Hexagon. If the right Hexagon is still null, the method generates a new
         * one there.
         *
         * @author Pieter Vogt
         * @since 2021-04-08
         */
        public void dockRight() {
            if (hexRight == null) {
                this.hexRight = new Hexagon();
            }

            hexRight.setStreetLeft(streetRight);
            hexRight.setBuildingTopLeft(buildingTopRight);
            hexRight.setBuildingBottomLeft(buildingBottomRight);
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
                this.hexLeft = new Hexagon();
            }

            hexLeft.setStreetRight(streetLeft);
            hexLeft.setBuildingTopRight(buildingTopLeft);
            hexLeft.setBuildingBottomRight(buildingBottomLeft);
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
                this.hexTopRight = new Hexagon();
            }

            hexTopRight.setBuildingBottomLeft(buildingTop);
            hexTopRight.setBuildingBottom(buildingTopRight);
            hexTopRight.setStreetBottomLeft(streetTopRight);
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
                this.hexBottomRight = new Hexagon();
            }

            hexBottomRight.setBuildingTop(buildingBottomRight);
            hexBottomRight.setBuildingTopLeft(buildingBottom);
            hexBottomRight.setStreetTopLeft(streetBottomRight);
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
                this.hexTopLeft = new Hexagon();
            }

            hexTopLeft.setBuildingBottom(buildingTopLeft);
            hexTopLeft.setBuildingBottomRight(buildingTop);
            hexTopLeft.setStreetBottomRight(streetTopLeft);
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
                this.hexBottomLeft = new Hexagon();
            }

            hexBottomLeft.setBuildingTop(buildingBottomLeft);
            hexBottomLeft.setBuildingTopRight(buildingBottom);
            hexBottomLeft.setStreetTopRight(streetBottomLeft);
        }
    }
}

