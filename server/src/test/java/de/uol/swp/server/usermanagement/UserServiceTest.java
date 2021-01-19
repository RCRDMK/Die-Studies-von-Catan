package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.DropUserRequest;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.response.DropUserSuccessfulResponse;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    static final User userToRegister = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final User userWithSameName = new UserDTO("Marco", "Marco2", "Marco2@Grawunder.com");
    static final User userToDrop = new UserDTO("Carsten", "Stahl", "Carsten@Stahl.com");

    final CountDownLatch lock = new CountDownLatch(1);

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement();
    final UserService userService = new UserService(bus, userManagement);

    UserServiceTest() throws SQLException {
    }

    @Test
    void registerUserTest() throws SQLException {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);

        // The post will lead to a call of a UserService function
        bus.post(request);

        // can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        assertNotNull(loggedInUser);
        assertEquals(loggedInUser, userToRegister);
    }

    @Test
    void registerSecondUserWithSameName() throws SQLException {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);
        final RegisterUserRequest request2 = new RegisterUserRequest(userWithSameName);

        bus.post(request);
        bus.post(request2);

        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        // old user should be still in the store
        assertNotNull(loggedInUser);
        assertEquals(loggedInUser, userToRegister);

        // old user should not be overwritten!
        assertNotEquals(loggedInUser.getEMail(), userWithSameName.getEMail());

    }

    /**
    * Test for the dropUser routine on the server
     *
     * This test method posts two Requests on the bus. The First request is a RegisterRequest and the
     * second one is a dropUserRequest.
     * First we expect the user userToDrop to be registered and then to get dropped.
     * We check if the registration was successful and if the user is dropped.
     *
     * @author Marius Birk und Carsten Dekker
     * @since 2020-12-15
    */
    @Test
    void dropUserTest() throws InterruptedException, SQLException {

        final RegisterUserRequest registerRequest = new RegisterUserRequest(userToDrop);
        final DropUserRequest dropUserRequest = new DropUserRequest(userToDrop);

        bus.post(registerRequest);
        lock.await(1000, TimeUnit.MILLISECONDS);
        assertTrue(userManagement.retrieveAllUsers().contains(registerRequest.getUser()));
        lock.await(1000, TimeUnit.MILLISECONDS);
        bus.post(dropUserRequest);
        assertFalse(userManagement.retrieveAllUsers().contains(dropUserRequest.getUser()));
    }
}