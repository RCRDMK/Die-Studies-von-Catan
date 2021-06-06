package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.inject.internal.cglib.core.$MethodWrapper;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.exception.RetrieveUserInformationExceptionMessage;
import de.uol.swp.common.user.request.*;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;
import org.w3c.dom.events.Event;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    static final User userToRegister = new UserDTO("Marco", "Marco", "Marco@Grawunder.com");
    static final User userWithSameName = new UserDTO("Marco", "Marco2", "Marco2@Grawunder.com");
    static final User userToDrop = new UserDTO("Carsten", "Stahl", "Carsten@Stahl.com");

    Event event;

    final CountDownLatch lock = new CountDownLatch(1);

    final EventBus bus = new EventBus();
    MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(mainMemoryBasedUserStore);
    final UserService userService = new UserService(bus, userManagement);

    UserServiceTest() throws SQLException {
    }

    @Test
    void registerUserTest() throws Exception {
        final RegisterUserRequest request = new RegisterUserRequest(userToRegister);

        request.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });

        // The post will lead to a call of a UserService function
        bus.post(request);

        // can only test, if something in the state has changed
        final User loggedInUser = userManagement.login(userToRegister.getUsername(), userToRegister.getPassword());

        assertNotNull(loggedInUser);
        assertEquals(loggedInUser, userToRegister);
        userManagement.dropUser(userToRegister);
    }

    @Test
    void registerSecondUserWithSameName() throws Exception {
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

        userManagement.dropUser(userToRegister);

    }

    /**
     * Test for the dropUser routine on the server
     * <p>
     * This test method posts two Requests on the bus. The First request is a RegisterRequest and the
     * second one is a dropUserRequest.
     * First we expect the user userToDrop to be registered and then to get dropped.
     * We check if the registration was successful and if the user is dropped.
     *
     * @author Marius Birk und Carsten Dekker
     * @since 2020-12-15
     */
    @Test
    void dropUserTest() throws SQLException {

        final RegisterUserRequest registerRequest = new RegisterUserRequest(userToDrop);
        final DropUserRequest dropUserRequest = new DropUserRequest(userToDrop);
        dropUserRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });

        bus.post(registerRequest);
        assertTrue(userManagement.retrieveAllUsers().contains(registerRequest.getUser()));
        bus.post(dropUserRequest);
        assertFalse(userManagement.retrieveAllUsers().contains(dropUserRequest.getUser()));
    }

    @Test
    void dropUnknownUserTest() throws Exception {

        userManagement.createUser(userToDrop);

        final DropUserRequest dropUserRequest = new DropUserRequest(userToRegister);

        bus.post(dropUserRequest);

        userManagement.dropUser(userToDrop);
    }

    @Test
    void UpdateUserPasswordTest() throws Exception {

        final RegisterUserRequest registerUserRequest = new RegisterUserRequest(userToRegister);

        bus.post(registerUserRequest);

        User withUpdatedPassword = new UserDTO(userToRegister.getUsername(), "newPassword", "", 1);

        final UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest(withUpdatedPassword, "Marco");

        updateUserPasswordRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });

        bus.post(registerUserRequest);

        assertTrue(userManagement.retrieveAllUsers().contains(registerUserRequest.getUser()));

        bus.post(updateUserPasswordRequest);

        userManagement.login(userToRegister.getUsername(), "newPassword");

        assertTrue(userManagement.isLoggedIn(userToRegister));

        userManagement.logout(userToRegister);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void updateUnknownUserPasswordTest() throws Exception {

        userManagement.createUser(userToRegister);

        final UpdateUserPasswordRequest updateUserPasswordRequest = new UpdateUserPasswordRequest(userToDrop, userToDrop.getEMail());

        bus.post(updateUserPasswordRequest);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void UpdateUserPictureTest() throws Exception {

        userManagement.createUser(userToRegister);

        User userWithNewPicture = new UserDTO(userToRegister.getUsername(), "", "", 30);

        final UpdateUserProfilePictureRequest updateUserProfilePictureRequest = new UpdateUserProfilePictureRequest(userWithNewPicture);

        updateUserProfilePictureRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });

        bus.post(updateUserProfilePictureRequest);

        User updatedUser = userManagement.retrieveUserInformation(userToRegister);

        assertEquals(userWithNewPicture.getProfilePictureID(), updatedUser.getProfilePictureID());

        userManagement.dropUser(userToRegister);
    }

    @Test
    void UpdateUnknownUserPictureTest() throws Exception {

        userManagement.createUser(userToRegister);

        final UpdateUserProfilePictureRequest updateUserProfilePictureRequest = new UpdateUserProfilePictureRequest(userToDrop);

        bus.post(updateUserProfilePictureRequest);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void UpdateUserMailTest() throws Exception {

        userManagement.createUser(userToRegister);

        User userWithNewMail = new UserDTO(userToRegister.getUsername(), "", "newEmail@Email.de", 1);

        final UpdateUserMailRequest updateUserMailRequest = new UpdateUserMailRequest(userWithNewMail);

        updateUserMailRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });

        bus.post(updateUserMailRequest);

        User updatedUser = userManagement.retrieveUserInformation(userToRegister);

        assertSame(updatedUser.getEMail(), userWithNewMail.getEMail());

        userManagement.dropUser(userToRegister);
    }

    @Test
    void UpdateUnknownUserMailTest() throws Exception {

        userManagement.createUser(userToRegister);

        final UpdateUserMailRequest updateUserMailRequest = new UpdateUserMailRequest(userToDrop);

        bus.post(updateUserMailRequest);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void retrieveUserInformationTest() throws Exception {

        userManagement.createUser(userToRegister);

        User userWithInformation = userService.retrieveUserInformation(userToRegister);

        assertEquals(userToRegister, userWithInformation);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void retrieveUnknownUserInformationTest() throws Exception {

        userManagement.createUser(userToRegister);

        userService.retrieveUserInformation(userToDrop);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void onRetrieveUserInformationTest() throws Exception {

        userManagement.createUser(userToRegister);

        RetrieveUserInformationRequest retrieveUserInformationRequest = new RetrieveUserInformationRequest(userToRegister);

        retrieveUserInformationRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });

        bus.post(retrieveUserInformationRequest);

        userManagement.dropUser(userToRegister);
    }

    @Test
    void onRetrieveUnknownUserInformationTest() throws Exception {

        userManagement.createUser(userToRegister);

        RetrieveUserInformationRequest retrieveUserInformationRequest = new RetrieveUserInformationRequest(userToDrop);

        retrieveUserInformationRequest.setMessageContext(new MessageContext() {
            @Override
            public void writeAndFlush(ResponseMessage message) {

            }

            @Override
            public void writeAndFlush(ServerMessage message) {

            }
        });
        bus.post(retrieveUserInformationRequest);

        userManagement.dropUser(userToRegister);
    }
}