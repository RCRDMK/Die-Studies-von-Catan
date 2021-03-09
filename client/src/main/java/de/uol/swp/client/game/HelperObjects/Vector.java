package de.uol.swp.client.game.HelperObjects;

/**
 * This class is used for navigating in the 6-angled grid of terrain-fields.
 *
 * @author pieter vogt
 * @since 24-01-2021
 */
public class Vector {
    //fields
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

    //Vector-functions

    /**
     * Adds 2 Vectors in JavaFX-Coordinate System
     *
     * @author Pieter Vogt
     * @since 24-01-2021
     * @param v1
     * @param v2
     * @return Vector as addition result
     */
    public static Vector addVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() + v2.getX(), v1.getY() - v2.getY());
    }

    /**
     * Subtracts 2 Vectors in JavaFX-Coordinate System
     *
     * @author Pieter Vogt
     * @since 24-01-2021
     * @param v1
     * @param v2
     * @return Vector as substraction result
     */
    public static Vector subVector(Vector v1, Vector v2) {
        return new Vector(v1.getX() - v2.getX(), v1.getY() + v2.getY());
    }

    //movement-functions d=distance to travel

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
}