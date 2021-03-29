package de.uol.swp.common.game.cards;

/**
 * Interface for the progresscard
 *
 * The progresscard is counted among the developmentcards.
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */

public interface cardProgress extends cardDevelopment{
    /**
     * Returns a string in which the specific progresscard is stored
     *
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-18
     * @return String
     */
    String getSpecificProgressCard();


}
