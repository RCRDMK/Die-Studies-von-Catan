package de.uol.swp.client.events;

import de.uol.swp.client.account.event.ChangeToCertainSizeEvent;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountEventTests {

    @Test
    public void onChangeToCertainSizeEventTest(){
        ChangeToCertainSizeEvent event = new ChangeToCertainSizeEvent(12.2, 14.4);

        assertEquals(event.getWidth(), 12.2);
        assertEquals(event.getHeight(), 14.4);
    }

    @Test
    public void onUserSettingsErrorEventTest(){
        String eventMessage = "Test";
        UserSettingsErrorEvent event = new UserSettingsErrorEvent(eventMessage);

        assertEquals(event.getMessage(), eventMessage);
    }

}
