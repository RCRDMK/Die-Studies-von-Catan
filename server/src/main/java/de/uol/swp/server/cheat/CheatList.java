package de.uol.swp.server.cheat;

import java.util.ArrayList;

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

    public ArrayList<String> get() {
        return this.cheatList;
    }
}
