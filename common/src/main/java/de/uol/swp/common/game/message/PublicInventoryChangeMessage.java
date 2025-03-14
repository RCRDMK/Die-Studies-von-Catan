package de.uol.swp.common.game.message;

import java.util.ArrayList;
import java.util.HashMap;

import de.uol.swp.common.user.UserDTO;

/**
 * Message send to all users, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PublicInventoryChangeMessage extends AbstractGameMessage {
    final private ArrayList<HashMap<String, Integer>> publicInventories;


    /**
     * Default constructor
     * <p>
     * enhanced by Carsten Dekker ,Marc Johannes Hermes, Marius Birk, Iskander Yusupov
     *
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-05-07
     * @since 2021-04-08
     */

    public PublicInventoryChangeMessage(String gameName, ArrayList<HashMap<String, Integer>> publicInventories) {
        super(gameName, new UserDTO("", "", ""));
        this.publicInventories = publicInventories;
    }

    /**
     * Getter for the arrayList of publicInventories
     *
     * @return the ArrayList representing the public View of the user
     * @author Iskander Yusupov
     * @since 2021-05-16
     */
    public ArrayList<HashMap<String, Integer>> getPublicInventories() {
        return publicInventories;
    }

}