package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.game.request.PlayerReadyRequest;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This a test of the class is used to hide the communication details
 *
 * @author Marius Birk, Carsten Dekker
 * @see de.uol.swp.client.lobby.LobbyService
 * @since 2020-12-02
 */
@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    final User defaultUser = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    final User defaultUser2 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");
    final EventBus bus = new EventBus();
    Object event;
    String lobbyName;
    LobbyService lobbyService = new LobbyService(bus);


    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected the event variable of this class gets updated
     * to its event and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
    }

    /**
     * Helper method run before each test case
     * <p>
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     *
     * @since 2020-12-02
     */
    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method only unregisters the object of this class from the EventBus.
     *
     * @since 2020-12-02
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Subroutine used for tests that need a logged in user
     * <p>
     * This subroutine creates a new UserService object registered to the EventBus
     * of this test class and class the objects login method for the default user.
     *
     * @since 2020-12-02
     */
    private void loginUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);
        userService.login(defaultUser.getUsername(), defaultUser.getPassword());
    }

    /**
     * Subroutine, used for tests, that need textfields and strings.
     *
     * @since 2020-12-02
     */
    private void initializeTextFields() {
        lobbyName = "Testlobby";

    }

    /**
     * Test for the create Lobby event.
     * <p>
     * This test first calls the loginUser subroutine. Afterwards it calls the initialize Method, where the
     * lobbyname gets a string. Then checks if a LoginRequest object got posted to the EventBus and if its content is
     * the default users information.
     * <p>
     * Then a new LobbyService will be created and a new UserDTO with the data from the defaultUser. Then we create
     * a new Lobby with the initialized name and UserDTO.
     * After that, we check if a CreateLobbyRequest object got posted to the EventBus.
     * The test fails if any of the checks fail.
     *
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Erstelle Lobby")
    void createLobbyTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        initializeTextFields();

        assertTrue(event instanceof LoginRequest);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        lobbyService.createNewLobby(lobbyName, userDTO);

        assertTrue(event instanceof CreateLobbyRequest);
    }

    /**
     * Test for the leave Lobby event.
     * <p>
     * This test first calls the loginUser subroutine. Afterwards it calls the initialize Method, where the
     * lobbyname gets a string. Then checks if a LoginRequest object got posted to the EventBus and if its content is
     * the default users information.
     * <p>
     * Then a new LobbyService will be created and a new UserDTO with the data from the defaultUser. Then we create
     * a new Lobby with the initialized name and UserDTO.
     * After that, we check if a CreateLobbyRequest object got posted to the EventBus.
     * <p>
     * Next we leave current Lobby with the initialized name and UserDTO.
     * After that, we check if LobbyLeaveUserRequest object got posted to the EventBus.
     * The test fails if any of the checks fail.
     *
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Verlasse Lobby")
    void leaveLobbyTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        initializeTextFields();

        assertTrue(event instanceof LoginRequest);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        lobbyService.createNewLobby(lobbyName, userDTO);

        assertTrue(event instanceof CreateLobbyRequest);

        lobbyService.leaveLobby(lobbyName, userDTO);

        assertTrue(event instanceof LobbyLeaveUserRequest);
    }

    /**
     * Test for the create Lobby event.
     * <p>
     * This test first calls the loginUser subroutine. Afterwards it calls the initialize Method, where the lobbyname
     * gets a string and contains vowel mutations. Then checks if a LoginRequest object got posted to the EventBus
     * and if its content is the default users information.
     * <p>
     * Then a new LobbyService will be created and a new UserDTO with the data from the defaultUser. Then we create
     * a new Lobby with the initialized name and UserDTO.
     * After that, we check if a CreateLobbyRequest object got posted to the EventBus.
     * The test fails if any of the checks fail.
     *
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Erstelle Lobby Umlaute")
    void createLobbyWithVowelMutationTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        lobbyName = "äüÖÄöÜ";

        assertTrue(event instanceof LoginRequest);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        lobbyService.createNewLobby(lobbyName, userDTO);

        assertTrue(event instanceof CreateLobbyRequest);
    }

    /**
     * Test for the retrieveAllLobbies routine
     * <p>
     * This Test creates a new LobbyService object registered to the EventBus of
     * this test class. It then calls the retrieveAllLobbies function of the object
     * and waits for it to post a retrieveAllLobbiesRequest object on the EventBus.
     * If this happens within one second, the test is successful.
     *
     * @author Carsten Dekker
     * @since 2020-07-12
     */
    @Test
    void retrieveAllLobbiesTest() {

        lobbyService.retrieveAllLobbies();

        assertTrue(event instanceof RetrieveAllLobbiesRequest);
    }

    /**
     * Test for leaveLobby()
     * <p>
     * This test checks if a user who created a lobby, can leave it
     *
     * @since 2020-12-10
     */
    @Test
    @DisplayName("Creator can leave")
    void lobbyCreatorCanLeaveTest() {
        lobbyService.createNewLobby("test", (UserDTO) defaultUser);
        lobbyService.leaveLobby("test", (UserDTO) defaultUser);
        assertTrue(event instanceof LobbyLeaveUserRequest);

    }

    /**
     * Test for leaveLobby()
     * <p>
     * This test checks if a user who joined a lobby, can leave it
     *
     * @since 2020-12-10
     */
    @Test
    @DisplayName("joined User can leave")
    void lobbyJoinedUserCanLeaveTest() {
        lobbyService.createNewLobby("test", (UserDTO) defaultUser);
        lobbyService.joinLobby("test", (UserDTO) defaultUser2);
        lobbyService.leaveLobby("test", (UserDTO) defaultUser2);
        assertTrue(event instanceof LobbyLeaveUserRequest);
    }

    /**
     * Test for leaveLobby()
     * <p>
     * This test checks if a owner of a lobby can leave it, if another user is in it
     *
     * @since 2020-12-10
     */
    @Test
    @DisplayName("Owner leaves, joined User stays in the lobby")
    void lobbyOwnerLeavesJoinedUserStaysTest() {
        lobbyService.createNewLobby("test", (UserDTO) defaultUser);
        lobbyService.joinLobby("test", (UserDTO) defaultUser2);
        lobbyService.leaveLobby("test", (UserDTO) defaultUser);
        assertTrue(event instanceof LobbyLeaveUserRequest);
    }


    /**
     * Test for joinLobby()
     * <p>
     * This test checks if a User can join Lobby
     */
    @Test
    @DisplayName("Beitrete Lobby")
    void joinLobbyTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        initializeTextFields();
        assertTrue(event instanceof LoginRequest);
        lobbyService.createNewLobby(lobbyName, (UserDTO) defaultUser);
        assertTrue(event instanceof CreateLobbyRequest);
        lobbyService.joinLobby(lobbyName, (UserDTO) defaultUser2);
        assertTrue(event instanceof LobbyJoinUserRequest);
    }

    @Test
    public void sendPlayerReadyRequestTest() {
        lobbyService.sendPlayerReadyRequest("Test", (UserDTO) defaultUser, true);

        assertTrue(event instanceof PlayerReadyRequest);
        assertEquals(defaultUser.getUsername(), ((PlayerReadyRequest) event).getUser().getUsername());
        assertEquals("Test", ((PlayerReadyRequest) event).getName());
    }

    @Test
    public void startGameTest() {
        lobbyService.startGame("Test", (UserDTO) defaultUser, "Standard", 2);

        assertTrue(event instanceof StartGameRequest);
        assertEquals("Test", ((StartGameRequest) event).getName());
        assertEquals("Standard", ((StartGameRequest) event).getGameFieldVariant());
        assertEquals(defaultUser.getUsername(), ((StartGameRequest) event).getUser().getUsername());
    }
}