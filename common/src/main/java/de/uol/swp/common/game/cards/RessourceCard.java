package de.uol.swp.common.game.cards;

/**
 * Class for the ressource cards in the game.
 *
 * @author Carsten Dekker und Anton Nikiforov
 * @see de.uol.swp.common.game.cards.GameCards
 * @since 2020-12-21
 */

public class RessourceCard extends GameCards {

    String ressourceType;

    public RessourceCard(int id, String name, String owner, String ressourceType) {
        super(id, name, owner);
        this.ressourceType = ressourceType;
    }

    public String getRessourceType() {
        return ressourceType;
    }

    public void setRessourceType(String ressourceType) {
        this.ressourceType = ressourceType;
    }
}
