package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public class SQLBasedUserStoreTest {
    private final UserDTO defaultUser = new UserDTO("Marco", "test", "marco@test.de", 1);

    @InjectMocks
    SQLBasedUserStore userStore = new SQLBasedUserStore();

    @Mock
    private Statement statement;

    @Spy
    private Connection connection;

    @Mock
    private ResultSet resultSet;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    final CountDownLatch lock = new CountDownLatch(1);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @BeforeEach
    public void deleteDefaultUser() throws Exception {
        Optional<User> user = userStore.findUser(defaultUser.getUsername(), defaultUser.getPassword());
        if (user.isPresent()) {
            userStore.removeUser(defaultUser.getUsername());
        }
    }

    public void setupResultset() throws SQLException {
        resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true);
        Mockito.when(resultSet.getString(2)).thenReturn(defaultUser.getEMail());
        Mockito.when(resultSet.getInt(3)).thenReturn(defaultUser.getProfilePictureID());
    }

    @Test
    public void findUserWithUserNameTestSuccess() throws Exception {
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(statement.execute(anyString())).thenReturn(true);
        doThrow(NullPointerException.class).when(connection).createStatement();
        setupResultset();

        Optional<User> foundUser = userStore.findUser(defaultUser.getUsername());
        assertTrue(foundUser.isPresent());
        assertEquals(defaultUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    public void findUserWithUserNameTestFail() throws Exception {
        Optional<User> user = userStore.findUser("Maro");

        assertTrue(user.isEmpty());
    }

    @Test
    public void findUserWithUserNamePasswordTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());
        lock.await(1000, TimeUnit.MILLISECONDS);
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
    public void updateUserPictureIDTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        userStore.updateUserPicture(defaultUser.getUsername(), 3);

        Optional<User> updatedUser = userStore.findUser(defaultUser.getUsername());

        assertNotEquals(defaultUser.getProfilePictureID(), updatedUser.get().getProfilePictureID());
        assertEquals(3, updatedUser.get().getProfilePictureID());

        userStore.removeUser(defaultUser.getUsername());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        List<User> userList = userStore.getAllUsers();

        String wantedToFind = null;
        for (User user : userList) {
            if (user.getUsername().equals("Marco")) {
                wantedToFind = user.getUsername();
            }
        }

        assertNotNull(wantedToFind);
        assertTrue(wantedToFind.equals(defaultUser.getUsername()));

        userStore.removeUser(defaultUser.getUsername());
    }
}
