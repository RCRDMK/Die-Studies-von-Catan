package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.request.GameLeaveUserRequest;
import de.uol.swp.common.game.request.RetrieveAllThisGameUsersRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
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

    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");
    UserDTO userDTO2 = new UserDTO("Test", "lustig1", "peterlustig@uol.de");
    UserDTO userDTO3 = new UserDTO("Test2", "lustig2", "test.lustig@uol.de");

    public GameServiceTest() throws SQLException {
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
     * This test checks if the distributeResource method works as intendet.
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
