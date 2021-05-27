package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;
import java.util.UUID;

/**
 * This class is only used for tests.
 * <p>
 * Changing parameters / function calls in this class may result in certain tests failing
 *
 * @author Marc Hermes
 * @since 2021-05-12
 */
public class TestAI extends AbstractAISystem {
    /**
     * Constructor
     *
     * @param thatGame the game that this AISystem will have to work with and of which a deep copy will be created
     * @author Marc Hermes
     * @since 2021-05-12
     */
    public TestAI(GameDTO thatGame) {
        super(thatGame);
    }

    /**
     * This method will try to make use of every possible action the AI may do for test purposes
     *
     * @return the List of AIActions
     * @author Marc Hermes
     * @since 2021-05-12
     */
    public ArrayList<AIAction> startTurnOrder() {
        buyDevelopmentCard();
        playDevelopmentCardMonopoly("Grain");
        playDevelopmentCardYearOfPlenty("Lumber", "Brick");
        int i = 0;
        UUID street1 = null;
        UUID street2 = null;
        for (MapGraph.StreetNode street : game.getMapGraph().getStreetNodeHashSet()) {
            if (i == 0 && street.getOccupiedByPlayer() == 666) {
                street1 = street.getUuid();

            }
            if (i == 1 && street.getOccupiedByPlayer() == 666) {
                street2 = street.getUuid();
            }
            if (i == 2) {
                buildStreet(street.getUuid());
                break;
            }
            i++;
        }
        playDevelopmentCardRoadBuilding(street1, street2);
        for (MapGraph.BuildingNode building : game.getMapGraph().getBuildingNodeHashSet()) {
            buildTown(building.getUuid());
            buildCity(building.getUuid());
            break;
        }
        i = 0;
        for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
            if (i == 0) {
                moveBandit(hexagon.getUuid());
            }
            if (i == 1) {
                playDevelopmentCardKnight(hexagon.getUuid());
                break;
            }
            i++;
        }
        endTurn();

        return this.aiActions;
    }
}
