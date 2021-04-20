package de.uol.swp.common.game.message;

import java.util.HashMap;

/**
 * Message send to an specific user, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PrivateInventoryChangeMessage extends AbstractGameMessage {

    private HashMap privateInventory;

    /**
     * Default constructor
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */

    public PrivateInventoryChangeMessage(HashMap privateInventory) {
        this.privateInventory = privateInventory;

    }

}