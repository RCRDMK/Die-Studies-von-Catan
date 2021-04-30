package de.uol.swp.server.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.game.Game;

import de.uol.swp.common.game.message.TradeCardErrorMessage;
import de.uol.swp.common.game.message.TradeEndedMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;

import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.request.GameLeaveUserRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.game.request.TradeChoiceRequest;
import de.uol.swp.common.game.request.TradeItemRequest;
import de.uol.swp.common.game.trade.TradeItem;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

//TODO um diese Klasse werde ich mich kümmern, sobald meine Tickets fertig sind (Carsten Dekker)

public class GameServiceTest {

    final EventBus bus = new EventBus();
    GameManagement gameManagement = new GameManagement();
    LobbyManagement lobbyManagement = new LobbyManagement();
    final UserManagement userManagement = new UserManagement();
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, new UserManagement()), bus);
    UserService userService = new UserService(bus, userManagement);
    GameService gameService = new GameService(gameManagement, lobbyService, new AuthenticationService(bus, new UserManagement()), bus, userService);
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);


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
/*
    @Test
    void onRetrieveAllThisGameUsersRequest() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
        GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), "Standard");
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
    /*
    @Test
    void onRetrieveAllThisGameUsersRequest3() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
        GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), "Standard");
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
    /*
    @Test
    void onRetrieveAllThisGameUsersRequest4() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        lobby.get().joinUser(userDTO3);
        List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
        GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), "Standard");
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
    /*
    @Test
    void onRetrieveAllThisGameUsersRequestUserLeft() {
        LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        lobby.get().joinUser(userDTO3);
        List<Session> lobbyUsers = authenticationService.getSessions(lobby.get().getUsers());
        GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), "Standard");
        Optional<Game> game = gameManagement.getGame(lobby.get().getName());
        assertTrue(game.isPresent());
        RetrieveAllThisGameUsersRequest retrieveAllThisGameUsersRequest = new RetrieveAllThisGameUsersRequest(lobby.get().getName());
        assertSame(gameManagement.getGame(lobby.get().getName()).get().getName(), retrieveAllThisGameUsersRequest.getName());
        GameLeaveUserRequest gameLeaveUserRequest = new GameLeaveUserRequest(lobby.get().getName(), userDTO1);
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest);
        assertFalse(game.get().getUsers().contains(userDTO1));
        List<Session> gameUsers = authenticationService.getSessions(game.get().getUsers());
        for (Session session : gameUsers) {
            assertTrue(userDTO == (session.getUser()) && userDTO1 != (session.getUser()) && userDTO2 == (session.getUser()) && userDTO3 == (session.getUser()));
        }
        GameLeaveUserRequest gameLeaveUserRequest2 = new GameLeaveUserRequest(lobby.get().getName(), userDTO2);
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest2);
        assertFalse(game.get().getUsers().contains(userDTO1));
        for (Session session : gameUsers) {
            assertTrue(userDTO == (session.getUser()) && userDTO1 != (session.getUser()) && userDTO2 != (session.getUser()) && userDTO3 == (session.getUser()));
        }
        GameLeaveUserRequest gameLeaveUserRequest3 = new GameLeaveUserRequest(lobby.get().getName(), userDTO3);
        gameService.onGameLeaveUserRequest(gameLeaveUserRequest3);
        assertFalse(game.get().getUsers().contains(userDTO3));
        for (Session session : gameUsers) {
            assertTrue(userDTO == (session.getUser()) && userDTO1 != (session.getUser()) && userDTO2 == (session.getUser()) && userDTO3 != (session.getUser()));
        }
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
  /* @Test
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
        GameService gameServiceTIRT = new GameService(gameManagement, lobbyService, authenticationService, bus, userService);

        gameManagement.createGame("test", userDTO, "Standard");
        Optional<Game> game = gameManagement.getGame("test");
        assertTrue(game.isPresent());

        game.get().joinUser(userDTO1);
        game.get().joinUser(userDTO2);
        game.get().joinUser(userDTO3);

        game.get().setUpUserArrayList();
        game.get().setUpInventories();

        //fill Inventory userDTO
        game.get().getInventory(userDTO).lumber.setNumber(0);
        game.get().getInventory(userDTO).incCard("Lumber", 10);
        game.get().getInventory(userDTO).ore.setNumber(0);
        game.get().getInventory(userDTO).incCard("Ore", 10);
        //fill Inventory userDTO1
        game.get().getInventory(userDTO1).ore.setNumber(0);
        game.get().getInventory(userDTO1).incCard("Ore", 10);
        //fill Inventory userDTO2
        game.get().getInventory(userDTO2).grain.setNumber(0);
        game.get().getInventory(userDTO2).incCard("Grain", 10);
        //fill Inventory userDTO3
        game.get().getInventory(userDTO3).wool.setNumber(0);
        game.get().getInventory(userDTO3).incCard("Wool", 10);

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

        TradeItemRequest sellerItemRequest = new TradeItemRequest(userDTO, game.get().getName(), sellerItems, tradeCode, wishItems);

        assertTrue(game.get().getTradeList().isEmpty());
        gameServiceTIRT.onTradeItemRequest(sellerItemRequest);
        assertTrue(event instanceof TradeOfferInformBiddersMessage);
        assertTrue(game.get().getTradeList().containsKey(tradeCode));
        assertTrue(game.get().getTradeList().size() == 1);
        assertTrue(game.get().getTradeList().get(tradeCode).getSeller().getUsername().equals(sellerItemRequest.getUser().getUsername()));
        assertTrue(game.get().getTradeList().get(tradeCode).getBidders().isEmpty());

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

        TradeItemRequest bidder1ItemRequest = new TradeItemRequest(userDTO1, game.get().getName(), bidder1ItemsWrong, tradeCode, bidder1wishItems);
        gameServiceTIRT.onTradeItemRequest(bidder1ItemRequest);

        assertTrue(event instanceof TradeCardErrorMessage);
        assertTrue(game.get().getTradeList().get(tradeCode).getBidders().isEmpty());
        assertTrue(game.get().getTradeList().get(tradeCode).getBids().isEmpty());
        assertTrue(game.get().getTradeList().size() == 1);

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

        bidder1ItemRequest = new TradeItemRequest(userDTO1, game.get().getName(), bidder1ItemsRight, tradeCode, bidder1wishItems);
        gameServiceTIRT.onTradeItemRequest(bidder1ItemRequest);

        assertFalse(event instanceof TradeInformSellerAboutBidsMessage);
        assertTrue(game.get().getTradeList().get(tradeCode).getBidders().size() == 1);
        assertTrue(game.get().getTradeList().size() == 1);

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

        TradeItemRequest bidder2ItemRequest = new TradeItemRequest(userDTO2, game.get().getName(), bidder2Items, tradeCode, bidder2wishItems);
        gameService.onTradeItemRequest(bidder2ItemRequest);

        assertFalse(event instanceof TradeInformSellerAboutBidsMessage);
        assertTrue(game.get().getTradeList().get(tradeCode).getBidders().size() == 2);

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

        TradeItemRequest bidder3ItemRequest = new TradeItemRequest(userDTO3, game.get().getName(), bidder3Items, tradeCode, bidder3wishItems);
        gameServiceTIRT.onTradeItemRequest(bidder3ItemRequest);

        assertTrue(event instanceof TradeInformSellerAboutBidsMessage);
        assertTrue(game.get().getTradeList().get(tradeCode).getBidders().size() == 3);

        //TradeChoice
        TradeChoiceRequest tradeChoiceRight = new TradeChoiceRequest(userDTO1, true, game.get().getName(), tradeCode);
        gameServiceTIRT.onTradeChoiceRequest(tradeChoiceRight);

        assertTrue(game.get().getInventory(userDTO).ore.getNumber() == 4);
        assertTrue(game.get().getInventory(userDTO).lumber.getNumber() == 5);
        assertTrue(game.get().getInventory(userDTO).grain.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO).brick.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO).wool.getNumber() == 0);

        assertTrue(game.get().getInventory(userDTO1).ore.getNumber() == 16);
        assertTrue(game.get().getInventory(userDTO1).lumber.getNumber() == 5);
        assertTrue(game.get().getInventory(userDTO1).grain.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO1).brick.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO1).wool.getNumber() == 0);

        assertTrue(game.get().getInventory(userDTO2).ore.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO2).lumber.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO2).grain.getNumber() == 10);
        assertTrue(game.get().getInventory(userDTO2).brick.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO2).wool.getNumber() == 0);

        assertTrue(game.get().getInventory(userDTO3).ore.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO3).lumber.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO3).grain.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO3).brick.getNumber() == 0);
        assertTrue(game.get().getInventory(userDTO3).wool.getNumber() == 10);
        assertTrue(event instanceof TradeEndedMessage);



    }



    /**
     * This test checks if the distributeResource method works as intendet.
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
        GameService gameService1 = new GameService(gameManagement, lobbyService, authenticationService, bus, userService);

        gameManagement.createGame("test", userDTO, "Standard");
        Optional<Game> game = gameManagement.getGame("test");
        assertTrue(game.isPresent());

        game.get().joinUser(userDTO1);
        game.get().joinUser(userDTO2);
        game.get().joinUser(userDTO3);

        game.get().setUpUserArrayList();
        game.get().setUpInventories();

        for (MapGraph.BuildingNode b : game.get().getMapGraph().getBuildingNodeHashSet()) {
            b.buildOrDevelopSettlement(1);
        }

        Map<String, Integer> inventoryEmpty = new HashMap<>();
        inventoryEmpty = game.get().getInventory(game.get().getUser(1)).getPrivateView();
        assertEquals(inventoryEmpty.get("Lumber"), 0);
        assertEquals(inventoryEmpty.get("Brick"), 0);
        assertEquals(inventoryEmpty.get("Grain"), 0);
        assertEquals(inventoryEmpty.get("Wool"), 0);
        assertEquals(inventoryEmpty.get("Ore"), 0);

        gameService1.distributeResources(5, "test");
        Map<String, Integer> inventoryFull = new HashMap<>();
        inventoryFull = game.get().getInventory(game.get().getUser(1)).getPrivateView();
        assertEquals(inventoryFull.get("Lumber"), 6);
        assertEquals(inventoryFull.get("Brick"), 0);
        assertEquals(inventoryFull.get("Grain"), 6);
        assertEquals(inventoryFull.get("Wool"), 0);
        assertEquals(inventoryFull.get("Ore"), 0);
        assertEquals(game.get().getInventory(game.get().getUser(1)).getResource(), 12);
        assertEquals(game.get().getInventory(game.get().getUser(0)).getResource(), 0);
        assertEquals(game.get().getInventory(game.get().getUser(2)).getResource(), 0);
    }

}
