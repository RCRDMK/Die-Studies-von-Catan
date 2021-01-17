package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.lobby.request.RetrieveAllLobbiesForUserRequest;
import de.uol.swp.common.lobby.response.AllLobbiesForSpecificUserResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.response.*;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

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

    private LobbyService lobbyService;

    private User user;

    private ClientConnection clientConnection;

    private EventBus eventBus;

    private SceneManager sceneManager;

    // -----------------------------------------------------
    // Java FX Methods
    // ----------------------------------------------------

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

        // get user service from guice, is needed for logout
        this.lobbyService = injector.getInstance(LobbyService.class);

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
    public void connectionEstablished(Channel ch) {
        sceneManager.showLoginScreen();
    }


    /**
     * Gets executed when the user exits the program via x Button
     * <p>
     *
     * @author enhanced by René Meyer, Sergej Tulnev
     * @since 2021-01-17
     */
    @Override
    public void stop() {
        // To prevent IllegalMonitorStateException - current Thread not owner
        synchronized (eventBus)
        {
            try{
                if (userService != null && user != null) {
                    lobbyService.retrieveAllLobbiesForSpecificUser(user);
                    // Wait for AllLobbiesForSpecificUserResponse on eventBus
                    // and the execution of the Subscribe Method
                    eventBus.wait();
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
            catch(Exception e){
                LOG.info("Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Logs when a RetrieveAllLobbiesForUserRequest was posted on the eventBus
     * <p>
     *
     * @author René Meyer, Sergej Tulnev
     * @param msg
     * @since 2021-01-17
     */
    @Subscribe
    public void retrieveAllLobbiesForUserRequest(RetrieveAllLobbiesForUserRequest msg){
        LOG.info("Sent RetrieveAllLobbiesForUserRequest to server...");
    }

    /**
     * Handles the lobby leaving and logout for the user
     * <p>
     * If an AllLobbiesForSpecificUserResponse is detected on the EventBus this
     * method is called. First it gets the LobbyDTOs from the message.
     * Then the leaveLobby function gets called for every lobby in the lobbies list.
     * Finally the user gets logged out.
     *
     * @author René Meyer, Sergej Tulnev
     * @param msg
     * @since 2021-01-17
     */
    @Subscribe
    public void retrieveAllLobbiesForSpecificUser(AllLobbiesForSpecificUserResponse msg) {
        var lobbies = msg.getLobbyDTOs();
        LOG.info("Retrieved AllLobbiesForSpecificUserResponse with " + (long) lobbies.size() + " lobbies from server...");
        for(var lobby : lobbies){
            lobbyService.leaveLobby(lobby.getName(), (UserDTO) msg.getUser());
            LOG.info("Left Lobby: " + lobby.getName());
        }
        userService.logout(user);
    }

    /**
     * Handles successful login
     * <p>
     * If an LoginSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the main menu and sets
     * this clients user to the user found in the object. If the loglevel is set
     * to DEBUG or higher "user logged in successfully " and the username of the
     * logged in user are written to the log.
     *
     * @param message The LoginSuccessfulResponse object detected on the EventBus
     * @see de.uol.swp.client.SceneManager
     * @since 2017-03-17
     */
    @Subscribe
    public void userLoggedIn(LoginSuccessfulResponse message) {
        LOG.debug("user logged in successfully " + message.getUser().getUsername());
        this.user = message.getUser();
        sceneManager.showMainScreen(user);
    }

    @Subscribe
    public void userLoggedOut(LogoutRequest message) {
        LOG.debug("user logged out ");
        sceneManager.showLoginScreen();
    }

    /**
     * Handles successful lobby creation
     * <p>
     * If an LobbyCreatedSuccessful object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the lobby menu and sets
     * this clients user to the user found in the object. If the loglevel is set
     * to DEBUG or higher "user created lobby " and the username of the
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
            sceneManager.showLobbyScreen(user, " ohne Name");
        } else {
            sceneManager.showLobbyScreen(user, message.getName());
        }
    }

    /**
     * Handles successful lobby joining
     * <p>
     * If a UserJoinedLobbyMessage object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the lobby menu and sets
     * this clients user to the user found in the object. If the loglevel is set
     * to DEBUG or higher "user joined lobby " is written to the log.
     *
     * @param message The UserJoinedLobbyMessage object detected on the EventBus
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-03
     */
    @Subscribe
    public void userJoinedLobby(LobbyJoinedSuccessfulResponse message) {
        //TODO: Make it possible for the User to select a Lobby that he wants to join,
        // currently the user is only able to join the only lobby on pressing the button
        LOG.debug("user joined lobby ");
        this.user = message.getUser();
            sceneManager.showLobbyScreen(user, message.getName());

    }
    /**
     * Handles the successful leaving of a user
     *
     * If an UserLeftLobbyMessage object is detected on the EventBus this method is called.
     * It tells the SceneManager to show the main menu.
     *
     * @param message
     * @see de.uol.swp.client.SceneManager
     */
    @Subscribe
    public void userLeftLobby(LobbyLeftSuccessfulResponse message) {
        LOG.debug("User " + message.getUser().getUsername() + " left lobby ");
            this.user = message.getUser();
            sceneManager.showMainScreen(user);
    }

    /**
     * Handles unsuccessful registrations
     * <p>
     * If an RegistrationExceptionMessage object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the sever error alert.
     * If the loglevel is set to Error or higher "Registration error " and the
     * error message are written to the log.
     *
     * @param message The RegistrationExceptionMessage object detected on the EventBus
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @Subscribe
    public void onRegistrationExceptionMessage(RegistrationExceptionMessage message) {
        sceneManager.showServerError("Registration error " + message);
        LOG.error("Registration error " + message);
    }

    /**
     * Handles successful registrations
     * <p>
     * If an RegistrationSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the login window. If
     * the loglevel is set to INFO or higher "Registration Successful." is written
     * to the log.
     *
     * @param message The RegistrationSuccessfulResponse object detected on the EventBus
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @Subscribe
    public void onRegistrationSuccessfulMessage(RegistrationSuccessfulResponse message) {
        LOG.info("Registration successful.");
        sceneManager.showLoginScreen();
    }

    /**
     * Handles errors produced by the EventBus
     * <p>
     * If an DeadEvent object is detected on the EventBus, this method is called.
     * It writes "DeadEvent detected " and the error message of the detected DeadEvent
     * object to the log, if the loglevel is set to ERROR or higher.
     *
     * @param deadEvent The DeadEvent object found on the EventBus
     * @since 2019-08-07
     */
    @Subscribe
    private void handleEventBusError(DeadEvent deadEvent) {
        LOG.error("DeadEvent detected " + deadEvent);
    }

    @Override
    public void exceptionOccurred(String e) {
        sceneManager.showServerError(e);
    }

    // -----------------------------------------------------
    // JavFX Help method
    // -----------------------------------------------------

    /**
     * Default startup method for javafx applications
     *
     * @param args Any arguments given when starting the application
     * @since 2017-03-17
     */
    public static void main(String[] args) {
        launch(args);
    }

}
