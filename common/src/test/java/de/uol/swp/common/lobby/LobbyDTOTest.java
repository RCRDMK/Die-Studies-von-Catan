package de.uol.swp.common.lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the LobbyDTO
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
class LobbyDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User notInLobbyUser = new UserDTO("no", "marco", "no@grawunder.de");

    private static final int NO_USERS = 10;
    private static final List<UserDTO> users;

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO("marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    /**
     * This test check whether a lobby is created correctly
     * <p>
     * If the variables are not set correctly the test fails
     *
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Test
    void createLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);

        assertEquals(lobby.getName(), "test");
        assertEquals(lobby.getUsers().size(), 1);
        assertEquals(lobby.getUsers().iterator().next(), defaultUser);
    }

    /**
     * This test check whether a protected lobby is created correctly
     * <p>
     * If the variables are not set correctly the test fails
     *
     * @author René Meyer
     * @since 2021-06-05
     */
    @Test
    void createProtectedLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        lobby.setPassword("testPw");
        assertEquals(lobby.getName(), "test");
        assertEquals(lobby.getUsers().size(), 1);
        assertEquals(lobby.getUsers().iterator().next(), defaultUser);
        assertEquals(lobby.getPasswordHash(), "testPw".hashCode());
    }

    /**
     * This test check whether a user can join a lobby
     * <p>
     * The test fails if the size of the user list of the lobby does not get bigger
     * or a user who joined is not in the list.
     *
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Test
    void joinUserLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);

        lobby.joinUser(users.get(0));
        assertEquals(lobby.getUsers().size(), 2);
        assertTrue(lobby.getUsers().contains(users.get(0)));

        lobby.joinUser(users.get(0));
        assertEquals(lobby.getUsers().size(), 2);

        lobby.joinUser(users.get(1));
        assertEquals(lobby.getUsers().size(), 3);
        assertTrue(lobby.getUsers().contains(users.get(1)));
    }

    /**
     * This test check whether a user can leave a lobby
     * <p>
     * The test fails if the size of the user list of the lobby does not get smaller
     * or the user who left is still in the list.
     *
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Test
    void leaveUserLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        users.forEach(lobby::joinUser);

        assertEquals(lobby.getUsers().size(), users.size() + 1);
        lobby.leaveUser(users.get(5));

        assertEquals(lobby.getUsers().size(), users.size() + 1 - 1);
        assertFalse(lobby.getUsers().contains(users.get(5)));
    }

    /**
     * Test to check if the owner can leave the Lobby correctly
     * <p>
     * This test fails if the owner field is not updated if the owner leaves the
     * lobby or if he still is in the user list of the lobby.
     *
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Test
    void removeOwnerFromLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        users.forEach(lobby::joinUser);

        lobby.leaveUser(defaultUser);

        assertNotEquals(lobby.getOwner(), defaultUser);
        assertTrue(users.contains(lobby.getOwner()));

    }

    /**
     * This checks if the owner of a lobby can be updated and if he has have joined
     * the lobby
     * <p>
     * This test fails if the owner cannot be updated or does not have to be joined
     *
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Test
    void updateOwnerTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        users.forEach(lobby::joinUser);

        lobby.updateOwner(users.get(6));
        assertEquals(lobby.getOwner(), users.get(6));

        assertThrows(IllegalArgumentException.class, () -> lobby.updateOwner(notInLobbyUser));
    }

    /**
     * This test check whether a lobby can be empty
     * <p>
     * If the leaveUser function does not throw an Exception the test fails
     *
     * @author Marco Grawunder
     * @since 2019-10-08
     */
    @Test
    void assureNonEmptyLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);

        assertThrows(IllegalArgumentException.class, () -> lobby.leaveUser(defaultUser));
    }


}
