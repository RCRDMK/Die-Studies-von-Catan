package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.server.AI.AIActions.AIAction;
import de.uol.swp.server.AI.AIActions.BuildAction;
import org.checkerframework.checker.nullness.Opt;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

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

    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }


    public ArrayList<AIAction> startTurnOrder() {

        System.out.println(inventory.brick.getNumber());
        System.out.println(inventory.ore.getNumber());
        System.out.println(inventory.wool.getNumber());
        System.out.println(inventory.grain.getNumber());
        System.out.println(inventory.lumber.getNumber());
        if(game.isStartingTurns()) {
            startingTurnLogic();
            System.out.println("IS starting turns");
        }
        else {
            if(game.getLastRolledDiceValue() == 7) {
                for(MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
                    if(!hx.isOccupiedByRobber()) {
                        moveBandit(hx.getUuid());
                        break;
                    }
                }
            }
            int amountOfActions = randomInt(0, 5);
            for(int i =0; i <= amountOfActions; i++) {
                int actionType = randomInt(0,3);
                switch (actionType) {
                    case 0:
                        if(canBuildStreet()) {
                            Optional<UUID> streetUUID = possibleStreet();
                            streetUUID.ifPresent(this::buildStreet);
                            break;
                        }
                    case 1:
                        if(canBuildTown()) {
                            Optional<UUID> townUUID = possibleTown();
                            townUUID.ifPresent(this::buildTown);
                            break;
                        }
                    case 2:
                        if(canBuildCity()) {
                            Optional<UUID> cityUUID = possibleCity();
                            cityUUID.ifPresent(this::buildCity);
                            break;
                        }
                    case 3:
                        if(canBuyDevelopmentCard()) {
                            buyDevelopmentCard();
                            break;
                        }
                }
            }
        }

        endTurn();
        return this.aiActions;
    }

    private void startingTurnLogic() {
        boolean doneBuilding = false;
        for(MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if(doneBuilding) {
                break;
            }
            if(bn.getOccupiedByPlayer() == 666 && bn.getParent().getHexagons().size()==6) {
                for(MapGraph.StreetNode sn : bn.getConnectedStreetNodes()) {
                    if(sn.getOccupiedByPlayer() == 666) {
                        buildTown(bn.getUuid());
                        buildStreet(sn.getUuid());
                        doneBuilding = true;
                        break;
                    }
                }
            }
        }
    }
    private Optional<UUID> possibleStreet() {
        for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
            if (sn.getOccupiedByPlayer() == 666) {
                //if(sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                    return Optional.of(sn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> possibleTown() {
        for(MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if(bn.getOccupiedByPlayer() == 666) {
                //if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                    return Optional.of(bn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> possibleCity() {
        for(MapGraph.BuildingNode bn : mapGraph.getBuiltBuildings()) {
            if(bn.getOccupiedByPlayer() == game.getTurn()) {
                //if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                    return Optional.of(bn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }
}
