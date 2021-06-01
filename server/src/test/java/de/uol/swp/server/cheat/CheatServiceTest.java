package de.uol.swp.server.cheat;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.game.Game;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheatServiceTest {
    final EventBus bus = new EventBus();
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    GameManagement gameManagement = new GameManagement();
    LobbyManagement lobbyManagement = new LobbyManagement();
    MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    LobbyService lobbyService = new LobbyService(lobbyManagement, new AuthenticationService(bus, userManagement), bus);
    UserService userService = new UserService(bus, userManagement);
    GameService gameService = new GameService(gameManagement, lobbyService, authenticationService, bus, userService);
    CheatService cheatService = new CheatService(gameService, bus);
    ChatService chatService = new ChatService(cheatService, bus);

    UserDTO userDTO = new UserDTO("test1", "47b7d407c2e2f3aff0e21aa16802006ba1793fd47b2d3cacee7cf7360e751bff7b7d0c7946b42b97a5306c6708ab006d0d81ef41a0c9f94537a2846327c51236", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("test2", "994dac907995937160371992ecbdf9b34242db0abb3943807b5baa6be0c6908f72ea87b7dadd2bce6cf700c8dfb7d57b0566f544af8c30336a15d5f732d85613", "carsten.stahl@uol.de");
    UserDTO userDTO2 = new UserDTO("test3", "b74a37371ca548bfd937410737b27f383e03021766e90f1180169691b8b15fc50aef49932c7413c0450823777ba46a34fd649b4da20b2e701c394c582ff6df55", "peterlustig@uol.de");
    UserDTO userDTO3 = new UserDTO("test4", "65dfe56dd0e9117907b11e440d99a667527ddb13244aa38f79d3ae61ee0b2ab4047c1218c4fb05d84f88b914826c45de3ab27a611ea910a4b14733ab1e32b125", "test.lustig@uol.de");

    Optional<Game> game;

    Object event;

    public CheatServiceTest() throws SQLException {
    }

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
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

    void loginUsers() {
        authenticationService.onLoginRequest(new LoginRequest(userDTO.getUsername(), userDTO.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO1.getUsername(), userDTO1.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO2.getUsername(), userDTO2.getPassword()));
        authenticationService.onLoginRequest(new LoginRequest(userDTO3.getUsername(), userDTO3.getPassword()));
    }

    @BeforeEach
    void setupLobbyAndGame() {
        loginUsers();
        lobbyManagement.createLobby("testLobby", userDTO);
        Optional<Lobby> lobby = lobbyManagement.getLobby("testLobby");
        assertTrue(lobby.isPresent());
        lobby.get().joinUser(userDTO1);
        lobby.get().joinUser(userDTO2);
        lobby.get().joinUser(userDTO3);
        gameManagement.createGame(lobby.get().getName(), lobby.get().getOwner(), "Standard");
        game = gameManagement.getGame(lobby.get().getName());
        game.get().joinUser(userDTO1);
        game.get().joinUser(userDTO2);
        game.get().joinUser(userDTO3);
        game.get().setUpUserArrayList();
        game.get().setUpInventories();
        assertTrue(game.isPresent());
    }

    @Test
    void giveMeAllCheat() {
        RequestChatMessage chatMessage = new RequestChatMessage("givemeall 15", "game_testLobby", userDTO2.getUsername(), 0);
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
        var cheatInventory = game.get().getInventory(userDTO2);
        var normalInventory1 = game.get().getInventory(userDTO1);
        var normalInventory2 = game.get().getInventory(userDTO3);
        var normalInventory3 = game.get().getInventory(userDTO);


        assertEquals(cheatInventory.lumber.getNumber(), 15);
        assertEquals(cheatInventory.grain.getNumber(), 15);
        assertEquals(cheatInventory.brick.getNumber(), 15);
        assertEquals(cheatInventory.wool.getNumber(), 15);
        assertEquals(cheatInventory.ore.getNumber(), 15);
        assertEquals(cheatInventory.cardKnight.getNumber(), 1);
        assertEquals(cheatInventory.cardYearOfPlenty.getNumber(), 1);
        assertEquals(cheatInventory.cardMonopoly.getNumber(), 1);
        assertEquals(cheatInventory.cardRoadBuilding.getNumber(), 1);

        assertEquals(normalInventory1.lumber.getNumber(), 0);
        assertEquals(normalInventory1.grain.getNumber(), 0);
        assertEquals(normalInventory1.brick.getNumber(), 0);
        assertEquals(normalInventory1.wool.getNumber(), 0);
        assertEquals(normalInventory1.ore.getNumber(), 0);
        assertEquals(normalInventory1.cardKnight.getNumber(), 0);
        assertEquals(normalInventory1.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory1.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory1.cardRoadBuilding.getNumber(), 0);

        assertEquals(normalInventory2.lumber.getNumber(), 0);
        assertEquals(normalInventory2.grain.getNumber(), 0);
        assertEquals(normalInventory2.brick.getNumber(), 0);
        assertEquals(normalInventory2.wool.getNumber(), 0);
        assertEquals(normalInventory2.ore.getNumber(), 0);
        assertEquals(normalInventory2.cardKnight.getNumber(), 0);
        assertEquals(normalInventory2.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory2.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory2.cardRoadBuilding.getNumber(), 0);

        assertEquals(normalInventory3.lumber.getNumber(), 0);
        assertEquals(normalInventory3.grain.getNumber(), 0);
        assertEquals(normalInventory3.brick.getNumber(), 0);
        assertEquals(normalInventory3.wool.getNumber(), 0);
        assertEquals(normalInventory3.ore.getNumber(), 0);
        assertEquals(normalInventory3.cardKnight.getNumber(), 0);
        assertEquals(normalInventory3.cardYearOfPlenty.getNumber(), 0);
        assertEquals(normalInventory3.cardMonopoly.getNumber(), 0);
        assertEquals(normalInventory3.cardRoadBuilding.getNumber(), 0);
    }
}
