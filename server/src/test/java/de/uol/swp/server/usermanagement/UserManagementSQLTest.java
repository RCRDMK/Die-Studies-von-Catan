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

    /**
     * This test closes the connection to the user database.
     * <p>
     * The test closes the connection to the database and reconnects automaticly.
     * Because of the parameter in the JDBC string, the re-establishment of the connection
     * is only made possible.
     *
     * @author Marius Birk
     * @since 2021-06-06
     * @throws Exception
     */
    @Test
    public void closeConnectionTest() throws Exception {
        userManagement.closeConnection();
        assertThrows(NullPointerException.class, ()-> userStore.findUser(defaultUser.getUsername()));
    }
}
