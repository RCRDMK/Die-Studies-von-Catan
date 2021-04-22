package de.uol.swp.common.game.inventory;

/**
 * Class for the cards in the game
 *
 * @author Anton Nikiforov
 * @since 2020-03-04
 */
public class Card {

    private int number = 0;

    //Getter
    public int getNumber() {
        return number;
    }

    //Setter
    public void setNumber(int number) {
        this.number = Math.min(Math.max(number, 0), 19); // (0 <= number <= 19)
    }

    //Incrementer
    public void incNumber() {
        if (this.number < 19) this.number++; // (number <= 19)
    }

    //Incrementer with number
    public void incNumber(int number) {
        this.number = Math.min(this.number + number, 19); // (number <= 19)
    }

    //Decrementer
    public void decNumber() {
        if (this.number > 0) this.number--; // (number >= 0)
    }

    //Decrementer with number
    public void decNumber(int number) {
        this.number = Math.max(this.number - number, 0);  // (number >= 0)
    }
}