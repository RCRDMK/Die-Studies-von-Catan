package de.uol.swp.common.game.message;

import de.uol.swp.common.message.AbstractServerMessage;

public class NextTurnMessage extends AbstractServerMessage {
    private int turn;
    private String gameName;
    private String playerWithCurrentTurn;

    public NextTurnMessage(String gameName, String playerWithCurrentTurn, int turn) {
        this.turn = turn;
        this.gameName = gameName;
        this.playerWithCurrentTurn = playerWithCurrentTurn;
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

}
