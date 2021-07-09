package de.uol.swp.common.game.request;

import java.util.HashMap;

import de.uol.swp.common.user.UserDTO;

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
