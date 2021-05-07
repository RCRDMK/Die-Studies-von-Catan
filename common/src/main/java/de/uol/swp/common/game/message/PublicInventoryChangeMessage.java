package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

import java.util.HashMap;

/**
 * Message send to all users, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PublicInventoryChangeMessage extends AbstractGameMessage {
    private HashMap publicInventory;
    private User user;

    /**
     * Default constructor
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */

    public PublicInventoryChangeMessage(HashMap publicInventory, User user) {
        this.publicInventory = publicInventory;
        this.user = user;

    }
}