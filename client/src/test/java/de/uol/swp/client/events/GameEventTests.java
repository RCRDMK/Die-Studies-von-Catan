package de.uol.swp.client.events;

import org.junit.jupiter.api.Test;

import de.uol.swp.client.game.event.SummaryConfirmedEvent;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameEventTests {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    @Test
    public void onSummaryConfirmedEventTest() {
        String game = "Game";
        SummaryConfirmedEvent event = new SummaryConfirmedEvent(game, defaultUser);

        assertEquals(event.getGameName(), "Game");
        assertEquals(event.getUser(), defaultUser);
    }
}
