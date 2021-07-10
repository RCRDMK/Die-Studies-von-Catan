package de.uol.swp.common.game.request;

import java.util.HashMap;

/**
 * This class is responsible for managing the resources a player has to discard when it's requested.
 *
 * @author Marius Birk
 */
import de.uol.swp.common.user.UserDTO;

public class ResourcesToDiscardRequest extends AbstractGameRequest {
    private final HashMap<String, Integer> inventory;

    public ResourcesToDiscardRequest(String name, UserDTO user, HashMap<String, Integer> inventory) {
        super(name, user);
        this.inventory = inventory;
    }

    /**
     * getter method to get the inventory of a player
     *
     * @return the inventory of a player
     * @author Marius Birk
     */
    public HashMap<String, Integer> getInventory() {
        return inventory;
    }
}
