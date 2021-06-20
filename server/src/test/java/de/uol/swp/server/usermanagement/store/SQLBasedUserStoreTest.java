package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SQLBasedUserStoreTest {
    private final UserDTO defaultUser = new UserDTO("Marco", "test", "marco@test.de", 1);
    SQLBasedUserStore userStore = new SQLBasedUserStore();

    @BeforeEach
    public void buildConnection() throws SQLException {
        userStore.buildConnection();
    }

    @BeforeEach
    public void deleteDefaultUser() throws Exception {
        Optional<User> user = userStore.findUser(defaultUser.getUsername(), defaultUser.getPassword());
        if(user.isPresent()){
            userStore.removeUser(defaultUser.getUsername());
        }
    }

    @AfterEach
    public void closeConnection() throws Exception {
        userStore.closeConnection();
    }

    @Test
    public void findUserWithUserNameTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        //lock.await(1000, TimeUnit.MILLISECONDS);
        Optional<User> foundUser = userStore.findUser(defaultUser.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals(defaultUser.getUsername(), foundUser.get().getUsername());
        assertTrue(foundUser.get().getPassword().isEmpty());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void findUserWithUserNameTestFail() throws Exception {
        Optional<User> user = userStore.findUser("Maro");

        assertTrue(user.isEmpty());
    }

    @Test
    public void findUserWithUserNamePasswordTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        //lock.await(1000, TimeUnit.MILLISECONDS);
        Optional<User> foundUser = userStore.findUser(defaultUser.getUsername(), defaultUser.getPassword());

        assertTrue(foundUser.isPresent());
        assertEquals(defaultUser.getUsername(), foundUser.get().getUsername());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void findUserWithUserNamePasswordTestFail() throws Exception {
        Optional<User> user = userStore.findUser("Maro", "Test");

        assertTrue(user.isEmpty());
    }

    @Test
    public void findUserWithUserNamePasswordNotEqualTestFail() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        Optional<User> user = userStore.findUser(defaultUser.getUsername(), "Test");

        assertTrue(user.isEmpty());
        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void updateUserMailTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        userStore.updateUserMail(defaultUser.getUsername(), "test@test.de");

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertTrue(updatedUser.isPresent());
        assertNotEquals(defaultUser.getEMail(), updatedUser.get().getEMail());
        assertEquals("test@test.de", updatedUser.get().getEMail());

        userStore.removeUser(defaultUser.getUsername());
    }


    @Test
    public void updateUserPasswordTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        //lock.await(1000, TimeUnit.MILLISECONDS);
        userStore.updateUserPassword(defaultUser.getUsername(), "123456789");

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertTrue(updatedUser.isPresent());
        assertNotEquals(defaultUser.getPassword(), updatedUser.get().getPassword());
        assertNotEquals("123456789", updatedUser.get().getPassword());

        userStore.removeUser(defaultUser.getUsername());
    }


    @Test
    public void updateUserPictureIDTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        userStore.updateUserPicture(defaultUser.getUsername(), 3);

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertTrue(updatedUser.isPresent());
        assertNotEquals(defaultUser.getProfilePictureID(), updatedUser.get().getProfilePictureID());
        assertEquals(3, updatedUser.get().getProfilePictureID());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        List<User> userList = userStore.getAllUsers();

        String wantedToFind = null;
        for(User user: userList){
            if (user.getUsername().equals("Marco")){
                wantedToFind = user.getUsername();
            }
        }

        assertNotNull(wantedToFind);
        assertEquals(defaultUser.getUsername(), wantedToFind);

        userStore.removeUser(defaultUser.getUsername());
    }

}
