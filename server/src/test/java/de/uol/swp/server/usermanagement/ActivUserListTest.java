package de.uol.swp.server.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import static org.junit.jupiter.api.Assertions.*;

class ActiveUserListTest {

    final User user = new UserDTO("name", "password", "email@test.de");
    final User user2 = new UserDTO("name2", "password2", "email@test.de2");

    private final ActiveUserList activeUserList = new ActiveUserList();
    private final long time = 1;

    @Test
    void addActiveUserTest() {
        assertTrue(activeUserList.activeUserTable.isEmpty());
        activeUserList.addActiveUser(user);
        activeUserList.addActiveUser(user);
        activeUserList.addActiveUser(user2);
        assertTrue(activeUserList.activeUserTable.containsKey(user));
        assertTrue(activeUserList.activeUserTable.containsKey(user2));
        activeUserList.removeActiveUser(user);
        activeUserList.removeActiveUser(user2);
    }

    @Test
    void removeActiveUserTest() {
        assertTrue(activeUserList.activeUserTable.isEmpty());
        activeUserList.addActiveUser(user);
        activeUserList.addActiveUser(user2);
        assertTrue(activeUserList.activeUserTable.containsKey(user));
        assertTrue(activeUserList.activeUserTable.containsKey(user2));
        activeUserList.removeActiveUser(user);
        activeUserList.removeActiveUser(user2);
        assertTrue(activeUserList.activeUserTable.isEmpty());
    }

    @Test
    void updateActiveUserTest() {
        assertTrue(activeUserList.activeUserTable.isEmpty());
        activeUserList.addActiveUser(user);
        activeUserList.updateActiveUser(user, time);
        assertEquals(time, activeUserList.activeUserTable.get(user));
        long t1 = System.currentTimeMillis();
        activeUserList.updateActiveUser(user, t1);
        assertEquals(t1, activeUserList.activeUserTable.get(user));
        activeUserList.removeActiveUser(user);
    }

    @Test
    void checkActiveUserTest() {
        activeUserList.addActiveUser(user);
        activeUserList.addActiveUser(user2);
        activeUserList.updateActiveUser(user, time);
        List<User> userList = new ArrayList<>();
        userList.add(user);
        assertEquals(userList, activeUserList.checkActiveUser());
        assertTrue(activeUserList.activeUserTable.containsKey(user));
        assertTrue(activeUserList.activeUserTable.containsKey(user2));
        activeUserList.removeActiveUser(user);
        activeUserList.removeActiveUser(user2);
    }

}
