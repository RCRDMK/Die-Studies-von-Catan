package de.uol.swp.client.game;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.uol.swp.client.game.event.SummaryConfirmedEvent;
import de.uol.swp.common.game.dto.StatsDTO;
import de.uol.swp.common.game.message.TradeEndedMessage;
import de.uol.swp.common.game.request.BankBuyRequest;
import de.uol.swp.common.game.request.BankRequest;
import de.uol.swp.common.game.request.BuyDevelopmentCardRequest;
import de.uol.swp.common.game.request.ConstructionRequest;
import de.uol.swp.common.game.request.DrawRandomResourceFromPlayerRequest;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.GameLeaveUserRequest;
import de.uol.swp.common.game.request.KickPlayerRequest;
import de.uol.swp.common.game.request.PlayDevelopmentCardRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardKnightRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardMonopolyRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardRoadBuildingRequest;
import de.uol.swp.common.game.request.ResolveDevelopmentCardYearOfPlentyRequest;
import de.uol.swp.common.game.request.ResourcesToDiscardRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.game.request.RobbersNewFieldRequest;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.common.game.request.TradeChoiceRequest;
import de.uol.swp.common.game.request.TradeItemRequest;
import de.uol.swp.common.game.request.TradeStartRequest;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Class that manages games
 * <p>
 *
 * @author Carsten Dekker
 * @since 2021-01-13
 */

@SuppressWarnings("UnstableApiUsage")
public class GameService {

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
     * Posts a request to kick the player from the game on the Eventbus
     * <p>
     *
     * @param gameName     name of the game
     * @param user         lobby/game owner
     * @param playerToKick name of the player that will be kicked
     * @param toBan        boolean for true or false (if true, player will be banned from the game, is false player will be kicked.)
     * @author Iskander Yusupov
     * @see KickPlayerRequest
     * @since 2021-06-24
     */
    public void kickPlayer(String gameName, User user, String playerToKick, boolean toBan) {
        KickPlayerRequest kickPlayerRequest = new KickPlayerRequest(gameName, (UserDTO) user, playerToKick, toBan);
        eventBus.post(kickPlayerRequest);
    }


    /**
     * Return from Summary Screen to main Screen
     * <p>
     * Leaves the current game and posts a new SummaryConfirmedMessage on the eventbus
     *
     * @param statsDTO    needed for the gameName
     * @param currentUser needed for the user object
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
     * <p>Because there is always just one option of what one can build on any kind of Node, we don't need to include
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
     * @param tradeCode the tradeCode
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.TradeItemRequest
     * @since 2021-04-21
     */
    public void sendItem(UserDTO bidder, String gameName, ArrayList<TradeItem> bidItems, String tradeCode,
                         ArrayList<TradeItem> wishItems) {
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
     * Sends the choice of the buyer to the server
     *
     * @param gameName  game name
     * @param tradeCode the specific trade code
     * @author Alexander Losse, Ricardo Mook
     * @see de.uol.swp.common.game.request.TradeItemRequest
     * @since 2021-04-21
     */
    public void sendBuyChoice(String gameName, UserDTO user, String tradeCode, String cardName,
                              ArrayList<TradeItem> offer) {
        BankBuyRequest bbr = new BankBuyRequest(gameName, user, tradeCode, cardName, offer);
        eventBus.post(bbr);
    }

    /**
     * This method creates a bank request to buy a resource via BankRequest
     *
     * @param gameName  the game name
     * @param user      the user who wanna buy
     * @param tradeCode the tradeCode
     * @param cardName  the name form the card he wanna buy
     * @author Anton Nikiforov
     * @see BankRequest
     * @since 2021-05-29
     */
    public void createBankRequest(String gameName, UserDTO user, String tradeCode, String cardName) {
        eventBus.post(new BankRequest(gameName, user, tradeCode, cardName));
    }

    /**
     * Sends a TradeEndedMessage
     * <p>
     * used to close the TradeTab if no Trade is saved at the server, e.g. the seller hit the TradeButton by accident and doesn't want to Trade (didn't send a TradeItemRequest)
     *
     * @param gameName  String
     * @param tradeCode String
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    public void endTradeBeforeItStarted(String gameName, String tradeCode) {
        TradeEndedMessage tem = new TradeEndedMessage(gameName, tradeCode);
        eventBus.post(tem);
    }

    /**
     * Sends a request to start a trade
     *
     * @param joinedLobbyUser the user who wants to trade
     * @param currentLobby    the game in which the user wants to trade
     * @param tradeCode       the code of the trade
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-06-15
     */
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
        eventBus.post(new RobbersNewFieldRequest(game, (UserDTO) user, uuid));
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
        DrawRandomResourceFromPlayerRequest drawRandomResourceFromPlayerRequest = new DrawRandomResourceFromPlayerRequest(
                gameName, (UserDTO) user, result);
        eventBus.post(drawRandomResourceFromPlayerRequest);
    }

    /**
     * Sends a request to discard resources to the server
     *
     * @param gameName  the name in which the user will discard resources
     * @param user      the name of the user who will discard resources
     * @param inventory the rest-inventory not to be left over
     * @author Marc Hermes
     * @since 2021-06-15
     */
    public void discardResources(String gameName, User user, HashMap<String, Integer> inventory) {
        eventBus.post(new ResourcesToDiscardRequest(gameName, (UserDTO) user, inventory));
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
    public void resolveDevelopmentCardMonopoly(UserDTO joinedLobbyUser, String currentLobby, String devCard,
                                               String resource) {
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
    public void resolveDevelopmentCardYearOfPlenty(UserDTO joinedLobbyUser, String currentLobby, String devCard,
                                                   String resource1, String resource2) {
        eventBus.post(new ResolveDevelopmentCardYearOfPlentyRequest(devCard, joinedLobbyUser, currentLobby, resource1,
                resource2));
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
    public void resolveDevelopmentCardRoadBuilding(UserDTO joinedLobbyUser, String currentLobby, String devCard,
                                                   UUID street1, UUID street2) {
        eventBus.post(new ResolveDevelopmentCardRoadBuildingRequest(devCard, joinedLobbyUser, currentLobby, street1,
                street2));
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

    /**
     * sends a request to end the turn to the server
     *
     * @param joinedLobbyUser the user who wants to end his turn
     * @param currentLobby    the name of the game in which the user wants to end the turn
     * @author Marc Hermes
     * @since 2021-06-15
     */
    public void endTurn(UserDTO joinedLobbyUser, String currentLobby) {
        eventBus.post(new EndTurnRequest(currentLobby, joinedLobbyUser));
    }
}
