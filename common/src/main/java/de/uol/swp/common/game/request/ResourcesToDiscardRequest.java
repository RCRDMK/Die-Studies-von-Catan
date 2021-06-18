package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserDTO;

import java.util.HashMap;

public class ResourcesToDiscardRequest extends AbstractGameRequest {
    private final HashMap<String, Integer> inventory;
    public ResourcesToDiscardRequest(String name, UserDTO user, HashMap<String, Integer> inventory) {
        super(name, user);
        this.inventory = inventory;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }
}
