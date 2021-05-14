package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;

/**
 * AI which makes choices randomly
 * <p>
 * //TODO: Work in progress
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public class RandomAI extends AbstractAISystem {

    /**
     * Constructor
     *
     * @param thatGame the game of this AI
     * @author Marc Hermes
     * @since 2021-05-08
     */
    public RandomAI(GameDTO thatGame) {
        super(thatGame);
    }

    public ArrayList<AIAction> startTurnAction() {

        playDevelopmentCardYearOfPlenty("Lumber", "Brick");
        for(MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
            moveBandit(hexagon.getUuid());
            break;
        }
        for (MapGraph.StreetNode street : game.getMapGraph().getStreetNodeHashSet()) {
            buildStreet(street.getUuid());
            break;
        }
        for (MapGraph.BuildingNode building : game.getMapGraph().getBuildingNodeHashSet()) {
            buildTown(building.getUuid());
            break;
        }
        endTurn();
        return this.aiActions;
    }
}
