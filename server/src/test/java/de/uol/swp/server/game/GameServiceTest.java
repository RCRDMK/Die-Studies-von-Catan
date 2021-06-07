package de.uol.swp.server.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.PlayDevelopmentCardResponse;
import de.uol.swp.common.game.response.ResolveDevelopmentCardNotSuccessfulResponse;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.server.AI.AIToServerTranslator;
import de.uol.swp.server.AI.TestAI;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {

    final EventBus bus = new EventBus();
    GameManagement gameManagement = new GameManagement();
    LobbyManagement lobbyManagement = new LobbyManagement();
    MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, userManagement), bus);
    UserService userService = new UserService(bus, userManagement);
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus, userService);

    UserDTO userDTO = new UserDTO("test1", "47b7d407c2e2f3aff0e21aa16802006ba1793fd47b2d3cacee7cf7360e751bff7b7d0c7946b42b97a5306c6708ab006d0d81ef41a0c9f94537a2846327c51236", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("test2", "994dac907995937160371992ecbdf9b34242db0abb3943807b5baa6be0c6908f72ea87b7dadd2bce6cf700c8dfb7d57b0566f544af8c30336a15d5f732d85613", "carsten.stahl@uol.de");
    UserDTO userDTO2 = new UserDTO("test3", "b74a37371ca548bfd937410737b27f383e03021766e90f1180169691b8b15fc50aef49932c7413c0450823777ba46a34fd649b4da20b2e701c394c582ff6df55", "peterlustig@uol.de");
    UserDTO userDTO3 = new UserDTO("test4", "65dfe56dd0e9117907b11e440d99a667527ddb13244aa38f79d3ae61ee0b2ab4047c1218c4fb05d84f88b914826c45de3ab27a611ea910a4b14733ab1e32b125", "test.lustig@uol.de");

    Object event;

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
    }

    public GameServiceTest() throws SQLException {
    }

    void loginUsers() {
        bus.post(new RegisterUserRequest(userDTO));
        bus.post(new RegisterUserRequest(userDTO1));
        bus.post(new RegisterUserRequest(userDTO2));
        bus.post(new RegisterUserRequest(userDTO3));
        authenticationService.onLoginRequest(new LoginRequest(userDTO.getUsername(), userDTO.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO1.getUsername(), userDTO1.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO2.getUsername(), userDTO2.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO3.getUsername(), userDTO3.getPassword()));
    }

    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    @AfterEach
    void logOutAllUsers() {
        userManagement.logout(userDTO);
        userManagement.logout(userDTO1);
        userManagement.logout(userDTO2);
        userManagement.logout(userDTO3);
    }

    /**
     * Test checks if created lobby exists, then it is joined by userDTO1.
     * <p>
     * Test also checks whether a game that is referenced by the RetrieveAllThisGameUsersRequest
     * <p>
     * is also the same as the game itself.
     * <p>
     * The game that was created by the User userDTO and joined by userDTO1 is checked whether the
     * <p>
     * game has references to the session of the users that joined the game.
     *
     * @author Iskander Yusupov
     * @since 2020-03-14
     */
    @Test
    void onRetrieveAllThisGameUsersRequest() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), null, "Standard");
        Optional<Game> game = gameManagement.getGame(lobby.get().getName());
        RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest = new RetrieveAllThisGameUsersRequest(lobby.get().getName());
        assertSame(gameManagement.getGame(lobby.get().getName()).get().getName(), retrieveAllThisGameUsersRequest.getName());
        List<Session> gameUsers = authenticationService.getSessions(game.get().getUsers());
        for (Session session : gameUsers) {
            assertTrue(userDTO == (session.getUser()) && userDTO1 == (session.getUser()));
        }

    }

    /**
     * Test checks if created lobby exists, then it is joined by userDTO1 and userDTO2.
     * <p>
     * Test also checks whether a game that is referenced by the RetrieveAllThisGameUsersRequest
     * <p>
     * is also the same as the game itself.
     * <p>
     * The game that was created by the User userDTO and joined by userDTO1 and userDTO2 is checked whether the
     * <p>
     * game has references to the session of the users that joined the game.
     *
     * @author Iskander Yusupov
     * @since 2020-03-14
     */
    @Test
    void onRetrieveAllThisGameUsersRequest3() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), null, "Standard");
        Optional<Game> game = gameManagement.getGame(lobby.get().getName());
        assertTrue(game.isPresent());
        RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest = new RetrieveAllThisGameUsersRequest(lobby.get().getName());
        assertSame(gameManagement.getGame(lobby.get().getName()).get().getName(), retrieveAllThisGameUsersRequest.getName());
        List<Session> gameUsers = authenticationService.getSessions(game.get().getUsers());
        for (Session session : gameUsers) {
            assertTrue(userDTO == (session.getUser()) || userDTO1 == (session.getUser()) && userDTO2 == (session.getUser()));
        }

    }

    /**
     * Test checks if created lobby exists, then it is joined by userDTO1, userDTO2 and userDTO3.
     * <p>
     * Test also checks whether a game that is referenced by the RetrieveAllThisGameUsersRequest
     * <p>
     * is also the same as the game itself.
     * <p>
     * The game that was created by the User userDTO and joined by userDTO1, userDTO2 and userDTO3 is checked whether the
     * <p>
     * game has references to the session of the users that joined the game.
     *
     * @author Iskander Yusupov
     * @since 2020-03-14
     */
    @Test
    void onRetrieveAllThisGameUsersRequest4() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        lobby.get().joinUser(userDTO3);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), null, "Standard");
        Optional<Game> game = gameManagement.getGame(lobby.get().getName());
        assertTrue(game.isPresent());
        RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest = new RetrieveAllThisGameUsersRequest(lobby.get().getName());
        assertSame(gameManagement.getGame(lobby.get().getName()).get().getName(), retrieveAllThisGameUsersRequest.getName());
        List<Session> gameUsers = authenticationService.getSessions(game.get().getUsers());
        for (Session session : gameUsers) {
            assertTrue(userDTO == (session.getUser()) && userDTO1 == (session.getUser()) && userDTO2 == (session.getUser()) && userDTO3 == (session.getUser()));
        }
    }

    /**
     * Test checks if created lobby exists, then it is joined by userDTO1 and userDTO2.
     * <p>
     * Test also checks whether a game that is referenced by the RetrieveAllThisGameUsersRequest
     * <p>
     * is also the same as the game itself.
     * <p>
     * userDTO1 leaves the game and test checks that the game doesn't contain him.
     * <p>
     * The game is checked that it has ONLY references to the session of the userDTO, userDTO2 and userDTO3.
     * <p>
     * userDTO2 leaves the game and test checks that the game doesn't contain him.
     * <p>
     * The game is checked that it has ONLY references to the session of the userDTO and userDTO3.
     * <p>
     * userDTO3 leaves the game and test checks that the game doesn't contain him.
     * <p>
     * The game is checked that it has ONLY references to the session of the userDTO.
     *
     * @author Iskander Yusupov
     * @since 2020-03-14
     */

    @Test
    void onRetrieveAllThisGameUsersRequestUserLeft() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest = new RetrieveAllThisGameUsersRequest(lobby.getName());
        assertSame(gameManagement.getGame(lobby.getName()).get().getName(), retrieveAllThisGameUsersRequest.getName());
        GameLeaveUserRequest gameLeaveUserRequest = new GameLeaveUserRequest(lobby.getName(), userDTO1);
        gameLeaveUserRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest);
        gameService.onRetrieveAllThisGameUsersRequest(retrieveAllThisGameUsersRequest);
        assertFalse(game.getUsers().contains(userDTO1));

        GameLeaveUserRequest gameLeaveUserRequest2 = new GameLeaveUserRequest(lobby.getName(), userDTO2);
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest2);
        assertFalse(game.getUsers().contains(userDTO1));

        GameLeaveUserRequest gameLeaveUserRequest3 = new GameLeaveUserRequest(lobby.getName(), userDTO3);
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest3);
        assertFalse(game.getUsers().contains(userDTO3));

        GameLeaveUserRequest gameLeaveUserRequest4 = new GameLeaveUserRequest(lobby.getName(), userDTO);
        gameLeaveUserRequest4.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest4);
        assertFalse(gameManagement.getGame(lobby.getName()).isPresent());
    }

    /**
     * This test checks if the distributeResource method works as intended.
     * <p>
     * We create a new gameService and we create a new game. After that we assert that, the game is present and we join some user to the game.
     * We setup the inventories and the UserArrayList. We assume that we rolled a 5 and give that to our method.
     * To check if it works fine, we assert that it incremented with one.
     *
     * @author Marius Birk, Carsten Dekker
     * @since 2021-04-06
     */
    /*
    @Test

    //TODO: This test needs to be reactivated after the dependencies to obsolete classes had been fixed
   @Test
    void onDistributeResourcesTest() {
        GameService gameService1 = new GameService(gameManagement, lobbyService, authenticationService, bus);

        gameManagement.createGame("test", userDTO, "Standard");
        Optional<Game> game = gameManagement.getGame("test");
        assertTrue(game.isPresent());

        game.get().joinUser(userDTO1);
        game.get().joinUser(userDTO2);
        game.get().joinUser(userDTO3);

        game.get().setUpUserArrayList();
        game.get().setUpInventories();

        int diceEyes = 5;
        gameService1.distributeResources(diceEyes, "test");

        assertEquals(game.get().getInventory(userDTO).lumber.getNumber(), 1);
        assertEquals(game.get().getInventory(userDTO).grain.getNumber(), 1);
    }*/

    /**
     * This test checks if the trading mechanism works properly.
     * <p>
     * logs in the users
     * fills the inventory of the users
     * checks if the number of trades in game is 0
     * starts the trade, seller is userDTO
     * checks if TradeInformsBiddersMessage is send
     * checks if only 1 trade is in game
     * checks if there are 0 bidders
     * checks if the right trade was created
     * sends a TradeItemRequest for userDTO1 with too many items
     * checks if TradeCardErrorMessage is send
     * checks if no bidder was added to the trade
     * checks if no trade was added to the game
     * sends a TradeItemRequest for userDTO1 with the right amount of items
     * checks if no TradeInformSellerAboutBidsMessage was send
     * checks if a bidder was added to the trade
     * checks if no trade was added to the game
     * sends a TradeItemRequest for userDTO2 with the right amount of items
     * checks if no TradeInformSellerAboutBidsMessage was send
     * checks if a bidder was added to the trade
     * sends a TradeItemRequest for userDTO3 with the right amount of items
     * checks if a TradeInformSellerAboutBidsMessage was send
     * sends TradeChoiceRequest with userDTO1 as accepted bidder
     * checks if the right amount of items were traded
     * checks if TradeEndedMessage was send
     *
     * @author Alexander Losse
     * @see TradeEndedMessage
     * @see TradeOfferInformBiddersMessage
     * @see TradeInformSellerAboutBidsMessage
     * @see TradeItemRequest
     * @see TradeChoiceRequest
     * @since 2021-04-30
     */
    @Test
    public void TradeTest() {
        String tradeCode = "seller1acv";
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        //fill Inventory userDTO
        game.getInventory(userDTO).lumber.setNumber(0);
        game.getInventory(userDTO).incCardStack("Lumber", 10);
        game.getInventory(userDTO).ore.setNumber(0);
        game.getInventory(userDTO).incCardStack("Ore", 10);
        //fill Inventory userDTO1
        game.getInventory(userDTO1).ore.setNumber(0);
        game.getInventory(userDTO1).incCardStack("Ore", 10);
        //fill Inventory userDTO2
        game.getInventory(userDTO2).grain.setNumber(0);
        game.getInventory(userDTO2).incCardStack("Grain", 10);
        //fill Inventory userDTO3
        game.getInventory(userDTO3).wool.setNumber(0);
        game.getInventory(userDTO3).incCardStack("Wool", 10);

        //tests the tradestart
        TradeItem sellerItemLumber = new TradeItem("Lumber", 5);
        TradeItem sellerItemOre = new TradeItem("Ore", 10);
        TradeItem sellerItemBrick = new TradeItem("Brick", 0);
        TradeItem sellerItemWool = new TradeItem("Wool", 0);
        TradeItem sellerItemGrain = new TradeItem("Grain", 0);

        ArrayList<TradeItem> sellerItems = new ArrayList<TradeItem>();
        ArrayList<TradeItem> wishItems = new ArrayList<TradeItem>();
        sellerItems.add(sellerItemLumber);
        sellerItems.add(sellerItemBrick);
        sellerItems.add(sellerItemWool);
        sellerItems.add(sellerItemGrain);
        sellerItems.add(sellerItemOre);

        TradeItemRequest sellerItemRequest = new TradeItemRequest(userDTO, game.getName(), sellerItems, tradeCode, wishItems);

        assertTrue(game.getTradeList().isEmpty());
        gameService.onTradeItemRequest(sellerItemRequest);
        assertTrue(event instanceof TradeOfferInformBiddersMessage);
        assertTrue(game.getTradeList().containsKey(tradeCode));
        assertEquals(game.getTradeList().size(), 1);
        assertEquals(sellerItemRequest.getUser().getUsername(), game.getTradeList().get(tradeCode).getSeller().getUsername());
        assertTrue(game.getTradeList().get(tradeCode).getBidders().isEmpty());

        //test bidder1 with too much items offered
        TradeItem bidder1ItemLumber = new TradeItem("Lumber", 5);
        TradeItem bidder1ItemOre = new TradeItem("Ore", 4);
        TradeItem bidder1ItemBrick = new TradeItem("Brick", 0);
        TradeItem bidder1ItemWool = new TradeItem("Wool", 15);
        TradeItem bidder1ItemGrain = new TradeItem("Grain", 20);

        ArrayList<TradeItem> bidder1ItemsWrong = new ArrayList<TradeItem>();
        ArrayList<TradeItem> bidder1wishItems = new ArrayList<TradeItem>();
        bidder1ItemsWrong.add(bidder1ItemLumber);
        bidder1ItemsWrong.add(bidder1ItemBrick);
        bidder1ItemsWrong.add(bidder1ItemWool);
        bidder1ItemsWrong.add(bidder1ItemGrain);
        bidder1ItemsWrong.add(bidder1ItemOre);

        TradeItemRequest bidder1ItemRequest = new TradeItemRequest(userDTO1, game.getName(), bidder1ItemsWrong, tradeCode, bidder1wishItems);
        gameService.onTradeItemRequest(bidder1ItemRequest);

        assertTrue(event instanceof TradeCardErrorMessage);
        assertTrue(game.getTradeList().get(tradeCode).getBidders().isEmpty());
        assertTrue(game.getTradeList().get(tradeCode).getBids().isEmpty());
        assertEquals(game.getTradeList().size(), 1);

        //bidder1 with right amount
        bidder1ItemLumber = new TradeItem("Lumber", 0);
        bidder1ItemWool = new TradeItem("Wool", 0);
        bidder1ItemGrain = new TradeItem("Grain", 0);

        ArrayList<TradeItem> bidder1ItemsRight = new ArrayList<TradeItem>();
        bidder1ItemsRight.add(bidder1ItemLumber);
        bidder1ItemsRight.add(bidder1ItemBrick);
        bidder1ItemsRight.add(bidder1ItemWool);
        bidder1ItemsRight.add(bidder1ItemGrain);
        bidder1ItemsRight.add(bidder1ItemOre);

        bidder1ItemRequest = new TradeItemRequest(userDTO1, game.getName(), bidder1ItemsRight, tradeCode, bidder1wishItems);
        gameService.onTradeItemRequest(bidder1ItemRequest);

        assertFalse(event instanceof TradeInformSellerAboutBidsMessage);
        assertEquals(game.getTradeList().get(tradeCode).getBidders().size(), 1);
        assertEquals(game.getTradeList().size(), 1);

        //test bidder2
        TradeItem bidder2ItemLumber = new TradeItem("Lumber", 0);
        TradeItem bidder2ItemOre = new TradeItem("Ore", 0);
        TradeItem bidder2ItemBrick = new TradeItem("Brick", 0);
        TradeItem bidder2ItemWool = new TradeItem("Wool", 0);
        TradeItem bidder2ItemGrain = new TradeItem("Grain", 10);

        ArrayList<TradeItem> bidder2Items = new ArrayList<TradeItem>();
        ArrayList<TradeItem> bidder2wishItems = new ArrayList<TradeItem>();
        bidder2Items.add(bidder2ItemLumber);
        bidder2Items.add(bidder2ItemBrick);
        bidder2Items.add(bidder2ItemWool);
        bidder2Items.add(bidder2ItemGrain);
        bidder2Items.add(bidder2ItemOre);

        TradeItemRequest bidder2ItemRequest = new TradeItemRequest(userDTO2, game.getName(), bidder2Items, tradeCode, bidder2wishItems);
        gameService.onTradeItemRequest(bidder2ItemRequest);

        assertFalse(event instanceof TradeInformSellerAboutBidsMessage);
        assertEquals(game.getTradeList().get(tradeCode).getBidders().size(), 2);

        //test bidder3
        TradeItem bidder3ItemLumber = new TradeItem("Lumber", 0);
        TradeItem bidder3ItemOre = new TradeItem("Ore", 0);
        TradeItem bidder3ItemBrick = new TradeItem("Brick", 0);
        TradeItem bidder3ItemWool = new TradeItem("Wool", 10);
        TradeItem bidder3ItemGrain = new TradeItem("Grain", 0);

        ArrayList<TradeItem> bidder3Items = new ArrayList<TradeItem>();
        ArrayList<TradeItem> bidder3wishItems = new ArrayList<TradeItem>();
        bidder3Items.add(bidder3ItemLumber);
        bidder3Items.add(bidder3ItemBrick);
        bidder3Items.add(bidder3ItemWool);
        bidder3Items.add(bidder3ItemGrain);
        bidder3Items.add(bidder3ItemOre);

        TradeItemRequest bidder3ItemRequest = new TradeItemRequest(userDTO3, game.getName(), bidder3Items, tradeCode, bidder3wishItems);
        gameService.onTradeItemRequest(bidder3ItemRequest);

        assertTrue(event instanceof TradeInformSellerAboutBidsMessage);
        assertEquals(game.getTradeList().get(tradeCode).getBidders().size(), 3);

        //TradeChoice
        TradeChoiceRequest tradeChoiceRight = new TradeChoiceRequest(userDTO1, true, game.getName(), tradeCode);
        gameService.onTradeChoiceRequest(tradeChoiceRight);

        assertEquals(game.getInventory(userDTO).ore.getNumber(), 4);
        assertEquals(game.getInventory(userDTO).lumber.getNumber(), 5);
        assertEquals(game.getInventory(userDTO).grain.getNumber(), 0);
        assertEquals(game.getInventory(userDTO).brick.getNumber(), 0);
        assertEquals(game.getInventory(userDTO).wool.getNumber(), 0);

        assertEquals(game.getInventory(userDTO1).ore.getNumber(), 16);
        assertEquals(game.getInventory(userDTO1).lumber.getNumber(), 5);
        assertEquals(game.getInventory(userDTO1).grain.getNumber(), 0);
        assertEquals(game.getInventory(userDTO1).brick.getNumber(), 0);
        assertEquals(game.getInventory(userDTO1).wool.getNumber(), 0);

        assertEquals(game.getInventory(userDTO2).ore.getNumber(), 0);
        assertEquals(game.getInventory(userDTO2).lumber.getNumber(), 0);
        assertEquals(game.getInventory(userDTO2).grain.getNumber(), 10);
        assertEquals(game.getInventory(userDTO2).brick.getNumber(), 0);
        assertEquals(game.getInventory(userDTO2).wool.getNumber(), 0);

        assertEquals(game.getInventory(userDTO3).ore.getNumber(), 0);
        assertEquals(game.getInventory(userDTO3).lumber.getNumber(), 0);
        assertEquals(game.getInventory(userDTO3).grain.getNumber(), 0);
        assertEquals(game.getInventory(userDTO3).brick.getNumber(), 0);
        assertEquals(game.getInventory(userDTO3).wool.getNumber(), 10);
        assertTrue(event instanceof PublicInventoryChangeMessage);
    }

    /**
     * This test checks if the distributeResource method works as intended
     * <p>
     * We create a new gameService and we create a new game. After that we assert that, the game is present and we
     * join some user to the game. We setup the inventories and the UserArrayList. We built at every possible
     * buildingspot. We assume that we rolled a 5 and give that to our method. To check if it works fine, we
     * assert that it incremented with 6.
     *
     * @author Philip Nitsche
     * @since 2021-04-26
     */
    @Test
    void distributeResourcesTest() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        for (MapGraph.BuildingNode b : game.getMapGraph().getBuildingNodeHashSet()) {
            b.buildOrDevelopSettlement(1);
        }

        Map<String, Integer> inventoryEmpty = new HashMap<>();
        inventoryEmpty = game.getInventory(game.getUser(1)).getPrivateView();
        assertEquals(inventoryEmpty.get("Lumber"), 0);
        assertEquals(inventoryEmpty.get("Brick"), 0);
        assertEquals(inventoryEmpty.get("Grain"), 0);
        assertEquals(inventoryEmpty.get("Wool"), 0);
        assertEquals(inventoryEmpty.get("Ore"), 0);

        gameService.distributeResources(5, "test");
        Map<String, Integer> inventoryFull = new HashMap<>();
        inventoryFull = game.getInventory(game.getUser(1)).getPrivateView();
        assertEquals(inventoryFull.get("Lumber"), 6);
        assertEquals(inventoryFull.get("Brick"), 0);
        assertEquals(inventoryFull.get("Grain"), 6);
        assertEquals(inventoryFull.get("Wool"), 0);
        assertEquals(inventoryFull.get("Ore"), 0);
        assertEquals(game.getInventory(game.getUser(1)).sumResource(), 12);
        assertEquals(game.getInventory(game.getUser(0)).sumResource(), 0);
        assertEquals(game.getInventory(game.getUser(2)).sumResource(), 0);
    }

    /**
     * Tests the playing and resolution of the developmentCards.
     * <p>
     * Logs in 4 users, creates a game and all 4 users are joined.
     * Then player 1 tries to play the devCard Year of Plenty, which is successful.
     * After that player 2 tries to play his devCard which he isn't allowed to.
     * Then player 1 tries to resolve the devCard Year of Plenty, which is successful and increases the amount
     * of Lumber and Ore in his inventory by 1 each.
     * Furthermore the functionality of the Monopoly and Road Building card is also tested.
     *
     * @author Marc Hermes
     * @since 2021-05-05
     */
    @Test
    void playAndResolveDevelopmentCardRequestTest() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        User userThatPlaysTheCard = game.getUser(0);
        Inventory inv0 = game.getInventory(userThatPlaysTheCard);
        Inventory inv1 = game.getInventory(game.getUser(1));
        Inventory inv2 = game.getInventory(game.getUser(2));
        Inventory inv3 = game.getInventory(game.getUser(3));


        inv0.cardYearOfPlenty.incNumber();
        inv0.cardRoadBuilding.incNumber();
        inv1.cardMonopoly.incNumber();
        inv2.cardRoadBuilding.incNumber();
        inv2.brick.incNumber(2);
        inv3.lumber.incNumber(3);
        inv3.cardKnight.incNumber();

        // do the opening turn so we may play developmentCards
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);
        buildStreetAndBuildingForOpeningTurn(game);

        RollDiceRequest rdr = new RollDiceRequest(game.getName(), userThatPlaysTheCard, 3);
        gameService.onRollDiceRequest(rdr);

        // Check if player 1 is allowed to play his decCardStack
        PlayDevelopmentCardRequest pdcr = new PlayDevelopmentCardRequest("Year of Plenty", "test", (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);

        assertTrue(event instanceof PublicInventoryChangeMessage);

        // Check if player 2 is not allowed to play his decCardStack because its not his turn
        pdcr = new PlayDevelopmentCardRequest("Road Building", "test", (UserDTO) game.getUser(2));
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PlayDevelopmentCardResponse);
        PlayDevelopmentCardResponse rsp = (PlayDevelopmentCardResponse) event;
        assertEquals(rsp.getUserName(), game.getUser(2).getUsername());
        assertFalse(rsp.isCanPlayCard());

        // Check if player 1 can try to resolve the wrong card
        int i = 0;
        MapGraph.StreetNode street1 = null;
        MapGraph.StreetNode street2 = null;
        for (MapGraph.BuildingNode bn : game.getMapGraph().getBuildingNodeHashSet()) {
            if (bn.getOccupiedByPlayer() == 0) {
                for (MapGraph.StreetNode street : bn.getConnectedStreetNodes()) {
                    if (i == 0 && street.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                        street1 = street;
                        i++;
                    } else if (i == 1 && street.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                        street2 = street;
                        i++;
                        break;
                    }
                }
            }
        }
        assert street1 != null;
        assert street2 != null;

        ResolveDevelopmentCardRoadBuildingRequest rdcrbr = new ResolveDevelopmentCardRoadBuildingRequest("Road Building", (UserDTO) userThatPlaysTheCard, game.getName(), street1.getUuid(), street2.getUuid());
        gameService.onResolveDevelopmentCardRequest(rdcrbr);
        assertFalse(event instanceof ResolveDevelopmentCardMessage);

        // Check if player 1 is allowed to resolve his devCard and if it resolves successfully
        int brick = inv0.brick.getNumber();
        int ore = inv0.ore.getNumber();
        int lumber = inv0.lumber.getNumber();
        ResolveDevelopmentCardYearOfPlentyRequest rdcyopr = new ResolveDevelopmentCardYearOfPlentyRequest("Year of Plenty", (UserDTO) userThatPlaysTheCard, game.getName(), "Lumber", "Ore");
        gameService.onResolveDevelopmentCardRequest(rdcyopr);
        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(inv0.brick.getNumber(), brick);
        assertEquals(inv0.ore.getNumber(), ore + 1);
        assertEquals(inv0.lumber.getNumber(), lumber + 1);

        // End the turn and let player 2 try to play the Monopoly devCard
        EndTurnRequest endTurnRequest = new EndTurnRequest(game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onEndTurnRequest(endTurnRequest);
        assertTrue(event instanceof NextTurnMessage);
        userThatPlaysTheCard = game.getUser(1);

        rdr = new RollDiceRequest(game.getName(), userThatPlaysTheCard, 3);
        gameService.onRollDiceRequest(rdr);

        pdcr = new PlayDevelopmentCardRequest("Monopoly", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);
        int amountOfAllLumber = inv0.lumber.getNumber() + inv1.lumber.getNumber() + inv2.lumber.getNumber() + inv3.lumber.getNumber();
        ResolveDevelopmentCardMonopolyRequest rdcMr = new ResolveDevelopmentCardMonopolyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "Lumber");
        gameService.onResolveDevelopmentCardRequest(rdcMr);
        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(inv0.lumber.getNumber(), 0);
        assertEquals(inv1.lumber.getNumber(), amountOfAllLumber);
        assertEquals(inv2.lumber.getNumber(), 0);
        assertEquals(inv3.lumber.getNumber(), 0);


        // End the turn and let player 3 try to play the Road Building card
        endTurnRequest = new EndTurnRequest(game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onEndTurnRequest(endTurnRequest);
        userThatPlaysTheCard = game.getUser(2);

        rdr = new RollDiceRequest(game.getName(), userThatPlaysTheCard, 3);
        gameService.onRollDiceRequest(rdr);

        pdcr = new PlayDevelopmentCardRequest("Road Building", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        i = 0;
        for(MapGraph.BuildingNode bn : game.getMapGraph().getBuildingNodeHashSet()) {
            if(bn.getOccupiedByPlayer() == game.getTurn()) {
                for(MapGraph.StreetNode sn : bn.getConnectedStreetNodes()) {
                    if (sn.getOccupiedByPlayer() == 666 && i == 0) {
                        street1 = sn;
                        i++;
                    } else if(sn.getOccupiedByPlayer() == 666 && i ==1) {
                        street2 = sn;
                        i++;
                        break;
                    }
                }
                if(i == 2) {
                    break;
                }
            }
        }

        rdcrbr = new ResolveDevelopmentCardRoadBuildingRequest("Road Building", (UserDTO) userThatPlaysTheCard, game.getName(), street1.getUuid(), street2.getUuid());
        gameService.onResolveDevelopmentCardRequest(rdcrbr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        // check if player 3 (index 2) is the occupier of the streets that were built with the Road Building decCardStack
        assertEquals(street1.getOccupiedByPlayer(), 2);
        assertEquals(street2.getOccupiedByPlayer(), 2);

        // End the turn and let player 4 try to play a card illegally and then play the knight card
        endTurnRequest = new EndTurnRequest(game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onEndTurnRequest(endTurnRequest);
        userThatPlaysTheCard = game.getUser(3);

        pdcr = new PlayDevelopmentCardRequest("illegalCard", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);

        pdcr = new PlayDevelopmentCardRequest("Knight", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        UUID hexagon = null;
        for (MapGraph.Hexagon hx : game.getMapGraph().getHexagonHashSet()) {
            if (!hx.isOccupiedByRobber()) {
                hexagon = hx.getUuid();
                break;
            }
        }
        ResolveDevelopmentCardKnightRequest rdckr;
        rdckr = new ResolveDevelopmentCardKnightRequest("IllegalName", (UserDTO) userThatPlaysTheCard, game.getName(), hexagon);
        gameService.onResolveDevelopmentCardRequest(rdckr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        rdckr = new ResolveDevelopmentCardKnightRequest("Knight", (UserDTO) userThatPlaysTheCard, game.getName(), hexagon);
        gameService.onResolveDevelopmentCardRequest(rdckr);
        assertTrue(event instanceof PublicInventoryChangeMessage);


        // From here on ignore rules and test for coverage
        game.setIsUsedForTest(true);
        inv3.cardMonopoly.incNumber(10);
        inv3.cardYearOfPlenty.incNumber(10);
        inv3.cardKnight.incNumber(10);
        inv3.cardRoadBuilding.incNumber(10);

        // year of plenty
        pdcr = new PlayDevelopmentCardRequest("Year of Plenty", "test", (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        rdckr = new ResolveDevelopmentCardKnightRequest("Year of Plenty", (UserDTO) userThatPlaysTheCard, game.getName(), hexagon);
        gameService.onResolveDevelopmentCardRequest(rdckr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);

        game.getBankInventory().lumber.setNumber(0);
        rdcyopr = new ResolveDevelopmentCardYearOfPlentyRequest("Year of Plenty", (UserDTO) userThatPlaysTheCard, game.getName(), "Lumber", "Ore");
        gameService.onResolveDevelopmentCardRequest(rdcyopr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);
        game.getBankInventory().lumber.setNumber(19);
        gameService.onResolveDevelopmentCardRequest(rdcyopr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        // knight
        pdcr = new PlayDevelopmentCardRequest("Knight", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        for (MapGraph.Hexagon hx : game.getMapGraph().getHexagonHashSet()) {
            if (!hx.isOccupiedByRobber()) {
                hexagon = hx.getUuid();
                break;
            }
        }
        rdcyopr = new ResolveDevelopmentCardYearOfPlentyRequest("Knight", (UserDTO) userThatPlaysTheCard, game.getName(), "Lumber", "Ore");
        gameService.onResolveDevelopmentCardRequest(rdcyopr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);

        rdckr = new ResolveDevelopmentCardKnightRequest("Knight", (UserDTO) userThatPlaysTheCard, game.getName(), hexagon);
        gameService.onResolveDevelopmentCardRequest(rdckr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        // monopoly
        pdcr = new PlayDevelopmentCardRequest("Monopoly", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        rdcyopr = new ResolveDevelopmentCardYearOfPlentyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "Lumber", "Ore");
        gameService.onResolveDevelopmentCardRequest(rdcyopr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);

        rdcMr = new ResolveDevelopmentCardMonopolyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "Grain");
        gameService.onResolveDevelopmentCardRequest(rdcMr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        pdcr = new PlayDevelopmentCardRequest("Monopoly", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);
        rdcMr = new ResolveDevelopmentCardMonopolyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "Wool");
        gameService.onResolveDevelopmentCardRequest(rdcMr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        pdcr = new PlayDevelopmentCardRequest("Monopoly", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);
        rdcMr = new ResolveDevelopmentCardMonopolyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "Ore");
        gameService.onResolveDevelopmentCardRequest(rdcMr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        pdcr = new PlayDevelopmentCardRequest("Monopoly", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);
        rdcMr = new ResolveDevelopmentCardMonopolyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "illegalResource");
        gameService.onResolveDevelopmentCardRequest(rdcMr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);
        rdcMr = new ResolveDevelopmentCardMonopolyRequest("Monopoly", (UserDTO) userThatPlaysTheCard, game.getName(), "Brick");
        gameService.onResolveDevelopmentCardRequest(rdcMr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        // road building
        pdcr = new PlayDevelopmentCardRequest("Road Building", game.getName(), (UserDTO) userThatPlaysTheCard);
        gameService.onPlayDevelopmentCardRequest(pdcr);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        rdckr = new ResolveDevelopmentCardKnightRequest("Road Building", (UserDTO) userThatPlaysTheCard, game.getName(), hexagon);
        gameService.onResolveDevelopmentCardRequest(rdckr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);

        rdcrbr = new ResolveDevelopmentCardRoadBuildingRequest("Road Building", (UserDTO) userThatPlaysTheCard, game.getName(), street1.getUuid(), street2.getUuid());
        gameService.onResolveDevelopmentCardRequest(rdcrbr);
        assertTrue(event instanceof ResolveDevelopmentCardNotSuccessfulResponse);

    }

    @Test
    public void ResourcesToDiscardTest() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        for (MapGraph.BuildingNode b : game.getMapGraph().getBuildingNodeHashSet()) {
            b.buildOrDevelopSettlement(1);
        }

        Map<String, Integer> inventoryEmpty = new HashMap<>();
        inventoryEmpty = game.getInventory(game.getUser(1)).getPrivateView();
        assertEquals(inventoryEmpty.get("Lumber"), 0);
        assertEquals(inventoryEmpty.get("Brick"), 0);
        assertEquals(inventoryEmpty.get("Grain"), 0);
        assertEquals(inventoryEmpty.get("Wool"), 0);
        assertEquals(inventoryEmpty.get("Ore"), 0);

        game.getInventory(userDTO1).lumber.setNumber(6);
        game.getInventory(userDTO1).grain.setNumber(5);

        HashMap<String, Integer> inventoryChosen = new HashMap<>();
        inventoryChosen.put("Lumber", 3);
        inventoryChosen.put("Wool", 0);
        inventoryChosen.put("Ore", 0);
        inventoryChosen.put("Brick", 0);
        inventoryChosen.put("Grain", 4);

        ResourcesToDiscardRequest resources = new ResourcesToDiscardRequest("test", userDTO1, inventoryChosen);
        gameService.onResourcesToDiscard(resources);

        assertEquals(game.getInventory(userDTO1).lumber.getNumber(), 3);
        assertEquals(game.getInventory(userDTO1).grain.getNumber(), 4);
    }

    /**
     * This test checks if the MapGraph can be generated randomly
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    @Test
    void randomGameFieldGenerateTest() {

        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        // Check if the amount of building nodes, street nodes and hexagons is correct.
        // because of the randomness of the generation an exact value for the street and buildings nodes as well as harbors cannot be checked.
        int harborCounter = 0;
        for (MapGraph.BuildingNode bn : game.getMapGraph().getBuildingNodeHashSet()) {
            if (bn.getTypeOfHarbor() != 0) {
                harborCounter++;
            }
        }

        assertTrue(harborCounter <= 18);
        assertEquals(game.getMapGraph().getHexagonHashSet().size(), 19);
        assertTrue(game.getMapGraph().getBuildingNodeHashSet().size() >= 54);
        assertTrue(game.getMapGraph().getStreetNodeHashSet().size() >= 72);
    }

    /**
     * This test checks if the AI is used when the player whose turn
     * it is right now is not in the game anymore.
     * <p>
     * The AI will play his first opening turn
     *
     * @author Marc Hermes
     * @since 2021-05-11
     */
    @Test
    void missingPlayerAITest() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        // the TestAI class will now be used
        game.setIsUsedForTest(true);

        // Player 1 leaves the game
        GameLeaveUserRequest glur = new GameLeaveUserRequest(game.getName(), userDTO1);
        gameService.onGameLeaveUserRequest(glur);

        // Player 0 ends his turn
        EndTurnRequest etr = new EndTurnRequest(game.getName(), userDTO);
        gameService.onEndTurnRequest(etr);

        int buildingCounter = 0;
        int streetCounter = 0;

        for (MapGraph.BuildingNode bn : game.getMapGraph().getBuildingNodeHashSet()) {
            if (bn.getOccupiedByPlayer() == 1) {
                buildingCounter++;
            }
        }

        for (MapGraph.StreetNode sn : game.getMapGraph().getStreetNodeHashSet()) {
            if (sn.getOccupiedByPlayer() == 1) {
                streetCounter++;

            }
        }

        // Check if the AI built the building and street
        assertEquals(buildingCounter, 1);
        assertEquals(streetCounter, 1);

        // Check if the turn started for the correct player (and thus the AI ended the turn)
        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO2, game.getUser(game.getTurn()));
    }

    /**
     * This test checks if the AI is used when the player whose turn
     * it is right now leaves the game.
     * <p>
     * The AI will first play the opening turn and then continue to do another turn where it
     * plays all developmentCards and trades
     * <p>
     * enhanced by Marc Hermes 2021-05-26
     *
     * @author Marc Hermes
     * @since 2021-05-11
     */
    @Test
    void replacePlayerDuringOwnTurnAITest() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);

        // the TestAI class will now be used
        game.setIsUsedForTest(true);

        Inventory aiInventory = game.getInventory(userDTO);

        // Player 0 (the turn player) leaves the game
        GameLeaveUserRequest glur = new GameLeaveUserRequest(game.getName(), userDTO);
        gameService.onGameLeaveUserRequest(glur);

        // play the opening turn for the AI

        // Check if the turn started for the correct player (and thus the AI ended the turn)
        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO1, game.getUser(game.getTurn()));

        // player 1 opening turn 1
        buildStreetAndBuildingForOpeningTurn(game);

        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO2, game.getUser(game.getTurn()));

        // player 2 opening turn 1
        buildStreetAndBuildingForOpeningTurn(game);

        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO3, game.getUser(game.getTurn()));

        // player 3 opening turn 1
        buildStreetAndBuildingForOpeningTurn(game);

        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO3, game.getUser(game.getTurn()));

        // player 3 opening turn 2
        buildStreetAndBuildingForOpeningTurn(game);

        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO2, game.getUser(game.getTurn()));

        // player 2 opening turn 2
        buildStreetAndBuildingForOpeningTurn(game);

        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO1, game.getUser(game.getTurn()));


        //give the AI the developmentCards to play
        aiInventory.cardRoadBuilding.incNumber();
        aiInventory.cardMonopoly.incNumber();
        aiInventory.cardYearOfPlenty.incNumber();
        aiInventory.cardKnight.incNumber();

        // give the AI a lot of resources so that it must discard some, build things and buy cards
        aiInventory.incCardStack("Lumber", 10);
        aiInventory.incCardStack("Brick", 10);
        aiInventory.incCardStack("Wool", 10);
        aiInventory.incCardStack("Ore", 10);
        aiInventory.incCardStack("Grain", 10);


        // player 1 opening turn 2
        buildStreetAndBuildingForOpeningTurn(game);

        // Opening turn is done, so now resources were distributed


        ArrayList<UserDTO> bidders = new ArrayList<>();
        ArrayList<TradeItem> wishList = new ArrayList<>();
        HashMap<UserDTO, ArrayList<TradeItem>> bids = new HashMap<>();
        // the AI will now try to trade, thus we send him an empty list of trades to accept
        for (String tc : game.getTradeList().keySet()) {
            if (tc != null) {

                var tisabm = new TradeInformSellerAboutBidsMessage(userDTO, game.getName(), tc, bidders, bids);
                AIToServerTranslator.translate(new TestAI((GameDTO) game).continueTurnOrder(tisabm, wishList), gameService);
            }
        }

        // check if the AI ended the trade and ended its turn so that now player 1 is the turnPlayer again
        // Check if the turn started for the correct player (and thus the AI ended the turn)
        // the opening phase is now over and the AI finished it's first actual turn
        assertTrue(event instanceof NextTurnMessage);
        assertEquals(userDTO1, game.getUser(game.getTurn()));

        int resourceAmountBefore = aiInventory.sumResource();

        RollDiceRequest rdr = new RollDiceRequest(game.getName(), userDTO1, 7);
        gameService.onRollDiceRequest(rdr);

        // Choose any hexagon to move the robber to
        UUID uuidForRobber = null;
        for (MapGraph.Hexagon hx : game.getMapGraph().getHexagonHashSet()) {
            if (!hx.isOccupiedByRobber()) {
                uuidForRobber = hx.getUuid();
            }
        }
        RobbersNewFieldRequest mrm = new RobbersNewFieldRequest(game.getName(), userDTO1, uuidForRobber);
        gameService.onRobbersNewFieldRequest(mrm);

        // Check if the AI discarded its resources and now has less than before and therefore discarded resources
        assertTrue(resourceAmountBefore > aiInventory.sumResource());
        String tradeCode = UUID.randomUUID().toString().trim().substring(0, 7);
        TradeItemRequest tri = new TradeItemRequest(userDTO1, game.getName(), wishList, tradeCode, wishList);
        gameService.onTradeItemRequest(tri);

    }

    public void buildStreetAndBuildingForOpeningTurn(Game game) {
        for (MapGraph.BuildingNode bn : game.getMapGraph().getBuildingNodeHashSet()) {

            if (bn.tryBuildOrDevelopSettlement(game.getTurn(), game.getStartingPhase())) {
                game.getMapGraph().getNumOfBuildings()[game.getTurn()] = game.getMapGraph().getNumOfBuildings()[game.getTurn()] - 1;
                ConstructionRequest cr1 = new ConstructionRequest((UserDTO) game.getUser(game.getTurn()), game.getName(), bn.getUuid(), "BuildingNode");
                gameService.onConstructionMessage(cr1);

                for (MapGraph.StreetNode sn : bn.getConnectedStreetNodes()) {
                    if (sn.tryBuildRoad(game.getTurn(), game.getStartingPhase())) {
                        game.getMapGraph().getNumOfRoads()[game.getTurn()] = game.getMapGraph().getNumOfRoads()[game.getTurn()] - 1;
                        ConstructionRequest cr2 = new ConstructionRequest((UserDTO) game.getUser(game.getTurn()), game.getName(), sn.getUuid(), "StreetNode");
                        gameService.onConstructionMessage(cr2);
                        break;
                    }
                }
                break;
            }
        }
    }

    /**
     * Test used for checking the general functionality of the randomAI (only check if it will end its turn)
     * <p>
     * Because of the randomness of the AI, no actual check can be done here. Over all test coverage will still be increased, because we give the AI the chance to perform actions.
     *
     * @author Marc Hermes
     * @since 2021-05-12
     */
    @Test
    void randomAITest() {

        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);


        Inventory aiInventory = game.getInventory(userDTO);
        aiInventory.incCardStack("Brick", 10);
        aiInventory.incCardStack("Ore", 10);
        aiInventory.incCardStack("Wool", 10);
        aiInventory.incCardStack("Grain", 10);
        aiInventory.incCardStack("Lumber", 10);
        aiInventory.cardRoadBuilding.incNumber();
        aiInventory.cardMonopoly.incNumber();
        aiInventory.cardKnight.incNumber();
        aiInventory.cardYearOfPlenty.incNumber();

        // Player 0 (the turn player) leaves the game
        GameLeaveUserRequest glur = new GameLeaveUserRequest(game.getName(), userDTO);
        gameService.onGameLeaveUserRequest(glur);

        // Check if the turn started for the correct player (and thus the AI ended the turn)
        assertTrue(event instanceof PublicInventoryChangeMessage);
        assertEquals(userDTO1, game.getUser(game.getTurn()));

        // End the turn twice for player 1, because its the opening phase
        EndTurnRequest etr = new EndTurnRequest(game.getName(), userDTO1);
        gameService.onEndTurnRequest(etr);
        gameService.onEndTurnRequest(etr);
    }

    /**
     * This test checks if the giveResource and the takeResource method works as intended
     * <p>
     * We create a new gameService and we create a new game. After that we assert that, the game is present and we
     * join some user to the game. We setup the UserArrayList and the inventories.
     * Than we checks the content of the inventories before and after the giveResource method, including the bank.
     * Finally we check the content of inventories the after the takeResource method, including the bank.
     *
     * @author Anton Nikiforov
     * @since 2021-04-26
     */
    @Test
    void onGiveAndTakeResourceTest() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.joinUser(userDTO2);
        lobby.joinUser(userDTO3);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        lobby.joinPlayerReady(userDTO2);
        lobby.joinPlayerReady(userDTO3);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);

        Inventory bank = game.getBankInventory();

        Inventory inventory1 = game.getInventory(userDTO1);
        Inventory inventory2 = game.getInventory(userDTO2);


        assertEquals(bank.getSpecificResourceAmount("Lumber"), 19);
        assertEquals(bank.getSpecificResourceAmount("Brick"), 19);
        assertEquals(bank.getSpecificResourceAmount("Grain"), 19);
        assertEquals(bank.getSpecificResourceAmount("Wool"), 19);
        assertEquals(bank.getSpecificResourceAmount("Ore"), 19);

        assertEquals(inventory1.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Brick"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Grain"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Wool"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Ore"), 0);

        assertEquals(inventory2.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Brick"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Grain"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Wool"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Ore"), 0);

        // giveResourceTest
        gameService.giveResource(game, userDTO1, "Lumber", 15);
        gameService.giveResource(game, userDTO1, "Brick", 15);
        gameService.giveResource(game, userDTO1, "Grain", 15);
        gameService.giveResource(game, userDTO1, "Wool", 15);
        gameService.giveResource(game, userDTO1, "Ore", 15);

        assertEquals(bank.getSpecificResourceAmount("Lumber"), 4);
        assertEquals(bank.getSpecificResourceAmount("Brick"), 4);
        assertEquals(bank.getSpecificResourceAmount("Grain"), 4);
        assertEquals(bank.getSpecificResourceAmount("Wool"), 4);
        assertEquals(bank.getSpecificResourceAmount("Ore"), 4);

        assertEquals(inventory1.getSpecificResourceAmount("Lumber"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Brick"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Grain"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Wool"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Ore"), 15);

        assertEquals(inventory2.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Brick"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Grain"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Wool"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Ore"), 0);


        gameService.giveResource(game, userDTO2, "Lumber", 10);
        gameService.giveResource(game, userDTO2, "Brick", 10);
        gameService.giveResource(game, userDTO2, "Grain", 10);
        gameService.giveResource(game, userDTO2, "Wool", 10);
        gameService.giveResource(game, userDTO2, "Ore", 10);

        assertEquals(bank.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(bank.getSpecificResourceAmount("Brick"), 0);
        assertEquals(bank.getSpecificResourceAmount("Grain"), 0);
        assertEquals(bank.getSpecificResourceAmount("Wool"), 0);
        assertEquals(bank.getSpecificResourceAmount("Ore"), 0);

        assertEquals(inventory1.getSpecificResourceAmount("Lumber"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Brick"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Grain"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Wool"), 15);
        assertEquals(inventory1.getSpecificResourceAmount("Ore"), 15);

        assertEquals(inventory2.getSpecificResourceAmount("Lumber"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Brick"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Grain"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Wool"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Ore"), 4);


        // takeResourceTest
        gameService.takeResource(game, userDTO1, "Lumber", 15);
        gameService.takeResource(game, userDTO1, "Brick", 15);
        gameService.takeResource(game, userDTO1, "Grain", 15);
        gameService.takeResource(game, userDTO1, "Wool", 15);
        gameService.takeResource(game, userDTO1, "Ore", 15);

        assertEquals(bank.getSpecificResourceAmount("Lumber"), 15);
        assertEquals(bank.getSpecificResourceAmount("Brick"), 15);
        assertEquals(bank.getSpecificResourceAmount("Grain"), 15);
        assertEquals(bank.getSpecificResourceAmount("Wool"), 15);
        assertEquals(bank.getSpecificResourceAmount("Ore"), 15);

        assertEquals(inventory1.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Brick"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Grain"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Wool"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Ore"), 0);

        assertEquals(inventory2.getSpecificResourceAmount("Lumber"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Brick"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Grain"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Wool"), 4);
        assertEquals(inventory2.getSpecificResourceAmount("Ore"), 4);


        gameService.takeResource(game, userDTO2, "Lumber", 10);
        gameService.takeResource(game, userDTO2, "Brick", 10);
        gameService.takeResource(game, userDTO2, "Grain", 10);
        gameService.takeResource(game, userDTO2, "Wool", 10);
        gameService.takeResource(game, userDTO2, "Ore", 10);

        assertEquals(bank.getSpecificResourceAmount("Lumber"), 19);
        assertEquals(bank.getSpecificResourceAmount("Brick"), 19);
        assertEquals(bank.getSpecificResourceAmount("Grain"), 19);
        assertEquals(bank.getSpecificResourceAmount("Wool"), 19);
        assertEquals(bank.getSpecificResourceAmount("Ore"), 19);

        assertEquals(inventory1.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Brick"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Grain"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Wool"), 0);
        assertEquals(inventory1.getSpecificResourceAmount("Ore"), 0);

        assertEquals(inventory2.getSpecificResourceAmount("Lumber"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Brick"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Grain"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Wool"), 0);
        assertEquals(inventory2.getSpecificResourceAmount("Ore"), 0);
    }

    /**
     * This test checks if the right user gets the largest army card
     * <p>
     * First the method logs all users in and joins 2 users into a game. Then it alters
     * the inventory of both users and gives them ten knight cards each. It then tests if
     * the largest army boolean is correctly set.
     *
     * @author Dekker Carsten
     * @since 2021-05-28
     */
    @Test
    void checkForLargestArmy() {
        loginUsers();
        lobbyManagement.createLobby("test", userDTO);
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby("test");
        assertTrue(optionalLobby.isPresent());
        Lobby lobby = optionalLobby.get();
        lobby.joinUser(userDTO1);
        lobby.setMinimumAmountOfPlayers(2);
        lobby.joinPlayerReady(userDTO);
        lobby.joinPlayerReady(userDTO1);
        gameService.startGame(lobby, "Standard");
        Optional<Game> optionalGame = gameManagement.getGame("test");
        assertTrue(optionalGame.isPresent());
        Game game = optionalGame.get();

        game.joinUser(userDTO1);

        game.getInventory(userDTO).setPlayedKnights(2);

        game.getInventory(userDTO).incCardStack("Knight", 10);

        game.getInventory(userDTO1).setPlayedKnights(2);

        game.getInventory(userDTO1).incCardStack("Knight", 10);

        PlayDevelopmentCardRequest playDevelopmentCardRequest = new PlayDevelopmentCardRequest("Knight", game.getName(), userDTO);

        gameService.onPlayDevelopmentCardRequest(playDevelopmentCardRequest);

        ResolveDevelopmentCardKnightRequest resolveDevelopmentCardKnightRequest = new ResolveDevelopmentCardKnightRequest();

        for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
            if (!hexagon.isOccupiedByRobber()) {
                resolveDevelopmentCardKnightRequest = new ResolveDevelopmentCardKnightRequest("Knight", userDTO, game.getName(), hexagon.getUuid());
                break;
            }
        }

        gameService.onResolveDevelopmentCardRequest(resolveDevelopmentCardKnightRequest);

        assertTrue(game.getInventory(userDTO).isLargestArmy());

        assertEquals(2, game.getInventory(userDTO).getVictoryPoints());

        EndTurnRequest endTurnRequest = new EndTurnRequest(game.getName(), userDTO);

        gameService.onEndTurnRequest(endTurnRequest);

        PlayDevelopmentCardRequest playDevelopmentCardRequest1 = new PlayDevelopmentCardRequest("Knight", game.getName(), userDTO1);

        gameService.onPlayDevelopmentCardRequest(playDevelopmentCardRequest1);

        ResolveDevelopmentCardKnightRequest resolveDevelopmentCardKnightRequest1 = new ResolveDevelopmentCardKnightRequest();

        for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
            if (!hexagon.isOccupiedByRobber()) {
                resolveDevelopmentCardKnightRequest1 = new ResolveDevelopmentCardKnightRequest("Knight", userDTO1, game.getName(), hexagon.getUuid());
                break;
            }
        }

        gameService.onResolveDevelopmentCardRequest(resolveDevelopmentCardKnightRequest1);

        assertFalse(game.getInventory(userDTO1).isLargestArmy());

        EndTurnRequest endTurnRequest1 = new EndTurnRequest(game.getName(), userDTO1);

        gameService.onEndTurnRequest(endTurnRequest1);

        PlayDevelopmentCardRequest playDevelopmentCardRequest2 = new PlayDevelopmentCardRequest("Knight", game.getName(), userDTO1);

        gameService.onPlayDevelopmentCardRequest(playDevelopmentCardRequest2);

        ResolveDevelopmentCardKnightRequest resolveDevelopmentCardKnightRequest2 = new ResolveDevelopmentCardKnightRequest();

        for (MapGraph.Hexagon hexagon : game.getMapGraph().getHexagonHashSet()) {
            if (!hexagon.isOccupiedByRobber()) {
                resolveDevelopmentCardKnightRequest2 = new ResolveDevelopmentCardKnightRequest("Knight", userDTO1, game.getName(), hexagon.getUuid());
                break;
            }
        }

        gameService.onResolveDevelopmentCardRequest(resolveDevelopmentCardKnightRequest2);

        assertFalse(game.getInventory(userDTO).isLargestArmy());

        assertEquals(0, game.getInventory(userDTO).getVictoryPoints());

        assertTrue(game.getInventory(userDTO1).isLargestArmy());

        assertEquals(2, game.getInventory(userDTO1).getVictoryPoints());
    }
}
