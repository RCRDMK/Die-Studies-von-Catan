package de.uol.swp.common.game.inventory;

import java.util.Collections;
import java.util.Stack;

/**
 * Creates and manages the development card deck
 * <p>
 * Creates the deck, fills it with cards, and shuffles them.
 *
 * @author Anton
 * @since 2021-01-17
 */
public class DevelopmentCardDeck {

    private Stack<String> deck = new Stack<>();

    public DevelopmentCardDeck(){

        for (int i=0; i<5; i++) deck.push("Victory Point");
        for (int i=0; i<14; i++) deck.push("Knight");

        for (int i=0; i<2; i++) {
            deck.push("Monopoly");
            deck.push("Road Building");
            deck.push("Year of Plenty");
        }

        Collections.shuffle(deck);
    }

    /**
     * Draw the development card from the deck
     *
     * @return
     */
    public String drawnCard() {
        if (deck.size()>0) return deck.pop();
        else return null;
    }
}