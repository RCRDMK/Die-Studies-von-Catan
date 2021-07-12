package de.uol.swp.client.user;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.uol.swp.client.account.UserSettingsService;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test class about the UserSettingsService
 */
@SuppressWarnings("UnstableApiUsage")
public class UserSettingsServiceTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    final EventBus bus = new EventBus();
    Object event;

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
    }

    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    @Test
    public void onRetrieveUserMailTest() {
        UserSettingsService userSettingsService = new UserSettingsService(bus);
        userSettingsService.retrieveUserMail(defaultUser);

        assertTrue(event instanceof RetrieveUserInformationRequest);
    }

    @Test
    public void isValidEmailAddressTestSuccess() {
        String mail = "carsten.stahl@gmx.de";
        UserSettingsService userSettingsService = new UserSettingsService(bus);

        assertTrue(userSettingsService.isValidEmailAddress(mail));
    }

    @Test
    public void isValidEmailAddressTestFailed() {
        String mail = "carsten.stahl!gmx.de";
        UserSettingsService userSettingsService = new UserSettingsService(bus);

        assertFalse(userSettingsService.isValidEmailAddress(mail));
    }

    @Test
    public void isValidEmailAddressTestNull() {
        UserSettingsService userSettingsService = new UserSettingsService(bus);

        assertFalse(userSettingsService.isValidEmailAddress(null));
    }
}
