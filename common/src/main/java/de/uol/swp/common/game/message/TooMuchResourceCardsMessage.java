package de.uol.swp.common.game.message;

import java.util.HashMap;

import de.uol.swp.common.user.UserDTO;

/**
 * Message sent to a user when the robber was moved and he has too many resources, thus having to discard some amount
 *
 * @author Marius Birk
 * @since 2021-05-30
 */
public class TooMuchResourceCardsMessage extends AbstractGameMessage {
    private final int cards;
    private final HashMap<String, Integer> inventory;

    /**
     * Constructor used for serialization
     *
     * @author Marc Hermes
     * @since 2021-05-30
     */
    public TooMuchResourceCardsMessage() {
        this.cards = 0;
        this.inventory = null;
    }

    /**
     * Constructor
     *
     * @param name      the name of the game
     * @param user      the name of the user who has too many resources
     * @param cards     the amount of cards that are too much
     * @param inventory the inventory of the user
     * @author Marius Birk
     * @since 2021-05-30
     */
    public TooMuchResourceCardsMessage(String name, UserDTO user, int cards, HashMap<String, Integer> inventory) {
        super(name, user);
        this.cards = cards;
        this.inventory = inventory;
    }

    /**
     * Getter for the inventory
     *
     * @return the inventory of the user who receives this message
     * @author Marius Birk
     * @since 2021-05-30
     */
    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    /**
     * Getter for the amount of cards needed to be discarded
     *
     * @return the amount of cards to discard
     * @author Marius Birk
     * @since 2021-05-30
     */
    public int getCards() {
        return cards;
    }
}
