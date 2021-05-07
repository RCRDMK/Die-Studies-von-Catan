package de.uol.swp.server.AI;

import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;
import java.util.UUID;

public interface AISystem {

    void endTurn();

    void buyDevelopmentCard();

    void placeStreet(UUID field);

    void placeTown(UUID field);

    void placeCity(UUID field);

    void tradeStart(ArrayList<TradeItem> wishList, ArrayList<TradeItem> offerList);

    void tradeBid(ArrayList<TradeItem> bidList, String tradeCode);

    void tradeOfferAccept(String tradeCode, boolean tradeAccepted, User acceptedBidder);

    void playDevelopmentCardKnight(UUID field);

    void playDevelopmentCardMonopoly(String resource);

    void playDevelopmentCardRoadBuilding(UUID street1, UUID street2);

    void playDevelopmentCardYearOfPlenty(String resource1, String resource2);

    void moveBandit(UUID field);

    ArrayList<AIAction> startTurnAction(int eyes);

    ArrayList<AIAction> continueTurnAction(Trade trade, String tradeCode);
}
