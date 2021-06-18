package de.uol.swp.client.events;


import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountEventTests {


    @Test
    public void onUserSettingsErrorEventTest(){
        String eventMessage = "Test";
        UserSettingsErrorEvent event = new UserSettingsErrorEvent(eventMessage);

        assertEquals(event.getMessage(), eventMessage);
    }

}
