package de.uol.swp.common.game.message;

public class RollDiceResultMessage extends AbstractGameMessage {

    private int diceEyes1;
    private int diceEyes2;
    private int turn;

    public RollDiceResultMessage() {
    }

    public RollDiceResultMessage(int diceEyes1, int diceEyes2, int turn) {
        this.diceEyes1 = diceEyes1;
        this.diceEyes2 = diceEyes2;
        this.turn = turn;
    }

    public int getDiceEyes1() {
        return diceEyes1;
    }

    public int getDiceEyes2() {
        return diceEyes2;
    }

    public int getTurn() {
        return turn;
    }
}
