package de.uol.swp.client;

import javafx.stage.Stage;

/**
 * Factory for use of injecting the SceneManager via giuce
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.di.ClientModule
 * @since 2019-09-19
 */
public interface SceneManagerFactory {

    /**
     * Creates an instance of the SceneManager
     *
     * @author Marco Grawunder
     * @param primaryStage The primary stage used by the javafx application
     * @return The SceneManger used inside the client
     * @author Marco Grawunder
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-19
     */
    SceneManager create(Stage primaryStage);
}
