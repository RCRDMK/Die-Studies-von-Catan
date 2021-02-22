package de.uol.swp.server.game.inventory;

/**
 * Class for the units in the game
 *
 * @author Anton Nikiforov
 * @since 2020-03-04
 */
public class Unit {

    private int number;

    public Unit (int number) {
        this.number = number;
    }

    //Getter
    public int getNumber() {
        return number;
    }

    //Decrementer until 0
    public void decNumber() {
        if (this.number > 0) this.number--;
    }

    //Incrementer until 5 for the Settlement
    public void incNumber() {
        if (this.number < 5) this.number++;
    }
}
