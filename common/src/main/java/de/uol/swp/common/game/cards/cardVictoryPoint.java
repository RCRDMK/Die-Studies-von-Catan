package de.uol.swp.common.game.cards;

/**
 * Interface for the victorypointcard
 * <p>
 * The victorypointcard is counted among the development cards
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */
public interface cardVictoryPoint extends cardDevelopment {
    /**
     * Reflects the number of victory points
     *
     * @return int
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-18
     */
    int getVictoryPoint();
}
