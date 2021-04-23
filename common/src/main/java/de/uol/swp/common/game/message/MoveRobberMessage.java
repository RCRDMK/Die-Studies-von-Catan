package de.uol.swp.common.game.message;

/**
 * Response send to the user, that rolled a seven.
 *
 * @author Marius Birk
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-04-07
 */
public class MoveRobberMessage extends AbstractGameMessage {
    private final String gameName;

    public MoveRobberMessage(String gameName) {
        this.gameName = gameName;
    }

    public String getName() {
        return gameName;
    }
}
