package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.HashMap;

/**
 * Message send to all users, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PublicInventoryChangeMessage extends AbstractGameMessage {
    private HashMap publicInventory;

    /**
     * Default constructor
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */

    public PublicInventoryChangeMessage(HashMap publicInventory, User user, String gameName) {
        super(gameName, (UserDTO) user.getWithoutPassword());
        this.publicInventory = publicInventory;

    }
}