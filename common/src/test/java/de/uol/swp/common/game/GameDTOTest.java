package de.uol.swp.common.game;

import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the GameDTO
 *
 * @author Marius Birk
 * @since 2021-03-17
 */

public class GameDTOTest {
    private final UserDTO userDTO = new UserDTO("test", "", "");
    private final UserDTO userDTO1 = new UserDTO("test1", "", "");
    private final UserDTO userDTO2 = new UserDTO("test2", "", "");
    private final UserDTO userDTO3 = new UserDTO("test3", "", "");
    private final Set<User> users = new TreeSet<>();

    private GameDTO defaultGame = new GameDTO("test", new UserDTO("test", "", ""), "",users );

    /**
     * This test checks if the user can join the game correctly.
     * <p>
     * We create a new userDTO and join the user to the default game.
     * This will add him to the user set in the gameDTO.
     *
     * @author Marius Birk
     * @since 2021-03-17
     */
    @Test
    void joinUserTest() {
        User user = new UserDTO("test2", "", "");
        defaultGame.joinUser(user);

        assertEquals(defaultGame.getUsers().size(), 2);
    }

    /**
     * This test checks if the user can leave the game correctly.
     * <p>
     * We create a new userDTO and join the user to the default game.
     * This will add him to the user set in the gameDTO.
     * <p>
     * After that, we want the user to leave the game and check if the user is not longer
     * in the set of the gameDTO.
     *
     * @author Marius Birk
     * @since 2021-03-17
     */
    @Test
    void leaveUserTestSuccess() {
        User user = new UserDTO("test2", "", "");
        defaultGame.joinUser(user);
        assertEquals(defaultGame.getUsers().size(), 2);

        defaultGame.leaveUser(user);
        assertEquals(defaultGame.getUsers().size(), 1);
    }

    /**
     * This test tries to remove the defaultUser from the defaultGame, which ends up in an IllegalArgumentException.
     *
     * @author Marius Birk
     * @since 2021-05-25
     */
    @Test
    public void leaveUserTestFail() {
        assertThrows(IllegalArgumentException.class, () -> defaultGame.leaveUser(userDTO));
    }

    /**
     * This test joins another user to the defaultGame. The original owner leaves the lobby and
     * the test checks if the new user is now the owner of the defaultGame.
     *
     * @author Marius Birk
     * @since 2021-05-25
     */
    @Test
    public void leaveUserTestOwner(){
        User user = new UserDTO("test2", "", "");
        defaultGame.joinUser(user);

        defaultGame.leaveUser(userDTO);

        assertEquals(user.getUsername(), defaultGame.getOwner().getUsername());
    }

    /**
     * This test checks if the owner of the game can be updated correctly.
     * <p>
     * We create a new userDTO and join the user to the default game.
     * This will add him to the user set in the gameDTO.
     * <p>
     * After that, we want the user to be the owner of the game and check if the user
     * is now the owner.
     *
     * @author Marius Birk
     * @since 2021-03-17
     */
    @Test
    void updateOwnerTest() {
        User user = new UserDTO("test2", "", "");
        defaultGame.joinUser(user);
        assertEquals(defaultGame.getUsers().size(), 2);

        defaultGame.updateOwner(user);

        assertEquals(defaultGame.getOwner(), user);
    }

    @Test
    public void updateOwnerTestFail(){
        User newUser = new UserDTO("Test", "", "");
        assertThrows(IllegalArgumentException.class, ()->defaultGame.updateOwner(newUser));
    }

    @Test
    public void setUpInventoriesTest(){
        users.addAll(Arrays.asList(userDTO, userDTO1, userDTO2, userDTO3));
        defaultGame = new GameDTO("test", userDTO, "Standard", users);
        defaultGame.joinUser(userDTO);
        defaultGame.joinUser(userDTO1);
        defaultGame.joinUser(userDTO2);
        defaultGame.joinUser(userDTO3);

        defaultGame.setUpUserArrayList();
        defaultGame.setUpInventories();

        assertEquals(userDTO.getUsername(), defaultGame.getInventory(userDTO).getUser().getUsername());
        assertEquals(userDTO1.getUsername(), defaultGame.getInventory(userDTO1).getUser().getUsername());
        assertEquals(userDTO2.getUsername(), defaultGame.getInventory(userDTO2).getUser().getUsername());
        assertEquals(userDTO3.getUsername(), defaultGame.getInventory(userDTO3).getUser().getUsername());
        assertNotEquals(userDTO.getUsername(), defaultGame.getInventory(userDTO3).getUser().getUsername());
    }

    @Test
    public void getInventoriesArrayListTest(){
        users.addAll(Arrays.asList(userDTO, userDTO1, userDTO2, userDTO3));
        defaultGame = new GameDTO("test", userDTO, "Standard", users);
        defaultGame.joinUser(userDTO);
        defaultGame.joinUser(userDTO1);
        defaultGame.joinUser(userDTO2);
        defaultGame.joinUser(userDTO3);

        defaultGame.setUpUserArrayList();
        defaultGame.setUpInventories();

        ArrayList<Inventory> inventories = defaultGame.getInventoriesArrayList();

        assertEquals(4, inventories.size());

        assertTrue(inventories.contains(defaultGame.getInventory(userDTO)));
        assertTrue(inventories.contains(defaultGame.getInventory(userDTO1)));
        assertTrue(inventories.contains(defaultGame.getInventory(userDTO2)));
        assertTrue(inventories.contains(defaultGame.getInventory(userDTO3)));
    }
}
