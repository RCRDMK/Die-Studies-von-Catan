package de.uol.swp.common.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the DevelopmentCardDeck
 *
 * @author Anton
 * @since 2021-01-20
 */
public class DevelopmentCardDeckTest {

    /**
     * This test make sure that the card draw works.
     * <p>
     * This test draws the development cards from the deck one by one
     * and examines their contents. At the end he checks whether the deck is empty.
     */
    @Test
    void onDrawnCardTest() {
        DevelopmentCardDeck developmentCardDeck = new DevelopmentCardDeck();
        for (int i = 0; i < 25; i++) {
            assertEquals(developmentCardDeck.getNumberOfDevCards(), 25 - i);
            String result = developmentCardDeck.drawnCard();
            assertTrue(result.equals("Victory Point Card") ||
                    result.equals("Knight") ||
                    result.equals("Monopoly") ||
                    result.equals("Road Building") ||
                    result.equals("Year of Plenty"));
        }
        assertNull(developmentCardDeck.drawnCard());
    }

}