package de.uol.swp.common.card;

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
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-16
     * @return boolean
     */
    Boolean getPlayed();

}
