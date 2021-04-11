package de.uol.swp.common.game.message;

import de.uol.swp.common.game.MapGraph;

/**
 * Distributes the MapGraph-object to every player in the game.
 *
 * @author Pieter Vogt
 * @see MapGraph
 * @since 2021-04-11
 */
public class MapGraphChangedMessage {

    //FIELDS

    private MapGraph mapGraph;
    private String gameName;

    //CONSTRUCTOR

    public MapGraphChangedMessage(String gameName, MapGraph mapGraph) {
        this.gameName = gameName;
        this.mapGraph = mapGraph;
    }

    // GETTER SETTER

    public MapGraph getMapGraph() {
        return mapGraph;
    }

    public String getGameName() {
        return gameName;
    }
}
