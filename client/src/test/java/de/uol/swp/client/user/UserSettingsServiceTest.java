package de.uol.swp.client.user;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.account.UserSettingsService;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a testclass about the UserSettingsService
 */
@SuppressWarnings("UnstableApiUsage")
public class UserSettingsServiceTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
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

    private void loginUser() throws InterruptedException, InvalidKeySpecException, NoSuchAlgorithmException {
        UserService userService = new UserService(bus);
        userService.login(defaultUser.getUsername(), defaultUser.getPassword());
        lock.await(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void onRetrieveUserMailTest() throws InterruptedException {
        UserSettingsService userSettingsService = new UserSettingsService(bus);
        userSettingsService.retrieveUserMail(defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveUserInformationRequest);
    }

    @Test
    public void isValidEmailAdressTestSuccess() {
        String mail = "carsten.stahl@gmx.de";
        UserSettingsService userSettingsService = new UserSettingsService(bus);

        assertTrue(userSettingsService.isValidEmailAddress(mail));
    }

    @Test
    public void isValidEmailAdressTestFailed() {
        String mail = "carsten.stahl!gmx.de";
        UserSettingsService userSettingsService = new UserSettingsService(bus);

        assertFalse(userSettingsService.isValidEmailAddress(mail));
    }
}
