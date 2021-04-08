package de.uol.swp.common.game.cards;

/**
 * Interface for the progresscard
 * <p>
 * The progresscard is counted among the developmentcards.
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */

public interface cardProgress extends cardDevelopment {
    /**
     * Returns a string in which the specific progresscard is stored
     *
     * @return String
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-18
     */
    String getSpecificProgressCard();


}
