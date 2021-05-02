package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.HashMap;

/**
 * Message send to an specific user, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PrivateInventoryChangeMessage extends AbstractGameMessage {

    private final HashMap<String, Integer> privateInventory;

    /**
     * Default constructor
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */

    public PrivateInventoryChangeMessage(User user, String gameName, HashMap<String, Integer> privateInventory) {
        super(gameName,(UserDTO) user);
        this.privateInventory = privateInventory;

    }

    public HashMap<String, Integer> getPrivateInventory() {
        return privateInventory;
    }
}