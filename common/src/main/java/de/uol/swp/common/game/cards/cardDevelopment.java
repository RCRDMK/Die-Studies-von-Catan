package de.uol.swp.common.game.cards;

/**
 * the developmentcard
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */
public interface cardDevelopment {
    String getName();

    /**
     * Returns if the card was used.
     *
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-16
     * @return boolean
     */
    Boolean getPlayed();

}
