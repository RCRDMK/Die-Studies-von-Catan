package de.uol.swp.client.game.HelperObjects;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import de.uol.swp.common.game.MapGraph;

/**
 * Links a Circle to a MapGraphNode and vice versa.
 *
 * <p>This class achieves the link between the two aforementioned Classes. This way, the drawn Circle-object knows wich
 * MapGraphNode-object it belongs to and the other way around. By using this class, we can change a certain
 * MapGraphNode-object by clicking on the corresponding drawn Circle-objects in the canvas, or we can change the
 * Circle-object by accessing it via the MapGraphNode-object.</p>
 *
 * @author Pieter Vogt
 * @see de.uol.swp.common.game.MapGraph.MapGraphNode
 * @see Circle
 * @since 2021-04-14
 */

public class MapGraphNodeContainer {

    //Fields

    private final Circle circle;
    private final MapGraph.MapGraphNode mapGraphNode;
    private final Rectangle rectangle;

    //Constructor

    public MapGraphNodeContainer(Circle c, MapGraph.MapGraphNode m) {
        circle = c;
        mapGraphNode = m;
        rectangle = null;
    }

    public MapGraphNodeContainer(Circle c, MapGraph.MapGraphNode m, Rectangle r) {
        circle = c;
        mapGraphNode = m;
        rectangle = r;
    }

    //Getter Setter

    public Circle getCircle() {
        return circle;
    }

    public MapGraph.MapGraphNode getMapGraphNode() {
        return mapGraphNode;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }
}
