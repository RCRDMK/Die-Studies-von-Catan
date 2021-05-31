package de.uol.swp.common.game.inventory;

import java.io.Serializable;

/**
 * Class for the cards in the game
 *
 * @author Anton Nikiforov
 * @since 2020-03-04
 */
public class CardStack implements Serializable {

    private int number = 0;

    //Getter
    public int getNumber() {
        return number;
    }

    //Setter
    public void setNumber(int number) {
        this.number = number;
    }

    //Incrementer
    public void incNumber() {
        this.number++;
    }

    //Incrementer with number
    public void incNumber(int number) {
        this.number += number;
    }

    //Decrementer
    public void decNumber() {
        this.number--;
    }

    //Decrementer with number
    public void decNumber(int number) {
        this.number -= number;
    }
}