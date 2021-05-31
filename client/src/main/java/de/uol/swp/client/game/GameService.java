package de.uol.swp.client.game;


import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.game.event.SummaryConfirmedEvent;
import de.uol.swp.common.game.dto.StatsDTO;
import de.uol.swp.common.game.message.RobbersNewFieldMessage;
import de.uol.swp.common.game.message.TradeEndedMessage;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Class that manages games
 * <p>
 *
 * @author Carsten Dekker
 * @since 2021-01-13
 */

@SuppressWarnings("UnstableApiUsage")
public class GameService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);

    private final EventBus eventBus;

    @Inject
    public GameService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Creates a new RollDiceRequest and puts it on the Eventbus
     * <p>
     *
     * @param name Name of the lobby/game where the user wants to roll the dice
     * @param user User who wants to roll the dice
     * @author Kirstin, Pieter
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @since 2021-01-07
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     * <p>
     * I have changed the place of the method to the new GameService. It is a temporary method.
     */

    public void rollDice(String name, UserDTO user) {
        RollDiceRequest rollDiceRequest = new RollDiceRequest(name, user);
        eventBus.post(rollDiceRequest);
    }

    /**
     * Posts a request to get a list of all existing games on the EventBus
     *
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.request.RetrieveAllGamesRequest
     * @since 2020-04-12
     */
    public void retrieveAllGames() {
        RetrieveAllGamesRequest cmd = new RetrieveAllGamesRequest();
        eventBus.post(cmd);
    }

    /**
     * Creates a new RetrieveAllThisGameUsersRequest and puts it on the Eventbus
     * <p>
     *
     * @param gameName Name of the game of which the User list was requested
     * @author Iskander Yusupov
     * @see de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest
     * @since 2020-03-14
     */
    public void retrieveAllThisGameUsers(String gameName) {
        RetrieveAllThisGameUsersRequest gameUsersRequest = new RetrieveAllThisGameUsersRequest(gameName);
        eventBus.post(gameUsersRequest);
    }

    /**
     * Posts a request to leave the game on the Eventbus
     *
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.GameLeaveUserRequest
     * @since 2021-03-04
     */
    public void leaveGame(String game, User user) {
        GameLeaveUserRequest leaveRequest = new GameLeaveUserRequest(game, (UserDTO) user);
        eventBus.post(leaveRequest);
    }

    /**
     * Return from Summary Screen to main Screen
     * <p>
     * Leaves the current game and posts a new SummaryConfirmedMessage on the eventbus
     *
     * @param statsDTO    needed for the gamename
     * @param currentUser needed for the userobject
     * @author René Meyer
     * @since 2021-05-08
     */
    public void returnFromSummaryScreen(StatsDTO statsDTO, User currentUser) {
        this.leaveGame(statsDTO.getGameName(), currentUser);
        eventBus.post(new SummaryConfirmedEvent(statsDTO.getGameName(), currentUser));
    }

    /**
     * Posts a request to buy a development card on the eventbus
     *
     * @author Marius Birk
     * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
     * @since 2021-04-03
     */
    public void buyDevelopmentCard(User user, String gameName) {
        BuyDevelopmentCardRequest buyDevelopmentCardRequest = new BuyDevelopmentCardRequest((UserDTO) user, gameName);
        eventBus.post(buyDevelopmentCardRequest);
    }

    /**
     * Sends the request to build a building.
     * <p>Because there is always just one option of what one can build on any kind of Node, we dont need to include
     * the type of building that is desired.</p>
     *
     * @author Pieter Vogt
     * @see de.uol.swp.common.game.MapGraph
     * @since 2021-04-14
     */
    public void constructBuilding(UserDTO user, String gameName, UUID uuid, String typeOfNode) {
        ConstructionRequest message = new ConstructionRequest(user, gameName, uuid, typeOfNode);
        eventBus.post(message);
    }

    /**
     * This methods sends the added trade items to the server via an TradeItemRequest
     *
     * @param bidder    the bidder
     * @param gameName  the game name
     * @param bidItems  the bid items
     * @param tradeCode the tradecode
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.TradeItemRequest
     * @since 2021-04-21
     */
    public void sendItem(UserDTO bidder, String gameName, ArrayList<TradeItem> bidItems, String tradeCode, ArrayList<TradeItem> wishItems) {
        TradeItemRequest tir = new TradeItemRequest(bidder, gameName, bidItems, tradeCode, wishItems);
        eventBus.post(tir);
    }

    /**
     * Sends the choice of the seller to the server
     *
     * @param tradePartner  the user from which the offer is accepted
     * @param tradeAccepted boolean for true or false
     * @param gameName      game name
     * @param tradeCode     the specific trade code
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.TradeItemRequest
     * @since 2021-04-21
     */
    public void sendTradeChoice(UserDTO tradePartner, Boolean tradeAccepted, String gameName, String tradeCode) {
        TradeChoiceRequest tcr = new TradeChoiceRequest(tradePartner, tradeAccepted, gameName, tradeCode);
        eventBus.post(tcr);
    }

    /**
     * Sends a TradeEndedMessage
     * <p>
     * used to close the TradeTab if no Trade is saved at the server, e.g. the seller hit the TradeButton by accident and doesnt want to Trade( didnt send a TradeItemRequest)
     *
     * @param tradeCode String
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    public void endTradeBeforeItStarted(String tradeCode) {
        TradeEndedMessage tem = new TradeEndedMessage(tradeCode);
        eventBus.post(tem);
    }

    public void sendTradeStartedRequest(UserDTO joinedLobbyUser, String currentLobby, String tradeCode) {
        eventBus.post(new TradeStartRequest(joinedLobbyUser, currentLobby, tradeCode));
    }

    /**
     * This method sends a RobbersNewFieldMessage to the server and updates the position of the robber.
     *
     * @param game String of the gameName
     * @param user String of the userName that invoked the method.
     * @param uuid UUID of the hexagon, where the user wants to move the robber.
     * @author Marius Birk
     * @since 2021-04-24
     */
    public void movedRobber(String game, User user, UUID uuid) {
        eventBus.post(new RobbersNewFieldMessage(game, (UserDTO) user, uuid));
    }

    /**
     * This method will be invoked if the robber is moved to a field, where a user has occupied a buildingNode.
     *
     * @param gameName String of the gameName
     * @param user     String of the userName that invoked the method.
     * @param result   String of the userName from that the card will be drawn.
     * @author Marius Birk
     * @since 2021-04-24
     */
    public void drawRandomCardFromPlayer(String gameName, User user, String result) {
        DrawRandomResourceFromPlayerRequest drawRandomResourceFromPlayerRequest = new DrawRandomResourceFromPlayerRequest(gameName, (UserDTO) user, result);
        eventBus.post(drawRandomResourceFromPlayerRequest);
    }

    /**
     * Sends a request to play a certain DevelopmentCard to the server
     *
     * @param joinedLobbyUser the user who wants to play the card
     * @param currentLobby    the name of the game in which the card is to be played
     * @param devCard         the name of the DevelopmentCard
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public void playDevelopmentCard(UserDTO joinedLobbyUser, String currentLobby, String devCard) {
        eventBus.post(new PlayDevelopmentCardRequest(devCard, currentLobby, joinedLobbyUser));
    }

    /**
     * Sends a request to resolve the Monopoly DevelopmentCard to the server
     *
     * @param joinedLobbyUser the user who wants to resolve the Monopoly card
     * @param currentLobby    the name of the game in which the card is to be resolved
     * @param devCard         the name of the DevelopmentCard, should be Monopoly
     * @param resource        the name of the resource for the Monopoly card
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public void resolveDevelopmentCardMonopoly(UserDTO joinedLobbyUser, String currentLobby, String devCard, String resource) {
        eventBus.post(new ResolveDevelopmentCardMonopolyRequest(devCard, joinedLobbyUser, currentLobby, resource));
    }

    /**
     * Sends a request to resolve the Year of Plenty DevelopmentCard to the server
     *
     * @param joinedLobbyUser the user who wants to resolve the Year of Plenty card
     * @param currentLobby    the name of the game in which the card is to be resolved
     * @param devCard         the name of the DevelopmentCard, should be Year of Plenty
     * @param resource1       the name of the first resource for the Year of Plenty card
     * @param resource2       the name of the second resource for the Year of Plenty card
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public void resolveDevelopmentCardYearOfPlenty(UserDTO joinedLobbyUser, String currentLobby, String devCard, String resource1, String resource2) {
        eventBus.post(new ResolveDevelopmentCardYearOfPlentyRequest(devCard, joinedLobbyUser, currentLobby, resource1, resource2));
    }

    /**
     * Sends a request to resolve the Road Building DevelopmentCard to the server
     *
     * @param joinedLobbyUser the user who wants to resolve the Road Building card
     * @param currentLobby    the name of the game in which the card is to be resolved
     * @param devCard         the name of the DevelopmentCard, should be Road Building
     * @param street1         the UUID of the first street for the Road Building card
     * @param street2         the UUID of the second street for the Road Building card
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public void resolveDevelopmentCardRoadBuilding(UserDTO joinedLobbyUser, String currentLobby, String devCard, UUID street1, UUID street2) {
        eventBus.post(new ResolveDevelopmentCardRoadBuildingRequest(devCard, joinedLobbyUser, currentLobby, street1, street2));
    }

    /**
     * sends a request to resolve the Knight DevelopmentCard to the server
     *
     * @param joinedLobbyUser the user who wants to resolve the Knight card
     * @param currentLobby    the name of the game in which the card is to be resolved
     * @param devCard         the name of the DevelopmentCard, should be Knight
     * @param field           the UUID of the field to move the robber to
     * @author Marc Hermes
     * @since 2021-05-14
     */
    public void resolveDevelopmentCardKnight(UserDTO joinedLobbyUser, String currentLobby, String devCard, UUID field) {
        eventBus.post(new ResolveDevelopmentCardKnightRequest(devCard, joinedLobbyUser, currentLobby, field));
    }
}
