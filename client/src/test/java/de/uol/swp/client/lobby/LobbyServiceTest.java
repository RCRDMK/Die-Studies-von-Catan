package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.request.LobbyLeaveUserRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;
    String lobbyname;


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
        lock.countDown();
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
     * @throws InterruptedException thrown by lock.await()
     * @since 2020-12-02
     */
    private void loginUser() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);
        userService.login(defaultUser.getUsername(), defaultUser.getPassword());
        lock.await(1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Subroutine, used for tests, that need textfields and strings.
     *
     * @throws InterruptedException thrown by lock.await()
     * @since 2020-12-02
     */
    private void initializeTextFields() throws InterruptedException {
        lobbyname = "Testlobby";

        lock.await(1000, TimeUnit.MILLISECONDS);
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
     * @throws InterruptedException thrown by loginUser() and initializeTextFields()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Erstelle Lobby")
    void createLobbyTest() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        initializeTextFields();

        assertTrue(event instanceof LoginRequest);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        lobbyService.createNewLobby(lobbyname, userDTO);

        lock.await(1000, TimeUnit.MILLISECONDS);

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
     * @throws InterruptedException thrown by loginUser() and initializeTextFields()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Verlasse Lobby")
    void leaveLobbyTest() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        initializeTextFields();

        assertTrue(event instanceof LoginRequest);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        lobbyService.createNewLobby(lobbyname, userDTO);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CreateLobbyRequest);

        lobbyService.leaveLobby(lobbyname, userDTO);

        lock.await(1000, TimeUnit.MILLISECONDS);

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
     * @throws InterruptedException thrown by loginUser() and initializeTextFields()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Erstelle Lobby Umlaute")
    void createLobbyWithVowelMutationTest() throws InterruptedException, InvalidKeySpecException,
            NoSuchAlgorithmException {
        loginUser();
        lobbyname = "äüÖÄöÜ";

        assertTrue(event instanceof LoginRequest);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        lobbyService.createNewLobby(lobbyname, userDTO);

        lock.await(1000, TimeUnit.MILLISECONDS);

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
     * @throws InterruptedException thrown by lock.await()
     * @author Carsten Dekker
     * @since 2020-07-12
     */

    @Test
    void retrieveAllLobbiesTest() throws InterruptedException {

        LobbyService lobbyService = new LobbyService(bus);

        lobbyService.retrieveAllLobbies();

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllLobbiesRequest);
    }

    /**
     * Test for leaveLobby()
     * <p>
     * This test checks if a user who created a lobby, can leave it
     *
     * @throws InterruptedException
     * @since 2020-12-10
     */
    @Test
    @DisplayName("Creator can leave")
    void lobbyCreatorCanLeaveTest() throws InterruptedException {
        LobbyService lobbyService = new LobbyService(bus);
        CreateLobbyRequest message = new CreateLobbyRequest("test", (UserDTO) defaultUser);
        lobbyService.createNewLobby("test", (UserDTO) defaultUser);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(defaultUser);
        lobbyService.leaveLobby("test", (UserDTO) defaultUser);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof LobbyLeaveUserRequest);

    }

    /**
     * Test for leaveLobby()
     * <p>
     * This test checks if a user who joined a lobby, can leave it
     *
     * @throws InterruptedException
     * @since 2020-12-10
     */
    @Test
    @DisplayName("joined User can leave")
    void lobbyJoinedUserCanLeaveTest() throws InterruptedException {
        LobbyService lobbyService = new LobbyService(bus);
        CreateLobbyRequest message = new CreateLobbyRequest("test", (UserDTO) defaultUser);
        lobbyService.createNewLobby("test", (UserDTO) defaultUser);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(defaultUser);
        lobbyService.joinLobby("test", (UserDTO) defaultUser2);
        ArrayList<UserDTO> users = new ArrayList<>();
        users.add((UserDTO) defaultUser);
        UserJoinedLobbyMessage message3 = new UserJoinedLobbyMessage("test", (UserDTO) defaultUser2,users);
        lobbyService.leaveLobby("test", (UserDTO) defaultUser2);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof LobbyLeaveUserRequest);
    }

    /**
     * Test for leaveLobby()
     * <p>
     * This test checks if a owner of a lobby can leave it, if another user is in it
     *
     * @throws InterruptedException
     * @since 2020-12-10
     */
    @Test
    @DisplayName("Owner leaves, joined User stays in the lobby")
    void lobbyOwnerLeavesJoinedUserStaysTest() throws InterruptedException {
        LobbyService lobbyService = new LobbyService(bus);
        CreateLobbyRequest message = new CreateLobbyRequest("test", (UserDTO) defaultUser);
        lobbyService.createNewLobby("test", (UserDTO) defaultUser);
        LobbyCreatedSuccessfulResponse message2 = new LobbyCreatedSuccessfulResponse(defaultUser);
        lobbyService.joinLobby("test", (UserDTO) defaultUser2);
        ArrayList<UserDTO> users = new ArrayList<>();
        users.add((UserDTO) defaultUser);
        UserJoinedLobbyMessage message3 = new UserJoinedLobbyMessage("test", (UserDTO) defaultUser2, users);
        lobbyService.leaveLobby("test", (UserDTO) defaultUser);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof LobbyLeaveUserRequest);
    }


    /**
     * @throws InterruptedException
     */

    @Test
    @DisplayName("Beitrete Lobby")
    void joinLobbyTest() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        initializeTextFields();
        loginUser();
        initializeTextFields();
        assertTrue(event instanceof LoginRequest);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        UserDTO userDTO2 = new UserDTO(defaultUser2.getUsername(), defaultUser2.getPassword(), defaultUser2.getEMail());
        lobbyService.createNewLobby(lobbyname, userDTO);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof CreateLobbyRequest);
        lobbyService.joinLobby(lobbyname, userDTO2);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(event instanceof LobbyJoinUserRequest);
    }

}