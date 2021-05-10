package de.uol.swp.server.AI;

import de.uol.swp.server.AI.AIActions.*;
import de.uol.swp.server.game.GameService;

import java.util.ArrayList;

public class AIToServerTranslator {


    public static void translate(ArrayList<AIAction> aiActions, GameService gameService) {

        for (AIAction aiAction : aiActions) {
            if (aiAction instanceof EndTurnAction) {
                EndTurnAction eta = (EndTurnAction) aiAction;

            } else if (aiAction instanceof BuildAction) {


            } else if (aiAction instanceof BuyDevelopmentCardAction) {

            } else if (aiAction instanceof MoveBanditAction) {

            } else if (aiAction instanceof PlayDevelopmentCardAction) {
                if (aiAction instanceof PlayDevelopmentCardKnightAction) {

                } else if (aiAction instanceof PlayDevelopmentCardMonopolyAction) {

                } else if (aiAction instanceof PlayDevelopmentCardRoadBuildingAction) {

                } else if (aiAction instanceof PlayDevelopmentCardYearOfPlentyAction) {

                }

            } else if (aiAction instanceof TradeStartAction) {

            } else if (aiAction instanceof TradeBidAction) {

            } else if (aiAction instanceof TradeOfferAcceptAction) {
            }

        }
    }

}
