package de.uol.swp.server.game.dice;

import java.util.Random;

/**
 * Class for the dice in the game.
 *
 * @author Kirstin
 * @since 2020-12-29
 */
public class Dice {

    private int diceEyes1 = 0;
    private int diceEyes2 = 0;

    public void rollDice() {
        Random r1 = new Random();
        int dice1 = 1 + r1.nextInt(6);

        Random r2 = new Random();
        int dice2 = 1 + r2.nextInt(6);

        diceEyes1 = dice1;
        diceEyes2 = dice2;
    }

    public int getDiceEyes1() {
        return diceEyes1;
    }

    public int getDiceEyes2() {
        return diceEyes2;
    }

    /**
     * Setter for Eyes
     *
     * @param eyes setEyes for rollCheat
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-17
     */
    public void setEyes(int eyes) {
        if (eyes > 6) {
            diceEyes1 = 6;
        } else {
            diceEyes1 = 1;
        }
        diceEyes2 = eyes - diceEyes1;
    }
}
