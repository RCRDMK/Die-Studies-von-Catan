package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.HashMap;

/**
 * Message send to an specific user, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PrivateInventoryChangeMessage extends AbstractGameMessage {

    private HashMap<String, Integer> privateInventory;

    /**
     * Default constructor
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */


    public PrivateInventoryChangeMessage(String name, UserDTO user, HashMap<String, Integer> privateInventory) {
        super(name, user);
        this.privateInventory = privateInventory;
    }
}