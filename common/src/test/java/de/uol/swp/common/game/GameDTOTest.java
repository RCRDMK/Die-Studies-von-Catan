package de.uol.swp.common.game;

import de.uol.swp.common.game.dto.GameDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test Class for the GameDTO
 *
 * @author Marius Birk
 * @since 2021-03-17
 */

public class GameDTOTest {
    private GameDTO defaultGame = new GameDTO("test", new UserDTO("test", "", ""));

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
    void leaveUserTest() {
        User user = new UserDTO("test2", "", "");
        defaultGame.joinUser(user);
        assertEquals(defaultGame.getUsers().size(), 2);

        defaultGame.leaveUser(user);
        assertEquals(defaultGame.getUsers().size(), 1);
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
}
