package de.uol.swp.common.game.message;

/**
 * Message send to all users in game, when the dice were thrown
 *
 * @author Carsten Dekker
 * @since 2021-04-30
 */

public class RollDiceResultMessage extends AbstractGameMessage {

    private int diceEyes1;
    private int diceEyes2;
    private int turn;

    /**
     * Default constructor
     * <p>
     *
     * @implNote this constructor is needed for serialization
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public RollDiceResultMessage() {
    }

    /**
     * Constructor, that gets both eyes, the turnnumber and the game name as parameters.
     *
     * @param diceEyes1 eyes from die one
     * @param diceEyes2 eyes from die two
     * @param turn      current turn in the game
     * @param gameName  name of the game
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public RollDiceResultMessage(int diceEyes1, int diceEyes2, int turn, String gameName) {
        this.diceEyes1 = diceEyes1;
        this.diceEyes2 = diceEyes2;
        this.turn = turn;
        super.name = gameName;
    }

    /**
     * getter for the first dice
     *
     * @return the eye number of the first dice
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public int getDiceEyes1() {
        return diceEyes1;
    }

    /**
     * getter for the second dice
     *
     * @return the eye number of the second dice
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public int getDiceEyes2() {
        return diceEyes2;
    }

    /**
     * getter for the turn number
     *
     * @return the number of the actual turn
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public int getTurn() {
        return turn;
    }
}
