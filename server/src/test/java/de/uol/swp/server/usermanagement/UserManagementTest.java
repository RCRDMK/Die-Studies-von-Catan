package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.SQLBasedUserStore;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


class UserManagementTest {

    private static final int NO_USERS = 10;
    private static final User userNotInStore = new UserDTO("marco" + NO_USERS, "marco" + NO_USERS, "marco" + NO_USERS + "@grawunder.de", 1);
    private final MainMemoryBasedUserStore mainMemoryBasedUserStore = new MainMemoryBasedUserStore();
    private final UserManagement management = new UserManagement(mainMemoryBasedUserStore);

    UserManagementTest() throws SQLException {
    }

    @Test
    void loginUser() throws Exception {

        User userToLogIn = userNotInStore;

        management.createUser(userNotInStore);

        management.login(userToLogIn.getUsername(), userToLogIn.getPassword());

        assertTrue(management.isLoggedIn(userToLogIn));

        management.logout(userToLogIn);
        management.dropUser(userToLogIn);
    }

    @Test
    void loginUserEmptyPassword() throws Exception {

        User userToLogIn = userNotInStore;

        management.createUser(userToLogIn);

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), ""));

        assertFalse(management.isLoggedIn(userToLogIn));

        management.dropUser(userToLogIn);
    }

    @Test
    void loginUserWrongPassword() throws Exception {

        User userToLogIn = userNotInStore;

        management.createUser(userToLogIn);

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(),
                "wrongPassword"));

        assertFalse(management.isLoggedIn(userToLogIn));

        management.dropUser(userToLogIn);
    }

    @Test
    void logoutUser() throws Exception {

        User userToLogin = userNotInStore;

        management.createUser(userToLogin);

        management.login(userToLogin.getUsername(), userToLogin.getPassword());

        assertTrue(management.isLoggedIn(userToLogin));

        management.logout(userToLogin);

        assertFalse(management.isLoggedIn(userToLogin));

        management.dropUser(userToLogin);
    }

    @Test
    void createUser() throws Exception {

        User userToCreate = userNotInStore;

        management.createUser(userToCreate);

        // Creation leads not to log in
        assertFalse(management.isLoggedIn(userToCreate));

        // Only way to test, if user is stored
        management.login(userToCreate.getUsername(), userToCreate.getPassword());

        assertTrue(management.isLoggedIn(userToCreate));

        management.logout(userToCreate);

        //After every testrun the user needs to be deleted
        management.dropUser(userToCreate);
    }

    @Test
    void createUserAlreadyCreated() throws Exception {

        User userToCreate = userNotInStore;

        management.createUser(userToCreate);

        assertThrows(UserManagementException.class, () -> management.createUser(userToCreate));

        management.dropUser(userToCreate);
    }

    @Test
    void dropUser() throws Exception {

        User userToDrop = userNotInStore;

        management.createUser(userToDrop);

        management.dropUser(userToDrop);

        assertThrows(SecurityException.class,
                () -> management.login(userToDrop.getUsername(), userToDrop.getPassword()));
    }

    @Test
    void dropUserNotExisting() {

        assertThrows(UserManagementException.class,
                () -> management.dropUser(userNotInStore));
    }

    @Test
    void updateUserPassword() throws Exception {

        User userToUpdatePassword = userNotInStore;

        User newPassword = new UserDTO(userToUpdatePassword.getUsername(), "newPassword", "", 1);

        management.createUser(userNotInStore);

        management.login(userToUpdatePassword.getUsername(), userToUpdatePassword.getPassword());

        assertTrue(management.isLoggedIn(userToUpdatePassword));

        management.logout(userToUpdatePassword);

        management.updateUserPassword(newPassword, userToUpdatePassword.getPassword());

        management.login(userToUpdatePassword.getUsername(), "newPassword");

        assertTrue(management.isLoggedIn(userToUpdatePassword));

        management.logout(userToUpdatePassword);

        management.dropUser(userToUpdatePassword);
    }

    @Test
    void updateUnknownUserPassword() {

        User userToUpdatePassword = userNotInStore;

        assertThrows(UserManagementException.class, () -> management.updateUserPassword(userToUpdatePassword, userToUpdatePassword.getPassword()));
    }

    @Test
    void updateUserMail() throws Exception {

        User userToUpdateMail = userNotInStore;

        User newEmail = new UserDTO(userToUpdateMail.getUsername(), "", "test@test.de", 1);

        management.createUser(userToUpdateMail);

        management.updateUserMail(newEmail);

        management.dropUser(userToUpdateMail);
    }

    @Test
    void updateUnknownUserMail() {

        assertThrows(UserManagementException.class, () -> management.updateUserMail(userNotInStore));
    }

    @Test
    void updateUserPictureID() throws Exception {

        User userToUpdatePictureID = userNotInStore;

        User newPictureID = new UserDTO(userToUpdatePictureID.getUsername(), "", "", 20);

        management.createUser(userToUpdatePictureID);

        management.updateUserPicture(newPictureID);

        management.dropUser(userToUpdatePictureID);

    }

    @Test
    void updateUnknownUserPictureID() {
        assertThrows(UserManagementException.class, () -> management.updateUserPicture(userNotInStore));
    }

    @Test
    void retrieveUserInformation() throws Exception {

        User user = userNotInStore;

        management.createUser(user);

        User withInformation = management.retrieveUserInformation(user);

        assertNotEquals(user.getPassword(), withInformation.getPassword());

        assertEquals(user.getUsername(), withInformation.getUsername());

        assertEquals(user.getProfilePictureID(), withInformation.getProfilePictureID());

        management.dropUser(user);
    }

    @Test
    void retrieveUnknownUserInformation() {
        assertThrows(UserManagementException.class, () -> management.retrieveUserInformation(userNotInStore));
    }
}