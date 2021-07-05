package de.uol.swp.server.cheat;

import java.sql.SQLException;
import java.util.Optional;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.game.message.PublicInventoryChangeMessage;
import de.uol.swp.common.game.message.RollDiceResultMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.server.chat.ChatService;
import de.uol.swp.server.game.GameManagement;
import de.uol.swp.server.game.GameService;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the CheatService
 * <p>
 * Covers all of the available cheats
 *
 * @author René Meyer
 * @see CheatService
 * @since 2021-06-01
 */
@SuppressWarnings("UnstableApiUsage")
public class CheatServiceTest {
    final EventBus bus = new EventBus();
    boolean gameFinished = false;
    GameManagement gameManagement = new GameManagement();
    LobbyManagement lobbyManagement = new LobbyManagement();
    MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, userManagement), bus);
    UserService userService = new UserService(bus, userManagement);
    GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus, userService);
    CheatService cheatService = new CheatService(gameService, bus);
    ChatService chatService = new ChatService(cheatService, bus);

    // Setup UserDTOs
    UserDTO userDTO = new UserDTO("catanprofi",
            "47b7d407c2e2f3aff0e21aa16802006ba1793fd47b2d3cacee7cf7360e751bff7b7d0c7946b42b97a5306c6708ab006d0d81ef41a0c9f94537a2846327c51236",
            "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("captain",
            "994dac907995937160371992ecbdf9b34242db0abb3943807b5baa6be0c6908f72ea87b7dadd2bce6cf700c8dfb7d57b0566f544af8c30336a15d5f732d85613",
            "carsten.stahl@uol.de");
    UserDTO userDTO2 = new UserDTO("marius1",
            "b74a37371ca548bfd937410737b27f383e03021766e90f1180169691b8b15fc50aef49932c7413c0450823777ba46a34fd649b4da20b2e701c394c582ff6df55",
            "peterlustig@uol.de");
    UserDTO userDTO3 = new UserDTO("marc1",
            "65dfe56dd0e9117907b11e440d99a667527ddb13244aa38f79d3ae61ee0b2ab4047c1218c4fb05d84f88b914826c45de3ab27a611ea910a4b14733ab1e32b125",
            "test.lustig@uol.de");

    Game game;

    Object event;

    /**
     * Constructor for the CheatServiceTest
     *
     * @author René Meyer
     * @see SQLException
     * @since 2021-06-01
     */
    public CheatServiceTest() throws SQLException {
    }

    /**
     * Handle dead events on the eventbus
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
    }

    @BeforeEach
    void registerBus() {
        gameFinished = false;
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Logout all Users 1-4 after Test finished
     *
     * @author René Meyer
     * @see CheatService
     * @since 2021-06-01
     */
    @AfterEach
    void logOutAllUsers() {
        userManagement.logout(userDTO);
        userManagement.logout(userDTO1);
        userManagement.logout(userDTO2);
        userManagement.logout(userDTO3);
    }

    /**
     * Login all Users 1-4
     *
     * @author René Meyer
     * @see CheatService
     * @since 2021-06-01
     */
    void loginUsers() {
        authenticationService.onLoginRequest(new LoginRequest(userDTO.getUsername(), userDTO.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO1.getUsername(), userDTO1.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO2.getUsername(), userDTO2.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO3.getUsername(), userDTO3.getPassword()));
    }

    /**
     * Setup Lobby and Game before each test
     * <p>
     * This function logs in the users 1-4 <br>
     * Then it creates a lobby <br>
     * Then it checks if the lobby is present <br>
     * Then it joins all users into the lobby <br>
     * Then it creates a game <br>
     * Then it joins all users into the game <br>
     * Then it sets up the Inventories for all players <br>
     * Then it checks if the game is present <br>
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @BeforeEach
    void setupLobbyAndGame() {
        loginUsers();
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        lobby.get().joinUser(userDTO3);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), lobby.get().getUsers(), "Standard");
        Optional<Game> optionalGame = gameManagement.getGame(lobby.get().getName());
        optionalGame.ifPresent(value -> game = value);
        game.joinUser(userDTO1);
        game.joinUser(userDTO2);
        game.joinUser(userDTO3);
        game.setUpUserArrayList();
        game.setUpInventories();
    }

    /**
     * Test for the givemecard Cheat - Testing the Victory Card and checks if the game ends instantly after a user cheats 10 victory cards
     * <p>
     * This test creates a new RequestChatMessage to emulate a sent ChatMessage from a client <br>
     * Then it sets the session of the RequestChatMessage <br>
     * Then it checks if the sent Message "givemecard victory 10" is recognized as a cheat. <br>
     * Then it calls the onRequestChatMessage function from the chatService <br>
     * Then it gets all inventories of the users  <br>
     * Then it checks all inventories if only the cheatUser has 10 victory points in the inventory <br>
     * Then it checks if the game is finished instantly after the cheat <br>
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Test
    @DisplayName("givemecard victory Cheat Test")
    void giveMeCardVictoryCheat() {
        RequestChatMessage chatMessage = new RequestChatMessage("givemecard victory 10", "game_testLobby",
                userDTO2.getUsername(), 0);
        chatMessage.setSession(new Session() {
            @Override
            public String getSessionId() {
                return "";
            }

            @Override
            public User getUser() {
                return userDTO2;
            }
        });
        assertTrue(cheatService.isCheat(chatMessage));
        chatService.onRequestChatMessage(chatMessage);

        var cheatInventory = game.getInventory(userDTO2);
        var normalInventory1 = game.getInventory(userDTO1);
        var normalInventory2 = game.getInventory(userDTO3);
        var normalInventory3 = game.getInventory(userDTO);

        assertEquals(cheatInventory.lumber.getNumber(), 0);
        assertEquals(cheatInventory.grain.getNumber(), 0);
        assertEquals(cheatInventory.brick.getNumber(), 0);
        assertEquals(cheatInventory.wool.getNumber(), 0);
        assertEquals(cheatInventory.ore.getNumber(), 0);
        assertEquals(cheatInventory.cardKnight.getNumber(), 0);
        assertEquals(cheatInventory.cardYearOfPlenty.getNumber(), 0);
        assertEquals(cheatInventory.cardMonopoly.getNumber(), 0);
        assertEquals(cheatInventory.cardRoadBuilding.getNumber(), 0);
        assertEquals(cheatInventory.getCardVictoryPoint(), 10);

        assertEquals(normalInventory1.lumber.getNumber(), 0);
        assertEquals(normalInventory1.grain.getNumber(), 0);
        assertEquals(normalInventory1.brick.getNumber(), 0);
        assertEquals(normalInventory1.wool.getNumber(), 0);
        assertEquals(normalInventory1.ore.getNumber(), 0);
        assertEquals(normalInventory1.cardKnight.getNumber(), 0);
        assertEquals(normalInventory1.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory1.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory1.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory1.getCardVictoryPoint(), 0);


        assertEquals(normalInventory2.lumber.getNumber(), 0);
        assertEquals(normalInventory2.grain.getNumber(), 0);
        assertEquals(normalInventory2.brick.getNumber(), 0);
        assertEquals(normalInventory2.wool.getNumber(), 0);
        assertEquals(normalInventory2.ore.getNumber(), 0);
        assertEquals(normalInventory2.cardKnight.getNumber(), 0);
        assertEquals(normalInventory2.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory2.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory2.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory2.getCardVictoryPoint(), 0);


        assertEquals(normalInventory3.lumber.getNumber(), 0);
        assertEquals(normalInventory3.grain.getNumber(), 0);
        assertEquals(normalInventory3.brick.getNumber(), 0);
        assertEquals(normalInventory3.wool.getNumber(), 0);
        assertEquals(normalInventory3.ore.getNumber(), 0);
        assertEquals(normalInventory3.cardKnight.getNumber(), 0);
        assertEquals(normalInventory3.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory3.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory3.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory3.getCardVictoryPoint(), 0);
        assertTrue(gameFinished);
    }

    /**
     * Test for the givemecard Cheat - Testing the Development Card
     * <p>
     * This test creates a new RequestChatMessage to emulate a sent ChatMessage from a client <br>
     * Then it sets the session of the RequestChatMessage <br>
     * Then it checks if the sent Message "givemecard knight 1" is recognized as a cheat. <br>
     * Then it calls the onRequestChatMessage function from the chatService <br>
     * Then it gets all inventories of the users  <br>
     * Then it checks all inventories if only the cheatUser has 1 knight card in the inventory <br>
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Test
    @DisplayName("givemecard x Development Cheat Test")
    void giveMeCardXDevelopmentCheat() {
        RequestChatMessage chatMessage = new RequestChatMessage("givemecard knight 1", "game_testLobby",
                userDTO2.getUsername(), 0);
        chatMessage.setSession(new Session() {
            @Override
            public String getSessionId() {
                return "";
            }

            @Override
            public User getUser() {
                return userDTO2;
            }
        });
        assertTrue(cheatService.isCheat(chatMessage));
        chatService.onRequestChatMessage(chatMessage);

        var cheatInventory = game.getInventory(userDTO2);
        var normalInventory1 = game.getInventory(userDTO1);
        var normalInventory2 = game.getInventory(userDTO3);
        var normalInventory3 = game.getInventory(userDTO);

        assertEquals(cheatInventory.lumber.getNumber(), 0);
        assertEquals(cheatInventory.grain.getNumber(), 0);
        assertEquals(cheatInventory.brick.getNumber(), 0);
        assertEquals(cheatInventory.wool.getNumber(), 0);
        assertEquals(cheatInventory.ore.getNumber(), 0);
        assertEquals(cheatInventory.cardKnight.getNumber(), 1);
        assertEquals(cheatInventory.cardYearOfPlenty.getNumber(), 0);
        assertEquals(cheatInventory.cardMonopoly.getNumber(), 0);
        assertEquals(cheatInventory.cardRoadBuilding.getNumber(), 0);
        assertEquals(cheatInventory.getCardVictoryPoint(), 0);

        assertEquals(normalInventory1.lumber.getNumber(), 0);
        assertEquals(normalInventory1.grain.getNumber(), 0);
        assertEquals(normalInventory1.brick.getNumber(), 0);
        assertEquals(normalInventory1.wool.getNumber(), 0);
        assertEquals(normalInventory1.ore.getNumber(), 0);
        assertEquals(normalInventory1.cardKnight.getNumber(), 0);
        assertEquals(normalInventory1.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory1.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory1.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory1.getCardVictoryPoint(), 0);

        assertEquals(normalInventory2.lumber.getNumber(), 0);
        assertEquals(normalInventory2.grain.getNumber(), 0);
        assertEquals(normalInventory2.brick.getNumber(), 0);
        assertEquals(normalInventory2.wool.getNumber(), 0);
        assertEquals(normalInventory2.ore.getNumber(), 0);
        assertEquals(normalInventory2.cardKnight.getNumber(), 0);
        assertEquals(normalInventory2.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory2.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory2.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory2.getCardVictoryPoint(), 0);

        assertEquals(normalInventory3.lumber.getNumber(), 0);
        assertEquals(normalInventory3.grain.getNumber(), 0);
        assertEquals(normalInventory3.brick.getNumber(), 0);
        assertEquals(normalInventory3.wool.getNumber(), 0);
        assertEquals(normalInventory3.ore.getNumber(), 0);
        assertEquals(normalInventory3.cardKnight.getNumber(), 0);
        assertEquals(normalInventory3.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory3.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory3.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory3.getCardVictoryPoint(), 0);
    }

    /**
     * Test for the givemecard Cheat - Testing the Resource Card
     * <p>
     * This test creates a new RequestChatMessage to emulate a sent ChatMessage from a client <br>
     * Then it sets the session of the RequestChatMessage <br>
     * Then it checks if the sent Message "givemecard ore 15" is recognized as a cheat. <br>
     * Then it calls the onRequestChatMessage function from the chatService <br>
     * Then it gets all inventories of the users  <br>
     * Then it checks all inventories if only the cheatUser has 15 ore cards in the inventory <br>
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Test
    @DisplayName("givemecard x Resource Cheat Test")
    void giveMeCardXResourceCheat() {
        RequestChatMessage chatMessage = new RequestChatMessage("givemecard ore 15", "game_testLobby",
                userDTO2.getUsername(), 0);
        chatMessage.setSession(new Session() {
            @Override
            public String getSessionId() {
                return "";
            }

            @Override
            public User getUser() {
                return userDTO2;
            }
        });
        assertTrue(cheatService.isCheat(chatMessage));
        chatService.onRequestChatMessage(chatMessage);

        var cheatInventory = game.getInventory(userDTO2);
        var normalInventory1 = game.getInventory(userDTO1);
        var normalInventory2 = game.getInventory(userDTO3);
        var normalInventory3 = game.getInventory(userDTO);

        assertEquals(cheatInventory.lumber.getNumber(), 0);
        assertEquals(cheatInventory.grain.getNumber(), 0);
        assertEquals(cheatInventory.brick.getNumber(), 0);
        assertEquals(cheatInventory.wool.getNumber(), 0);
        assertEquals(cheatInventory.ore.getNumber(), 15);
        assertEquals(cheatInventory.cardKnight.getNumber(), 0);
        assertEquals(cheatInventory.cardYearOfPlenty.getNumber(), 0);
        assertEquals(cheatInventory.cardMonopoly.getNumber(), 0);
        assertEquals(cheatInventory.cardRoadBuilding.getNumber(), 0);
        assertEquals(cheatInventory.getCardVictoryPoint(), 0);

        assertEquals(normalInventory1.lumber.getNumber(), 0);
        assertEquals(normalInventory1.grain.getNumber(), 0);
        assertEquals(normalInventory1.brick.getNumber(), 0);
        assertEquals(normalInventory1.wool.getNumber(), 0);
        assertEquals(normalInventory1.ore.getNumber(), 0);
        assertEquals(normalInventory1.cardKnight.getNumber(), 0);
        assertEquals(normalInventory1.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory1.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory1.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory1.getCardVictoryPoint(), 0);

        assertEquals(normalInventory2.lumber.getNumber(), 0);
        assertEquals(normalInventory2.grain.getNumber(), 0);
        assertEquals(normalInventory2.brick.getNumber(), 0);
        assertEquals(normalInventory2.wool.getNumber(), 0);
        assertEquals(normalInventory2.ore.getNumber(), 0);
        assertEquals(normalInventory2.cardKnight.getNumber(), 0);
        assertEquals(normalInventory2.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory2.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory2.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory2.getCardVictoryPoint(), 0);

        assertEquals(normalInventory3.lumber.getNumber(), 0);
        assertEquals(normalInventory3.grain.getNumber(), 0);
        assertEquals(normalInventory3.brick.getNumber(), 0);
        assertEquals(normalInventory3.wool.getNumber(), 0);
        assertEquals(normalInventory3.ore.getNumber(), 0);
        assertEquals(normalInventory3.cardKnight.getNumber(), 0);
        assertEquals(normalInventory3.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory3.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory3.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory3.getCardVictoryPoint(), 0);
    }

    /**
     * Test for the roll Cheat
     * <p>
     * This test creates a new RequestChatMessage to emulate a sent ChatMessage from a client <br>
     * Then it sets the session of the RequestChatMessage <br>
     * Then it checks if the sent Message "roll 5" is recognized as a cheat. <br>
     * Then it calls the onRequestChatMessage function from the chatService <br>
     * Then it checks if the event is an instance of the RollDiceResultMessage  <br>
     * Then it checks if the diceResult from the RollDiceResultMessage actually equals the provided integer in the roll cheat (5) <br>
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Test
    @DisplayName("roll Cheat Test")
    void rollCheat() {
        // Get user that currently is onTurn
        var userTurn = game.getUser(game.getTurn());
        RequestChatMessage chatMessage = new RequestChatMessage("roll 5", "game_testLobby", userTurn.getUsername(), 0);
        chatMessage.setSession(new Session() {
            @Override
            public String getSessionId() {
                return "";
            }

            @Override
            public User getUser() {
                return userTurn;
            }
        });
        assertTrue(cheatService.isCheat(chatMessage));

        chatService.onRequestChatMessage(chatMessage);
        assertTrue(event instanceof RollDiceResultMessage);
        var diceResult = ((RollDiceResultMessage) event).getDiceEyes1() + ((RollDiceResultMessage) event)
                .getDiceEyes2();
        assertEquals(diceResult, 5);
    }

    /**
     * Subscribe to the GameFinishedMessage
     * <p>
     * This subscribe method is needed to check if the user receives a GameFinishedMessage after the endgame Cheat
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Subscribe
    void onGameFinishedMessage(GameFinishedMessage message) {
        gameFinished = true;
    }

    /**
     * Test for the endgame Cheat
     * <p>
     * This test creates a new RequestChatMessage to emulate a sent ChatMessage from a client <br>
     * Then it sets the session of the RequestChatMessage <br>
     * Then it checks if the sent Message "endgame 1" is recognized as a cheat. <br>
     * Then it calls the onRequestChatMessage function from the chatService <br>
     * Then it checks if the event is an instanceof PublicInventoryMessage  <br>
     * Then it gets and checks all inventories to if only the cheatUser has 10 victory points in the inventory <br>
     * Finally it checks if the user received a GameFinishedMessage on the bus.
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Test
    @DisplayName("endgame Cheat Test")
    void endGameCheat() {
        RequestChatMessage chatMessage = new RequestChatMessage("endgame 1", "game_testLobby", userDTO2.getUsername(),
                0);
        chatMessage.setSession(new Session() {
            @Override
            public String getSessionId() {
                return "";
            }

            @Override
            public User getUser() {
                return userDTO2;
            }
        });
        assertTrue(cheatService.isCheat(chatMessage));
        chatService.onRequestChatMessage(chatMessage);
        assertTrue(event instanceof PublicInventoryChangeMessage);

        var cheatInventory = game.getInventory(userDTO2);
        var normalInventory1 = game.getInventory(userDTO1);
        var normalInventory2 = game.getInventory(userDTO3);
        var normalInventory3 = game.getInventory(userDTO);

        assertEquals(cheatInventory.getVictoryPoints(), 10);
        assertEquals(normalInventory1.getVictoryPoints(), 0);
        assertEquals(normalInventory2.getVictoryPoints(), 0);
        assertEquals(normalInventory3.getVictoryPoints(), 0);

        assertTrue(gameFinished);
    }

    /**
     * Test for the givemeAll Cheat
     * <p>
     * This test creates a new RequestChatMessage to emulate a sent ChatMessage from a client <br>
     * Then it sets the session of the RequestChatMessage <br>
     * Then it checks if the sent Message "givemeall 15" is recognized as a cheat. <br>
     * Then it calls the onRequestChatMessage function from the chatService <br>
     * Then it gets all inventories of the users  <br>
     * Then it checks all inventories if only the cheatUser has 15 of all resource cards and 1 of every development card in the inventory <br>
     *
     * @author René Meyer
     * @since 2021-06-01
     */
    @Test
    @DisplayName("giveMeAll Cheat Test")
    void giveMeAllCheat() {
        RequestChatMessage chatMessage = new RequestChatMessage("givemeall 15", "game_testLobby",
                userDTO2.getUsername(), 0);
        chatMessage.setSession(new Session() {
            @Override
            public String getSessionId() {
                return "";
            }

            @Override
            public User getUser() {
                return userDTO2;
            }
        });
        assertTrue(cheatService.isCheat(chatMessage));
        chatService.onRequestChatMessage(chatMessage);
        var cheatInventory = game.getInventory(userDTO2);
        var normalInventory1 = game.getInventory(userDTO1);
        var normalInventory2 = game.getInventory(userDTO3);
        var normalInventory3 = game.getInventory(userDTO);


        assertEquals(cheatInventory.lumber.getNumber(), 15);
        assertEquals(cheatInventory.grain.getNumber(), 15);
        assertEquals(cheatInventory.brick.getNumber(), 15);
        assertEquals(cheatInventory.wool.getNumber(), 15);
        assertEquals(cheatInventory.ore.getNumber(), 15);
        assertEquals(cheatInventory.cardKnight.getNumber(), 1);
        assertEquals(cheatInventory.cardYearOfPlenty.getNumber(), 1);
        assertEquals(cheatInventory.cardMonopoly.getNumber(), 1);
        assertEquals(cheatInventory.cardRoadBuilding.getNumber(), 1);
        assertEquals(cheatInventory.getCardVictoryPoint(), 0);

        assertEquals(normalInventory1.lumber.getNumber(), 0);
        assertEquals(normalInventory1.grain.getNumber(), 0);
        assertEquals(normalInventory1.brick.getNumber(), 0);
        assertEquals(normalInventory1.wool.getNumber(), 0);
        assertEquals(normalInventory1.ore.getNumber(), 0);
        assertEquals(normalInventory1.cardKnight.getNumber(), 0);
        assertEquals(normalInventory1.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory1.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory1.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory1.getCardVictoryPoint(), 0);

        assertEquals(normalInventory2.lumber.getNumber(), 0);
        assertEquals(normalInventory2.grain.getNumber(), 0);
        assertEquals(normalInventory2.brick.getNumber(), 0);
        assertEquals(normalInventory2.wool.getNumber(), 0);
        assertEquals(normalInventory2.ore.getNumber(), 0);
        assertEquals(normalInventory2.cardKnight.getNumber(), 0);
        assertEquals(normalInventory2.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory2.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory2.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory2.getCardVictoryPoint(), 0);

        assertEquals(normalInventory3.lumber.getNumber(), 0);
        assertEquals(normalInventory3.grain.getNumber(), 0);
        assertEquals(normalInventory3.brick.getNumber(), 0);
        assertEquals(normalInventory3.wool.getNumber(), 0);
        assertEquals(normalInventory3.ore.getNumber(), 0);
        assertEquals(normalInventory3.cardKnight.getNumber(), 0);
        assertEquals(normalInventory3.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory3.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory3.cardRoadBuilding.getNumber(), 0);
        assertEquals(normalInventory3.getCardVictoryPoint(), 0);
    }
}
