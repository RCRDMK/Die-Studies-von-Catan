package de.uol.swp.common.game.exception;

/**
 * This exception is been used if a node on the mapgraph has the maximum amount of connections.
 */
public class ListFullException extends Exception {

    /**
     * Constructor
     *
     * @param errorMessage that will be used.
     */
    public ListFullException(String errorMessage) {
        super(errorMessage);
    }
}
