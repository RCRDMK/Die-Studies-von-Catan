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

    final ArrayList<String> cheatList = new ArrayList<String>();

    public CheatList() {
        cheatList.add("endgame");
        cheatList.add("givememoney");
        cheatList.add("givemecard");
        cheatList.add("letmebuild");
        cheatList.add("roll");
        cheatList.add("moveburglar");
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
