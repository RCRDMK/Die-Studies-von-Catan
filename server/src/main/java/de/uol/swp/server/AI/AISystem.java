package de.uol.swp.server.AI;

import de.uol.swp.common.game.trade.Trade;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AI.AIActions.AIAction;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Interface used for the AISystem
 *
 * @author Marc Hermes
 * @since 2021-05-08
 */
public interface AISystem {

    /**
     * Method used for ending turns
     *
     * @author Marc Hermes
     * @since 2021-05-08
     */
    void endTurn();

    /**
     * Method used for buying developmentCards
     *
     * @author Marc Hermes
     * @since 2021-05-08
     */
    void buyDevelopmentCard();

    /**
     * Method used for building a street
     *
     * @param field the UUID of the streetNode to be built
     * @author Marc Hermes
     * @since 2021-05-08
     */
    void buildStreet(UUID field);

    /**
     * Method used for building a town
     *
     * @param field the UUID of the buildingNode to be built
     * @author Marc Hermes
     * @since 2021-05-08
     */
    void buildTown(UUID field);

    /**
     * Method used for building a city
     *
     * @param field the UUID of the buildingNode to be upgraded to a city
     * @author Marc Hermes
     * @since 2021-05-08
     */
    void buildCity(UUID field);

    /**
     * Method used for starting a trade
     *
     * @param wishList  the List of items wanted to be received
     * @param offerList the List of items being offered
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void tradeStart(ArrayList<TradeItem> wishList, ArrayList<TradeItem> offerList);

    /**
     * Method used to participate in an ongoing trade through bidding
     *
     * @param bidList   the list of bids
     * @param tradeCode the String used for identifying the trade
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void tradeBid(ArrayList<TradeItem> bidList, String tradeCode);

    /**
     * Method used to accept offers from an initiated trade
     *
     * @param tradeCode      the String used for identifying the trade
     * @param tradeAccepted  boolean value, false when not accepted, true when accepted
     * @param acceptedBidder the User whose bid was accepted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void tradeOfferAccept(String tradeCode, boolean tradeAccepted, User acceptedBidder);

    /**
     * Method used to play and resolve the developmentCard "Knight"
     *
     * @param field the UUID of the field the robber will be moved to
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void playDevelopmentCardKnight(UUID field);

    /**
     * Method used to play and resolve the developmentCard "Monopoly"
     *
     * @param resource the String name of the resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void playDevelopmentCardMonopoly(String resource);

    /**
     * Method used to play and resolve the developmentCard "Road Building"
     *
     * @param street1 the UUID of the first streetNode
     * @param street2 the UUID of the second streetNode
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void playDevelopmentCardRoadBuilding(UUID street1, UUID street2);

    /**
     * Method used to play and resolve the developmentCard "Year of Plenty"
     *
     * @param resource1 the String name of the first resource
     * @param resource2 the String name of the second resource
     * @author Alexander Losse, Marc Hermes
     * @since 2021-05-08
     */
    void playDevelopmentCardYearOfPlenty(String resource1, String resource2);

    /**
     * Method used to move the robber, when a 7 is rolled with the dice
     *
     * @param field the UUID of the field the robber is to be moved to
     * @author Marc Hermes
     * @since 2021-05-08
     */
    void moveBandit(UUID field);

    /**
     * Method used to start the turn of the AI.
     * <p>
     * This method is called by the server to engage the AI to start it's turn.
     *
     * @param eyes the Dice value rolled at the start of the turn of the AI
     * @return an ArrayList of AIActions dedicated through the AI which the server will have to resolve
     * @author Marc Hermes
     * @since 2021-05-08
     */
    ArrayList<AIAction> startTurnAction(int eyes);

    /**
     * Method used to continue the turn of the AI.
     * <p>
     * This method is called by the server to re-engage the AI to continue it's turn after it stopped because
     * it had to wait on trade responses of the other players.
     *
     * @param trade     the Trade because of which the AI had to stop and wait in the first place
     * @param tradeCode the String identifying the Trade
     * @return an ArrayList of AIActions dedicated through the AI which the server will have to resolve
     */
    ArrayList<AIAction> continueTurnAction(Trade trade, String tradeCode);
}
