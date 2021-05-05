package de.uol.swp.common.game.message;

import de.uol.swp.common.game.MapGraph;

/**
 * Distributes the MapGraph-object to every player in the game.
 *
 * @author Pieter Vogt
 * @see MapGraph
 * @since 2021-04-11
 */
public class MapGraphChangedMessage extends AbstractGameMessage {

    private final MapGraph mapGraph;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    public MapGraphChangedMessage() {
        mapGraph = null;
    }

    public MapGraphChangedMessage(String gameName, MapGraph mapGraph) {
        this.name = gameName;
        this.mapGraph = mapGraph;
    }

    public MapGraph getMapGraph() {
        return mapGraph;
    }

    public String getGameName() {
        return name;
    }
}
