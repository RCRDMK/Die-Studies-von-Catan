package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Message send to all users, when an inventory is updated
 *
 * @author Iskander Yusupov, Anton Nikiforov
 * @since 2021-04-08
 */
public class PublicInventoryChangeMessage extends AbstractGameMessage {
    final private ArrayList<HashMap<String, Integer>> publicInventories;
    final private ArrayList<User> userList;


    /**
     * Default constructor
     *
     * enhanced by Carsten Dekker ,Marc Johannes Hermes, Marius Birk, Iskander Yusupov
     * @since 2021-05-07
     * @author Iskander Yusupov, Anton Nikiforov
     * @since 2021-04-08
     */

    public PublicInventoryChangeMessage(String gameName, ArrayList <User> userList, ArrayList<HashMap<String, Integer>> publicInventories) {
        super(gameName, new UserDTO("", "", ""));
        this.publicInventories = publicInventories;
        this.userList = userList;
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

    /**
     * Getter for the arrayList of players in game
     *
     * @return the ArrayList representing the users in game
     * @author Iskander Yusupov
     * @since 2021-05-28
     */
    public ArrayList<User> getPlayers() {return userList;}
}