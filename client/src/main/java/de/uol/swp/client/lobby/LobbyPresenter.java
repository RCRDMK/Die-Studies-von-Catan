package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages the lobby menu
 * <p>
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

    private User joinedLobbyUser;

    private String currentLobby;

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
     * <p>
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage If this will result in an exception, go log the exception
     *
     * @param event The ActionEvent created by pressing the send Message button
     *
     * @author Anton, René, Sergej
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-12-06
     */
    @FXML
    void onSendMessage(ActionEvent event) {
        try {
            var chatMessage = lobbyChatInput.getCharacters().toString();
            // ChatID = gets lobby name
            var chatId = currentLobby;
            if (!chatMessage.isEmpty()) {
                RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, joinedLobbyUser.getUsername(), System.currentTimeMillis());
                chatService.sendMessage(message);
            }
            this.lobbyChatInput.setText("");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * Handles successful lobby creation
     * <p>
     * If a LobbyCreatedSuccessfulResponse is detected on the EventBus this method invokes createdSuccessfulLogic.
     *
     * @param message the LobbyCreatedSuccessfulResponse object seen on the EventBus
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse
     * @since 2020-12-02
     */
    @Subscribe
    public void createdSuccessful(LobbyCreatedSuccessfulResponse message) {
        createdSuccessfulLogic(message);
    }

    /**
     * The Method invoked by createdSuccessful()
     * <p>
     * If the currentLobby is null, meaning this is an empty LobbyPresenter that is ready to be used for a new lobby tab,
     * the parameters of this LobbyPresenter are updated to the User and Lobby given by the lcsr Response.
     * An update of the Users in the currentLobby is also requested.
     *
     * @param lcsr the LobbyCreatedSuccessfulResponse given by the original subscriber method.
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse
     * @since 2021-01-20
     */
    public void createdSuccessfulLogic(LobbyCreatedSuccessfulResponse lcsr) {
        if (this.currentLobby == null) {
            LOG.debug("Requesting update of User list in lobby because lobby was created.");
            this.joinedLobbyUser = lcsr.getUser();
            this.currentLobby = lcsr.getName();
            this.lobbyChatInput.setText("");
            lobbyChatArea.deleteText(0, lobbyChatArea.getLength());
            lobbyService.retrieveAllThisLobbyUsers(lcsr.getName());
        }
    }

    /**
     * Handles successful joining in the lobby
     * <p>
     * If a LobbyJoinedSuccessfulResponse is detected on the EventBus this method invokes userJoinedSuccessfulLogic
     *
     * @param message the LobbyJoinedSuccessfulResponse object seen on the EventBus
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.LobbyJoinedSuccessfulResponse
     * @since 2020-12-10
     */
    @Subscribe
    public void userJoinedSuccessful(LobbyJoinedSuccessfulResponse message) {
        userJoinedSuccessfulLogic(message);
    }
    /**
     * The Method invoked by userJoinedSuccessful()
     * <p>
     * If the currentLobby is null, meaning this is an empty LobbyPresenter that is ready to be used for a new lobby tab,
     * the parameters of this LobbyPresenter are updated to the User and Lobby given by the ljsr Response.
     * An update of the Users in the currentLobby is also requested.
     *
     * @param ljsr the LobbyJoinedSuccessfulResponse given by the original subscriber method.
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.LobbyJoinedSuccessfulResponse
     * @since 2021-01-20
     */
    public void userJoinedSuccessfulLogic(LobbyJoinedSuccessfulResponse ljsr) {
        if (this.currentLobby == null) {
            LOG.debug("LobbyJoinedSuccessfulResponse successfully received");
            this.joinedLobbyUser = ljsr.getUser();
            this.currentLobby = ljsr.getName();
            this.lobbyChatInput.setText("");
            lobbyChatArea.deleteText(0, lobbyChatArea.getLength());
            lobbyService.retrieveAllThisLobbyUsers(ljsr.getName());
        }
    }


    /**
     * Handles successful leaving of lobby
     * <p>
     * If a LobbyLeftSuccessfulResponse is detected on the EventBus the method userLeftSuccessfulLogic is invoked.
     *
     * @param message the LobbyLeftSuccessfulResponse object seen on the EventBus
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.LobbyLeftSuccessfulResponse
     * @since 2020-12-10
     */
    @Subscribe
    public void userLeftSuccessful(LobbyLeftSuccessfulResponse message) {
        userLeftSuccessfulLogic(message);
    }

    /**
     * Has no functionality currently, but might be used in the future.
     *
     * @param llsr the LobbyLeftSuccessfulResponse given by the original subscriber method
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.LobbyLeftSuccessfulResponse
     * @since 2021-01-20
     */
    public void userLeftSuccessfulLogic(LobbyLeftSuccessfulResponse llsr) {
    }

    /**
     * Handles successful lobby join from the user
     * <p>
     * If a UserJoinedLobbyMessage is detected on the EventBus the method joinedSuccessfulLogic is invoked.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-03
     */
    @Subscribe
    public void joinedSuccessful(UserJoinedLobbyMessage message) {
        joinedSuccessfulLogic(message);
    }

    /**
     * The Method invoked by joinedSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Message, an update of the Users in the currentLobby
     * is requested.
     *
     * @param ujlm the UserJoinedLobbyMessage given by the original subscriber method.
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2021-01-20
     */
    public void joinedSuccessfulLogic(UserJoinedLobbyMessage ujlm){
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(ujlm.getName())) {
                LOG.debug("Requesting update of User list in lobby because a User joined the lobby.");
                lobbyService.retrieveAllThisLobbyUsers(ujlm.getName());
            }
        }
    }

    /**
     * Handles successful lobby leave of the user
     * <p>
     * If a UserJoinedLobbyMessage is detected on the EventBus the method leftSuccessfulLogic is invoked.
     *
     * @param message the UserLeftLobbyMessage object seen on the EventBus
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2020-12-03
     */
    @Subscribe
    public void leftSuccessful(UserLeftLobbyMessage message) {
leftSuccessfulLogic(message);
    }

    /**
     * The Method invoked by leftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Message, an update of the Users in the currentLobby
     * is requested.
     *
     * @param ullm the UserLeftLobbyMessage given by the original subscriber method.
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-20
     */
    public void leftSuccessfulLogic(UserLeftLobbyMessage ullm){
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(ullm.getName())) {
                LOG.debug("Requesting update of User list in lobby because a User left the lobby.");
                lobbyService.retrieveAllThisLobbyUsers(ullm.getName());
            }
        }
    }

    /**
     * Handles new list of users
     * <p>
     * If a AllThisLobbyUsersResponse is detected on the EventBus the method lobbyUserListLogic is invoked.
     *
     * @param allThisLobbyUsersResponse the AllThisLobbyUsersResponse object seen on the EventBus
     *
     * @author Marc Hermes, Ricardo Mook
     * @see AllThisLobbyUsersResponse
     * @since 2020-12-02
     */
    @Subscribe
    public void lobbyUserList(AllThisLobbyUsersResponse allThisLobbyUsersResponse) {
        lobbyUserListLogic(allThisLobbyUsersResponse);
    }

    /**
     * The Method invoked by leftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Response, the method updateLobbyUsersList is invoked
     * to update the List of the Users in the currentLobby in regards to the list given by the response.
     *
     * @param atlur the AllThisLobbyUsersResponse given by the original subscriber method.
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.AllThisLobbyUsersResponse
     * @since 2021-01-20
     */
    public void lobbyUserListLogic(AllThisLobbyUsersResponse atlur){
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(atlur.getName())) {
                LOG.debug("Update of user list " + atlur.getUsers());
                updateLobbyUsersList(atlur.getUsers());
            }
        }
    }

    /**
     * Updates the lobby menu user list of the current lobby according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user in the list given to the lobby menu
     * user list. If there ist no user list this creates one.
     *
     * @param lobbyUserList A list of UserDTO objects including all currently logged in users
     *
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Marc Hermes, Ricardo Mook
     * @see de.uol.swp.common.user.UserDTO
     * @since 2020-12-02
     */
    private void updateLobbyUsersList(List<UserDTO> lobbyUserList) {
updateLobbyUsersListLogic(lobbyUserList);
    }
    public void updateLobbyUsersListLogic(List<UserDTO> l){
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyUsers == null) {
                lobbyUsers = FXCollections.observableArrayList();
                lobbyUsersView.setItems(lobbyUsers);
            }
            lobbyUsers.clear();
            l.forEach(u -> lobbyUsers.add(u.getUsername()));
        });
    }

    /**
     * Updates the lobby chat when a ResponseChatMessage was posted to the EventBus.
     * <p>
     * If a ResponseChatMessage is detected on the EventBus the method onResponseChatMessageLogic is invoked.
     *
     * @param message the ResponseChatMessage object seen on the EventBus
     *
     * @author ?
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since ?
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        onResponseChatMessageLogic(message);
    }

    /**
     * The Method invoked by leftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Response, the method updateChat is invoked
     * to update the chat of the currentLobby in regards to the input given by the response.
     *
     * @param rcm the ResponseChatMessage given by the original subscriber method.
     *
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-01-20
     */
    public void onResponseChatMessageLogic(ResponseChatMessage rcm){
        // Only update Messages from used lobby chat
        if (this.currentLobby != null) {
            if (rcm.getChat().equals(currentLobby)) {
                LOG.debug("Updated lobby chat area with new message..");
                updateChat(rcm);
            }
        }
    }

    /**
     * Adds the ResponseChatMessage to the textArea
     *
     * @param message
     */
    private void updateChat(ResponseChatMessage message) {
updateChatLogic(message);
    }
    private void updateChatLogic(ResponseChatMessage rcm){
        var time = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date((long) rcm.getTime().doubleValue());
        var readableTime = time.format(resultdate);
        lobbyChatArea.insertText(lobbyChatArea.getLength(), readableTime + " " + rcm.getUsername() + ": " + rcm.getMessage() + "\n");
    }
}
