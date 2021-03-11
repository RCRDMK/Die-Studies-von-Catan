package de.uol.swp.client;

import de.uol.swp.client.game.HelperObjects.Vector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
     * The y-axis increases when going down istead of going up (like in normal cartesian coordinate-systems).
     * </p>
     *
     * @author Pieter Vogt
     * @date 2021-03-10
     */
    @Test
    void addVectors() {
        Vector v3 = Vector.addVector(v1, v2);
        assertTrue(v3.getX() == 18);
        assertTrue(v3.getY() == 1);
    }

    /**
     * This test checks, if the Vector-class subtracts two vectors correctly, according to the special rule of the
     * y-axis in JavaFX.
     * <p>
     * The y-axis increases when going down istead of going up (like in normal cartesian coordinate-systems).
     * </p>
     *
     * @author Pieter Vogt
     * @date 2021-03-10
     */
    @Test
    void subtractVectors() {
        Vector v4 = Vector.subVector(v1, v2);
        assertTrue(v4.getX() == 2);
        assertTrue(v4.getY() == -1);
    }

    /**
     * This test checks, if the directional methods of the Vector-class work properly.
     * <p>
     * The directional methods in combination with a double value are used to determine the vector that needs to be
     * added to a position in order to move (from that position) in a certain direction for a given distance.
     * </p>
     *
     * @author Pieter Vogt
     * @date 2021-03-10
     */
    @Test
    void checkDirectionalVectorMethods() {
        assertEquals(Math.cos(1 * (2 * Math.PI / 12)) * 5, Vector.topRight(5).getX());
        assertEquals(Math.cos(3 * (2 * Math.PI / 12)) * 0, Vector.top(0).getX());
        assertEquals(Math.cos(5 * (2 * Math.PI / 12)) * 32, Vector.topLeft(32).getX());
        assertEquals(Math.cos(7 * (2 * Math.PI / 12)) * 1, Vector.bottomLeft(1).getX());
        assertEquals(Math.cos(9 * (2 * Math.PI / 12)) * -5, Vector.bottom(-5).getX());
        assertEquals(Math.cos(11 * (2 * Math.PI / 12)) * 567, Vector.bottomRight(567).getX());
    }
}