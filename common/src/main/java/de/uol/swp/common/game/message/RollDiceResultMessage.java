package de.uol.swp.common.game.message;

/**
 * Message send to al users in game, when the dice were thrown
 *
 * @author Carsten Dekker
 * @since 2021-04-30
 */

public class RollDiceResultMessage extends AbstractGameMessage {

    private int diceEyes1;
    private int diceEyes2;
    private int turn;

    public RollDiceResultMessage() {
    }

    /**
     * Default Constructor
     *
     * @param diceEyes1 eyes from die one
     * @param diceEyes2 eyes from die two
     * @param turn      current turn in the game
     * @param gameName  name of the game
     */
    public RollDiceResultMessage(int diceEyes1, int diceEyes2, int turn, String gameName) {
        this.diceEyes1 = diceEyes1;
        this.diceEyes2 = diceEyes2;
        this.turn = turn;
        super.name = gameName;
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
