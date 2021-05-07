package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

public abstract class AIAction {

    String actionType;
    User user;
    String gameName;

    public AIAction(String actionType, User user, String gameName) {
        this.actionType = actionType;
        this.user = user;
        this.gameName = gameName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
}
