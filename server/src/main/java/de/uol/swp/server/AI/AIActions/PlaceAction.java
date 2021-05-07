package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

import java.util.UUID;

public class PlaceAction extends AIAction{

    private final UUID field;
    public PlaceAction(String actionType, User user, String gameName, UUID field) {
        super(actionType, user, gameName);
        this.field = field;
    }

    public UUID getField() {
        return field;
    }
}
