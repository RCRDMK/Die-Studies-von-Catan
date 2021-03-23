package de.uol.swp.common.game.cards;

/**
 * Die Entwicklungskarte
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */
public interface cardDevelopment {
    String getName();

    /**
     * Gibt wieder, ob die Karte verwendet wurde.
     *
     * @return boolean
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-16
     */
    Boolean getPlayed();

}
