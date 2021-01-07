package de.uol.swp.server.game.dice;

import java.util.Random;

/**
 * Class for the dice in the game.
 *
 * @author Kirstin
 * @since 2020-12-29
 */

public class Dice {

    int eyes = 0;

    public void rollDice(){
        Random r1 = new Random();
        int dice1 = 1 + r1.nextInt(6);

        Random r2 = new Random();
        int dice2 = 1 + r2.nextInt(6);

        eyes = dice1 + dice2;
    }

    public int getEyes() {
        return eyes;
    }

}
