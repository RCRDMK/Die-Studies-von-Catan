package de.uol.swp.common.game.message;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Delivers the information of an ended Turn and the next player to make his move to all players in the game. It also
 * contains the name of the player with the currentTurn and the information, if the game is in the opening phase.
 *
 * @author Pieter Vogt
 * @since 2021-03-22
 * <p>
 * Enhanced by Carsten Dekker
 * @since 2021-04-30
 */
public class NextTurnMessage extends AbstractServerMessage {
    final private int turn;
    final private String gameName;
    final private String playerWithCurrentTurn;
    final private boolean isInStartingTurn;

    /**
     * Constructor for the nextTurnMessage.
     *
     * @param gameName
     * @param playerWithCurrentTurn
     * @param turn
     * @param isInStartingTurn
     * @author Pieter Vogt
     * @since 2021-03-22
     */
    public NextTurnMessage(String gameName, String playerWithCurrentTurn, int turn, boolean isInStartingTurn) {
        this.turn = turn;
        this.gameName = gameName;
        this.playerWithCurrentTurn = playerWithCurrentTurn;
        this.isInStartingTurn = isInStartingTurn;
    }

    /**
     * getter for the gameName
     *
     * @return the name of the game
     * @author Pieter Vogt
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * getter for the turn
     *
     * @return the turn
     * @author Pieter Vogt
     */
    public int getTurn() {
        return turn;
    }

    /**
     * getter for player at the current turn
     *
     * @return the player at current turn
     * @author Pieter Vogt
     */
    public String getPlayerWithCurrentTurn() {
        return playerWithCurrentTurn;
    }

    /**
     * getter to check if it is a startingTurn.
     *
     * @return the if it is a startingTurn
     * @author Pieter Vogt
     */
    public boolean isInStartingTurn() {
        return isInStartingTurn;
    }
}
