package de.uol.swp.common.game.card;

/**
 *  Interface für die Siegpunktkarte
 *
 * Die Siegpunktkarte wird zu den Entwicklungskarten gezählt
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */
public interface cardVictoryPoint extends cardDevelopment {
    /**
     * Gibt die Anzahl der Siegpunkte wieder
     *
     * @return int
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-18
     */
    int getVictoryPoint();
}
