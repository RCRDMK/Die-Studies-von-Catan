package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.common.user.UserDTO;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


//Test currently unavailable -> TestFx library needed
class SceneManagerTest {
/*
    final EventBus bus = new EventBus();
    UserDTO userDTO = new UserDTO("Peter", "lustig", "peter.lustig@uol.de");
    UserDTO userDTO1 = new UserDTO("Carsten", "stahl", "carsten.stahl@uol.de");
    Object event;
    final CountDownLatch lock = new CountDownLatch(1);

    private Stage primaryStage = new Stage();
    Injector injector = Guice.createInjector(new ClientModule());
    SceneManagerFactory sceneManagerFactory = injector.getInstance(SceneManagerFactory.class);
    SceneManager sceneManager = sceneManagerFactory.create(primaryStage);



    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();

    }

    @BeforeEach
    void setUp() {
        event = null;
        bus.register(this);
    }

    @AfterEach
    void tearDown() {
        bus.unregister(this);
    }




    @Test
    void showMainScreen() throws InterruptedException{
        sceneManager.showMainTab(userDTO);
        lock.await(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    void showMainTab() {
    }

    @Test
    void showLobbyScreen() {
    }

    @Test
    void newLobbyTab() {
    }

    @Test
    void removeLobbyTab() {
    }

    @Test
    void initLobbyView(){

    }

    @Test
    void initMainView() {

    }

    @Test
    void initViews() {

    }

 */
}