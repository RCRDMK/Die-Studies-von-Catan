package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class SQLBasedUserStoreTest {
    private final UserDTO defaultUser = new UserDTO("Marco", "test", "marco@test.de", 1);
    SQLBasedUserStore userStore = new SQLBasedUserStore();
    final CountDownLatch lock = new CountDownLatch(1);

    @BeforeEach
    public void buildConnection() throws SQLException {
        userStore.buildConnection();
    }

    @AfterEach
    public void closeConnection() throws Exception {
        userStore.closeConnection();
    }

    @Test
    public void findUserWithUserNameTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        lock.await(1000, TimeUnit.MILLISECONDS);
        Optional<User> foundUser = userStore.findUser(defaultUser.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals(defaultUser.getUsername(), foundUser.get().getUsername());
        assertTrue(foundUser.get().getPassword().isEmpty());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void findUserWithUserNameTestFail() throws Exception {
        Optional<User> foundUser = userStore.findUser("SWPJ2020/21");

        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void findUserWithUserNamePasswordTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        lock.await(1000, TimeUnit.MILLISECONDS);
        Optional<User> foundUser = userStore.findUser(defaultUser.getUsername(), defaultUser.getPassword());

        assertTrue(foundUser.isPresent());
        assertEquals(defaultUser.getUsername(), foundUser.get().getUsername());
        //TODO Same TODO as above
        assertEquals(defaultUser.getPassword(), foundUser.get().getPassword());
        assertTrue(foundUser.get().getPassword().isEmpty());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void findUserWithUserNamePasswordTestFail() throws Exception {
        Optional<User> foundUser = userStore.findUser("SWPJ2020/21", defaultUser.getPassword());

        assertTrue(foundUser.isEmpty());
    }

    @Test
    public void createUserDuplicateTest() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        lock.await(1000, TimeUnit.MILLISECONDS);

        assertThrows(Exception.class, () -> userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail()));

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void removeNonExistingUserTest()  {

        assertThrows(Exception.class, () -> userStore.removeUser("SWPJ2020/21"));
    }

    @Test
    public void updateUserMailTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        userStore.updateUserMail(defaultUser.getUsername(), "test@test.de");

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertNotEquals(defaultUser.getEMail(), updatedUser.get().getEMail());
        assertEquals("test@test.de", updatedUser.get().getEMail());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void updateUserMailUserUnknownTest()  {

        assertThrows(Exception.class, () -> userStore.updateUserMail("SWPJ2020/21", "test@test.de"));
    }

    @Test
    public void updateUserPasswordTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        lock.await(1000, TimeUnit.MILLISECONDS);
        userStore.updateUserPassword(defaultUser.getUsername(), "123456789");

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertNotEquals(defaultUser.getPassword(), updatedUser.get().getPassword());
        //TODO Carsten gefragt, wie der Umgang mit Passwörtern seit Umstellung ist.
        assertNotEquals("123456789", updatedUser.get().getPassword());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void updateUserPasswordUserUnknownTest()  {

        assertThrows(Exception.class, () -> userStore.updateUserPassword("SWPJ2020/21", "123456789"));
    }

    @Test
    public void updateUserPictureIDTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        userStore.updateUserPicture(defaultUser.getUsername(), 3);

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertNotEquals(defaultUser.getProfilePictureID(), updatedUser.get().getProfilePictureID());
        assertEquals(3, updatedUser.get().getProfilePictureID());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void updateUserPictureIDUserUnknownTest()  {

        assertThrows(Exception.class, () -> userStore.updateUserPicture("SWPJ2020/21", 5));
    }
}
