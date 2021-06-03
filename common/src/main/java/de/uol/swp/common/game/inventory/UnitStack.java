package de.uol.swp.common.game.inventory;

import java.io.Serializable;

/**
 * Class for the units in the game
 *
 * @author Anton Nikiforov
 * @since 2020-03-04
 */
public class UnitStack implements Serializable {

    private int number;

    public UnitStack(int number) {
        this.number = number;
    }

    //Getter
    public int getNumber() {
        return number;
    }

    //Decrementer until 0
    public void decNumber() {
        this.number--;
    }

    //Incrementer until 5 for the Settlement
    public void incNumber() {
        this.number++;
    }
}
