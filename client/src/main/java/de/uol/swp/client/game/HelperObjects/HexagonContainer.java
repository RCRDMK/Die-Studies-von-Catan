package de.uol.swp.client.game.HelperObjects;

import de.uol.swp.common.game.MapGraph;
import javafx.scene.shape.Polygon;

/**
 * Links a hexagon Polygon to a Hexagon and vice versa.
 *
 * <p>This class achieves the link between the two aforementioned Classes. This way, the drawn Polygon-object knows which
 * Hexagon-object it is representing and the other way around. By using this class, we can change a certain
 * Hexagon-object by clicking on the corresponding drawn Polygon-objects in the canvas, or we can change the
 * Polygon-object by accessing it via the Hexagon-object.</p>
 * <p>
 * enhanced by Marc Hermes - 2021-04-28
 *
 * @author Pieter Vogt
 * @see de.uol.swp.common.game.MapGraph.Hexagon
 * @see Polygon
 * @since 2021-04-14
 */

public class HexagonContainer {

    //Fields

    private final MapGraph.Hexagon hexagon;

    private final Polygon hexagonShape;

    //Constructor

    public HexagonContainer(MapGraph.Hexagon hexagon, double cardSize) {
        this.hexagon = hexagon;
        hexagonShape = calculateHexagon(cardSize);
    }

    //Getter Setter


    public MapGraph.Hexagon getHexagon() {
        return hexagon;
    }

    public Polygon getHexagonShape() {
        return hexagonShape;
    }

    //Methods

    /**
     * This method creates the 6 points of the Polygon so that it resembles a Hexagon.
     *
     * @param cardSize the size of the cards on the field
     * @author Marc Hermes
     * @since 2021-04-28
     */
    private Polygon calculateHexagon(double cardSize) {
        double resizingFactor = cardSize / Math.sqrt(3);
        Polygon hexagonShape = new Polygon();

        Vector direction = Vector.right(resizingFactor);
        hexagonShape.getPoints().add(direction.getY());
        hexagonShape.getPoints().add(direction.getX());

        direction = Vector.topRight(resizingFactor);
        hexagonShape.getPoints().add(direction.getY());
        hexagonShape.getPoints().add(direction.getX());

        direction = Vector.topLeft(resizingFactor);
        hexagonShape.getPoints().add(direction.getY());
        hexagonShape.getPoints().add(direction.getX());

        direction = Vector.left(resizingFactor);
        hexagonShape.getPoints().add(direction.getY());
        hexagonShape.getPoints().add(direction.getX());

        direction = Vector.bottomLeft(resizingFactor);
        hexagonShape.getPoints().add(direction.getY());
        hexagonShape.getPoints().add(direction.getX());

        direction = Vector.bottomRight(resizingFactor);
        hexagonShape.getPoints().add(direction.getY());
        hexagonShape.getPoints().add(direction.getX());

        return hexagonShape;
    }
}
