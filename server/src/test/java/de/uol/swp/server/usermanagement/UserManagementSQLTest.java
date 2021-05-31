package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.store.SQLBasedUserStore;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserManagementSQLTest {
    private final SQLBasedUserStore userStore = new SQLBasedUserStore();
    private final UserManagement userManagement = new UserManagement(userStore);
    private final UserDTO defaultUser = new UserDTO("Marco", "test", "marco@test.de", 1);


    public UserManagementSQLTest() throws SQLException {
    }

    @Test
    public void closeConnectionTest() throws Exception {
        userManagement.closeConnection();
        assertThrows(SQLNonTransientConnectionException.class, () -> userStore.createUser(defaultUser.getUsername(), defaultUser.getPassword(), defaultUser.getEMail()));
    }
}
