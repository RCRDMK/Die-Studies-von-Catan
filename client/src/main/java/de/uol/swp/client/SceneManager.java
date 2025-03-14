package de.uol.swp.client;

import java.net.URISyntaxException;
import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.client.account.UserSettingsPresenter;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.account.event.ShowUserSettingsViewEvent;
import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.game.GameRulesPresenter;
import de.uol.swp.client.game.SummaryPresenter;
import de.uol.swp.client.game.TradePresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.main.event.MuteMusicEvent;
import de.uol.swp.client.main.event.UnmuteMusicEvent;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.register.event.ShowGameRulesEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.common.game.message.TradeEndedMessage;

/**
 * Class that manages which window/scene is currently shown
 * <p>
 *
 * @author Marco Grawunder
 * @since 2019-09-03
 */
@SuppressWarnings("UnstableApiUsage")
public class SceneManager {

    static final Logger LOG = LogManager.getLogger(SceneManager.class);
    static final String styleSheet = "css/swp.css";

    final private Stage primaryStage;
    private final Injector injector;
    private final TabPane tabPane = new TabPane();
    private Scene loginScene;
    private String lastTitle;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private Scene tabScene;
    private Scene nextLobbyScene;
    private Scene nextGameScene;
    private Scene nextTradeScene;
    private Scene userSettingsScene;
    private Scene userGameRulesScene;
    private MediaPlayer player;
    private Scene summaryScene;
    private Scene nextSummaryScene;
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
     * <p>
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     * <p>
     * enhanced by Ricardo Mook, 2021-05-13
     * added the ability to have background music playing
     *
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void initViews() {
        this.tabHelper = new TabHelper(this.tabPane);
        VBox vBox = new VBox(tabHelper.getTabPane());
        tabScene = new Scene(vBox);
        initLoginView();
        initMainView();
        initRegistrationView();
        initUserSettingsView();
        initGameRulesView();
        nextSummaryScene = initSummaryView();
        nextLobbyScene = initLobbyView();
        nextGameScene = initGameView();
        nextTradeScene = initTradeView();
        primaryStage.setResizable(false);

        //Royalty free music from Pixabay was used. For more information see https://pixabay.com/service/license/.
        try {
            Media backgroundMusic = new Media(
                    getClass().getResource("/backgroundMusic/the-last-october-day-3915.mp3").toURI().toString());
            player = new MediaPlayer(backgroundMusic);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        player.setCycleCount(MediaPlayer.INDEFINITE);//loops the musicFile indefinitely
        player.setVolume(0.4);
        player.play();
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
     * This mainMenuTab is then added to the tabPane.
     * <p>
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.main.MainMenuPresenter
     * @since 2019-09-03
     */
    private void initMainView() {
        if (mainScene == null) {
            Parent rootPane = initPresenter(MainMenuPresenter.fxml);
            mainScene = new Scene(rootPane, 800, 600);
            mainScene.getStylesheets().add(styleSheet);
            rootPane.getStyleClass().add("menuView");
            Tab mainMenuTab = new Tab("Main Menu");
            mainMenuTab.setClosable(false);
            mainMenuTab.setContent(mainScene.getRoot());
            Platform.runLater(() ->
                    tabHelper.getTabPane().getTabs().add(mainMenuTab)
            );
        }
    }

    /**
     * Initializes the login view
     * <p>
     * If the loginScene is null it gets set to a new scene containing the
     * a pane showing the login view as specified by the LoginView FXML file.
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.auth.LoginPresenter
     * @since 2019-09-03
     */
    private void initLoginView() {
        if (loginScene == null) {
            Parent rootPane = initPresenter(LoginPresenter.fxml);
            loginScene = new Scene(rootPane, 400, 300);
            loginScene.getStylesheets().add(styleSheet);
            rootPane.getStyleClass().add("login");
        }
    }

    /**
     * Initializes the registration view
     * <p>
     * If the registrationScene is null it gets set to a new scene containing the
     * a pane showing the registration view as specified by the RegistrationView
     * FXML file.
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.RegistrationPresenter
     * @since 2019-09-03
     */
    private void initRegistrationView() {
        if (registrationScene == null) {
            Parent rootPane = initPresenter(RegistrationPresenter.fxml);
            registrationScene = new Scene(rootPane, 400, 260);
            registrationScene.getStylesheets().add(styleSheet);
            rootPane.getStyleClass().add("registration");
        }
    }

    /**
     * Initializes the lobby view
     * <p>
     * If a new lobbyScene is needed this method will return a new lobbyScene containing the
     * pane showing the lobby view as specified by the LobbyView FXML file
     * <p>
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Marc Hermes, Ricardo Mook
     * @see de.uol.swp.client.lobby.LobbyPresenter
     * @since 2020-11-19
     */
    private Scene initLobbyView() {
        Parent rootPane = initPresenter(LobbyPresenter.fxml);
        Scene lobbyScene = new Scene(rootPane, 800, 600);
        lobbyScene.getStylesheets().add(styleSheet);
        rootPane.getStyleClass().add("menuView");
        return lobbyScene;
    }

    /**
     * Initializes the game view
     * <p>
     * If the gameScene is null it gets set to a new scene containing the
     * a pane showing the game view as specified by the GameView
     * FXML file
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Kirstin Beyer
     * @see de.uol.swp.client.game.GamePresenter
     * @since 2021-01-14
     */
    private Scene initGameView() {
        Parent rootPane = initPresenter(GamePresenter.fxml);
        Scene gameScene = new Scene(rootPane, 800, 600);
        gameScene.getStylesheets().add(styleSheet);
        rootPane.getStyleClass().add("game");
        return gameScene;
    }

    /**
     * Initializes the trade view
     * <p>
     * If the tradeScene is null it gets set to a new scene containing the
     * a pane showing the game view as specified by the TradeView
     * FXML file
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Alexander Lossa, Ricardo Mook
     * @see de.uol.swp.client.game.TradePresenter
     * @since 2021-04-21
     */
    private Scene initTradeView() {
        Parent rootPane = initPresenter(TradePresenter.fxml);
        Scene tradeScene = new Scene(rootPane, 800, 600);
        tradeScene.getStylesheets().add(styleSheet);
        rootPane.getStyleClass().add("trade");
        return tradeScene;
    }


    /**
     * Initializes the SummaryView
     * <p>
     * If the SummaryScene is null it gets set to a new scene containing the
     * a pane showing the Summary view as specified by the SummaryView
     * FXML file
     *
     * @return summaryScene
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.client.game.GamePresenter
     * @since 2021-04-18
     */
    private Scene initSummaryView() {
        Parent rootPane = initPresenter(SummaryPresenter.fxml);
        Scene summaryScene = new Scene(rootPane, 800, 600);
        summaryScene.getStylesheets().add(styleSheet);
        return summaryScene;
    }

    /**
     * Initializes the userSettings view
     * <p>
     * If the userSettingsScene is null it gets set to a new scene containing the
     * a pane showing the userSettings view as specified by the UserSettingsView
     * FXML file.
     * <p>
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Carsten Dekker
     * @see UserSettingsPresenter
     * @since 2021-03-04
     */
    private void initUserSettingsView() {
        if (userSettingsScene == null) {
            Parent rootPane = initPresenter(UserSettingsPresenter.fxml);
            userSettingsScene = new Scene(rootPane, 800, 500);
            userSettingsScene.getStylesheets().add(styleSheet);
            rootPane.getStyleClass().add("settings");
        }
    }

    /**
     * Initializes the userGameRules view
     * <p>
     * If the userGameRules is null it gets set to a new scene containing the
     * a pane showing the GameRules view as specified by the UserGameRulesView
     * FXML file.
     *
     * @author Sergej Tulnev
     * @see GameRulesPresenter
     * @since 2021-05-18
     */
    private void initGameRulesView() {
        Parent rootPane = initPresenter(GameRulesPresenter.fxml);
        userGameRulesScene = new Scene(rootPane, 800, 500);
        userGameRulesScene.getStylesheets().add(styleSheet);
        rootPane.getStyleClass().add("rules");
    }

    /**
     * Handles ShowRegistrationViewEvent detected on the EventBus
     * <p>
     * If a ShowRegistrationViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the registration
     * screen.
     *
     * @param event The ShowRegistrationViewEvent detected on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.event.ShowRegistrationViewEvent
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
     * @author Marco Grawunder
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
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
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.event.RegistrationCanceledEvent
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
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @since 2019-09-03
     */
    @Subscribe
    public void onRegistrationErrorEvent(RegistrationErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Handles ShowUserSettingsViewEvent detected on the EventBus
     * <p>
     * If a ShowUserSettingsViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the showUserSettings screen.
     *
     * @param event The ShowUserSettingsViewEvent detected on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.event.ShowUserSettingsViewEvent
     * @since 2021-04-03
     */
    @Subscribe
    public void onShowUserSettingsViewEvent(ShowUserSettingsViewEvent event) {
        showUserSettingsScreen();
    }


    /**
     * Handles LeaveUserSettingsEvent detected on the EventBus
     * <p>
     * If a LeaveUserSettingsEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the screen shown before userSettings.
     *
     * @param event The LeaveUserSettingsEvent detected on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.event.LeaveUserSettingsEvent
     * @since 2021-03-04
     */
    @Subscribe
    public void onLeaveUserSettingsEvent(LeaveUserSettingsEvent event) {
        showScene(lastScene, lastTitle);
    }

    /**
     * Handles ShowGameRulesEvent detected on the EventBus
     * <p>
     * If a ShowGameRules is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the Game Rules screen.
     *
     * @param event The ShowGameRules detected on the EventBus
     * @author Sergej Tulnev
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @since 2020-05-18
     */
    @Subscribe
    public void onShowGameRulesMessage(ShowGameRulesEvent event) {
        LOG.info("ShowGameRulesEvent");
        removeGameRulesTab();
        newGameRulesTab();
    }


    /**
     * Handles UserSettingsErrorEvent detected on the EventBus
     * <p>
     * If a UserSettingsErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @param event The UserSettingsErrorEvent detected on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.event.UserSettingsErrorEvent
     * @since 2021-03-06
     */
    @Subscribe
    public void onUserSettingsErrorEvent(UserSettingsErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Pauses the background music when a MuteMusicMessage on the Eventbus is detected
     *
     * @param mmm The MuteMusicMessage on the Eventbus
     * @author Ricardo Mook
     * @since 2021-05-08
     */
    @Subscribe
    public void onMuteMusicEvent(MuteMusicEvent mmm) {
        player.pause();
    }

    /**
     * Continues the background music when a UnmuteMusicMessage on the Eventbus is detected
     *
     * @param umm The UnmuteMusicMessage on the Eventbus
     * @author Ricardo Mook
     * @since 2021-05-08
     */
    @Subscribe
    public void onUnmuteMusicEvent(UnmuteMusicEvent umm) {
        player.play();
    }

    /**
     * Shows an error message inside an error alert
     * <p>
     *
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
     *
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
     *
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
     * Shows the main menu
     * <p>
     * Invokes the Method showMainTab instead of switching the Scene to the MainScene
     * <p>
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     *
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showMainScreen() {
        showMainTab();

    }

    /**
     * Shows the main menu tab
     * <p>
     * The tabScene is shown on the primary stage and it's name is set to Catan.
     *
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void showMainTab() {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = tabScene;
        Platform.runLater(() -> {
            primaryStage.setTitle("Catan");
            primaryStage.setScene(tabScene);
            primaryStage.show();
            tabPane.getSelectionModel().select(tabHelper.getTabByText("Main Menu"));
        });
    }

    /**
     * Shows the login screen
     * <p>
     * Switches the current Scene to the loginScene and sets the title of
     * the window to "Login"
     *
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
     *
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    public void showRegistrationScreen() {
        showScene(registrationScene, "Registration");
    }

    /**
     * Shows the summary screen
     * <p>
     * Switches the current Scene to the SummaryScene and sets the title of
     * the window to the gameName.
     *
     * @param gameName name of the game
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public void createSummaryTab(String gameName) {
        newSummaryTab(gameName);
        hideSummaryTab(gameName);
    }

    /**
     * Shows the lobby screen
     * <p>
     * This method invokes the newLobbyTab() method resulting in the creation of a new lobby tab
     * <p>
     * enhanced by Alexander Losse and Marc Hermes - 2021-01-20
     *
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-11-19
     */
    public void showLobbyScreen(String lobbyName) {
        newLobbyTab(lobbyName);
    }

    /**
     * Shows the userSettings screen
     * <p>
     * Switches the current Scene to the userSettingsScene and sets the title of
     * the window to "UserSettings"
     *
     * @author Carsten Dekker
     * @since 2021-04-03
     */
    public void showUserSettingsScreen() {
        showScene(userSettingsScene, "UserSettings");
    }

    /**
     * calls newTradeTab()
     *
     * @param tradeCode AbstractMessage(either TradeOfferInformBiddersMessage, or TradeStartedMessage)
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    public void showTradeScreen(String tradeCode) {
        newTradeTab(tradeCode);
    }

    /**
     * Creates a new lobby tab
     * <p>
     * When this method is invoked a new lobby tab with a specific name is created.
     * The content of the new lobby tab is set to the root of the currently empty nextLobbyScene
     * The lobby tab is then added to the TabPane.
     * Afterwards a new empty nextLobbyScene is created, for the next usage of this method.
     * Also the new Tab is shown immediately
     *
     * @param lobbyName the name of the lobby for which a tab is created
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void newLobbyTab(String lobbyName) {
        Tab lobbyTab = new Tab("Lobby " + lobbyName);
        lobbyTab.setContent(nextLobbyScene.getRoot());
        lobbyTab.setClosable(false);
        Platform.runLater(() -> {
            tabHelper.addTab(lobbyTab);
            tabHelper.getTabPane().getSelectionModel().select(lobbyTab);
        });
        nextLobbyScene = initLobbyView();
    }

    /**
     * Removes an old lobby tab
     * <p>
     * When this method is invoked a lobby tab with a specific name is removed from
     * the TabPane.
     *
     * @param lobbyName the name of the lobby that corresponds to the tab that is to be deleted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void removeLobbyTab(String lobbyName) {
        Platform.runLater(() -> tabHelper.removeTab("Lobby " + lobbyName));
    }

    /**
     * Removes an old summary tab
     * <p>
     * When this method is invoked a summaryTab with a specific name is removed from the TabPane.
     *
     * @param gameName the name of the game that corresponds to the tab that is to be deleted
     * @author René Meyer, Sergej Tulnev
     * @since 2021-05-01
     */
    public void removeSummaryTab(String gameName) {
        Platform.runLater(() -> tabHelper.removeTab("Summary of Game " + gameName));
    }

    /**
     * Shows the game screen
     * <p>
     * This method invokes the newGameTab() method resulting in the creation of a new game tab
     *
     * @param gameName the name of the game
     * @author Kirstin Beyer
     * @since 2021-01-14
     */
    public void showGameScreen(String gameName) {
        newGameTab(gameName);
    }

    /**
     * Creates a new Tab
     * <p>
     * This method invokes the newGameRulesTab method resulting in the creation of a new gameRules tab
     *
     * @author Sergej Tulnev
     * @since 2021-05-18
     */
    public void newGameRulesTab() {
        Tab gameRulesTab = new Tab("GameRules ");
        gameRulesTab.setContent(userGameRulesScene.getRoot());
        gameRulesTab.setClosable(true);
        Platform.runLater(() -> {
            tabHelper.addTab(gameRulesTab);
            tabHelper.getTabPane().getSelectionModel().select(gameRulesTab);
        });
    }

    /**
     * Creates a new game tab
     * <p>
     * When this method is invoked a new game tab with a specific name is created.
     * The content of the new game tab is set to the root of the currently empty nextGameScene
     * The game tab is then added to the TabPane.
     * Afterwards a new empty nextGameScene is created, for the next usage of this method.
     * Also the new Tab is shown immediately
     *
     * @param gameName the name of the game for which a tab is created
     * @author Marc Hermes
     * @since 2021-01-21
     */
    public void newGameTab(String gameName) {
        Tab gameTab = new Tab("Game " + gameName);
        gameTab.setContent(nextGameScene.getRoot());
        gameTab.setClosable(false);
        Platform.runLater(() -> {
            tabHelper.addTab(gameTab);
            tabHelper.getTabPane().getSelectionModel().select(gameTab);
        });
        nextGameScene = initGameView();
    }

    /**
     * Opens a new trade tab on call
     *
     * @param tradeID the tradeCode for the trade
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    public void newTradeTab(String tradeID) {
        Tab tradeTab = new Tab("Trade " + tradeID);
        tradeTab.setContent(nextTradeScene.getRoot());
        tradeTab.setClosable(false);
        Platform.runLater(() -> {
            tabHelper.addTab(tradeTab);
            tabHelper.getTabPane().getSelectionModel().select(tradeTab);
        });
        nextTradeScene = initTradeView();
    }

    /**
     * Creates a new summary tab
     * <p>
     * When this method is invoked a new summary tab with a specific name is created.
     * The content of the new summary tab is set to the root of the summaryScene
     * The game tab is then added to the TabPane.
     * Also the new Tab is shown immediately
     *
     * @param gameName the name of the game for which a tab is created
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public void newSummaryTab(String gameName) {
        Tab summaryTab = new Tab("Summary of Game " + gameName);
        summaryTab.setContent(nextSummaryScene.getRoot());
        summaryTab.setClosable(false);
        Platform.runLater(() -> tabHelper.addTab(summaryTab));
        nextSummaryScene = initSummaryView();
    }

    /**
     * Removes an old game tab
     * <p>
     * When this method is invoked a game tab with a specific name is removed from
     * the TabPane.
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook - 2021-03-05
     *
     * @param gameName the name of the game that corresponds to the tab that is to be deleted
     * @author Marc Hermes
     * @since 2021-01-21
     */
    public void removeGameTab(String gameName) {
        Platform.runLater(() -> tabHelper.removeTab("Game " + gameName));
    }

    /**
     * Removes an old trade tab
     * <p>
     * When this method is invoked a trade tab with a specific tradeCode is removed from
     *
     * @author Alexander Losse, Ricardo Mook - 2021-03-05
     * @since 2021-04-21
     */
    public void removeTradeTab(TradeEndedMessage tem) {
        Platform.runLater(() -> tabHelper.removeTab("Trade " + tem.getName() + " " + tem.getTradeCode()));
    }

    /**
     * Removes an old GameRules tab
     * <p>
     * When this method is invoked a GameRules tab, is removed the old GameRules tab
     *
     * @author Sergej Tulnev
     * @since 2021-05-19
     */
    public void removeGameRulesTab() {
        Platform.runLater(() -> tabHelper.removeTab("GameRules "));
    }

    /**
     * Suspends a certain lobby Tab
     * <p>
     * When this method is invoked the tabHelper is used to suspend a lobby Tab.
     * Suspended Tabs are removed from tabPane but not deleted.
     *
     * @param lobbyName the name of the Lobby corresponding to the lobby Tab
     * @author Marc Hermes
     * @since 2021-03-16
     */
    public void suspendLobbyTab(String lobbyName) {
        Platform.runLater(() -> tabHelper.suspendTab("Lobby " + lobbyName));
    }

    /**
     * Unsuspends a certain lobby Tab
     * <p>
     * When this method is invoked the tabHelper is used to unsuspend a lobby Tab.
     *
     * @param lobbyName the name of the Lobby corresponding to the lobby Tab
     * @author Marc Hermes
     * @since 2021-03-16
     */
    public void unsuspendLobbyTab(String lobbyName) {
        Platform.runLater(() -> {
            tabHelper.unsuspendTab("Lobby " + lobbyName);
            tabHelper.getTabPane().getSelectionModel().select(tabHelper.getTabByText("Lobby " + lobbyName));
        });
    }

    /**
     * Hides a summaryTab
     *
     * @param gameName the String name of the game
     * @author Marc Hermes
     * @since 2021-05-10
     */
    public void hideSummaryTab(String gameName) {
        Platform.runLater(() -> tabHelper.suspendTab("Summary of Game " + gameName));
    }

    /**
     * Shows a summaryTab
     *
     * @param gameName the String name of the game
     * @author Marc Hermes
     * @since 2021-05-10
     */
    public void showSummaryTab(String gameName) {
        Platform.runLater(() -> {
            tabHelper.unsuspendTab("Summary of Game " + gameName);
            tabPane.getSelectionModel().select(tabHelper.getTabByText("Summary of Game " + gameName));
        });
    }

}
