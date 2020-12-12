package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyAlreadyExistsMessage;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest;
import de.uol.swp.common.lobby.message.LobbyLeaveUserRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This a test of the class is used to hide the communication details
 *
 * @author Marius Birk, Carsten Dekker
 * @see de.uol.swp.client.lobby.LobbyService
 * @since 2020-12-02
 *
 */
@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    final User defaultUser = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");

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
    private void loginUser() throws InterruptedException {
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
     * This test first calls the loginUser subroutine. Afterwards it calls the initialize Method, where the lobbyname gets a string.
     * Then checks if a LoginRequest object got posted to the EventBus and if its content is the
     * default users information.
     * <p>
     * Then a new LobbyService will be created and a new UserDTO with the data from the defaultUser. Then we create a new Lobby with the initialized name and UserDTO.
     * After that, we check if a CreateLobbyRequest object got posted to the EventBus.
     * The test fails if any of the checks fail.
     *
     * @throws InterruptedException thrown by loginUser() and initializeTextFields()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Erstelle Lobby")
    void createLobbyTest() throws InterruptedException {
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
     * This test first calls the loginUser subroutine. Afterwards it calls the initialize Method, where the lobbyname gets a string.
     * Then checks if a LoginRequest object got posted to the EventBus and if its content is the
     * default users information.
     * <p>
     * Then a new LobbyService will be created and a new UserDTO with the data from the defaultUser. Then we create a new Lobby with the initialized name and UserDTO.
     * After that, we check if a CreateLobbyRequest object got posted to the EventBus.
     *
     * Next we leave current Lobby with the initialized name and UserDTO.
     * After that, we check if LobbyLeaveUserRequest object got posted to the EventBus.
     * The test fails if any of the checks fail.
     *
     * @throws InterruptedException thrown by loginUser() and initializeTextFields()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Verlasse Lobby")
    void leaveLobbyTest() throws InterruptedException {
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
     * Test for create lobby method, with empty lobbyname
     * <p>
     * This test first calls the loginUser subroutine. We assume that the lobbyname is empty. Also
     * we create a new UserService and a new LobbyService, also we create a new UserDTO.
     * <p>
     * Then we call the createNewLobby Method and assume that it returns false.
     * The test fails if lobbyname is not empty.
     *
     * @throws InterruptedException thrown by loginUser() and lock.await()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Namensfeld leer")
    void createLobbyWithEmptyNameFieldTest() throws InterruptedException {
        loginUser();
        lobbyname = "";

        UserService userService = new UserService(bus);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        assertFalse(lobbyService.createNewLobby(lobbyname, userDTO));
    }

    /**
     * Test for create lobby method, with blank lobbyname
     * <p>
     * This test first calls the loginUser subroutine. We assume that the lobbyname is blank. Also
     * we create a new UserService and a new LobbyService, also we create a new UserDTO.
     * <p>
     * Then we call the createNewLobby Method and assume that it returns false.
     * The test fails if lobbyname is not blank.
     *
     * @throws InterruptedException thrown by loginUser() and lock.await()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Namensfeld blank")
    void createLobbyWithBlankNameFieldTest() throws InterruptedException {
        loginUser();
        lobbyname = "        ";

        UserService userService = new UserService(bus);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        assertFalse(lobbyService.createNewLobby(lobbyname, userDTO));
    }

    /**
     * Test for create lobby method, with null lobbyname
     * <p>
     * This test first calls the loginUser subroutine. We assume that the lobbyname is null. Also
     * we create a new UserService and a new LobbyService, also we create a new UserDTO.
     * <p>
     * Then we call the createNewLobby Method and assume that it returns false.
     * The test fails if lobbyname is not null.
     *
     * @throws InterruptedException thrown by loginUser() and lock.await()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Namensfeld null")
    void createLobbyWithNullNameFieldTest() throws InterruptedException, NullPointerException {
        loginUser();
        lobbyname = null;

        UserService userService = new UserService(bus);
        LobbyService lobbyService = new LobbyService(bus);
        UserDTO userDTO = new UserDTO(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        assertFalse(lobbyService.createNewLobby(lobbyname, userDTO));
    }

    /**
     * Test for the create Lobby event.
     * <p>
     * This test first calls the loginUser subroutine. Afterwards it calls the initialize Method, where the lobbyname gets a string and contains vowel mutations.
     * Then checks if a LoginRequest object got posted to the EventBus and if its content is the
     * default users information.
     * <p>
     * Then a new LobbyService will be created and a new UserDTO with the data from the defaultUser. Then we create a new Lobby with the initialized name and UserDTO.
     * After that, we check if a CreateLobbyRequest object got posted to the EventBus.
     * The test fails if any of the checks fail.
     *
     * @throws InterruptedException thrown by loginUser() and initializeTextFields()
     * @since 2020-12-02
     */
    @Test
    @DisplayName("Erstelle Lobby Umlaute")
    void createLobbyWithVowelMutationTest() throws InterruptedException {
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
     *
     * This Test creates a new LobbyService object registered to the EventBus of
     * this test class. It then calls the retrieveAllLobbies function of the object
     * and waits for it to post a retrieveAllLobbiesRequest object on the EventBus.
     * If this happens within one second, the test is successful.
     *
     * @author Carsten Dekker
     * @throws InterruptedException thrown by lock.await()
     * @since 2020-07-12
     */

    @Test
    void retrieveAllLobbiesTest() throws InterruptedException {

        LobbyService lobbyService = new LobbyService(bus);

        lobbyService.retrieveAllLobbies();

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllLobbiesRequest);
    }
}