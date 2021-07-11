package de.uol.swp.client.events;


import org.junit.jupiter.api.Test;

import de.uol.swp.client.account.event.UserSettingsErrorEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountEventTests {


    @Test
    public void onUserSettingsErrorEventTest() {
        String eventMessage = "Test";
        UserSettingsErrorEvent event = new UserSettingsErrorEvent(eventMessage);

        assertEquals(event.getMessage(), eventMessage);
    }

}
