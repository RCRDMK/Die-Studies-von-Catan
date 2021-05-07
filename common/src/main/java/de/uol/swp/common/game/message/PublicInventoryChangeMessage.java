package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Message send to all users, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PublicInventoryChangeMessage extends AbstractGameMessage {
    private ArrayList <HashMap <String, Integer>> publicInventories;


    /**
     * Default constructor
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */

    public PublicInventoryChangeMessage(String name, ArrayList<HashMap<String, Integer>> publicInventories) {
        super(name, new UserDTO("","",""));
        this.publicInventories = publicInventories;
    }
}