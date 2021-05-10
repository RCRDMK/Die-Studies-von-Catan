package de.uol.swp.server.AI;

import de.uol.swp.common.game.request.*;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.AI.AIActions.*;
import de.uol.swp.server.game.GameService;

import java.util.ArrayList;

public class AIToServerTranslator {


    public static void translate(ArrayList<AIAction> aiActions, GameService gameService) {

        for (AIAction aiAction : aiActions) {
            String aiActionName = aiAction.getActionType();
            String gameName = aiAction.getGameName();
            UserDTO user = (UserDTO) aiAction.getUser();

            if (aiAction instanceof EndTurnAction) {
                EndTurnAction eta = (EndTurnAction) aiAction;
                EndTurnRequest etr = new EndTurnRequest(gameName, user);
                gameService.onEndTurnRequest(etr);

            } else if (aiAction instanceof BuildAction) {
                BuildAction ba = (BuildAction) aiAction;
                String typeOfNode;
                switch (aiActionName) {
                    case "BuildTown":
                    case "BuildCity":
                        typeOfNode = "BuildingNode";
                        break;
                    default:
                        typeOfNode = "StreetNode";

                }
                ConstructionRequest cr = new ConstructionRequest(user, gameName, ba.getField(), typeOfNode);
                gameService.onConstructionMessage(cr);

            } else if (aiAction instanceof BuyDevelopmentCardAction) {
                BuyDevelopmentCardAction bdca = (BuyDevelopmentCardAction) aiAction;
                BuyDevelopmentCardRequest bdcr = new BuyDevelopmentCardRequest(user, gameName);
                gameService.onBuyDevelopmentCardRequest(bdcr);

            } else if (aiAction instanceof MoveBanditAction) {
                MoveBanditAction mba = (MoveBanditAction) aiAction;
                //TODO: move bandit stuff

            } else if (aiAction instanceof PlayDevelopmentCardAction) {
                PlayDevelopmentCardAction pda = (PlayDevelopmentCardAction) aiAction;
                PlayDevelopmentCardRequest pdcr = new PlayDevelopmentCardRequest(pda.getDevCard(), gameName, user);
                gameService.onPlayDevelopmentCardRequest(pdcr);

                if (aiAction instanceof PlayDevelopmentCardKnightAction) {
                    PlayDevelopmentCardKnightAction pka = (PlayDevelopmentCardKnightAction) aiAction;
                    //TODO: resolveDevelopmentCardKnight stuff

                } else if (aiAction instanceof PlayDevelopmentCardMonopolyAction) {
                    PlayDevelopmentCardMonopolyAction pma = (PlayDevelopmentCardMonopolyAction) aiAction;
                    ResolveDevelopmentCardMonopolyRequest rmr = new ResolveDevelopmentCardMonopolyRequest(pma.getDevCard(), user, gameName, pma.getResource());
                    gameService.onResolveDevelopmentCardRequest(rmr);

                } else if (aiAction instanceof PlayDevelopmentCardRoadBuildingAction) {
                    PlayDevelopmentCardRoadBuildingAction pba = (PlayDevelopmentCardRoadBuildingAction) aiAction;
                    ResolveDevelopmentCardRoadBuildingRequest rbr = new ResolveDevelopmentCardRoadBuildingRequest(pba.getDevCard(), user, gameName, pba.getStreet1(), pba.getStreet2());
                    gameService.onResolveDevelopmentCardRequest(rbr);

                } else if (aiAction instanceof PlayDevelopmentCardYearOfPlentyAction) {
                    PlayDevelopmentCardYearOfPlentyAction pya = (PlayDevelopmentCardYearOfPlentyAction) aiAction;
                    ResolveDevelopmentCardYearOfPlentyRequest ryr = new ResolveDevelopmentCardYearOfPlentyRequest(pya.getDevCard(), user, gameName, pya.getResource1(), pya.getResource2());
                    gameService.onResolveDevelopmentCardRequest(ryr);

                }

            } else if (aiAction instanceof TradeStartAction) {
                TradeStartAction tsa = (TradeStartAction) aiAction;
                TradeStartRequest tsr = new TradeStartRequest(user, gameName, tsa.getTradeCode());
                gameService.onTradeStartedRequest(tsr);
                TradeItemRequest tir = new TradeItemRequest(user, gameName, tsa.getOfferList(), tsa.getTradeCode(), tsa.getWishList());
                gameService.onTradeItemRequest(tir);

            } else if (aiAction instanceof TradeBidAction) {
                TradeBidAction tba = (TradeBidAction) aiAction;
                TradeItemRequest tir = new TradeItemRequest(user, gameName, tba.getBidList(), tba.getTradeCode(), tba.getBidList());
                gameService.onTradeItemRequest(tir);

            } else if (aiAction instanceof TradeOfferAcceptAction) {
                TradeOfferAcceptAction toaa = (TradeOfferAcceptAction) aiAction;
                TradeChoiceRequest tcr = new TradeChoiceRequest((UserDTO) toaa.getAcceptedBidder(), toaa.getTradeAccepted(), gameName, toaa.getTradeCode());
                gameService.onTradeChoiceRequest(tcr);
            }

        }
    }

}
