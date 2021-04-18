package de.uol.swp.common.game;

import java.io.Serializable;
//TODO: Diese Klasse ist obsolet! Es müssen alle Uses überarbeitet werden, sodass diese Klasse schnellstmöglich wieder entfernt werden kann!
/**
 * This class is used to define the BuildingSpots of the TerrainFieldContainers.
 * <p>
 * This class doesn't have a use yet and may be deleted/changed if the future implementation sees fit.
 *
 * @author Pieter Vogt, Marc Hermes
 * @since 2021-03-14
 */
public class BuildingSpot implements Serializable {

    public boolean isOccupied = false;
    public boolean isStreet;
    public String name;

}
