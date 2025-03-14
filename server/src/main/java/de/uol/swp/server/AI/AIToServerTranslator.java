package de.uol.swp.server.AI;

import java.util.ArrayList;
import java.util.Optional;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.request.ConstructionRequest;
import de.uol.swp.common.game.request.DrawRandomResourceFromPlayerRequest;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.PlayDevelopmentCardRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardKnightRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardMonopolyRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardRoadBuildingRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardYearOfPlentyRequest;
import de.uol.swp.common.game.request.ResourcesToDiscardRequest;
import de.uol.swp.common.game.request.RobbersNewFieldRequest;
import de.uol.swp.common.game.request.TradeChoiceRequest;
import de.uol.swp.common.game.request.TradeItemRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.AI.AIActions.AIAction;
import de.uol.swp.server.AI.AIActions.BuildAction;
import de.uol.swp.server.AI.AIActions.BuyDevelopmentCardAction;
import de.uol.swp.server.AI.AIActions.DiscardResourcesAction;
import de.uol.swp.server.AI.AIActions.DrawRandomResourceFromPlayerAction;
import de.uol.swp.server.AI.AIActions.EndTurnAction;
import de.uol.swp.server.AI.AIActions.MoveBanditAction;
import de.uol.swp.server.AI.AIActions.PlayDevelopmentCardAction;
import de.uol.swp.server.AI.AIActions.PlayDevelopmentCardKnightAction;
import de.uol.swp.server.AI.AIActions.PlayDevelopmentCardMonopolyAction;
import de.uol.swp.server.AI.AIActions.PlayDevelopmentCardRoadBuildingAction;
import de.uol.swp.server.AI.AIActions.PlayDevelopmentCardYearOfPlentyAction;
import de.uol.swp.server.AI.AIActions.TradeBidAction;
import de.uol.swp.server.AI.AIActions.TradeOfferAcceptAction;
import de.uol.swp.server.AI.AIActions.TradeStartAction;
import de.uol.swp.server.game.GameService;

/**
 * Class used for translating between the AI and the Server
 *
 * @author Marc Hermes
 * @since 2021-05-11
 */
public class AIToServerTranslator {

    /**
     * This method is used to translate a list of AIActions into messages and requests the gameService can interpret
     * <p>
     * Depending on the class of the aiAction different messages and requests will be created.
     * These messages will then be used as parameters for the corresponding gameService method calls.
     *
     * @param aiActions   the ArrayList of AIActions the AI wants to do
     * @param gameService the GameService of the Server
     * @author Marc Hermes
     * @since 2021-05-11
     */
    public static void translate(ArrayList<AIAction> aiActions, GameService gameService) throws InterruptedException {

        for (AIAction aiAction : aiActions) {

            String aiActionName = aiAction.getActionType();
            String gameName = aiAction.getGameName();
            UserDTO user = (UserDTO) aiAction.getUser();

            // before every action put the thread of the AI to sleep, which forces a delay in it's actions,
            // thus increasing the readability of the game
            Optional<Game> game = gameService.getGameManagement().getGame(gameName);
            if (game.isPresent()) {
                if (game.get().getUsers().size() > 0 && !game.get().isUsedForTest()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (aiAction instanceof EndTurnAction) {
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
                gameService.onConstructionRequest(cr);

            } else if (aiAction instanceof BuyDevelopmentCardAction) {
                BuyDevelopmentCardRequest bdcr = new BuyDevelopmentCardRequest(user, gameName);
                gameService.onBuyDevelopmentCardRequest(bdcr);

            } else if (aiAction instanceof MoveBanditAction) {
                MoveBanditAction mba = (MoveBanditAction) aiAction;
                RobbersNewFieldRequest rnfm = new RobbersNewFieldRequest(gameName, user, mba.getField());
                gameService.onRobbersNewFieldRequest(rnfm);

            } else if (aiAction instanceof DiscardResourcesAction) {
                DiscardResourcesAction dra = (DiscardResourcesAction) aiAction;
                ResourcesToDiscardRequest rdr = new ResourcesToDiscardRequest(gameName, user,
                        dra.getResourcesToDiscard());
                gameService.onResourcesToDiscard(rdr);

            } else if (aiAction instanceof DrawRandomResourceFromPlayerAction) {
                DrawRandomResourceFromPlayerAction drrfpa = (DrawRandomResourceFromPlayerAction) aiAction;
                DrawRandomResourceFromPlayerRequest drrfpm = new DrawRandomResourceFromPlayerRequest(gameName, user,
                        drrfpa.getPlayerName(), drrfpa.getResource());
                gameService.onDrawRandomResourceFromPlayerRequest(drrfpm);

            } else if (aiAction instanceof PlayDevelopmentCardAction) {
                PlayDevelopmentCardAction pda = (PlayDevelopmentCardAction) aiAction;
                PlayDevelopmentCardRequest pdcr = new PlayDevelopmentCardRequest(pda.getDevCard(), gameName, user);
                gameService.onPlayDevelopmentCardRequest(pdcr);

                if (aiAction instanceof PlayDevelopmentCardKnightAction) {
                    PlayDevelopmentCardKnightAction pka = (PlayDevelopmentCardKnightAction) aiAction;
                    ResolveDevelopmentCardKnightRequest rkr = new ResolveDevelopmentCardKnightRequest(pka.getDevCard(),
                            user, gameName, pka.getField());
                    gameService.onResolveDevelopmentCardRequest(rkr);

                } else if (aiAction instanceof PlayDevelopmentCardMonopolyAction) {
                    PlayDevelopmentCardMonopolyAction pma = (PlayDevelopmentCardMonopolyAction) aiAction;
                    ResolveDevelopmentCardMonopolyRequest rmr = new ResolveDevelopmentCardMonopolyRequest(
                            pma.getDevCard(), user, gameName, pma.getResource());
                    gameService.onResolveDevelopmentCardRequest(rmr);

                } else if (aiAction instanceof PlayDevelopmentCardRoadBuildingAction) {
                    PlayDevelopmentCardRoadBuildingAction pba = (PlayDevelopmentCardRoadBuildingAction) aiAction;
                    ResolveDevelopmentCardRoadBuildingRequest rbr = new ResolveDevelopmentCardRoadBuildingRequest(
                            pba.getDevCard(), user, gameName, pba.getStreet1(), pba.getStreet2());
                    gameService.onResolveDevelopmentCardRequest(rbr);

                } else if (aiAction instanceof PlayDevelopmentCardYearOfPlentyAction) {
                    PlayDevelopmentCardYearOfPlentyAction pya = (PlayDevelopmentCardYearOfPlentyAction) aiAction;
                    ResolveDevelopmentCardYearOfPlentyRequest ryr = new ResolveDevelopmentCardYearOfPlentyRequest(
                            pya.getDevCard(), user, gameName, pya.getResource1(), pya.getResource2());
                    gameService.onResolveDevelopmentCardRequest(ryr);

                }

            } else if (aiAction instanceof TradeStartAction) {
                TradeStartAction tsa = (TradeStartAction) aiAction;
                TradeItemRequest tir = new TradeItemRequest(user, gameName, tsa.getOfferList(), tsa.getTradeCode(),
                        tsa.getWishList());
                gameService.onTradeItemRequest(tir);

            } else if (aiAction instanceof TradeBidAction) {
                TradeBidAction tba = (TradeBidAction) aiAction;
                TradeItemRequest tir = new TradeItemRequest(user, gameName, tba.getBidList(), tba.getTradeCode(),
                        tba.getBidList());
                gameService.onTradeItemRequest(tir);

            } else if (aiAction instanceof TradeOfferAcceptAction) {
                TradeOfferAcceptAction toaa = (TradeOfferAcceptAction) aiAction;
                TradeChoiceRequest tcr = new TradeChoiceRequest((UserDTO) toaa.getAcceptedBidder(),
                        toaa.getTradeAccepted(), gameName, toaa.getTradeCode());
                gameService.onTradeChoiceRequest(tcr);
            }
        }
    }
}
