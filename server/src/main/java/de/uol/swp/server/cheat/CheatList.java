package de.uol.swp.server.cheat;

import java.util.ArrayList;

/**
 * CheatList class
 * <p>
 * In Constructor add hardcoded cheats
 *
 * @author René Meyer, Sergej Tulnev
 * @see ArrayList
 * @since 2021-04-17
 */
public class CheatList {

    // Cheatcodes with Examples:

    // Cheatcode "givemecard"
    // Usage: givemecard [string] [int]
    //      e.g. givemecard knight 1
    // possible strings:
    //      lumber
    //      brick
    //      grain
    //      wool
    //      ore
    //      monopoly
    //      knight
    //      roadbuilding
    //      yearofplenty
    //      victory
    // Gives the user [int] amount of the provided cards.


    // Cheatcode "givemeall"
    // Usage: givemeall [int]
    //      e.g. givemeall 15
    // Gives user [int] ressources of each card and 1 of each development cards


    // Cheatcode "endgame 1"
    // Usage: endgame 1
    // Gives user 10 victory points and ends the game


    // Cheatcode "roll"
    // Usage: roll [int]
    //      e.g. roll 2
    // Rolls the dice with the provided [int]

    final ArrayList<String> cheatList = new ArrayList<String>();

    public CheatList() {
        cheatList.add("endgame");
        cheatList.add("givemeall");
        cheatList.add("givemecard");
        cheatList.add("roll");
    }

    /**
     * Getter for the cheatList ArrayList
     * <p>
     *
     * @return ArrayList<String>
     * @author René Meyer, Segej Tulnev
     * @since 2021-04-17
     */
    public ArrayList<String> get() {
        return this.cheatList;
    }
}
