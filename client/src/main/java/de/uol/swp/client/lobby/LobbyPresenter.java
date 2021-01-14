package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.ClientApp;
import de.uol.swp.client.SceneManager;
import de.uol.swp.client.lobby.Event.ChangeToLobbyViewEvent;
import de.uol.swp.client.main.Event.ChangeToMainViewEvent;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.response.AllThisLobbyUsersResponse;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.LobbyJoinedSuccessfulResponse;
import de.uol.swp.common.user.response.LobbyLeftSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Manages the lobby menu
 *<p>
 * Class was build exactly like MainMenuPresenter. Only ActionEvents were added
 *
 * @author Ricardo Mook, Marc Hermes
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-19
 */
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyUsers;

    private List<String> joinedLobbies = new ArrayList<>();

    private User joinedLobbyUser;

    private String currentLobby;

    @FXML
    private TabPane tabs;

    private final Tab mainMenuTab = new Tab("Main Menu");

    @FXML
    public TextField lobbyChatInput;

    @FXML
    public TextArea lobbyChatArea;

    @FXML
    private ListView<String> lobbyUsersView;

    @Inject
    private LobbyService lobbyService;

    @Inject
    private ChatService chatService;

    @FXML
    public void onStartGame(ActionEvent event) {
        //TODO:
    }

    /**
     * Method called when the RollDice button is pressed
     * <p>
     * If the RollDice button is pressed,
     * this methods tries to request the lobbyService to send a RollDiceRequest.
     * @param event The ActionEvent created by pressing the send Message button
     * @author Kirstin, Pieter
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-01-07
     */
    @FXML
    public void onRollDice (ActionEvent event) {
        lobbyService.rollDiceTest(this.currentLobby, this.joinedLobbyUser);
    }


    @FXML
    public void onLeaveLobby(ActionEvent event) {
        if (this.currentLobby != null && this.joinedLobbyUser != null) {
            lobbyService.leaveLobby(this.currentLobby, (UserDTO) this.joinedLobbyUser);
        } else if (this.currentLobby == null && this.joinedLobbyUser != null) {
            throw new LobbyPresenterException("Name der jetzigen Lobby ist nicht vorhanden!");
        } else {
            throw new LobbyPresenterException("Der jetzige User ist nicht vorhanden");
        }
    }

    /**
     * Method called when the send Message button is pressed
     *
     * If the send Message button is pressed,
     * this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage
     * If this will result in an exception, go log the exception
     *
     * @param event The ActionEvent created by pressing the send Message button
     * @author Anton, René, Sergej
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-12-06
     */
    @FXML
    void onSendMessage(ActionEvent event) {
        try{
            var chatMessage = lobbyChatInput.getCharacters().toString();
            // ChatID = gets lobby name
            var chatId = currentLobby;
            if(!chatMessage.isEmpty()){
                RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, joinedLobbyUser.getUsername(), System.currentTimeMillis());
                chatService.sendMessage(message);
            }
            this.lobbyChatInput.setText("");
        }
        catch(Exception e){
            LOG.debug(e);
        }
    }


    /**
     * Handles successful lobby creation
     *<p>
     * If a LobbyCreatedSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received and the full
     * list of users currently in the lobby is requested.
     *
     * @param message the LobbyCreatedSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse
     * @author Marc Hermes
     * @since 2020-12-02
     */
    @Subscribe
    public void createdSuccessful(LobbyCreatedSuccessfulResponse message) {
        LOG.debug("Requesting update of User list in lobby because lobby was created.");
        this.joinedLobbyUser = message.getUser();
        this.currentLobby = message.getName();
        this.lobbyChatInput.setText("");
        lobbyChatArea.deleteText(0, lobbyChatArea.getLength());
        lobbyService.retrieveAllThisLobbyUsers(message.getName());
        lobbyService.addLobby(message.getName());
        this.joinedLobbies = lobbyService.getJoinedLobbies();
        System.out.println("LP " + joinedLobbies);
        updateTabs(joinedLobbies);
    }

    /**
     * Handles successful joining in the lobby
     *<p>
     * If a LobbyJoinedSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received.
     *
     * @param message the LobbyJoinedSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LobbyJoinedSuccessfulResponse
     * @author Marc Hermes
     * @since 2020-12-10
     */
    @Subscribe
    public void userJoinedSuccessful(LobbyJoinedSuccessfulResponse message) {
        LOG.debug("LobbyJoinedSuccessfulResponse successfully received");
        this.joinedLobbyUser = message.getUser();
        this.currentLobby = message.getName();
        this.lobbyChatInput.setText("");
        lobbyChatArea.deleteText(0, lobbyChatArea.getLength());
        lobbyService.addLobby(message.getName());
        this.joinedLobbies = lobbyService.getJoinedLobbies();
        updateTabs(joinedLobbies);
    }

    /**
     * Handles successful leaving of lobby
     *<p>
     * If a LobbyLeftSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received.
     *
     * @param message the LobbyLeftSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LobbyLeftSuccessfulResponse
     * @author Marc Hermes
     * @since 2020-12-10
     */
    @Subscribe
    public void userLeftSuccessful(LobbyLeftSuccessfulResponse message) {
        LOG.debug("LobbyLeftSuccessfulResponse successfully received");
        this.joinedLobbyUser = message.getUser();
        this.currentLobby = message.getName();
        lobbyService.removeLobby(message.getName());
        this.joinedLobbies = lobbyService.getJoinedLobbies();
        updateTabs(joinedLobbies);
    }

    /**
     * Handles successful lobby join from the user
     *<p>
     * If a UserJoinedLobbyMessage is posted to the EventBus the joinedLobbyUser
     * of this client is set to the one in the message received and the full
     * list of users currently in the lobby is requested.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @author Marc Hermes
     * @since 2020-12-03
     */
    @Subscribe
    public void joinedSuccessful(UserJoinedLobbyMessage message) {
        LOG.debug("Requesting update of User list in lobby because a User joined the lobby.");
        lobbyService.retrieveAllThisLobbyUsers(message.getName());
    }

    /**
     * Handles successful lobby leave of the user
     *<p>
     * If a UserLeftLobbyMessage is posted to the EventBus the joinedLobbyUser
     * of this client is set to the one in the message received and the full
     * list of users currently remaining in the lobby is requested.
     *
     * @param message the UserLeftLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @author Marc Hermes
     * @since 2020-12-03
     */
    @Subscribe
    public void leftSuccessful(UserLeftLobbyMessage message) {
        LOG.debug("Requesting update of User list in lobby because a User left the lobby.");
        lobbyService.retrieveAllThisLobbyUsers(message.getName());
    }

    /**
     * Handles new list of users
     *<p>
     * If a new AllThisLobbyUsersResponse object is posted to the EventBus the names
     * of users currently in this lobby are put onto the user list in the lobby menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all current users in the lobby is displayed in the
     * log.
     *
     * @param allThisLobbyUsersResponse the AllThisLobbyUsersResponse object seen on the EventBus
     * @see AllThisLobbyUsersResponse
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-12-02
     */
    @Subscribe
    public void lobbyUserList(AllThisLobbyUsersResponse allThisLobbyUsersResponse) {
        LOG.debug("Update of user list " + allThisLobbyUsersResponse.getUsers());
        updateLobbyUsersList(allThisLobbyUsersResponse.getUsers());
    }

    /**
     * Updates the lobby menu user list of the current lobby according to the list given
     *
     * This method clears the entire user list and then adds the name of each user
     * in the list given to the lobby menu user list. If there ist no user list
     * this creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param lobbyUserList A list of UserDTO objects including all currently logged in
     *                 users
     * @see de.uol.swp.common.user.UserDTO
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-12-02
     */
    private void updateLobbyUsersList(List<UserDTO> lobbyUserList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyUsers == null) {
                lobbyUsers = FXCollections.observableArrayList();
                lobbyUsersView.setItems(lobbyUsers);
            }
            lobbyUsers.clear();
            lobbyUserList.forEach(u -> lobbyUsers.add(u.getUsername()));
        });
    }

    private void updateTabs(List<String> joinedLobbies) {
        //Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {

            for (Iterator<Tab> iterator = tabs.getTabs().iterator(); iterator.hasNext();) {
                Tab tab = iterator.next();
                iterator.remove();
            }
            tabs.getTabs().add(mainMenuTab);
            if (!joinedLobbies.isEmpty()) {
                joinedLobbies.forEach(lobbyName -> implementNewTab(lobbyName));
            }
        });
    }

    private void changeScene() {
        if (tabs.getSelectionModel().getSelectedIndex()  == 0) {
            ChangeToMainViewEvent changeToMainViewMessage = new ChangeToMainViewEvent(joinedLobbyUser);
            eventBus.post(changeToMainViewMessage);
            System.out.println("Versuche Message zu posten. Transfer ins Main Menu.");
        }
        else if (tabs.getSelectionModel().getSelectedIndex() != 0) {
            String lobbyToChangeTo = tabs.getSelectionModel().getSelectedItem().getText();
            ChangeToLobbyViewEvent changeToLobbyViewMessage = new ChangeToLobbyViewEvent(joinedLobbyUser, lobbyToChangeTo);
            eventBus.post(changeToLobbyViewMessage);
            System.out.println("Versuche Message zu posten. Transfer in die Lobby." + lobbyToChangeTo);
        }

    }

    public void implementNewTab(String tabName) {
        Tab tab = new Tab(tabName);
        /*tab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                changeScene();
                    System.out.println("Versuche Message zu posten");
                }

    });*/
        tabs.getTabs().add(tab);
        installTabHandlers(tabs);
    }

    /**
     * looks up the styled part of tab header and installs a mouseHandler
     * which calls the work load method.
     *
     * @param tabPane
     */
    private void installTabHandlers(TabPane tabPane) {
        Set<Node> headers = tabPane.lookupAll(".tab-container");
        headers.forEach(node -> {
            // implementation detail: header of tabContainer is the TabHeaderSkin
            Parent parent = node.getParent();
            parent.setOnMouseClicked(ev -> handleHeader(parent));
        });
    }

    /**
     * Workload for tab.
     * @param tabHeaderSkin
     */
    private void handleHeader(Node tabHeaderSkin) {
        // implementation detail: skin keeps reference to associated Tab
        Tab tab = (Tab) tabHeaderSkin.getProperties().get(Tab.class);
        System.out.println("do stuff for tab: " + tab.getText());
        changeScene();
    }


    /**
     * Updates the lobby chat when a ResponseChatMessage was posted to the EventBus.
     *
     * @param message
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        // Only update Messages from used lobby chat
        if (message.getChat().equals(currentLobby)) {
            LOG.debug("Updated lobby chat area with new message..");
            updateChat(message);
        }
    }

    /**
     * Adds the ResponseChatMessage to the textArea
     *
     * @param msg
     */
    private void updateChat(ResponseChatMessage msg){
        var time =  new SimpleDateFormat("HH:mm");
        Date resultdate = new Date((long) msg.getTime().doubleValue());
        var readableTime = time.format(resultdate);
        lobbyChatArea.insertText(lobbyChatArea.getLength(), readableTime +" " +msg.getUsername() +": " + msg.getMessage() +"\n");
    }
}
