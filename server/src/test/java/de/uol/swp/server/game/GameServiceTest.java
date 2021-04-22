package de.uol.swp.server.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.TradeCardErrorMessage;
import de.uol.swp.common.game.message.TradeInformSellerAboutBidsMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.inventory.Inventory;
import de.uol.swp.common.game.message.TradeSuccessfulMessage;
import de.uol.swp.common.game.request.GameLeaveUserRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    final EventBus bus = new EventBus();
    GameManagement gameManagement = new GameManagement();
    LobbyManagement lobbyManagement = new LobbyManagement();
    final UserManagement userManagement = new UserManagement();
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, new UserManagement()), bus);
    GameService gameService = new GameService(gameManagement, lobbyService, new AuthenticationService(bus, new UserManagement()), bus);
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
    }

    @Test
    void TradeTest(){

        String tradeCode = "seller1acv";
        loginUsers();
        GameService gameServiceTIRT = new GameService(gameManagement, lobbyService, authenticationService, bus);

        gameManagement.createGame("test", userDTO, "Standard");
        Optional<Game> game = gameManagement.getGame("test");
        assertTrue(game.isPresent());

        game.get().joinUser(userDTO1);
        game.get().joinUser(userDTO2);
        game.get().joinUser(userDTO3);

        game.get().setUpUserArrayList();
        game.get().setUpInventories();

        //fill Inventory
        game.get().getInventory(userDTO).lumber.setNumber(0);
        game.get().getInventory(userDTO).incCard("Lumber", 10);
        game.get().getInventory(userDTO).ore.setNumber(0);
        game.get().getInventory(userDTO).incCard("Ore", 10);
        game.get().getInventory(userDTO1).ore.setNumber(0);
        game.get().getInventory(userDTO1).incCard("Ore", 10);
        game.get().getInventory(userDTO2).grain.setNumber(0);
        game.get().getInventory(userDTO2).incCard("Grain", 10);
        game.get().getInventory(userDTO3).wool.setNumber(0);
        game.get().getInventory(userDTO3).incCard("Wool", 10);

        //create TradeItems to be sold
        //
        TradeItem sellerItem1 = new TradeItem("Lumber", 5);
        TradeItem sellerItem2 = new TradeItem("Ore", 10);
        ArrayList<TradeItem> sellerItems= new ArrayList<TradeItem>();
        sellerItems.add(sellerItem1);
        sellerItems.add(sellerItem2);

        TradeItemRequest sellerItemRequest = new TradeItemRequest(userDTO, game.get().getName(),sellerItems, tradeCode);
        gameServiceTIRT.onTradeItemRequest(sellerItemRequest);
        assertTrue(event instanceof TradeOfferInformBiddersMessage);

        //add Item bidder
        ArrayList<TradeItem> bidder1Items= new ArrayList<TradeItem>();
        TradeItem bidder1Item1 = new TradeItem("Ore", 4);
        bidder1Items.add(bidder1Item1);

        ArrayList<TradeItem> bidder2Items= new ArrayList<TradeItem>();
        TradeItem bidder2Item1 = new TradeItem("Grain", 6);
        bidder2Items.add(bidder2Item1);


        ArrayList<TradeItem> bidder3Items= new ArrayList<TradeItem>();
        TradeItem bidder3Item1 = new TradeItem("Wool", 7);
        bidder3Items.add(bidder3Item1);

        //create TradeItemRequests and call onTradeItemRequest
        TradeItemRequest bidder1ItemRequest = new TradeItemRequest(userDTO1, game.get().getName(),bidder1Items, tradeCode);
        gameServiceTIRT.onTradeItemRequest(bidder1ItemRequest);
        assertFalse(event instanceof TradeInformSellerAboutBidsMessage);

        TradeItemRequest bidder2ItemRequest = new TradeItemRequest(userDTO2, game.get().getName(),bidder2Items, tradeCode);
        gameServiceTIRT.onTradeItemRequest(bidder2ItemRequest);
        assertFalse(event instanceof TradeInformSellerAboutBidsMessage);

        TradeItemRequest bidder3ItemRequest = new TradeItemRequest(userDTO3, game.get().getName(),bidder3Items, tradeCode);
        gameServiceTIRT.onTradeItemRequest(bidder3ItemRequest);
        assertTrue(event instanceof TradeInformSellerAboutBidsMessage);


        TradeChoiceRequest tradeChoiceRequest = new TradeChoiceRequest(userDTO1,true,game.get().getName(),tradeCode);
        gameServiceTIRT.onTradeChoiceRequest(tradeChoiceRequest);

        assertTrue(game.get().getTradeList().size() ==0);
    }
}
