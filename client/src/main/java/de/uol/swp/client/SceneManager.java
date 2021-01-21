package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

/**
 * Class that manages which window/scene is currently shown
 *<p>
 * @author Marco Grawunder
 * @since 2019-09-03
 */
public class SceneManager {

    static final Logger LOG = LogManager.getLogger(SceneManager.class);
    static final String styleSheet = "css/swp.css";

    final private Stage primaryStage;
    private Scene loginScene;
    private String lastTitle;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private Scene lobbyScene;
    private Scene gameScene;
    private Tab mainMenuTab;
    private VBox vBox;
    private Scene tabScene;
    private Scene nextLobbyScene;
    private final Injector injector;
    private TabPane tabPane;
    private TabHelper tabHelper;

    @Inject
    public SceneManager(EventBus eventBus, Injector injected, @Assisted Stage primaryStage) {
        eventBus.register(this);
        this.primaryStage = primaryStage;
        this.injector = injected;
        initViews();
    }

    /**
     * Subroutine to initialize all views
     * <p>
     * This is a subroutine of the constructor to initialize all views, as well as creating the TabPane
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void initViews() {
        initLoginView();
        initMainView();
        initRegistrationView();
        nextLobbyScene = initLobbyView();
        TabPane tabPane = new TabPane();
        this.tabPane = tabPane;
        this.tabHelper = new TabHelper(this.tabPane);
        vBox = new VBox(tabHelper.getTabPane());
        tabScene = new Scene(vBox);

    }

    /**
     * Subroutine creating parent panes from FXML files
     * <p>
     * This Method tries to create a parent pane from the FXML file specified by
     * the URL String given to it. If the LOG-Level is set to Debug or higher loading
     * is written to the LOG.
     * If it fails to load the view a RuntimeException is thrown.
     *
     * @param fxmlFile FXML file to load the view from
     * @return view loaded from FXML or null
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private Parent initPresenter(String fxmlFile) {
        Parent rootPane;
        FXMLLoader loader = injector.getInstance(FXMLLoader.class);
        try {
            URL url = getClass().getResource(fxmlFile);
            LOG.debug("Loading " + url);
            loader.setLocation(url);
            rootPane = loader.load();
        } catch (Exception e) {
            throw new RuntimeException("Could not load View!" + e.getMessage(), e);
        }
        return rootPane;
    }

    /**
     * Initializes the main menu view
     * <p>
     * If the mainScene is null it gets set to a new scene containing the
     * a pane showing the main menu view as specified by the MainMenuView
     * FXML file. ALso a mainMenuTab is created which cannot be closed.
     *
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     *
     * @see de.uol.swp.client.main.MainMenuPresenter
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void initMainView() {
        if (mainScene == null) {
            Parent rootPane = initPresenter(MainMenuPresenter.fxml);
            mainScene = new Scene(rootPane, 800, 600);
            mainScene.getStylesheets().add(styleSheet);
            mainMenuTab = new Tab("Main Menu");
            mainMenuTab.setClosable(false);
        }
    }

    /**
     * Initializes the login view
     * <p>
     * If the loginScene is null it gets set to a new scene containing the
     * a pane showing the login view as specified by the LoginView FXML file.
     *
     * @see de.uol.swp.client.auth.LoginPresenter
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void initLoginView() {
        if (loginScene == null) {
            Parent rootPane = initPresenter(LoginPresenter.fxml);
            loginScene = new Scene(rootPane, 400, 200);
            loginScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Initializes the registration view
     * <p>
     * If the registrationScene is null it gets set to a new scene containing the
     * a pane showing the registration view as specified by the RegistrationView
     * FXML file.
     *
     * @see de.uol.swp.client.register.RegistrationPresenter
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void initRegistrationView() {
        if (registrationScene == null) {
            Parent rootPane = initPresenter(RegistrationPresenter.fxml);
            registrationScene = new Scene(rootPane, 400, 260);
            registrationScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Initializes the lobby view
     * <p>
     *  If a new lobbyScene is needed this method will return a new lobbyScene containing the
     *  pane showing the lobby view as specified by the LobbyView FXML file
     *
     *  enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     *
     * @see de.uol.swp.client.lobby.LobbyPresenter
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-11-19
     */
    private Scene initLobbyView() {
            Parent rootPane = initPresenter(LobbyPresenter.fxml);
            lobbyScene = new Scene(rootPane, 800, 600);
            lobbyScene.getStylesheets().add(styleSheet);
        return lobbyScene;
    }

    /**
     * Initializes the game view
     * <p>
     *  If the gameScene is null it gets set to a new scene containing the
     *  a pane showing the game view as specified by the GameView
     *  FXML file
     *
     * @see de.uol.swp.client.game.GamePresenter
     * @author Kirstin Beyer
     * @since 2021-01-14
     */
    private void initGameView() {
        if (gameScene == null) {
            Parent rootPane = initPresenter(GamePresenter.fxml);
            gameScene = new Scene(rootPane, 800, 600);
            gameScene.getStylesheets().add(styleSheet);
        }
    }


    /**
     * Handles ShowRegistrationViewEvent detected on the EventBus
     * <p>
     * If a ShowRegistrationViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the registration
     * screen.
     *
     * @param event The ShowRegistrationViewEvent detected on the EventBus
     * @see de.uol.swp.client.register.event.ShowRegistrationViewEvent
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    @Subscribe
    public void onShowRegistrationViewEvent(ShowRegistrationViewEvent event) {
        showRegistrationScreen();
    }

    /**
     * Handles ShowLoginViewEvent detected on the EventBus
     * <p>
     * If a ShowLoginViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the login screen.
     *
     * @param event The ShowLoginViewEvent detected on the EventBus
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    @Subscribe
    public void onShowLoginViewEvent(ShowLoginViewEvent event) {
        showLoginScreen();
    }


    /**
     * Handles RegistrationCanceledEvent detected on the EventBus
     * <p>
     * If a RegistrationCanceledEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the screen shown before registration.
     *
     * @param event The RegistrationCanceledEvent detected on the EventBus
     * @see de.uol.swp.client.register.event.RegistrationCanceledEvent
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    @Subscribe
    public void onRegistrationCanceledEvent(RegistrationCanceledEvent event) {
        showScene(lastScene, lastTitle);
    }

    /**
     * Handles RegistrationErrorEvent detected on the EventBus
     * <p>
     * If a RegistrationErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @param event The RegistrationErrorEvent detected on the EventBus
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    @Subscribe
    public void onRegistrationErrorEvent(RegistrationErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Shows an error message inside an error alert
     * <p>
     * @param message The type of error to be shown
     * @param e       The error message
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showError(String message, String e) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, message + e);
            a.showAndWait();
        });
    }

    /**
     * Shows a server error message inside an error alert
     * <p>
     * @param e The error message
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showServerError(String e) {
        showError("Server returned an error:\n", e);
    }

    /**
     * Shows an error message inside an error alert
     * <p>
     * @param e The error message
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showError(String e) {
        showError("Error:\n", e);
    }

    /**
     * Switches the current scene and title to the given ones
     * <p>
     * The current scene and title are saved in the lastScene and lastTitle variables,
     * before the new scene and title are set and shown.
     *
     * @param scene New scene to show
     * @param title New window title
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void showScene(final Scene scene, final String title) {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = scene;
        Platform.runLater(() -> {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    /**
     * Shows the login error alert
     * <p>
     * Opens an ErrorAlert popup saying "Error logging in to server"
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showLoginErrorScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error logging in to server");
            alert.showAndWait();
            showLoginScreen();
        });
    }

    /**
     * Shows the main menu
     * <p>
     * Invoked the Method showMainTab instead of switching the Scene to the MainScene
     *
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     *
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showMainScreen(User currentUser) {
        showMainTab(currentUser);

    }

    /**
     * Shows the main menu tab
     * <p>
     * This method will set the content of the mainMenuTab to that of the view of the mainScene
     * Also the mainMenuTab is added to the TabPane and the tabScene is shown on the primary stage.
     *
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void showMainTab(User currentUser) {
        mainMenuTab.setContent(mainScene.getRoot());
        Platform.runLater(() -> {
            tabHelper.getTabPane().getTabs().add(mainMenuTab);
            primaryStage.setTitle("Catan");
            primaryStage.setScene(tabScene);
            primaryStage.show();});
    }

    /**
     * Shows the login screen
     * <p>
     * Switches the current Scene to the loginScene and sets the title of
     * the window to "Login"
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showLoginScreen() {
        showScene(loginScene, "Login");

    }

    /**
     * Shows the registration screen
     * <p>
     * Switches the current Scene to the registrationScene and sets the title of
     * the window to "Registration"
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showRegistrationScreen() {
        showScene(registrationScene, "Registration");
    }

    /**
     * Shows the lobby screen
     * <p>
     * This method invokes the newLobbyTab() method resulting in the creation of a new lobby tab
     *
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     *
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-11-19
     */
    public void showLobbyScreen(User currentUser, String lobbyname) {
        newLobbyTab(currentUser, lobbyname);
    }

    /**
     * Creates a new lobby tab
     *
     * When this method is invoked a new lobby tab with a specific name is created.
     * The content of the new lobby tab is set to the root of the currently empty nextLobbyScene
     * The lobby tab is then added to the TabPane.
     * Afterwards a new empty nextLobbyScene is created, for the next usage of this method.
     *
     * @param lobbyname the name of the lobby for which a tab is created
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void newLobbyTab(User currentUser, String lobbyname) {
        Tab lobbyTab = new Tab("Lobby " + lobbyname);
        lobbyTab.setContent(nextLobbyScene.getRoot());
        lobbyTab.setClosable(false);
        Platform.runLater(() -> {
            tabHelper.addTab(lobbyTab);
        });
        nextLobbyScene = initLobbyView();
    }

    /**
     * Removes an old lobby tab
     *
     * When this method is invoked a lobby tab with a specific name is removed from
     * the TabPane.
     *
     * @param lobbyname the name of the lobby that corresponds to the tab that is to be deleted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void removeLobbyTab(User currentUser, String lobbyname) {
        Platform.runLater(() -> {
            tabHelper.removeTab("Lobby " + lobbyname);
        });
    }
    /**
     * Shows the game screen
     * <p>
     * Switches the current Scene to the gameScene and sets the title of
     * the window to "Game"
     * @author Kirstin Beyer
     * @since 2021-01-14
     */
    public void showGameScreen(User currentUser, String lobbyname) {
        showScene(gameScene, "Game " + lobbyname );
    }

}

