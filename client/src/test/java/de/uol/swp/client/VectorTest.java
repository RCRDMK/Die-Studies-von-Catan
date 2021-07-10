package de.uol.swp.client;

import org.junit.jupiter.api.Test;

import de.uol.swp.client.game.HelperObjects.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VectorTest {
    double a = 10;
    double b = 0;
    double c = 8;
    double d = -1;

    Vector v1 = new Vector(a, b);
    Vector v2 = new Vector(c, d);

    /**
     * This test checks, if the Vector-class adds two vectors correctly, according to the special rule of the y-axis in
     * JavaFX.
     * <p>
     * The y-axis increases when going down instead of going up (like in normal cartesian coordinate-systems).
     * </p>
     *
     * @author Pieter Vogt
     * @since 2021-03-10
     */
    @Test
    void addVectors() {
        Vector v3 = Vector.addVector(v1, v2);
        assertEquals(v3.getX(), 18);
        assertEquals(v3.getY(), 1);
    }

    /**
     * This test checks, if the Vector-class subtracts two vectors correctly, according to the special rule of the
     * y-axis in JavaFX.
     * <p>
     * The y-axis increases when going down instead of going up (like in normal cartesian coordinate-systems).
     * </p>
     *
     * @author Pieter Vogt
     * @since 2021-03-10
     */
    @Test
    void subtractVectors() {
        Vector v4 = Vector.subVector(v1, v2);
        assertEquals(v4.getX(), 2);
        assertEquals(-1, v4.getY());
    }

    /**
     * This test checks, if the directional methods of the Vector-class work properly.
     * <p>
     * The directional methods in combination with a double value are used to determine the vector that needs to be
     * added to a position in order to move (from that position) in a certain direction for a given distance.
     * </p>
     * enhanced by Pieter Vogt
     *
     * @author Pieter Vogt
     * @since 2021-03-10
     */
    @Test
    void checkDirectionalVectorMethods() {
        double value = 2 * Math.PI / 12;
        assertEquals(Math.cos(0 * (value)) * 5, Vector.right(5).getX());
        assertEquals(Math.cos(2 * (value)) * 0, Vector.topRight(0).getX());
        assertEquals(Math.cos(4 * (value)) * 32, Vector.topLeft(32).getX());
        assertEquals(Math.cos(6 * (value)) * 1, Vector.left(1).getX());
        assertEquals(Math.cos(8 * (value)) * -5, Vector.bottomLeft(-5).getX());
        assertEquals(Math.cos(10 * (value)) * 567, Vector.bottomRight(567).getX());
    }
}