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

    private HashMap privateInventory;

    public PrivateInventoryChangeMessage(String name, UserDTO user, HashMap privateInventory) {
        super(name, user);
        this.privateInventory = privateInventory;
    }

    /**
     * Default constructor
     *
     * Enhanced with game name and userDTO.
     * @author Marius Birk
     * @since 2021-05-3
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */



    public HashMap getPrivateInventory() {
        return privateInventory;
    }
}