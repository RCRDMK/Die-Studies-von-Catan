package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SQLBasedUserStoreTest {
    private final UserDTO defaultUser = new UserDTO("Marco", "test", "marco@test.de", 1);
    SQLBasedUserStore userStore = new SQLBasedUserStore();

    @BeforeEach
    public void buildConnection() throws SQLException {
        userStore.buildConnection();
    }

    @AfterEach
    public void closeConnection() throws SQLException {
        userStore.closeConnection();
    }

    @Test
    public void findUserWithUserNameTestSuccess() throws Exception {
        userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail());

        Optional<User> foundUser = userStore.findUser(defaultUser.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals(defaultUser.getUsername(), foundUser.get().getUsername());
        assertTrue(defaultUser.getPassword().isEmpty());
    }
}
