package de.uol.swp.common.game.message;

import java.util.HashMap;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

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

    public PrivateInventoryChangeMessage(String gameName, User user, HashMap<String, Integer> privateInventory) {
        super(gameName, (UserDTO) user);
        this.privateInventory = privateInventory;

    }

    /**
     * Getter for the hashMap of the privateInventory
     *
     * @return the HashMap representing the private View of the user
     * @author Marc Hermes
     * @since 2021-05-03
     */
    public HashMap<String, Integer> getPrivateInventory() {
        return privateInventory;
    }
}