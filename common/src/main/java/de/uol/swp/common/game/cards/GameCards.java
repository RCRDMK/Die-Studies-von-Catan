package de.uol.swp.common.game.cards;

/**
 * Base class of all cards in the game.
 *
 * @author Carsten Dekker und Anton Nikiforov
 * @since 2020-12-21
 */

public abstract class GameCards {

    private final int ID;
    private String name;
    private String owner;

    public GameCards(int id, String name, String owner) {
        this.ID = id;
        this.name = name;
        this.owner = owner;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String newOwner) {
        this.owner = newOwner;
    }

}
