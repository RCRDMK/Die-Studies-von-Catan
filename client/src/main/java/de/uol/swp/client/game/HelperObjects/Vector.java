package de.uol.swp.client.game.HelperObjects;

import de.uol.swp.common.game.MapGraph;

import java.util.Collection;

/**
 * This class is used for navigating in the 6-angled grid of terrain-fields.
 * <p></p>
 * enhanced by Pieter Vogt 2021-04-07
 *
 * @author Pieter Vogt
 * @since 2021-01-24
 */
public class Vector {
    //attributes
    private final double x;
    private final double y;

    //getter

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector getVectorFromMapGraphNode(MapGraph.MapGraphNode node, double d) {
        Vector returnVector = new Vector(0, 0);
        if (node.getClass().equals(MapGraph.BuildingNode.class)) {
            MapGraph.BuildingNode buildingNode = (MapGraph.BuildingNode) node;
            double angle;
            //This gets calculated because the BuildingNodes are further away from the hexagonal center than the StreetNodes.
            double distance = d / Math.sin(1 / 12 * Math.PI);
            switch (buildingNode.getPositionToParent()) {

                case "topRight":
                    angle = 1 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                case "top":
                    angle = 3 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                case "topLeft":
                    angle = 5 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                case "bottomLeft":
                    angle = 7 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                case "bottom":
                    angle = 9 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
                case "bottomRight":
                    angle = 11 * (2 * Math.PI / 12);
                    returnVector = new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
            }
        } else {
            MapGraph.StreetNode streetNode = (MapGraph.StreetNode) node;
            double angle;
            switch (streetNode.getPositionToParent()) {
                case "right":
                    angle = 0 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                case "topRight":
                    angle = 2 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                case "topLeft":
                    angle = 4 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                case "left":
                    angle = 6 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                case "bottomLeft":
                    angle = 8 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
                case "bottomRight":
                    angle = 10 * (2 * Math.PI / 12);
                    returnVector = new Vector(d * Math.cos(angle), d * Math.sin(angle));
            }
        }
        return returnVector;
    }

    /**
     * Adds 2 Vectors in JavaFX coordinate-system.
     * <p>
     * Because the JavaFX coordinate-system has its origin in the upper left corner and increments only into positive
     * space, the addition of two vectors needs to subtract the y-values. The reason is, that - although the x-axis in
     * JavaFX behaves like the x-axis of a normal cartesian system - the y-axis doesnt. The values of the y axis
     * actually go up when you go down in screen-direction. To compensate for that, i changed the add- and substract-
     * methods for vectors in the game.
     * </p>
     *
     * @param v1 Vector
     * @param v2 Vector
     *
     * @return Vector as addition result
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    public static Vector addVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() + v2.getX(), v1.getY() - v2.getY());
    }

    //constructor

    public static Vector convertStringListToVector(Collection<String> positions, double d) {
        Vector returnVector = new Vector(0, 0);
        for (String s : positions) {
            switch (s) {
                case "left":
                    Vector.addVector(returnVector, Vector.left(d));
                    break;
                case "topLeft":
                    Vector.addVector(returnVector, Vector.topLeft(d));
                    break;
                case "right":
                    Vector.addVector(returnVector, Vector.right(d));
                    break;
                case "topRight":
                    Vector.addVector(returnVector, Vector.topRight(d));
                    break;
                case "bottomLeft":
                    Vector.addVector(returnVector, Vector.bottomLeft(d));
                    break;
                case "bottomRight":
                    Vector.addVector(returnVector, Vector.bottomRight(d));
                    break;
                default:
                    break;
            }
        }
        return returnVector;
    }

    //Vector-math-functions

    /**
     * Subtracts 2 Vectors in JavaFX-Coordinate System
     * <p>
     * Because the JavaFX coordinate-system has its origin in the upper left corner and increments only into positive
     * space, the substraction of two vectors needs to add the y-values. The reason is, that - although the x-axis in
     * JavaFX behaves like the x-axis of a normal cartesian system - the y-axis doesnt. The values of the y axis
     * actually go up when you go down in screen-direction. To compensate for that, i changed the add- and substract-
     * methods for vectors in the game.
     * </p>
     *
     * @param v1 Vector
     * @param v2 Vector
     *
     * @return Vector as subtraction result
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    public static Vector subVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() - v2.getX(), v1.getY() + v2.getY());
    }

    public static Vector right(double d) {
        double angle = 0 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector topRight(double d) {
        double angle = 2 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    //Movement-related functions. Double d is the distance to travel.

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

    // method to generate any vector with length d and angle angle_deg in degree
    public static Vector generalVector(double d, double angle_deg) {
        double angle = angle_deg / 360 * 2 * Math.PI;
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}