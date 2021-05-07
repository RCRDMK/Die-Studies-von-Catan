package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public class EndTurnAction extends AIAction{

    public EndTurnAction(User user, String gameName) {
        super("EndTurn", user, gameName);
    }
}
