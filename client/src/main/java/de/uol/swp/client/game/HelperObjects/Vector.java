package de.uol.swp.client.game.HelperObjects;

import de.uol.swp.common.game.MapGraph;

import java.util.Collection;

/**
 * This class is used for navigating in the 12-angled grid of terrain-fields.
 * <p>
 * enhanced by Pieter Vogt 2021-04-07
 *
 * @author Pieter Vogt
 * @since 2021-01-24
 */
public class Vector {

    private final double x;
    private final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns a vector depending on a parsed MapGraphNode and distance (CardSize).
     * <p>This method is used to place Nodes.</p>
     *
     * @param node MapGraphNode to extract vector from
     * @param d    cardSize
     * @return the Vector to specify the position of the MapGraphNode
     */
    public static Vector getVectorFromMapGraphNode(MapGraph.MapGraphNode node, double d) {
        Vector returnVector = new Vector(0, 0);
        if (node.getClass().equals(MapGraph.BuildingNode.class)) {
            MapGraph.BuildingNode buildingNode = (MapGraph.BuildingNode) node;
            double angle;
            double distance = d / Math.sqrt(3);

            switch (buildingNode.getPositionToParent()) {

                case "topRight":
                    angle = 1 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                    break;
                case "top":
                    angle = 3 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                    break;
                case "topLeft":
                    angle = 5 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                    break;
                case "bottomLeft":
                    angle = 7 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                    break;
                case "bottom":
                    angle = 9 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                    break;
                case "bottomRight":
                    angle = 11 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                    break;
            }
        } else {
            MapGraph.StreetNode streetNode = (MapGraph.StreetNode) node;
            double angle;
            d = d * 0.5;
            switch (streetNode.getPositionToParent()) {
                case "right":
                    angle = 0 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                    break;
                case "topRight":
                    angle = 2 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                    break;
                case "topLeft":
                    angle = 4 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                    break;
                case "left":
                    angle = 6 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                    break;
                case "bottomLeft":
                    angle = 8 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                    break;
                case "bottomRight":
                    angle = 10 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                    break;
            }
        }
        return returnVector;
    }

    /**
     * Creates a Vector from a list of Strings.
     * <p>This can only be parsed a Collection of Strings from a Hexagon because this already considers the cardSize of
     * the hexagons. </p>
     *
     * @param positions    The Collection of Strings that describe the relational position of different objects to one
     *                     another.
     * @param d            the distance traveled from one object to the next.
     * @param sourceVector The start of the resulting vector. Usually the center of the canvas, but can be something
     *                     else.
     * @return Vector that sums up all vectors from the list plus the sourceVector.
     * @author Pieter Vogt
     * @see de.uol.swp.common.game.MapGraph.Hexagon
     * @since 2021-04-12
     */

    public static Vector convertStringListToVector(Collection<String> positions, double d, Vector sourceVector) {
        Vector returnVector = sourceVector;
        for (String s : positions) {
            switch (s) {
                case "left":
                    returnVector = Vector.addVector(returnVector, Vector.left(d));
                    break;
                case "topLeft":
                    returnVector = Vector.addVector(returnVector, Vector.topLeft(d));
                    break;
                case "right":
                    returnVector = Vector.addVector(returnVector, Vector.right(d));
                    break;
                case "topRight":
                    returnVector = Vector.addVector(returnVector, Vector.topRight(d));
                    break;
                case "bottomLeft":
                    returnVector = Vector.addVector(returnVector, Vector.bottomLeft(d));
                    break;
                case "bottomRight":
                    returnVector = Vector.addVector(returnVector, Vector.bottomRight(d));
                    break;
                default:
                    returnVector = Vector.addVector(returnVector, new Vector(0.0, 0.0));
                    break;
            }
        }
        return returnVector;
    }

    /**
     * Subtracts 2 Vectors in JavaFX-Coordinate System
     * <p>
     * Because the JavaFX coordinate-system has its origin in the upper left corner and increments only into positive
     * space, the subtraction of two vectors needs to add the y-values. The reason is, that - although the x-axis in
     * JavaFX behaves like the x-axis of a normal cartesian system - the y-axis doesn't. The values of the y axis
     * actually go up when you go down in screen-direction. To compensate for that, i changed the add- and subtract-
     * methods for vectors in the game.
     * </p>
     *
     * @param v1 Vector
     * @param v2 Vector
     * @return Vector as subtraction result
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    public static Vector subVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() - v2.getX(), v1.getY() + v2.getY());
    }

    /**
     * Adds 2 Vectors in JavaFX coordinate-system.
     * <p>
     * Because the JavaFX coordinate-system has its origin in the upper left corner and increments only into positive
     * space, the addition of two vectors needs to subtract the y-values. The reason is, that - although the x-axis in
     * JavaFX behaves like the x-axis of a normal cartesian system - the y-axis doesn't. The values of the y axis
     * actually go up when you go down in screen-direction. To compensate for that, i changed the add- and subtract-
     * methods for vectors in the game.
     * </p>
     *
     * @param v1 Vector
     * @param v2 Vector
     * @return Vector as addition result
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    public static Vector addVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() + v2.getX(), v1.getY() - v2.getY());
    }

    public static Vector right(double d) {
        double angle = 0 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector topRight(double d) {
        double angle = 2 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector topLeft(double d) {
        double angle = 4 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector left(double d) {
        double angle = 6 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector bottomLeft(double d) {
        double angle = 8 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector bottomRight(double d) {
        double angle = 10 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


}