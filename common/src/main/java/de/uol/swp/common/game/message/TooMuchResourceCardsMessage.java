package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

import java.util.HashMap;

public class TooMuchResourceCardsMessage extends AbstractGameMessage {
    private final int cards;
    private final HashMap<String, Integer> inventory;

    public TooMuchResourceCardsMessage(String name, UserDTO user, int cards, HashMap<String, Integer> inventory) {
        super(name, user);
        this.cards = cards;
        this.inventory = inventory;
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public int getCards() {
        return cards;
    }
}
