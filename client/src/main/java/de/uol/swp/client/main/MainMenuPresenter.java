package de.uol.swp.client.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.account.UserSettingsService;
import de.uol.swp.client.account.event.ShowUserSettingsViewEvent;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyCell;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.register.event.ShowGameRulesEvent;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.message.GameDroppedMessage;
import de.uol.swp.common.game.message.GameStartedMessage;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.LobbyDroppedMessage;
import de.uol.swp.common.lobby.message.LobbySizeChangedMessage;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.lobby.response.AlreadyJoinedThisLobbyResponse;
import de.uol.swp.common.lobby.response.LobbyAlreadyExistsResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.JoinDeletedLobbyResponse;
import de.uol.swp.common.user.response.lobby.LobbyFullResponse;
import de.uol.swp.common.user.response.lobby.WrongLobbyPasswordResponse;

/**
 * Manages the main menu
 * <p>
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */
public class MainMenuPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/MainMenuView.fxml";

    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    private static final ShowUserSettingsViewEvent showSettingsViewEvent = new ShowUserSettingsViewEvent();

    private static final ShowGameRulesEvent showGameRulesEvent = new ShowGameRulesEvent();
    @FXML
    CheckBox passwordCheckBox;
    private ObservableList<String> users;
    private ObservableList<LobbyDTO> lobbies;
    private User loggedInUser;
    @FXML
    private PasswordField lobbyPasswordField;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField inputField;

    @FXML
    private Label lobbyNameInvalid;

    @FXML
    private Label lobbyAlreadyExistsLabel;

    @FXML
    private TextField lobbyNameTextField;

    @Inject
    private LobbyService lobbyService;

    @Inject
    private ChatService chatService;

    @Inject
    private UserSettingsService userSettingsService;

    @FXML
    private ListView<String> usersView;

    @FXML
    private ListView<LobbyDTO> lobbiesView;


    /**
     * Handles successful login
     * <p>
     * If a LoginSuccessfulResponse is posted to the EventBus the loggedInUser of this client is set to the one in the
     * message received and the full list of users currently logged in is requested.
     *
     * @param message the LoginSuccessfulResponse object seen on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2019-09-05
     */
    @Subscribe
    public void loginSuccessful(LoginSuccessfulResponse message) {
        loginSuccessfulLogic(message);
    }

    /**
     * This method gets invoked by the loginSuccessful method
     *
     * @param lsr a LoginSuccessfulResponse
     * @author Marco Grawunder
     * @since 2020-12-02
     */
    public void loginSuccessfulLogic(LoginSuccessfulResponse lsr) {
        this.loggedInUser = lsr.getUser();
        userService.retrieveAllUsers();
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Handles successful lobby creation
     * <p>
     * If a LobbyCreatedMessage is detected on the event bus the retrieveAllLobbies() Method is called, resulting in the
     * update of the list of the current lobbies for the User. Further, if LOG is set as "debug" a debug message is
     * posted in the console.
     *
     * @param message the LobbyCreatedMessage detected on the event bus
     * @author Ricardo Mook, Marc Hermes
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2020-11-19
     */
    @Subscribe
    public void lobbyCreatedSuccessful(LobbyCreatedMessage message) {
        lobbyCreatedSuccessfulLogic(message);
    }

    /**
     * This method gets invoked by the lobbyCreatedSuccessful method
     *
     * @param lcm a LobbyCreatedMessage
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-11-19
     */
    public void lobbyCreatedSuccessfulLogic(LobbyCreatedMessage lcm) {
        LOG.debug("New lobby created by " + lcm.getUser().getUsername());
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Handles successful lobby dropping
     * <p>
     * If a LobbyDroppedMessage is detected on the event bus the retrieveAllLobbies() Method is called, resulting in the
     * update of the list of the current lobbies for the User. Further, if LOG is set as "debug" a debug message is
     * posted in the console.
     *
     * @param message the LobbyDroppedMessage detected on the event bus
     * @author Ricardo Mook, Marc Hermes
     * @see de.uol.swp.common.lobby.message.LobbyDroppedMessage
     * @since 2020-12-17
     */
    @Subscribe
    public void lobbyDroppedSuccessful(LobbyDroppedMessage message) {
        lobbyDroppedSuccessfulLogic(message);
    }

    /**
     * This method gets invoked by the lobbyDroppedSuccessful method
     *
     * @param ldm a LobbyDroppedMessage
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-12-17
     */
    public void lobbyDroppedSuccessfulLogic(LobbyDroppedMessage ldm) {
        LOG.debug("The lobby: " + ldm.getName() + " was dropped");
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Handles successful change in size of lobbies
     * <p>
     * If a LobbyChangedSizeMessage is detected on the event bus the retrieveAllLobbies() Method is called, resulting in
     * the update of the list of the current lobbies for the User. Further, if LOG is set as "debug" a debug message is
     * posted in the console.
     *
     * @param message the LobbyDroppedMessage detected on the event bus
     * @author Ricardo Mook, Marc Hermes
     * @see de.uol.swp.common.lobby.message.LobbySizeChangedMessage
     * @since 2020-12-18
     */
    @Subscribe
    public void lobbySizeChanged(LobbySizeChangedMessage message) {
        lobbySizeChangedLogic(message);
    }

    /**
     * This method gets invoked by the lobbySizeChanged method
     *
     * @param lscm a LobbySizeChangedMessage
     * @author Ricardo Mook, Marc Hermes
     * @since 2020-12-18
     */
    public void lobbySizeChangedLogic(LobbySizeChangedMessage lscm) {
        LOG.debug("The lobby: " + lscm.getName() + " changed it's size");
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Handles new logged in users
     * <p>
     * If a new UserLoggedInMessage object is posted to the EventBus the name of the newly logged in user is appended to
     * the user list in the main menu. Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} logged in." is displayed in the log.
     *
     * @param message the UserLoggedInMessage object seen on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.message.UserLoggedInMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void newUser(UserLoggedInMessage message) {
        newUserLogic(message);

    }

    /**
     * This method gets invoked by the newUser method
     *
     * @param ulim a UserLoggedInMessage
     * @author Marco Grawunder
     * @since 2019-08-29
     */
    public void newUserLogic(UserLoggedInMessage ulim) {
        LOG.debug("New user " + ulim.getUsername() + " logged in");
        Platform.runLater(() -> {
            if (users != null && loggedInUser != null && !loggedInUser.getUsername().equals(ulim.getUsername())) {
                users.add(ulim.getUsername());
            }
        });
    }

    /**
     * Handles new logged out users
     * <p>
     * If a new UserLoggedOutMessage object is posted to the EventBus the name of the newly logged out user is removed
     * from the user list in the main menu. Furthermore if the LOG-Level is set to DEBUG the message "User {@literal
     * <Username>} logged out." is displayed in the log.
     *
     * @param message the UserLoggedOutMessage object seen on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void userLeft(UserLoggedOutMessage message) {
        userLeftLogic(message);
    }

    /**
     * This method gets invoked by the userLeft method
     *
     * @param ulom a UserLoggedOutMessage
     * @author Marco Grawunder
     * @since 2019-08-29
     */
    public void userLeftLogic(UserLoggedOutMessage ulom) {
        LOG.debug("User " + ulom.getUsername() + " logged out");
        Platform.runLater(() -> users.remove(ulom.getUsername()));
    }

    /**
     * Handles new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names of currently logged in users are put
     * onto the user list in the main menu. Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all currently logged in users is displayed in the log.
     *
     * @param allUsersResponse the AllOnlineUsersResponse object seen on the EventBus
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-29
     */
    @Subscribe
    public void userList(AllOnlineUsersResponse allUsersResponse) {
        userListLogic(allUsersResponse);
    }

    /**
     * This method gets invoked by the userList method
     *
     * @param aour a AllOnlineUsersResponse
     * @author Marco Grawunder
     * @since 2019-08-29
     */
    public void userListLogic(AllOnlineUsersResponse aour) {
        LOG.debug("Update of user list " + aour.getUsers());
        updateUsersList(aour.getUsers());
    }

    /**
     * Handles new list of lobbies
     * <p>
     * If a new AllCreatedLobbiesResponse is posted on the eventBus, the Method updateLobbyList gets all the LobbyDTOs
     * that are in the response. The LobbyList is shown in the main menu. Furthermore if the LOG-Level is set to DEBUG
     * the message "Update of lobby list" with the names of all currently existing lobbies is displayed in the log.
     *
     * @param allCreatedLobbiesResponse the AllCreatedLobbiesResponse object seen on the Eventbus
     * @author Carsten Dekker and Marius Birk
     * @see de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse
     * @since 2020-04-12
     */

    @Subscribe
    public void lobbyList(AllCreatedLobbiesResponse allCreatedLobbiesResponse) {
        lobbyListLogic(allCreatedLobbiesResponse);
    }

    /**
     * This method gets invoked by the lobbyList method
     *
     * @param aclr a AllCreatedLobbiesResponse
     * @author Carsten Dekker and Marius Birk
     * @since 2020-04-12
     */
    public void lobbyListLogic(AllCreatedLobbiesResponse aclr) {
        LOG.debug("Update of lobby list " + aclr.getLobbyDTOs());
        updateLobbyList(aclr.getLobbyDTOs());
    }

    /**
     * Updates the chat when a ResponseChatMessage was detected on the eventBus.
     * <p>
     * If a ResponseChatMessage is detected on the eventbus, this method calls the updateChat method
     * with the ResponseChatMessage as parameter
     *
     * @param message the message that is detected on the eventBus
     * @author René Meyer
     * @since 31-11-2020
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        onResponseChatMessageLogic(message);
    }

    /**
     * This method gets invoked by the onResponseChatMessage method
     *
     * @param rcm a ResponseChatMessage
     * @author René Meyer
     * @since 31-11-2020
     */
    public void onResponseChatMessageLogic(ResponseChatMessage rcm) {
        // Only update Messages from main chat
        if (rcm.getChat().equals("main")) {
            LOG.debug("Updated chat area with new message..");
            updateChat(rcm);
        }
    }

    /**
     * Method called when a LobbyFullResponse was detected on the eventBus.
     * <p>
     * If a LobbyFullResponse was posted on the eventBus, this method will let the User know the lobby is full via
     * posting a 'Can't join lobby' message to the local chat. This action will also be logged.
     *
     * @param response the LobbyFullResponse that was detected on the eventBus
     * @author René Meyer
     * @see LobbyFullResponse
     * @since 2020-12-17
     */
    @Subscribe
    public void onLobbyFullResponse(LobbyFullResponse response) {
        onLobbyFullResponseLogic(response);
    }

    /**
     * This method gets invoked by the onLobbyFullResponse method
     *
     * @param lfr a LobbyFullResponse
     * @author René Meyer
     * @since 2020-12-17
     */
    public void onLobbyFullResponseLogic(LobbyFullResponse lfr) {
        LOG.debug("Can't join lobby " + lfr.getLobbyName() + " because the lobby is full.");
        var time = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date();
        var readableTime = time.format(resultDate);
        textArea.insertText(textArea.getLength(),
                readableTime + " SYSTEM: Can't join full lobby " + lfr.getLobbyName() + " \n");
    }

    /**
     * Method called when a WrongLobbyPasswordResponse was detected on the eventBus.
     * <p>
     * If a WrongLobbyPasswordResponse was posted on the eventBus, this method will let the User know the lobby password is wrong
     * showing a new popup with the error message. This action will also be logged.
     *
     * @param response the WrongLobbyPasswordResponse that was detected on the eventBus
     * @author René Meyer
     * @see de.uol.swp.common.user.response.lobby.WrongLobbyPasswordResponse
     * @since 2020-06-05
     */
    @Subscribe
    public void onWrongLobbyPasswordResponse(WrongLobbyPasswordResponse response) {
        onWrongPasswordResponseLogic(response);
    }

    /**
     * This method gets invoked by the onWrongLobbyPasswordResponse method
     *
     * @param lfr a WrongLobbyPasswordResponse
     * @author René Meyer
     * @since 2020-06-05
     */
    public void onWrongPasswordResponseLogic(WrongLobbyPasswordResponse lfr) {
        LOG.debug("Can't join lobby " + lfr.getLobbyName() + " because the lobby password is wrong.");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Wrong password");
            alert.setContentText("Please make sure to enter the correct password!");
            alert.showAndWait();
        });
    }

    /**
     * Method called when an AlreadyJoinedThisLobbyResponse was posted on the eventBus.
     * <p>
     * If an AlreadyJoinedThisLobbyResponse was posted on the eventBus, this method will remember the User , that
     * he already joined this lobby in another Tab.
     *
     * @param response The ResponseMessage contains the name of the lobby.
     * @author Carsten Dekker
     * @see de.uol.swp.common.lobby.response.AlreadyJoinedThisLobbyResponse
     * @since 2021-01-22
     */

    @Subscribe
    public void onAlreadyJoinedThisLobbyResponse(AlreadyJoinedThisLobbyResponse response) {
        onAlreadyJoinedThisLobbyResponseLogic(response);
    }

    /**
     * This method gets invoked by the onAlreadyJoinedThisLobbyResponse method
     *
     * @param response a AlreadyJoinedThisLobbyResponse
     * @author Carsten Dekker
     * @since 2021-01-22
     */
    public void onAlreadyJoinedThisLobbyResponseLogic(AlreadyJoinedThisLobbyResponse response) {
        LOG.debug("Can't join lobby " + response.getLobbyName() + " because the User joined this lobby already.");
        var time = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date();
        var readableTime = time.format(resultDate);
        textArea.insertText(textArea.getLength(),
                readableTime + " SYSTEM: Can't join the lobby " + response.getLobbyName() + " twice." + "\n");
    }

    /**
     * Method called when a JoinDeletedLobbyResponse was detected on the eventBus.
     * <p>
     * If a JoinDeletedLobbyResponse was posted on the eventBus, this method will let the User know the lobby was
     * deleted via posting a 'Lobby deleted' message to the local chat. This action will also be logged.
     *
     * @param response the JoinDeletedLobbyResponse that was detected on the eventBus
     * @author Sergej Tulnev, René Meyer
     * @since 2020-12-17
     */
    @Subscribe
    public void onJoinDeletedLobbyResponse(JoinDeletedLobbyResponse response) {
        onJoinDeletedLobbyResponseLogic(response);
    }

    /**
     * This method gets invoked by the onJoinDeletedLobbyResponse method
     *
     * @param jdlr a JoinDeletedLobbyResponse
     * @author Sergej Tulnev, René Meyer
     * @since 2020-12-17
     */
    public void onJoinDeletedLobbyResponseLogic(JoinDeletedLobbyResponse jdlr) {
        LOG.debug("Can't join lobby " + jdlr.getLobbyName() + " because the lobby was deleted.");
        var time = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date();
        var readableTime = time.format(resultDate);
        textArea.insertText(textArea.getLength(),
                readableTime + " SYSTEM: Can't join deleted lobby " + jdlr.getLobbyName() + " \n");
    }


    /**
     * Method called when a LobbyAlreadyExistsResponse was posted on the eventBus.
     *
     * @param message the LobbyAlreadyExistsResponse detected on the EventBus
     * @author Marius Birk
     * @since 2020-12-02
     */
    @Subscribe
    public void onLobbyAlreadyExistsMessage(LobbyAlreadyExistsResponse message) {
        onLobbyAlreadyExistsMessageLogic(message);
    }

    /**
     * This method gets invoked by the onLobbyAlreadyExistsMessage method
     *
     * @param laer a LobbyAlreadyExistsResponse
     * @author Marius Birk
     * @since 2020-12-02
     */
    public void onLobbyAlreadyExistsMessageLogic(LobbyAlreadyExistsResponse laer) {
        LOG.debug("Lobby with Name " + lobbyNameTextField.getText() + " already exists.");
        lobbyNameInvalid.setVisible(false);
        lobbyAlreadyExistsLabel.setVisible(true);
    }

    /**
     * Updates the main menus user list according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user in the list given to the main menus
     * user list. If there is no user list this it creates one.
     *
     * @param userList A list of UserDTO objects including all currently logged in users
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Marco Grawunder
     * @see de.uol.swp.common.user.UserDTO
     * @since 2019-08-29
     */
    private void updateUsersList(List<UserDTO> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (users == null) {
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u.getUsername()));
        });
    }

    /**
     * Adds the ResponseChatMessage to the textArea
     * <p>
     * The message is formatted before it gets added to the textArea.
     * The formatted message contains the username, readableTime and the message.
     *
     * @param msg the ResponseChatMessage
     * @author René Meyer
     * @see SimpleDateFormat
     * @see ResponseChatMessage
     * @since 2020-11-30
     * <p>
     * Enhanced by Sergej Tulnev
     * @since 2021-06-17
     * <p>
     * If the user has a long message, it will have a line break
     */
    private void updateChat(ResponseChatMessage msg) {
        var time = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date((long) msg.getTime().doubleValue());
        var readableTime = time.format(resultDate);
        textArea.setWrapText(true);
        textArea.insertText(textArea.getLength(),
                readableTime + " " + msg.getUsername() + ": " + msg.getMessage() + "\n");
    }

    /**
     * Updates the list of the lobbies in the main menu.
     * <p>
     * This method clears the entire lobby list and then adds a new list of lobbies.
     *
     * @param lobbyList A list of LobbyDTO objects including all existing lobbies
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Carsten Dekker and Marius Birk
     * @see de.uol.swp.common.lobby.dto.LobbyDTO
     * @since 2020-04-12
     */

    private void updateLobbyList(List<LobbyDTO> lobbyList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbies == null) {
                lobbies = FXCollections.observableArrayList();
                lobbiesView.setItems(lobbies);
            }
            lobbies.clear();
            lobbies.addAll(lobbyList);
            lobbiesView.setCellFactory(x -> new LobbyCell(lobbyService, loggedInUser));
        });
    }

    /**
     * Method called when the create lobby button is pressed
     * <p>
     * If the create lobby button is pressed, this method requests the lobby service to create a new lobby. Therefore it
     * currently uses the lobby name "test" and an user called whoever is the current logged in User that called that
     * action
     * <p>
     * <p>
     * Enhanced the Method with a query that checks if the lobbyName is blank, null or empty. If the lobbyName is one of
     * these, the lobbyNameInvalid shows up and asks for a new name. It also works with vowel mutation.
     * <p>
     * Enhanced by Marius Birk and Carsten Dekker, 2020-02-12
     * Enhanced by René Meyer for the support of password protected lobbies, 2021-06-05
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onCreateLobby() {
        if (lobbyNameTextField.getText().isBlank() || lobbyNameTextField.getText().isEmpty()
                || lobbyNameTextField.getText().startsWith(" ") || lobbyNameTextField.getText()
                .endsWith(" ") || lobbyNameTextField.getText() == null) {
            lobbyNameInvalid.setVisible(true);
            lobbyAlreadyExistsLabel.setVisible(false);
        } else {
            // If pw provided
            lobbyNameInvalid.setVisible(false);
            lobbyAlreadyExistsLabel.setVisible(false);
            if (passwordCheckBox.isSelected() && !lobbyPasswordField.getText().isEmpty()) {
                lobbyService.createNewProtectedLobby(lobbyNameTextField.getText(), (UserDTO) this.loggedInUser,
                        lobbyPasswordField.getText());
            } else {
                // lobby without pw
                lobbyService.createNewLobby(lobbyNameTextField.getText(), (UserDTO) this.loggedInUser);
            }
        }
        lobbyNameTextField.clear();
        lobbyPasswordField.clear();
        passwordCheckBox.setSelected(false);
        lobbyPasswordField.setVisible(false);
    }

    @FXML
    void onLogout() {
        userService.logout(this.loggedInUser);
    }


    /**
     * Method called when the send Message button is pressed
     * <p>
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage if this will result in an exception it logs the exception.
     *
     * @author René Meyer
     * @see de.uol.swp.client.chat.ChatService
     * @see ActionEvent
     * @since 2020-11-22
     */
    @FXML
    void onSendMessage() {
        try {
            var chatMessage = inputField.getCharacters().toString();
            // ChatID = "main" means main chat
            var chatId = "main";
            RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, loggedInUser.getUsername(),
                    System.currentTimeMillis());
            chatService.sendMessage(message);
            this.inputField.setText("");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * Method called when the settings button is pressed
     * <p>
     * This Method is called when the settings button is pressed. It posts an instance
     * of the ShowUserSettingsViewEvent to the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.event.ShowUserSettingsViewEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2021-03-04
     */
    @FXML
    private void onUserSettingsButtonPressed() {
        eventBus.post(showSettingsViewEvent);
        userSettingsService.retrieveUserMail(this.loggedInUser);
    }


    /**
     * Method called when the Game Rules button is pressed
     * <p>
     * This Method is called when the Game Rules button is pressed. It posts an instance
     * of the ShowUserGameRulesEvent to the EventBus the SceneManager is
     * subscribe to.
     *
     * @author Sergej Tulnev
     * @see de.uol.swp.client.SceneManager
     * @since 2021-05-12
     */
    @FXML
    void onGameRulesPressed() {
        eventBus.post(showGameRulesEvent);
    }


    /**
     * Method called when a GameDroppedMessage was posted on the eventBus.
     * <p>
     * If a GameDroppedMessage is detected on the eventBus the retrieveAllLobbies() Method is called, resulting in the
     * update of the list of the current lobbies for the User. Further, if LOG is set as "debug", a debug message is
     * posted in the console.
     *
     * @param message the GameDroppedMessage detected on the eventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.game.message.GameDroppedMessage
     * @since 2021-04-08
     */
    @Subscribe
    public void droppedGame(GameDroppedMessage message) {
        LOG.debug("Received GameDroppedMessage from game: " + message.getName());
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Method called when a GameStartedMessage was posted on the eventBus.
     * <p>
     * If a GameStartedMessage is detected on the eventBus the retrieveAllLobbies() Method is called, resulting in the
     * update of the list of the current lobbies for the User. Further, if LOG is set as "debug", a debug message is
     * posted in the console.
     *
     * @param message the GameStartedMessage detected on the eventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.game.message.GameStartedMessage
     * @since 2021-04-08
     */
    @Subscribe
    public void gameStarted(GameStartedMessage message) {
        LOG.debug("Received GameStartedMessage from game: " + message.getLobbyName());
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Action event for the passwordCheckBox
     * <p>
     * Triggers the lobbyPasswordField Visibility depending on the checkBox State.
     *
     * @author René Meyer
     * @since 2021-06-05
     */
    public void onLobbyPw() {
        this.lobbyPasswordField.setVisible(passwordCheckBox.isSelected());
    }
}
