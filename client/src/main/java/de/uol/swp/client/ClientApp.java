package de.uol.swp.client;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.stage.Stage;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;

import io.netty.channel.Channel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.client.di.ClientModule;
import de.uol.swp.client.game.event.SummaryConfirmedEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameDroppedMessage;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.game.message.PlayerKickedMessage;
import de.uol.swp.common.game.message.TradeEndedMessage;
import de.uol.swp.common.game.message.TradeOfferInformBiddersMessage;
import de.uol.swp.common.game.message.TradeStartedMessage;
import de.uol.swp.common.game.response.GameLeftSuccessfulResponse;
import de.uol.swp.common.lobby.response.JoinOnGoingGameResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.exception.UpdateUserExceptionMessage;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.response.DropUserSuccessfulResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.response.PingResponse;
import de.uol.swp.common.user.response.RegistrationSuccessfulResponse;
import de.uol.swp.common.user.response.RetrieveUserInformationResponse;
import de.uol.swp.common.user.response.UpdateUserSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse;

/**
 * The application class of the client
 * <p>
 * This class handles the startup of the application, as well as, incoming login
 * and registration responses and error messages
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.ConnectionListener
 * @see javafx.application.Application
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class ClientApp extends Application implements ConnectionListener {

    private static final Logger LOG = LogManager.getLogger(ClientApp.class);
    private String host;
    private int port;
    private ClientUserService userService;
    private User user;
    private ClientConnection clientConnection;
    private EventBus eventBus;
    private SceneManager sceneManager;
    private long lastPingResponse;
    private Timer timer;

    // -----------------------------------------------------
    // Java FX Methods
    // ----------------------------------------------------

    /**
     * Default startup method for javafx applications
     *
     * @param args Any arguments given when starting the application
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        Parameters p = getParameters();
        List<String> args = p.getRaw();

        if (args.size() != 2) {
            host = "duemmer.informatik.uni-oldenburg.de";
            port = 50100;
            System.err.println("Usage: " + ClientConnection.class.getSimpleName() + " host port");
            System.err.println("Using default port " + port + " on " + host);
        } else {
            host = args.get(0);
            port = Integer.parseInt(args.get(1));
        }

        // do not establish connection here
        // if connection is established in this stage, no GUI is shown and
        // exceptions are only visible in console!
    }

    @Override
    public void start(Stage primaryStage) {

        // Client app is created by java, so injection must
        // be handled here manually
        Injector injector = Guice.createInjector(new ClientModule());

        // get user service from guice, is needed for logout
        this.userService = injector.getInstance(ClientUserService.class);

        // get event bus from guice
        eventBus = injector.getInstance(EventBus.class);
        // Register this class for de.uol.swp.client.events (e.g. for exceptions)
        eventBus.register(this);

        // Client app is created by java, so injection must
        // be handled here manually
        SceneManagerFactory sceneManagerFactory = injector.getInstance(SceneManagerFactory.class);
        this.sceneManager = sceneManagerFactory.create(primaryStage);

        ClientConnectionFactory connectionFactory = injector.getInstance(ClientConnectionFactory.class);
        clientConnection = connectionFactory.create(host, port);
        clientConnection.addConnectionListener(this);
        // JavaFX Thread should not be blocked to long!
        Thread t = new Thread(() -> {
            try {
                clientConnection.start();
            } catch (Exception e) {
                exceptionOccurred(e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void stop() {
        if (userService != null && user != null) {
            userService.logout(user);
            user = null;
        }
        eventBus.unregister(this);
        // Important: Close connection so connection thread can terminate
        // else client application will not stop
        LOG.trace("Trying to shutting down client ...");
        if (clientConnection != null) {
            clientConnection.close();
        }
        LOG.info("ClientConnection shutdown");
    }

    @Override
    public void connectionEstablished(Channel ch) {
        sceneManager.showLoginScreen();
    }

    @Override
    public void exceptionOccurred(String e) {
        sceneManager.showServerError(e);
    }

    /**
     * Handles successful login
     * <p>
     * If an LoginSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the main menu and sets
     * this clients user to the user found in the object. If the log level is set
     * to DEBUG or higher "user logged in successfully " and the username of the
     * logged in user are written to the log.
     * It also starts a timer for the ping message and a separate timer for the client to check if he has a timeout.
     *
     * @param message The LoginSuccessfulResponse object detected on the EventBus
     * @author Marco Grawunder, Philip Nitsche
     * @see de.uol.swp.client.SceneManager
     * @since 2021-01-21
     */
    @Subscribe
    public void userLoggedIn(LoginSuccessfulResponse message) {
        LOG.debug("user logged in successfully " + message.getUser().getUsername());
        this.user = message.getUser();
        sceneManager.showMainScreen();
        userService.startTimerForPing(message.getUser());
        lastPingResponse = System.currentTimeMillis();
        checkForTimeout();
    }

    /**
     * Handles successful Mail information response
     * <p>
     * If an RetrieveUserMailResponse object is detected on the EventBus this
     * method is called. If the logLevel is set to INFO or higher "Got the response with the Mail from User "
     * is written to the log.
     *
     * @param response The RetrieveUserMailResponse object detected on the EventBus
     * @see de.uol.swp.client.account.UserSettingsPresenter
     * @since 2021-03-14
     */
    @Subscribe
    public void onRetrieveUserInformationResponse(RetrieveUserInformationResponse response) {
        LOG.debug("Got the response with the Information from User " + response.getUser().getUsername());
        this.user = response.getUser();
    }

    /**
     * Handles successful logout
     * <p>
     * If an LogoutSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the LoginScree.
     * It also ends the timer for the ping message and the separate timer for the client to check if he has a timeout.
     *
     * @param message The LogoutSuccessfulResponse object detected on the EventBus
     * @author Philip Nitsche
     * @see de.uol.swp.client.SceneManager
     * @since 2021-01-21
     */
    @Subscribe
    public void userLoggedOut(LogoutRequest message) {
        LOG.debug("user logged out ");
        sceneManager.showLoginScreen();
        userService.endTimerForPing();
        timer.cancel();
    }

    /**
     * Handles successful lobby creation
     * <p>
     * If an LobbyCreatedSuccessful object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the lobby menu and sets
     * this clients user to the user found in the object. If the logLevel is set
     * to DEBUG or higher "user created lobby" and the username of the
     * logged in user are written to the log.
     *
     * @param message The LobbyCreatedMessage object detected on the EventBus
     * @see de.uol.swp.client.SceneManager
     * @since 2020-11-24
     */
    @Subscribe
    public void userCreatedLobby(LobbyCreatedSuccessfulResponse message) {
        LOG.debug("user created lobby " + message.getUser());
        this.user = message.getUser();
        if (message.getName() == null) {
            sceneManager.showLobbyScreen(" ohne Name");
        } else {
            sceneManager.showLobbyScreen(message.getName());
        }
    }

    /**
     * Handles successful lobby joining
     * <p>
     * If a UserJoinedLobbyMessage object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the lobby menu and sets
     * this clients user to the user found in the object. If the logLevel is set
     * to DEBUG or higher "user joined lobby " is written to the log.
     *
     * @param message The UserJoinedLobbyMessage object detected on the EventBus
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-03
     */
    @Subscribe
    public void userJoinedLobby(LobbyJoinedSuccessfulResponse message) {
        LOG.debug("user joined lobby ");
        this.user = message.getUser();
        sceneManager.showLobbyScreen(message.getName());

    }

    /**
     * Handles successful start of a game
     * <p>
     * If a GameCreatedMessage object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the game menu and suspend
     * the corresponding LobbyTab. Also a new summaryTab is created. If the logLevel is set
     * to DEBUG or higher "user joined lobby " is written to the log.
     * <p>
     * enhanced by Marc Hermes - 2021-03-15
     *
     * @param message The StartGameResponse object detected on the EventBus
     * @author Kirstin Beyer
     * @see de.uol.swp.common.game.message.GameCreatedMessage
     * @since 2021-01-14
     */
    @Subscribe
    public void userStartedGame(GameCreatedMessage message) {
        LOG.debug(" Started a game " + message.getName());
        sceneManager.showGameScreen(message.getName());
        sceneManager.suspendLobbyTab(message.getName());
        sceneManager.createSummaryTab(message.getName());
    }

    /**
     * When a JoinOnGoingGameResponse is detected on the EventBus this method is invoked
     * <p>
     * The same actions are done as when a GameCreatedMessage is detected
     *
     * @param response the JoinOnGoingGameResponse detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void userJoinedOnGoingGame(JoinOnGoingGameResponse response) {
        LOG.debug("Received JoinOnGoingGameResponse from Server, may " + (!response.isJoinedSuccessful() ? "not" :
                "") + " join the game.");
        if (response.isJoinedSuccessful()) {
            sceneManager.showGameScreen(response.getGameName());
            sceneManager.suspendLobbyTab(response.getGameName());
            sceneManager.createSummaryTab(response.getGameName());
        }
    }

    /**
     * reacts to TradeStartedMessage
     *
     * @param message TradeStartedMessage
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    @Subscribe
    public void userStartedTrade(TradeStartedMessage message) {
        LOG.debug("Started a trade " + message.getGame());
        sceneManager.showTradeScreen(message.getGame() + " " + message.getTradeCode());
    }

    /**
     * reacts to TradeOfferInformBiddersMessage
     *
     * @param message TradeStartedMessage
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-21
     */
    @Subscribe
    public void tradeRegistered(TradeOfferInformBiddersMessage message) {
        LOG.debug("A trade request was registered");
        sceneManager.showTradeScreen(message.getName() + " " + message.getTradeCode());
    }

    /**
     * Handles the successful leaving of a user from a lobby
     * <p>
     * If an LobbyLeftSuccessfulResponse object is detected on the EventBus this method is called.
     * It tells the SceneManager to remove the tab corresponding to the lobby that was left.
     *
     * @param message the LobbyLeftSuccessfulResponse detected on the EventBus
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse
     * @since 2021-01-20
     */
    @Subscribe
    public void userLeftLobby(LobbyLeftSuccessfulResponse message) {
        LOG.debug("User " + message.getUser().getUsername() + " left lobby ");
        this.user = message.getUser();
        sceneManager.removeLobbyTab(message.getName());
    }

    /**
     * Handles a GameDroppedMessage when detected on the Eventbus
     * <p>
     * <p>
     * If a GameDroppedMessage is detected on the Eventbus this method
     * gets called. It removes the GameTab which was passed on from the
     * GameDroppedMessage and unsuspends the corresponding LobbyTab.
     * <p>
     * enhanced by Marc Hermes - 2021-03-15
     *
     * @param message The GameDroppedMessage detected on the Eventbus
     * @author Ricardo Mook, Alexander Losse
     * @see de.uol.swp.common.game.message.GameDroppedMessage
     * @since 2021-03-04
     */
    @Subscribe
    public void userDroppedGame(GameDroppedMessage message) {
        LOG.debug("Successfully dropped game  " + message.getName());
        sceneManager.removeGameTab(message.getName());
        sceneManager.unsuspendLobbyTab(message.getName());
    }

    /**
     * Handles the successful leaving of a user from a game
     * <p>
     * If an GameLeftSuccessfulResponse object is detected on the EventBus this method is called.
     * It tells the SceneManager to remove the tab corresponding to the game that was left
     * and unsuspends the LobbyTab
     * <p>
     * enhanced by Marc Hermes - 2021-03-15
     *
     * @param message the LobbyLeftSuccessfulResponse detected on the EventBus
     * @author Marc Hermes
     * @see GameLeftSuccessfulResponse
     * @since 2021-01-21
     */
    @Subscribe
    public void userLeftGame(GameLeftSuccessfulResponse message) {
        LOG.debug("Successfully left game  " + message.getName());
        sceneManager.removeGameTab(message.getName());
        sceneManager.unsuspendLobbyTab(message.getName());
    }

    /**
     * Handles the successful kicking of a player from the game
     * <p>
     * If an PlayerKickedMessage object is detected on the EventBus this method is called.
     * It tells the SceneManager to remove the tab corresponding to the game that was kicked
     * and unsuspends the LobbyTab.
     * If isToBan equals true, method tells the SceneManager to remove lobby tab, so the player will
     * be banned from the game.
     * <p>
     *
     * @param message the PlayerKickedMessage detected on the EventBus
     * @author Iskander Yusupov
     * @see PlayerKickedMessage
     * @since 2021-06-25
     */
    @Subscribe
    public void playerKicked(PlayerKickedMessage message) {
        LOG.debug("Successfully kicked from the game game  " + message.getName());
        sceneManager.removeGameTab(message.getName());
        sceneManager.unsuspendLobbyTab(message.getName());
        if (message.isToBan()) {
            sceneManager.removeLobbyTab(message.getName());
        }
    }

    /**
     * Handles unsuccessful registrations
     * <p>
     * If a RegistrationExceptionMessage object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the sever error alert.
     * If the logLevel is set to DEBUG or higher "Registration error " and the
     * error message are written to the log.
     *
     * @param message The RegistrationExceptionMessage object detected on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @Subscribe
    public void onRegistrationExceptionMessage(RegistrationExceptionMessage message) {
        sceneManager.showServerError("Registration error " + message);
        LOG.error("Registration error " + message);
    }

    /**
     * Handles unsuccessful user updates
     * <p>
     * If an UpdateUserExceptionMessage object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the sever error alert.
     * If the logLevel is set to DEBUG or higher "UpdateUser error " and the
     * error message are written to the log.
     *
     * @param message The UpdateUserExceptionMessage object detected on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.client.SceneManager
     * @since 2021-03-04
     */
    @Subscribe
    public void onUpdateUserExceptionMessage(UpdateUserExceptionMessage message) {
        sceneManager.showServerError("UpdateUser error " + message);
        LOG.error("UpdateUser error " + message);
    }

    /**
     * Handles successful registrations
     * <p>
     * If an RegistrationSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the login window. If
     * the logLevel is set to INFO or higher "Registration Successful." is written
     * to the log.
     *
     * @param message The RegistrationSuccessfulResponse object detected on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @Subscribe
    public void onRegistrationSuccessfulMessage(RegistrationSuccessfulResponse message) {
        LOG.info("Registration successful.");
        sceneManager.showLoginScreen();
    }

    /**
     * Handles successful user updates
     * <p>
     * If an UpdateUserSuccessfulResponse object is detected on the EventBus this
     * method is called. If the logLevel is set to INFO or higher "Update user Successful."
     * is written to the log.
     *
     * @param response The UpdateUserSuccessfulResponse object detected on the EventBus
     * @author Carsten Dekker
     * @since 2021-03-04
     */
    @Subscribe
    public void onUpdateUserSuccessfulResponse(UpdateUserSuccessfulResponse response) {
        LOG.info("Update user successful.");
    }

    /**
     * Handles the successful drop of a user
     * <p>
     * If an DropUserSuccessfulResponse object is detected on the EventBus this
     * method is called. If the logLevel is set to INFO or higher "Drop user was successful."
     * is written to the log.
     *
     * @param response The DropUserSuccessfulResponse object detected on the EventBus
     * @author Carsten Dekker
     * @since 2021-03-14
     */
    @Subscribe
    public void onDropUserSuccessfulResponse(DropUserSuccessfulResponse response) {
        LOG.info("Drop user was successful.");
    }

    /**
     * Handles errors produced by the EventBus
     * <p>
     * If an DeadEvent object is detected on the EventBus, this method is called.
     * It writes "DeadEvent detected " and the error message of the detected DeadEvent
     * object to the log, if the logLevel is set to ERROR or higher.
     *
     * @param deadEvent The DeadEvent object found on the EventBus
     * @author Marco Grawunder
     * @since 2019-08-07
     */
    @Subscribe
    private void handleEventBusError(DeadEvent deadEvent) {
        LOG.error("DeadEvent detected " + deadEvent);
    }

    // -----------------------------------------------------
    // JavFX Help method
    // -----------------------------------------------------

    /**
     * Handles the Ping Response Messages
     * <p>
     * Gets the latest Time form the Ping Response
     *
     * @author Philip Nitsche, Marc Hermes
     * @since 2021-01-22
     */
    @Subscribe
    private void onPingResponse(PingResponse message) {
        lastPingResponse = message.getTime();
    }

    /**
     * Shows the login screen
     * <p>
     * If a user had a timeout this method is called to show the login screen.
     *
     * @author Philip Nitsche, Marc Hermes
     * @since 2021-01-22
     */

    private void checkoutTimeout() {
        sceneManager.showLoginScreen();
        userService.endTimerForPing();
        timer.cancel();
    }

    /**
     * Handles a Timer to check for a Timeout
     * <p>
     * Checks if the user has received a ping response in the last 120 seconds.
     * If not checkoutTimeout will be called.
     *
     * @author Philip Nitsche, Marc Hermes
     * @since 2021-01-22
     */
    private void checkForTimeout() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if ((System.currentTimeMillis() - lastPingResponse) >= 120000) {
                    checkoutTimeout();
                }
            }
        }, 60000, 60000);
    }

    /**
     * Removes the trade tab, when the trade ended
     *
     * @param message the TradeEndedMessage detected on the EventBus
     * @author Alexander Lossa, Ricardo Mook
     * @since 2021-04-21
     */
    @Subscribe
    public void onTradeEndedMessage(TradeEndedMessage message) {
        LOG.info("TradeEndedMessage");
        sceneManager.removeTradeTab(message);
    }

    /**
     * Handles GameFinishedMessage detected on the EventBus
     * <p>
     * If a GameFinishedMessage is detected on the EventBus, this method gets
     * called. It calls a method to show a Summary tab and removes the gameTab
     *
     * @param message ShowSummaryEvent that contains the GameName
     * @author René Meyer, Sergej Tulnev
     * @see GameFinishedMessage
     * @since 2021-04-18
     */
    @Subscribe
    public void onFinishedGameMessage(GameFinishedMessage message) {
        var gameName = message.getStatsDTO().getGameName();
        sceneManager.removeGameTab(gameName);
        sceneManager.showSummaryTab(gameName);
    }

    /**
     * Handles SummaryConfirmedMessage detected on the EventBus
     * <p>
     * If a SummaryConfirmedMessage is detected on the EventBus, this method gets
     * called. It removes the summaryTab and shows the MainTab.
     *
     * @param event SummaryConfirmedEvent that contains the GameName and user
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.client.game.event.SummaryConfirmedEvent
     * @since 2021-05-01
     */
    @Subscribe
    public void onConfirmedSummaryMessage(SummaryConfirmedEvent event) {
        var gameName = event.getGameName();
        sceneManager.removeSummaryTab(gameName);
        sceneManager.showMainTab();
    }
}
