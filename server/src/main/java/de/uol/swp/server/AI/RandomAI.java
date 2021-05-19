package de.uol.swp.server.AI;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.server.AI.AIActions.AIAction;

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
        if (game.isStartingTurns()) {
            startingTurnLogic();
            System.out.println("IS starting turns");
        } else {
            // if a 7 was rolled, move the robber to a random hexagon
            if (game.getLastRolledDiceValue() == 7) {
                for (MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
                    if (!hx.isOccupiedByRobber()) {
                        moveBandit(hx.getUuid());
                        break;
                    }
                }
            }

            // do some random actions
            makeRandomActionsLogic();

            // try to play a developmentCard
            ArrayList<String> cards = canPlayDevelopmentCard();
            if (cards.size() > 0) {
                playDevelopmentCardLogic(cards);
            }
        }

        endTurn();
        return this.aiActions;
    }

    private void startingTurnLogic() {
        boolean doneBuilding = false;
        for (MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if (doneBuilding) {
                break;
            }
            if (bn.getOccupiedByPlayer() == 666 && bn.getParent().getHexagons().size() == 6) {
                for (MapGraph.StreetNode sn : bn.getConnectedStreetNodes()) {
                    if (sn.getOccupiedByPlayer() == 666) {
                        buildTown(bn.getUuid());
                        buildStreet(sn.getUuid());
                        doneBuilding = true;
                        break;
                    }
                }
            }
        }
    }

    private void playDevelopmentCardLogic(ArrayList<String> cards) {
        String cardToPlay = cards.get(randomInt(0, cards.size() - 1));
        switch (cardToPlay) {
            case "Year of Plenty":
                playDevelopmentCardYearOfPlenty(returnRandomResource(), returnRandomResource());
                break;
            case "Knight":
                UUID hexagon = null;
                for (MapGraph.Hexagon hx : mapGraph.getHexagonHashSet()) {
                    if (!hx.isOccupiedByRobber()) {
                        hexagon = hx.getUuid();
                        break;
                    }
                }
                playDevelopmentCardKnight(hexagon);
                break;
            case "Monopoly":
                playDevelopmentCardMonopoly(returnRandomResource());
                break;
            case "Road Building":
                UUID street1 = null;
                UUID street2 = null;
                int streets = 0;
                for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
                    if (sn.getOccupiedByPlayer() == 666) {
                        //if(sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                        if (street1 == null) {
                            street1 = sn.getUuid();
                            streets = streets + 1;
                        } else if (streets == 1) {
                            street2 = sn.getUuid();
                            break;
                        }
                        //}
                    }
                }
                if(street1 != null & street2 != null) {
                    playDevelopmentCardRoadBuilding(street1, street2);
                }
                break;

        }
    }

    private void makeRandomActionsLogic() {
        int amountOfActions = randomInt(0, 5);
        for (int i = 0; i <= amountOfActions; i++) {
            int actionType = randomInt(0, 3);
            switch (actionType) {
                case 0:
                    if (canBuildStreet()) {
                        Optional<UUID> streetUUID = returnPossibleStreet();
                        streetUUID.ifPresent(this::buildStreet);
                        break;
                    }
                case 1:
                    if (canBuildTown()) {
                        Optional<UUID> townUUID = returnPossibleTown();
                        townUUID.ifPresent(this::buildTown);
                        break;
                    }
                case 2:
                    if (canBuildCity()) {
                        Optional<UUID> cityUUID = returnPossibleCity();
                        cityUUID.ifPresent(this::buildCity);
                        break;
                    }
                case 3:
                    if (canBuyDevelopmentCard()) {
                        buyDevelopmentCard();
                        break;
                    }
            }
        }
    }

    private Optional<UUID> returnPossibleStreet() {
        for (MapGraph.StreetNode sn : mapGraph.getStreetNodeHashSet()) {
            if (sn.getOccupiedByPlayer() == 666) {
                //if(sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(sn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> returnPossibleTown() {
        for (MapGraph.BuildingNode bn : mapGraph.getBuildingNodeHashSet()) {
            if (bn.getOccupiedByPlayer() == 666) {
                //if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(bn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    private Optional<UUID> returnPossibleCity() {
        for (MapGraph.BuildingNode bn : mapGraph.getBuiltBuildings()) {
            if (bn.getOccupiedByPlayer() == game.getTurn()) {
                //if(bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                return Optional.of(bn.getUuid());
                //}
            }
        }
        return Optional.empty();
    }

    private String returnRandomResource() {
        String resource;
        switch (randomInt(0, 4)) {
            case 0:
                resource = "Ore";
                break;
            case 1:
                resource = "Brick";
                break;
            case 2:
                resource = "Lumber";
                break;
            case 3:
                resource = "Wool";
                break;
            case 4:
                resource = "Grain";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + randomInt(0, 4));
        }
        return resource;
    }
}
