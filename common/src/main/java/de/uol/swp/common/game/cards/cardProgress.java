package de.uol.swp.common.game.cards;

/**
 * Interface für die Fortschrittskarte
 * <p>
 * Die Fortschrittskarte wird zu den Entwicklungskarten gezählt
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */

public interface cardProgress extends cardDevelopment {
    /**
     * Gibt einen String wieder, in welchem die spezifische Fortschrittskarte gespeichert ist
     *
     * @return String
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-18
     */
    String getSpecificProgressCard();


}
