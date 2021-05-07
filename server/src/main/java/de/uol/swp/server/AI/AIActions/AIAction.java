package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public abstract class AIAction {

    private final String actionType;
    private final User user;
    private final  String gameName;

    public AIAction(String actionType, User user, String gameName) {
        this.actionType = actionType;
        this.user = user;
        this.gameName = gameName;
    }

    public String getActionType() {
        return actionType;
    }

    public User getUser() {
        return user;
    }

    public String getGameName() {
        return gameName;
    }
}
