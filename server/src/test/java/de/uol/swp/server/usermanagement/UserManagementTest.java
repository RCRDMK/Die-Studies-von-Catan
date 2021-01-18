package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.SqlUserStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserManagementTest {

    private static final int NO_USERS = 10;
    private static final User userNotInStore = new UserDTO("marco" + NO_USERS, "marco" + NO_USERS, "marco" + NO_USERS + "@grawunder.de");

    UserManagement getDefaultManagement() {
        SqlUserStore store = new SqlUserStore();
        return new UserManagement(store);
    }

    @Test
    void loginUser() throws SQLException {
        UserManagement management = getDefaultManagement();
        management.buildConnection();
        User userToLogIn = new UserDTO("test","test", "test");

        management.login(userToLogIn.getUsername(), userToLogIn.getPassword());

        assertTrue(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserEmptyPassword() {
        UserManagement management = getDefaultManagement();
        User userToLogIn = new UserDTO("test", "", "");

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), ""));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserWrongPassword() throws SQLException {
        UserManagement management = getDefaultManagement();
        management.buildConnection();
        User userToLogIn = new UserDTO("test", "", "");
        User secondUser = new UserDTO("test1", "test1", "");

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), secondUser.getPassword()));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void logoutUser() throws SQLException {
        UserManagement management = getDefaultManagement();
        management.buildConnection();
        User userToLogin = new UserDTO("test", "test", "");

        management.login(userToLogin.getUsername(), userToLogin.getPassword());

        assertTrue(management.isLoggedIn(userToLogin));

        management.logout(userToLogin);

        assertFalse(management.isLoggedIn(userToLogin));

    }

    @Test
    void createUser() throws SQLException {
        UserManagement management = getDefaultManagement();
        management.buildConnection();
        User one = new UserDTO("test32", "test32", "test32");

        management.createUser(one);

        // Creation leads not to log in
        assertFalse(management.isLoggedIn(one));

        // Only way to test, if user is stored
        management.login(one.getUsername(), one.getPassword());

        assertTrue(management.isLoggedIn(one));

        //After every testrun the user needs to be deleted
        management.dropUser(one);
    }

    @Test
    void dropUser() throws SQLException {
        UserManagement management = getDefaultManagement();
        management.buildConnection();
        User one = new UserDTO("test32", "test32", "test32");
        management.createUser(one);

        management.dropUser(one);

        assertThrows(SecurityException.class,
                () -> management.login(one.getUsername(), one.getPassword()));
    }

    @Test
    void dropUserNotExisting() {
        UserManagement management = getDefaultManagement();
        assertThrows(UserManagementException.class,
                () -> management.dropUser(userNotInStore));
    }

    @Test
    void createUserAlreadyExisting() {
        UserManagement management = getDefaultManagement();
        User userToCreate = new UserDTO("test", "", "");

        assertThrows(UserManagementException.class, () -> management.createUser(userToCreate));

    }

    @Test
    void updateUserPassword_NotLoggedIn() throws SQLException {
        UserManagement management = getDefaultManagement();
        User userToUpdate = new UserDTO("test", "", "");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "newPassword", null);

        assertFalse(management.isLoggedIn(userToUpdate));
        management.updateUser(updatedUser);

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
    }

    @Test
    void updateUser_Mail() throws SQLException {
        UserManagement management = getDefaultManagement();
        User userToUpdate = new UserDTO("test", "", "");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "", "newMail@mail.com");

        management.updateUser(updatedUser);

        User user = management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
        assertEquals(user.getEMail(), updatedUser.getEMail());
    }

    @Test
    void updateUserPassword_LoggedIn() throws SQLException {
        UserManagement management = getDefaultManagement();
        User userToUpdate = new UserDTO("test", "", "");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "newPassword", null);

        management.login(userToUpdate.getUsername(), userToUpdate.getPassword());
        assertTrue(management.isLoggedIn(userToUpdate));

        management.updateUser(updatedUser);
        assertTrue(management.isLoggedIn(updatedUser));

        management.logout(updatedUser);
        assertFalse(management.isLoggedIn(updatedUser));

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));

    }

    @Test
    void updateUnknownUser() {
        UserManagement management = getDefaultManagement();
        assertThrows(UserManagementException.class, () -> management.updateUser(userNotInStore));
    }

    @Test
    void retrieveAllUsers() throws SQLException {

    }

    @Test
    void connectJDBCTest() throws SQLException {
        UserManagement userManagement = getDefaultManagement();
        userManagement.buildConnection();
    }

}