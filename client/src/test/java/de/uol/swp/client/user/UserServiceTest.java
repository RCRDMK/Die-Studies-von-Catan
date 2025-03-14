package de.uol.swp.client.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.CountDownLatch;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.DropUserRequest;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This a test of the class is used to hide the communication details
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.user.UserService
 * @since 2019-10-10
 */
@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

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
     * @author Marco Grawunder
     * @since 2019-10-10
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
     * @author Marco Grawunder
     * @since 2019-10-10
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
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    private void loginUser() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);
        userService.login(defaultUser.getUsername(), defaultUser.getPassword());
    }

    /**
     * Test for the login method
     * <p>
     * This test first calls the loginUser subroutine. Afterwards it checks if a
     * LoginRequest object got posted to the EventBus and if its content is the
     * default users information.
     * The test fails if any of the checks fail.
     *
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    @Test
    void loginTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);
        loginUser();

        assertTrue(event instanceof LoginRequest);

        LoginRequest loginRequest = (LoginRequest) event;
        assertEquals(loginRequest.getUsername(), defaultUser.getUsername());
        assertEquals(loginRequest.getPassword(), userService.convertStringToHash(defaultUser.getPassword()));
    }

    /**
     * Test for the logout method
     * <p>
     * This test first calls the loginUser subroutine. Afterwards it creates a new
     * UserService object registered to the EventBus of this test class. It then
     * calls the logout function of the object using the defaultUser as parameter
     * and waits for it to post an LogoutRequest object on the EventBus. It then
     * checks if authorization is needed to logout the user.
     * The test fails if no LogoutRequest is posted within one second or the request
     * says that no authorization is needed
     *
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    @Test
    void logoutTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();
        event = null;

        UserService userService = new UserService(bus);
        userService.logout(defaultUser);

        assertTrue(event instanceof LogoutRequest);

        LogoutRequest request = (LogoutRequest) event;

        assertTrue(request.authorizationNeeded());
    }

    /**
     * Test for the createUser routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the createUser function of the object using
     * the defaultUser as parameter and waits for it to post an updateUserRequest
     * object on the EventBus.
     * If this happens within one second, it checks if the user in the request object
     * is the same as the default user and if authorization is needed.
     * Authorization should not be needed.
     * If any of these checks fail or the method takes to long, this test is unsuccessful.
     *
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    @Test
    void createUserTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);
        userService.createUser(defaultUser);

        assertTrue(event instanceof RegisterUserRequest);

        RegisterUserRequest request = (RegisterUserRequest) event;

        assertEquals(request.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(request.getUser().getPassword(), userService.convertStringToHash(defaultUser.getPassword()));
        assertEquals(request.getUser().getEMail(), defaultUser.getEMail());
        assertFalse(request.authorizationNeeded());

    }

    /**
     * Test for the dropUser routine
     * <p>
     * This test case has to be implemented after the respective dropUser method
     * has been implemented
     *
     * @author Marco Grawunder
     * @author Marius Birk und Carsten Dekker
     * @since 2019-10-10
     * <p>
     * Enhanced the test method
     * <p>
     * This test method creates an instance of userService and uses the dropUser method.
     * We expect an event instanceof DropUserRequest. There is also an event instanceof
     * LogoutRequest.
     * @since 2020-12-15
     */
    @Test
    void dropUserTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        loginUser();

        UserService userService = new UserService(bus);
        userService.dropUser(defaultUser);

        assertTrue(event instanceof DropUserRequest);
    }

    /**
     * Test for the retrieveAllUsers routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the retrieveAllUsers function of the object
     * and waits for it to post a retrieveAllUsersRequest object on the EventBus.
     * If this happens within one second, the test is successful.
     *
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    @Test
    void retrieveAllUsersTest() {
        UserService userService = new UserService(bus);
        userService.retrieveAllUsers();

        assertTrue(event instanceof RetrieveAllOnlineUsersRequest);
    }

    @Test
    void convertStringToHashTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);

        assertEquals(userService.convertStringToHash("test"),
                userService.convertStringToHash(defaultUser.getPassword()));
    }

    @Test
    void failedConvertStringToHashTest() throws InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);

        assertNotEquals(userService.convertStringToHash("Test"),
                userService.convertStringToHash(defaultUser.getPassword()));
    }
}