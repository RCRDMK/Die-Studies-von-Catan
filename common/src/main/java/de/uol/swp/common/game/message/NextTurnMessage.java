package de.uol.swp.common.game.message;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * Delivers the information of an ended Turn and the next player to make his move to all players in the game.
 *
 * @author Pieter Vogt
 * @since 2021
 */
public class NextTurnMessage extends AbstractServerMessage {
    private int turn;
    private String gameName;
    private String playerWithCurrentTurn;
    private boolean isInStartingTurn;

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
