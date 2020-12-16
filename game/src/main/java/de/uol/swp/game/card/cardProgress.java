package de.uol.swp.game.card;

/**
 *  Interface für die Fortschrittkarte
 *
 * Die Fortschrittkarte wird zu den Entwicklungskarten gezählt
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */

public interface cardProgress extends cardDevelopment{
    /**
     * Gibt einen String wieder, in welchem die Kartenaktion gespeichert ist
     *
     * @author Alexander Losse, Iskander Yusupov
     * @since 2020-12-16
     * @return String
     */
    String getCardAction();


}
