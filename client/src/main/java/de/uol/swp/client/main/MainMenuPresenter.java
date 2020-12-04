package de.uol.swp.client.main;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.message.LobbyAlreadyExistsMessage;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 *
 */
public class MainMenuPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/MainMenuView.fxml";

    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);

    private ObservableList<String> users;

    private ObservableList<String> messages;

    private ObservableList<String> lobbies;

    private User loggedInUser;

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

    @FXML
    private ListView<String> usersView;

    @FXML
    private ListView<String> lobbiesView;

    /**
     * Handles successful login
     *
     * If a LoginSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received and the full
     * list of users currently logged in is requested.
     *
     * @param message the LoginSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2019-09-05
     */
    @Subscribe
    public void loginSuccessful(LoginSuccessfulResponse message) {
        this.loggedInUser = message.getUser();
        userService.retrieveAllUsers();
        lobbyService.retrieveAllLobbies();
    }

    @Subscribe
    public void lobbyCreatedSuccessful(LobbyCreatedMessage message) {
        LOG.debug("New lobby created by " + message.getUser().getUsername());
    }
    /**
     * Handles new logged in users
     *
     * If a new UserLoggedInMessage object is posted to the EventBus the name of the newly
     * logged in user is appended to the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} logged in." is displayed in the log.
     *
     * @param message the UserLoggedInMessage object seen on the EventBus
     * @see de.uol.swp.common.user.message.UserLoggedInMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void newUser(UserLoggedInMessage message) {

        LOG.debug("New user " + message.getUsername() + " logged in");
        Platform.runLater(() -> {
            if (users != null && loggedInUser != null && !loggedInUser.getUsername().equals(message.getUsername()))
                users.add(message.getUsername());
        });
    }

    /**
     * Handles new logged out users
     *
     * If a new UserLoggedOutMessage object is posted to the EventBus the name of the newly
     * logged out user is removed from the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "User {@literal
     * <Username>} logged out." is displayed in the log.
     *
     * @param message the UserLoggedOutMessage object seen on the EventBus
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-29
     */
    @Subscribe
    public void userLeft(UserLoggedOutMessage message) {
        LOG.debug("User " + message.getUsername() + " logged out");
        Platform.runLater(() -> users.remove(message.getUsername()));
    }

    /**
     * Handles new list of users
     *
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names
     * of currently logged in users are put onto the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all currently logged in users is displayed in the
     * log.
     *
     * @param allUsersResponse the AllOnlineUsersResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-29
     */
    @Subscribe
    public void userList(AllOnlineUsersResponse allUsersResponse) {
        LOG.debug("Update of user list " + allUsersResponse.getUsers());
        updateUsersList(allUsersResponse.getUsers());
    }

    /**
     * Handles new list of lobbies
     *
     * If a new AllCreatedLobbiesResponse is posted on the eventBus, the Method updateLobbyList gets all
     * the LobbyDTOs that are int the response.
     * The LobbyList is shown in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of lobby
     * list" with the names of all currently existing lobbies is displayed in the
     * log.
     *
     * @param allCreatedLobbiesResponse the AllCreatedLobbiesResponse object seen on the Eventbus
     * @see de.uol.swp.common.lobby.response.AllCreatedLobbiesResponse
     * @since 2020-04-12
     * @author Carsten Dekker and Marius Birk
     */

    @Subscribe
    public void lobbyList(AllCreatedLobbiesResponse allCreatedLobbiesResponse) {
        LOG.debug("Update of lobby list " + allCreatedLobbiesResponse.getLobbyDTOs());
        updateLobbyList(allCreatedLobbiesResponse.getLobbyDTOs());
    }

    /**
     * Updates the chat when a ResponseChatMessage was posted to the eventBus.
     * @param message
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message){
        // Only update Messages from main chat
        if(message.getChat() == 0){
            LOG.debug("Updated chat area with new message..");
            updateChat(message);
        }
    }

    /**
     * Method called when a LobbyAlreadyExistsMessage was posted on the eventBus.
     * @param message
     * @since 2020-12-02
     */

    @Subscribe
    public void onLobbyAlreadyExistsMessage(LobbyAlreadyExistsMessage message){
        LOG.debug("Lobby with Name "+ lobbyNameTextField.getText() + " already exists.");
        lobbyNameInvalid.setVisible(false);
        lobbyAlreadyExistsLabel.setVisible(true);
    }

    /**
     * Updates the main menus user list according to the list given
     *
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the main menus user list. If there ist no user list
     * this it creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param userList A list of UserDTO objects including all currently logged in
     *                 users
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
     * @param msg
     */
    private void updateChat(ResponseChatMessage msg){
        // Attention: This must be done on the FX Thread!
        Platform.runLater(()->{
            if(messages == null){
                messages = FXCollections.observableArrayList();
            }

            var time =  new SimpleDateFormat("HH:mm");
            Date resultdate = new Date((long) msg.getTime().doubleValue());
            var readableTime = time.format(resultdate);
            textArea.insertText(textArea.getLength(), readableTime +" " +msg.getUser() +": " + msg.getMessage() +"\n");
        });
    }

    /**
     * Updates the list of the lobbies in the main menu.
     *
     * This method clears the entire lobby list and then adds a new list of lobbies.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param lobbyList A list of UserDTO objects including all existing lobbies
     * @see de.uol.swp.common.lobby.dto.LobbyDTO
     * @since 2020-04-12
     * @author Carsten Dekker and Marius Birk
     */

    private void updateLobbyList(List<LobbyDTO> lobbyList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbies == null) {
                lobbies = FXCollections.observableArrayList();
                lobbiesView.setItems(lobbies);
            }
            lobbies.clear();
            lobbyList.forEach(u -> lobbies.add(u.getName()));
        });
    }
    /**
     * Method called when the create lobby button is pressed
     *
     * If the create lobby button is pressed, this method requests the lobby service
     * to create a new lobby. Therefore it currently uses the lobby name "test"
     * and an user called whoever is the current logged in User that called that action
     *
     *
     * Enhanced the Method with a query that checks if the lobbyName is blank, null or empty. If the lobbyName is one of these,
     * the lobbyNameInvalid shows up and asks for a new name.
     * It also works with vowel mutation.
     *
     * Enhanced by Marius Birk and Carsten Dekker, 2020-02-12
     *
     * @param event The ActionEvent created by pressing the create lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onCreateLobby(ActionEvent event) {
        String lobbyName = lobbyNameTextField.getText();
        if(lobbyService.createNewLobby(lobbyName, (UserDTO) this.loggedInUser) == false){
            lobbyAlreadyExistsLabel.setVisible(false);
            lobbyNameInvalid.setVisible(true);
        }
    }

    /**
     * Method called when the join lobby button is pressed
     *
     * If the join lobby button is pressed, this method requests the lobby service
     * to join a specified lobby. Therefore it currently uses the lobby name "test"
     * and an user called "ich"
     *
     * @param event The ActionEvent created by pressing the join lobby button
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2019-11-20
     */
    @FXML
    void onJoinLobby(ActionEvent event) {
        lobbyService.joinLobby("test", new UserDTO("ich", "", ""));
    }

    @FXML
    void onLogout(ActionEvent event){
        userService.logout(this.loggedInUser);
    }


    /**
     * Method called when the send Message button is pressed
     *
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage
     * If this will result in an exception, go log the exception
     *
     * @param event The ActionEvent created by pressing the send Message button
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-11-22
     */
    @FXML
    void onSendMessage(ActionEvent event) {
        try{
            var chatMessage = inputField.getCharacters().toString();
            // ChatID = 0 means main chat
            var chatId = 0;
            if(!chatMessage.isEmpty()){
                RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, loggedInUser.getUsername(), System.currentTimeMillis());
                chatService.sendMessage(message);
            }
            this.inputField.setText("");
        }
        catch(Exception e){
            LOG.debug(e);
        }
    }

}
