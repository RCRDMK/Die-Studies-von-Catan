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
        User userToLogIn = new UserDTO("test2", "994dac907995937160371992ecbdf9b34242db0abb3943807b5baa6be0c6908f72ea87b7dadd2bce6cf700c8dfb7d57b0566f544af8c30336a15d5f732d85613", "t@te.de");

        management.login(userToLogIn.getUsername(), userToLogIn.getPassword());

        assertTrue(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserEmptyPassword() throws SQLException {
        management.buildConnection();
        User userToLogIn = new UserDTO("test", "33eda9895af9f99456b85c2381bfc49543531e92517e3b7c67e86310874dd3a0e08b0dae5d3103ddabcf1794d3833c52659c35c2980f71ce6705bf967a96d856", "");

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), ""));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void loginUserWrongPassword() throws SQLException {

        management.buildConnection();
        User userToLogIn = new UserDTO("test", "33eda9895af9f99456b85c2381bfc49543531e92517e3b7c67e86310874dd3a0e08b0dae5d3103ddabcf1794d3833c52659c35c2980f71ce6705bf967a96d856", "");
        User secondUser = new UserDTO("test1", "47b7d407c2e2f3aff0e21aa16802006ba1793fd47b2d3cacee7cf7360e751bff7b7d0c7946b42b97a5306c6708ab006d0d81ef41a0c9f94537a2846327c51236", "");

        assertThrows(SecurityException.class, () -> management.login(userToLogIn.getUsername(), secondUser.getPassword()));

        assertFalse(management.isLoggedIn(userToLogIn));
    }

    @Test
    void logoutUser() throws SQLException {
        management.buildConnection();
        User userToLogin = new UserDTO("test", "0835ae0b1f8bcb3508e09990403eea4200e294be58224fb0c97ea652cd59fcd97219815a27564680a72ee28b614adcc2843df4c7dcc3f64cf721dea5189db475", "");

        management.login(userToLogin.getUsername(), userToLogin.getPassword());

        assertTrue(management.isLoggedIn(userToLogin));

        management.logout(userToLogin);

        assertFalse(management.isLoggedIn(userToLogin));
    }

    @Test
    void createUser() throws SQLException {
        management.buildConnection();
        User one = new UserDTO("test32", "84b1497a64ae274f2b4829d521feb94ce6facd582c9db6423fc76bb652b698441551aa32d063c8ae090e31b074dc05d65ff4b426ccab6ebf02a02fcd4e816586", "test32@test.de");

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
        User one = new UserDTO("test32", "276d2b015fbce039d39fe7d31000073e4513fe3571af6671e59563f357189f975339750cc09ed78aa88e156b209e44216ca3239fda5471f27a55cf49bee172f3", "test32@test.de");

        management.createUser(one);

        management.dropUser(one);

        assertThrows(SecurityException.class,
                () -> management.login(one.getUsername(), one.getPassword()));
    }

    @Test
    void dropUserNotExisting() throws SQLException {
        management.buildConnection();
        User one = new UserDTO("test32", "276d2b015fbce039d39fe7d31000073e4513fe3571af6671e59563f357189f975339750cc09ed78aa88e156b209e44216ca3239fda5471f27a55cf49bee172f3", "test32@test32.de");

        assertThrows(UserManagementException.class,
                () -> management.dropUser(one));
    }

    @Test
    void createUserAlreadyExisting() throws SQLException {
        management.buildConnection();

        User userToCreate = new UserDTO("test", "33eda9895af9f99456b85c2381bfc49543531e92517e3b7c67e86310874dd3a0e08b0dae5d3103ddabcf1794d3833c52659c35c2980f71ce6705bf967a96d856", "test@test.de");

        assertThrows(UserManagementException.class, () -> management.createUser(userToCreate));

    }

    @Test
    void updateUserPassword_NotLoggedIn() throws SQLException {
        management.buildConnection();

        User userToUpdate = new UserDTO("test", "33eda9895af9f99456b85c2381bfc49543531e92517e3b7c67e86310874dd3a0e08b0dae5d3103ddabcf1794d3833c52659c35c2980f71ce6705bf967a96d856", "irgendwas@irgendwo.de");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "0835ae0b1f8bcb3508e09990403eea4200e294be58224fb0c97ea652cd59fcd97219815a27564680a72ee28b614adcc2843df4c7dcc3f64cf721dea5189db475", "irgendwas@irgendwo.de");

        assertFalse(management.isLoggedIn(userToUpdate));
        management.updateUser(updatedUser);

        management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
    }

    @Test
    void updateUser_Mail() throws SQLException {
        management.buildConnection();

        User userToUpdate = new UserDTO("test2", "33eda9895af9f99456b85c2381bfc49543531e92517e3b7c67e86310874dd3a0e08b0dae5d3103ddabcf1794d3833c52659c35c2980f71ce6705bf967a96d856", "");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "994dac907995937160371992ecbdf9b34242db0abb3943807b5baa6be0c6908f72ea87b7dadd2bce6cf700c8dfb7d57b0566f544af8c30336a15d5f732d85613", "new1Mail@mail.com");

        management.updateUser(updatedUser);

        User user = management.login(updatedUser.getUsername(), updatedUser.getPassword());
        assertTrue(management.isLoggedIn(updatedUser));
        assertEquals(user.getEMail(), updatedUser.getEMail());
    }

    @Test
    void updateUserPassword_LoggedIn() throws SQLException {
        management.buildConnection();

        User userToUpdate = new UserDTO("test2", "994dac907995937160371992ecbdf9b34242db0abb3943807b5baa6be0c6908f72ea87b7dadd2bce6cf700c8dfb7d57b0566f544af8c30336a15d5f732d85613", "irgendwas@irgendwo.de");
        User updatedUser = new UserDTO(userToUpdate.getUsername(), "0835ae0b1f8bcb3508e09990403eea4200e294be58224fb0c97ea652cd59fcd97219815a27564680a72ee28b614adcc2843df4c7dcc3f64cf721dea5189db475", "irgendwas@irgendwo.de");

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