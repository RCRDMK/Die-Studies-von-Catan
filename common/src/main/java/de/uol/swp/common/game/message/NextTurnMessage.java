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

    public NextTurnMessage(String gameName, String playerWithCurrentTurn, int turn, boolean isInStartingTurn) {
        this.turn = turn;
        this.gameName = gameName;
        this.playerWithCurrentTurn = playerWithCurrentTurn;
        this.isInStartingTurn = isInStartingTurn;
    }

    public String getGameName() {
        return gameName;
    }

    public int getTurn() {
        return turn;
    }

    public String getPlayerWithCurrentTurn() {
        return playerWithCurrentTurn;
    }

    public boolean isInStartingTurn() {
        return isInStartingTurn;
    }
}
