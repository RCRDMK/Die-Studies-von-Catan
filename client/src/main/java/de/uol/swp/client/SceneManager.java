package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.client.account.UserSettingsPresenter;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.account.event.ChangeToCertainSizeEvent;
import de.uol.swp.client.account.event.ShowUserSettingsViewEvent;
import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.game.GamePresenter;
import de.uol.swp.client.game.SummaryPresenter;
import de.uol.swp.client.game.TradePresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.game.message.SummaryConfirmedMessage;
import de.uol.swp.common.game.message.TradeEndedMessage;
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
 * <p>
 *
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
    private Scene tradeScene;
    private Tab mainMenuTab;
    private VBox vBox;
    private Scene tabScene;
    private Scene nextLobbyScene;
    private Scene nextGameScene;
    private Scene nextTradeScene;
    private final Injector injector;
    private TabPane tabPane = new TabPane();
    private TabHelper tabHelper;
    private Scene userSettingsScene;
    private Scene summaryScene;



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
     *
     * @author Marco Grawunder
     * @since 2019-09-03
     */
    private void initViews() {
        this.tabHelper = new TabHelper(this.tabPane);
        vBox = new VBox(tabHelper.getTabPane());
        tabScene = new Scene(vBox);
        initLoginView();
        initMainView();
        initRegistrationView();
        initUserSettingsView();
        summaryScene = initSummaryView();
        nextLobbyScene = initLobbyView();
        nextGameScene = initGameView();
        nextTradeScene = initTradeView();
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
     *
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
            mainMenuTab = new Tab("Main Menu");
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
     *
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
     *
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
     *
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Marc Hermes, Ricardo Mook
     * @see de.uol.swp.client.lobby.LobbyPresenter
     * @since 2020-11-19
     */
    private Scene initLobbyView() {
        Parent rootPane = initPresenter(LobbyPresenter.fxml);
        lobbyScene = new Scene(rootPane, 800, 600);
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
     *
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Kirstin Beyer
     * @see de.uol.swp.client.game.GamePresenter
     * @since 2021-01-14
     */
    private Scene initGameView() {
        Parent rootPane = initPresenter(GamePresenter.fxml);
        gameScene = new Scene(rootPane, 800, 600);
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
     *
     * enhanced by Ricardo Mook - 2021-04-28
     * added a CSS rule
     *
     * @author Alexander Lossa, Ricardo Mook
     * @see de.uol.swp.client.game.TradePresenter
     * @since 2021-04-21
     */
    private Scene initTradeView() {
        Parent rootPane = initPresenter(TradePresenter.fxml);
        tradeScene = new Scene(rootPane, 800, 600);
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
        summaryScene = new Scene(rootPane, 800, 600);
        summaryScene.getStylesheets().add(styleSheet);
        return summaryScene;
    }

    /**
     * Initializes the userSettings view
     * <p>
     * If the userSettingsScene is null it gets set to a new scene containing the
     * a pane showing the userSettings view as specified by the UserSettingsView
     * FXML file.
     *
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

    /*
    @Subscribe
    public void onChangeToCertainSizeEvent(ChangeToCertainSizeEvent event) {
       primaryStage.setWidth(event.getWidth());
       primaryStage.setHeight(event.getHeight());
    }
     */


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
     * Handles GameFinishedMessage detected on the EventBus
     * <p>
     * If a GameFinishedMessage is detected on the EventBus, this method gets
     * called. It calls a method to add a Summary tab and removes the gameTab
     *
     * @param message ShowSummaryEvent that contains the GameName
     * @author René Meyer, Sergej Tulnev
     * @see GameFinishedMessage
     * @since 2021-04-18
     */
    @Subscribe
    public void onFinishedGameMessage(GameFinishedMessage message) {
        var gameName = message.GetGame().getName();
        removeGameTab(gameName);
        showSummaryScreen(gameName);
    }

    @Subscribe
    public void onConfirmedSummaryMessage(SummaryConfirmedMessage message) {
        var gameName = message.GetGameName();
        var user = message.getUser();
        removeSummaryTab(gameName);
        showMainTab(user);
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
     * Shows the login error alert
     * <p>
     * Opens an ErrorAlert popup saying "Error logging in to server"
     *
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
     * Invokes the Method showMainTab instead of switching the Scene to the MainScene
     * <p>
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
     * The tabScene is shown on the primary stage and it's name is set to Catan.
     *
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void showMainTab(User currentUser) {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = tabScene;
        Platform.runLater(() -> {
            primaryStage.setTitle("Catan");
            primaryStage.setScene(tabScene);
            primaryStage.show();
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
     * the window to the gamename.
     *
     * @param gameName name of the game
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public void showSummaryScreen(String gameName) {
        newSummaryTab(gameName);
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
    public void showLobbyScreen(User currentUser, String lobbyname) {
        newLobbyTab(currentUser, lobbyname);
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
     * @param lobbyname the name of the lobby that corresponds to the tab that is to be deleted
     * @author Alexander Losse, Marc Hermes
     * @since 2021-01-20
     */
    public void removeLobbyTab(User currentUser, String lobbyname) {
        Platform.runLater(() -> {
            tabHelper.removeTab("Lobby " + lobbyname);
        });
    }

    public void removeSummaryTab(String gamename) {
        Platform.runLater(() -> {
            tabHelper.removeTab("Summary of Game " + gamename);
        });
    }

    /**
     * Shows the game screen
     * <p>
     * This method invokes the newGameTab() method resulting in the creation of a new game tab
     *
     * @author Kirstin Beyer
     * @since 2021-01-14
     */
    public void showGameScreen(String gamename) {
        newGameTab(gamename);
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
     * @param gamename the name of the game for which a tab is created
     * @author Marc Hermes
     * @since 2021-01-21
     */
    public void newGameTab(String gamename) {
        Tab gameTab = new Tab("Game " + gamename);
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
     * @param gamename the name of the game for which a tab is created
     * @author René Meyer, Sergej Tulnev
     * @since 2021-04-18
     */
    public void newSummaryTab(String gamename) {
        Tab summaryTab = new Tab("Summary of Game " + gamename);
        summaryTab.setContent(summaryScene.getRoot());
        summaryTab.setClosable(false);
        Platform.runLater(() -> {
            tabHelper.addTab(summaryTab);
            tabHelper.getTabPane().getSelectionModel().select(summaryTab);
        });
        summaryScene = initSummaryView();
    }

    /**
     * Removes an old game tab
     * <p>
     * When this method is invoked a game tab with a specific name is removed from
     * the TabPane.
     * <p>
     * enhanced by Alexander Losse, Ricardo Mook - 2021-03-05
     *
     * @param gamename the name of the game that corresponds to the tab that is to be deleted
     * @author Marc Hermes
     * @since 2021-01-21
     */
    public void removeGameTab(String gamename) {
        Platform.runLater(() -> {
            tabHelper.removeTab("Game " + gamename);
        });
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
        Platform.runLater(() -> {
            tabHelper.removeTab("Trade " + tem.getTradeCode());
        });
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
        Platform.runLater(() -> {
            tabHelper.suspendTab("Lobby " + lobbyName);
        });
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
        });
    }
}
