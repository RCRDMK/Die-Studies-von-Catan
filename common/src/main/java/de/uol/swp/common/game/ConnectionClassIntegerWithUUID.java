package de.uol.swp.common.game;

import java.io.Serializable;
import java.util.UUID;

public class ConnectionClassIntegerWithUUID implements Serializable {
    private Integer integer;
    private UUID uuidForRow;
    private UUID uuidForColumn;

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

    public void setUuidForRow(UUID uuidForRow) {
        this.uuidForRow = uuidForRow;
    }

    public UUID getUuidForColumn() {
        return uuidForColumn;
    }

    public void setUuidForColumn(UUID uuidForColumn) {
        this.uuidForColumn = uuidForColumn;
    }
}
