package de.uol.swp.server.AI.AIActions;

import de.uol.swp.common.user.User;

import java.util.UUID;

public class PlayDevelopmentCardKnightAction extends PlayDevelopmentCardAction{
    private final UUID field;
    public PlayDevelopmentCardKnightAction(User user, String gameName, String devCard, UUID field) {
        super(user, gameName, devCard);

        this.field = field;
    }

    public UUID getField() {
        return field;
    }
}
