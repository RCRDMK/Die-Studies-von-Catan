package de.uol.swp.common.game.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Response send to the user, that rolled a seven.
 *
 * @author Marius Birk
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-04-07
 */
public class MoveRobberResponse extends AbstractResponseMessage {
    private final String gameName;

    public MoveRobberResponse(String gameName) {
        this.gameName = gameName;
    }

    public String getName() {
        return gameName;
    }
}
