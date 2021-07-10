package de.uol.swp.common.game;

import java.io.Serializable;
import java.util.Collections;
import java.util.Stack;

/**
 * Creates and manages the development card deck
 * <p>
 * Creates the deck, fills it with cards, and shuffles them.
 *
 * @author Anton Nikiforov
 * @since 2021-01-17
 */
public class DevelopmentCardDeck implements Serializable {

    private final Stack<String> deck = new Stack<>();

    /**
     * Fills deck with cards and then shuffles them.
     *
     * @author Anton Nikiforov
     * @since 2021-01-17
     */
    public DevelopmentCardDeck() {

        for (int i = 0; i < 5; i++) deck.push("Victory Point Card");
        for (int i = 0; i < 14; i++) deck.push("Knight");

        for (int i = 0; i < 2; i++) {
            deck.push("Monopoly");
            deck.push("Road Building");
            deck.push("Year of Plenty");
        }

        Collections.shuffle(deck);
    }

    /**
     * Draw the development card from the deck
     *
     * @return a drawn the card if there is one left, otherwise null
     * @author Anton Nikiforov
     * @since 2021-01-17
     */
    public String drawnCard() {
        if (deck.size() > 0) return deck.pop();
        else return null;
    }

    /**
     * Getter for the number of dev cards in the deck
     *
     * @return the number of dev Cards in the deck
     * @author Marc Hermes
     * @since 2021-06-19
     */
    public int getNumberOfDevCards() {
        return deck.size();
    }
}