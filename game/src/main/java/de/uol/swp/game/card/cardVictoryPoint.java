package de.uol.swp.game.card;

/**
 *  Interface für die Siegpunktkarte
 *
 * Die Siegpunktkarte wird zu den Entwicklungskarten gezählt
 *
 * @author Alexander Losse, Iskander Yusupov
 * @since 2020-12-16
 */
public interface cardVictoryPoint extends cardDevelopment {
    int getVictoryPoint();
    boolean getHidden();
}
