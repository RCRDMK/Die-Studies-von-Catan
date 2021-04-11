package de.uol.swp.client.game.HelperObjects;

import java.util.Collection;

/**
 * This class is used for navigating in the 6-angled grid of terrain-fields.
 *
 * @author Pieter Vogt
 * @since 2021-01-24
 */
public class Vector {
    //attributes
    private final double x;
    private final double y;

    //getter

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    //constructor

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //Vector-math-functions

    /**
     * Adds 2 Vectors in JavaFX coordinate-system.
     *<p>
     * Because the JavaFX coordinate-system has its origin in the upper left corner and increments only into positive space,
     * the addition of two vectors needs to subtract the y-values. The reason is, that - although the x-axis in JavaFX behaves like the x-axis of a normal cartesian system -
     * the y-axis doesnt. The values of the y axis actually go up when you go down in screen-direction. To compensate for that, i changed the add- and substract- methods for vectors in the game.
     *</p>
     * @author Pieter Vogt
     * @since 2021-01-24
     * @param v1 Vector
     * @param v2 Vector
     * @return Vector as addition result
     */
    public static Vector addVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() + v2.getX(), v1.getY() - v2.getY());
    }

    public static Vector addVectors(Collection<String> positions) {
        Vector returnVector = new Vector(0, 0);
        for (String s : positions) {
            switch (s) {
                case "left":
            }
        }
        int numberOfVectors = positions.size();

    }

    /**
     * Subtracts 2 Vectors in JavaFX-Coordinate System
     *<p>
     *Because the JavaFX coordinate-system has its origin in the upper left corner and increments only into positive space,
     *the substraction of two vectors needs to add the y-values. The reason is, that - although the x-axis in JavaFX behaves like the x-axis of a normal cartesian system -
     *the y-axis doesnt. The values of the y axis actually go up when you go down in screen-direction. To compensate for that, i changed the add- and substract- methods for vectors in the game.
     *</p>
     * @author Pieter Vogt
     * @since 2021-01-24
     * @param v1 Vector
     * @param v2 Vector
     * @return Vector as subtraction result
     */
    public static Vector subVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() - v2.getX(), v1.getY() + v2.getY());
    }

    //Movement-related functions. Double d is the distance to travel.

    public static Vector topRight(double d) {
        double angle = 1 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector top(double d) {
        double angle = 3 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector topLeft(double d) {
        double angle = 5 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector bottomLeft(double d) {
        double angle = 7 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector bottom(double d) {
        double angle = 9 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    public static Vector bottomRight(double d) {
        double angle = 11 * (2 * Math.PI / 12);
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }

    // method to generate any vector with length d and angle angle_deg in degree
    public static Vector generalVector(double d, double angle_deg) {
        double angle = angle_deg / 360 * 2 * Math.PI;
        return new Vector(d * Math.cos(angle), d * Math.sin(angle));
    }
}