package de.uol.swp.common.game.cards;

/**
 * The developmentcard
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */
public interface cardDevelopment {
    String getName();

    /**
     * Returns whether the card has been used.
     *
     * @return boolean
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-16
     */
    Boolean getPlayed();

}
