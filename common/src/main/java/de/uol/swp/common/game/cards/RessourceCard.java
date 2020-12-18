package de.uol.swp.common.game.cards;

/**
 * Class for the ressource cards in the game.
 *
 * @author Carsten Dekker
 * @since 2020-12-18
 */

public class RessourceCard extends GameCards {

    String ressourceType;

    public RessourceCard(String ressourceType) {
        this.ressourceType = ressourceType;
    }

    public String getRessourceType() {
        return ressourceType;
    }

    public void setRessourceType(String ressourceType) {
        this.ressourceType = ressourceType;
    }
}
