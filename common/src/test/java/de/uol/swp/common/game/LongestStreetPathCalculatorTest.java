package de.uol.swp.common.game;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test Class for the LongestStreetPathCalculator
 * <p>
 *
 * @author Marc, Kirstin
 * @since 2021-04-30
 */

public class LongestStreetPathCalculatorTest {
    private final MapGraph mapGraph = new MapGraph("");
    private final HashSet<MapGraph.StreetNode> streetNodes = mapGraph.getStreetNodeHashSet();
    private final HashSet<MapGraph.BuildingNode> buildingNodes = mapGraph.getBuildingNodeHashSet();
    private final HashSet<MapGraph.Hexagon> hexagons =  mapGraph.getHexagonHashSet();


    /**
     * This test checks if the longest street path is calculated correctly for one player after interconnection of two
     * streets.
     * <p>
     * We build 3 streets for player 1 and 2 streets for player 2 and check if the longest street path for player 1
     * equals 3 and for player 2 equals 2.
     *
     * @author Marc, Kirstin
     * @since 2021-04-30
     */
    @Test
    void blackBoxTest1() {
        for (MapGraph.Hexagon hexagon : hexagons) {
            if (hexagon.getSelfPosition().contains("left") && hexagon.getSelfPosition().size() == 2){
                hexagon.getStreetTopLeft().buildRoad(1);
                hexagon.getStreetBottomLeft().buildRoad(1);
                hexagon.getStreetBottomRight().buildRoad(2);
                hexagon.getStreetLeft().buildRoad(1);
                hexagon.getStreetRight().buildRoad(2);
            }
        }
        assertEquals(mapGraph.getLongestStreetPathCalculator().getLongestPath(1),3);
        assertEquals(mapGraph.getLongestStreetPathCalculator().getLongestPath(2),2);

    }

    /**
     * This test checks if the longest street path is calculated correctly if the streets build a circle.
     * <p>
     * We build 6 streets for player 1 in a circle and check if the longest street path for player 1 equals 6.
     *
     * @author Marc, Kirstin
     * @since 2021-04-30
     */
    @Test
    void blackBoxTest2() {
        for (MapGraph.Hexagon hexagon : hexagons) {
            if (hexagon.getSelfPosition().contains("left") && hexagon.getSelfPosition().size() == 2){
                hexagon.getStreetTopLeft().buildRoad(1);
                hexagon.getStreetBottomLeft().buildRoad(1);
                hexagon.getStreetLeft().buildRoad(1);
                hexagon.getStreetRight().buildRoad(1);
                hexagon.getStreetBottomRight().buildRoad(1);
                hexagon.getStreetTopRight().buildRoad(1);
            }
        }
        assertEquals(mapGraph.getLongestStreetPathCalculator().getLongestPath(1),6);
    }

    /**
     * This test checks if the longest street path is calculated correctly if a building is placed within the path from
     * the same player.
     * <p>
     * We build 5 streets for player 1 and place a building from player 1 between these streets.
     *
     * @author Marc, Kirstin
     * @since 2021-04-30
     */
    @Test
    void blackBoxTest3() {
        for (MapGraph.Hexagon hexagon : hexagons) {
            if (hexagon.getSelfPosition().contains("left") && hexagon.getSelfPosition().size() == 2){
                hexagon.getStreetTopLeft().buildRoad(1);
                hexagon.getStreetBottomLeft().buildRoad(1);
                hexagon.getStreetLeft().buildRoad(1);
                hexagon.getStreetRight().buildRoad(1);
                hexagon.getStreetBottomRight().buildRoad(1);
                hexagon.getBuildingBottom().buildOrDevelopSettlement(1);
            }
        }
        assertEquals(mapGraph.getLongestStreetPathCalculator().getLongestPath(1),5);
    }

    /**
     * This test checks if the longest street path is calculated correctly if a building is placed within the path from
     * a different player.
     * <p>
     * We build 5 streets for player 1 and place a building from a different between these streets.
     *
     * @author Marc, Kirstin
     * @since 2021-04-30
     */
    @Test
    void blackBoxTest4() {
        for (MapGraph.Hexagon hexagon : hexagons) {
            if (hexagon.getSelfPosition().contains("left") && hexagon.getSelfPosition().size() == 2){
                hexagon.getStreetTopLeft().buildRoad(1);
                hexagon.getStreetBottomLeft().buildRoad(1);
                hexagon.getStreetLeft().buildRoad(1);
                hexagon.getStreetRight().buildRoad(1);
                hexagon.getStreetBottomRight().buildRoad(1);
                hexagon.getBuildingBottom().buildOrDevelopSettlement(2);
            }
        }
        assertEquals(mapGraph.getLongestStreetPathCalculator().getLongestPath(1),3);
    }

    /**
     * This test checks if the longest street path is calculated correctly if a junction appears in the mapgraph.
     * <p>
     * We build 7 streets for player 1 and 6 of them at the left hexagon and one at the left side of the topleft hexagon.
     *
     * @author Marc, Marius
     * @since 2021-05-25
     */

    @Test
    void blackBoxTest5(){
        for (MapGraph.Hexagon hexagon: hexagons){
            if (hexagon.getSelfPosition().contains("left") && hexagon.getSelfPosition().size() == 2){
                hexagon.getStreetTopLeft().buildRoad(1);
                hexagon.getStreetBottomLeft().buildRoad(1);
                hexagon.getStreetLeft().buildRoad(1);
                hexagon.getStreetRight().buildRoad(1);
                hexagon.getStreetBottomRight().buildRoad(1);
                hexagon.getStreetTopRight().buildRoad(1);
            }
            if(hexagon.getSelfPosition().contains("topLeft") && hexagon.getSelfPosition().size() == 2){
                hexagon.getStreetLeft().buildRoad(1);
            }
        }
        assertEquals(mapGraph.getLongestStreetPathCalculator().getLongestPath(1),7);
        mapGraph.getLongestStreetPathCalculator().printAdjacencyMatrix(1);
    }
}
