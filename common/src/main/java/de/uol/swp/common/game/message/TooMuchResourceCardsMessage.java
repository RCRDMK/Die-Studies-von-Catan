package de.uol.swp.common.game.message;

import de.uol.swp.common.user.UserDTO;

public class TooMuchResourceCardsMessage extends AbstractGameMessage {
    private final int cards;

    public TooMuchResourceCardsMessage(String name, UserDTO user, int cards) {
        super(name, user);
        this.cards = cards;
    }
}
