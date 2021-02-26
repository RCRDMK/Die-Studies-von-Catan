package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class UserManagementTest {

    private static final int NO_USERS = 10;
    private static final User userNotInStore = new UserDTO("marco" + NO_USERS, "marco" + NO_USERS, "marco" + NO_USERS + "@grawunder.de");
    private UserManagement management = new UserManagement();

    @Test
    void loginUser() throws SQLException {
        management.buildConnection();
        User userToLogIn = new UserDTO("test2", "test2", "test");

        management.login(userToLogIn.getUsername(), userToLogIn.getPassword());

        assertTrue(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserEmptyPassword() throws SQLException {
        management.buildConnection();
        User userToLogIn = new UserDTO("test", "test", "");

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), ""));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserWrongPassword() throws SQLException {

        management.buildConnection();
        User userToLogIn = new UserDTO("test", "test", "");
        User secondUser = new UserDTO("test1", "test1", "");

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), secondUser.getPassword()));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void logoutUser() throws SQLException {
        management.buildConnection();
        User userToLogin = new UserDTO("test", "newPassword", "");

        management.login(userToLogin.getUsername(), userToLogin.getPassword());

        assertTrue(management.isLoggedIn(userToLogin));

        management.logout(userToLogin);

        assertFalse(management.isLoggedIn(userToLogin));

    }

    @Test
    void createUser() throws SQLException {
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
        management.buildConnection();
        User one = new UserDTO("test32", "test32", "test32");

        management.createUser(one);

        management.dropUser(one);

        assertThrows(SecurityException.class,
                () -> management.login(one.getUsername(), one.getPassword()));
    }

    @Test
    void dropUserNotExisting() throws SQLException {
        management.buildConnection();
        User one = new UserDTO("test32", "test32", "test32");

        assertThrows(UserManagementException.class,
                () -> management.dropUser(one));
    }

    @Test
    void createUserAlreadyExisting() throws SQLException {
        management.buildConnection();

        User userToCreate = new UserDTO("test", "test", "test");

        assertThrows(UserManagementException.class, () -> management.createUser(userToCreate));

    }

    @Test
    void updateUserPassword_NotLoggedIn() throws SQLException {
        management.buildConnection();

        User userToUpdate = new UserDTO("test", "test", "irgendwas@irgendwo.de");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "newPassword", "irgendwas@irgendwo.de");

        assertFalse(management.isLoggedIn(userToUpdate));
        management.updateUser(updatedUser);

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
    }

    @Test
    void updateUser_Mail() throws SQLException {
        management.buildConnection();

        User userToUpdate = new UserDTO("test2", "test", "");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "test2", "new1Mail@mail.com");

        management.updateUser(updatedUser);

        User user = management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
        assertEquals(user.getEMail(), updatedUser.getEMail());
    }

    @Test
    void updateUserPassword_LoggedIn() throws SQLException {
        management.buildConnection();

        User userToUpdate = new UserDTO("test2", "test2", "irgendwas@irgendwo.de");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "newPassword", "irgendwas@irgendwo.de");

        management.login(userToUpdate.getUsername(), userToUpdate.getPassword());
        assertTrue(management.isLoggedIn(userToUpdate));

        management.updateUser(updatedUser);
        assertTrue(management.isLoggedIn(updatedUser));

        management.logout(updatedUser);
        assertFalse(management.isLoggedIn(updatedUser));

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));

        management.updateUser(userToUpdate);

    }

    @Test
    void updateUnknownUser() throws SQLException {
        management.buildConnection();
        assertThrows(UserManagementException.class, () -> management.updateUser(userNotInStore));
    }

    @Test
    void retrieveAllUsers() throws SQLException {
        management.buildConnection();
        List<User> userList = management.retrieveAllUsers();

        assertEquals(management.retrieveAllUsers().size(), userList.size());
    }

    @Test
    void connectJDBCTest() throws SQLException {
        management.buildConnection();
    }
}