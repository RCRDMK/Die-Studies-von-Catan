package de.uol.swp.server.HelperObjects;

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
    //Fields

    //Constructor

    /**
     * Creates the interconnected Grid of StreetNodes and BuildingNodes.
     *
     * @author Pieter Vogt
     * @since 2021-04-02
     */
    public MapGraph() {

    }

    //Methods

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
        //Fields
        private final int position;
        private final BuildingNode[] connectedBuildingNodes = new BuildingNode[2];
        private int occupiedByPlayer;

        {
        }
        //Constructor

        public StreetNode(int position, BuildingNode node1, BuildingNode node2) {
            this.position = position;
            this.connectedBuildingNodes[0] = node1;
            this.connectedBuildingNodes[1] = node2;
        }

        //Methods


        public int getPosition() {
            return position;
        }

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
        //Methods


        public List<StreetNode> getStreetNodesVisited() {
            return StreetNodesVisited;
        }

        public int getIdentifier() {
            return identifier;
        }

        public void addStreetNode(StreetNode streetNode) {
            this.StreetNodesVisited.add(streetNode);
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
        //Fields
        private final char position;
        private final StreetNode[] connectedStreetNodes = new StreetNode[3];
        private final int typeOfHarbor;
        private int occupiedByPlayer;
        private boolean settlementUpgraded = false;

        /**
         * Creates a new BuildingNode
         *
         * <p>The type of harbor refers to this:
         * 0 = no harbor, 1 = 2:1 Sheep, 2 = 2:1 Clay, 3 = 2:1 Wood, 4 = 2:1 Grain, 5 = 2:1 Ore, 6 = 3:1 Any
         * </p>
         *
         * @param position
         * @param typeOfHarbor
         */
        //Constructor
        public BuildingNode(char position, int typeOfHarbor) {
            this.position = position;
            this.typeOfHarbor = typeOfHarbor;
        }

        //Methods

        public char getPosition() {
            return position;
        }

        public StreetNode[] getConnectedStreetNodes() {
            return connectedStreetNodes;
        }

        public int getOccupiedByPlayer() {
            return occupiedByPlayer;
        }

        public void setOccupiedByPlayer(int occupiedByPlayer) {
            this.occupiedByPlayer = occupiedByPlayer;
        }

        public boolean isSettlementUpgraded() {
            return settlementUpgraded;
        }

        public void setSettlementUpgraded(boolean settlementUpgraded) {
            this.settlementUpgraded = settlementUpgraded;
        }

        public int getTypeOfHarbor() {
            return typeOfHarbor;
        }
    }
}
