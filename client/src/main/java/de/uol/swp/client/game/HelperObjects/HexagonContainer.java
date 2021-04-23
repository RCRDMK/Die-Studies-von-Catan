package de.uol.swp.client.game.HelperObjects;

import de.uol.swp.common.game.MapGraph;
import javafx.scene.shape.Circle;

/**
 * Links a Circle to a Hexagon and vice versa.
 *
 * <p>This class achieves the link between the two aforementioned Classes. This way, the drawn Circle-object knows wich
 * Hexagon-object it is representing and the other way around. By using this class, we can change a certain
 * Hexagon-object by clicking on the corresponding drawn Circle-objects in the canvas, or we can change the
 * Circle-object by accessing it via the Hexagon-object.</p>
 *
 * @author Pieter Vogt
 * @see de.uol.swp.common.game.MapGraph.Hexagon
 * @see Circle
 * @since 2021-04-14
 */

public class HexagonContainer {

    //Fields

    private Circle circle;
    private MapGraph.Hexagon hexagon;

    //Constructor

    public HexagonContainer(MapGraph.Hexagon hexagon, Circle circle) {
        this.hexagon = hexagon;
        this.circle = circle;
    }

    //Getter Setter

    public Circle getCircle() {
        return circle;
    }

    public MapGraph.Hexagon getHexagon() {
        return hexagon;
    }

    //Methods


}
