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

    //Setter (nicht kleiner als 0 und nicht größer als 19)
    public void setNumber(int number) {
        this.number = Math.min(Math.max(number, 0), 19);
    }

    //Incrementer (nicht mehr als 19)
    public void incNumber() {
        if (this.number < 19) this.number++;
    }

    //Incrementer with number (nicht mehr als 19)
    public void incNumber(int number) {
        this.number = Math.min(this.number + number, 19);
    }

    //Decrementer (nicht weniger als 0)
    public void decNumber() {
        if (this.number > 0) this.number--;
    }

    //Decrementer with number (nicht weniger als 0)
    public void decNumber(int number) {
        this.number = Math.max(this.number - number, 0);
    }
}