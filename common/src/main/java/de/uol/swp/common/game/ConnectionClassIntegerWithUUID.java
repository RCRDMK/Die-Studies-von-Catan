package de.uol.swp.common.game;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class to connect 2 UUIDs with an integer for the entries in longest StreetPath matrix.
 * <p>
 * First UUID indicates the row, second UUID indicates the column. The integer values is 0 if the corresponding streets are not
 * connected and 1 if they are connected.
 *
 * @author Marc, Kirstin
 * @since 2021-04-23
 */

public class ConnectionClassIntegerWithUUID implements Serializable {
    private Integer integer;
    private final UUID uuidForRow;
    private final UUID uuidForColumn;

    /**
     * Constructor
     *
     * @param uuidForRow UUID for corresponding street in that row
     * @param uuidForColumn UUID for corresponding street in that column
     * @param integer integer value (0 or 1), indicating if streets are connected
     * @author Marc, Kirstin
     * @since 2021-04-23
     */
    public ConnectionClassIntegerWithUUID( UUID uuidForRow, UUID uuidForColumn, Integer integer) {
        this.integer = integer;
        this.uuidForColumn = uuidForColumn;
        this.uuidForRow = uuidForRow;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public UUID getUuidForRow() {
        return uuidForRow;
    }

    public UUID getUuidForColumn() {
        return uuidForColumn;
    }

}
