package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.client.lobby.LobbyService;
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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages the lobby
 *
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

    private ObservableList<String> messages;

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

    /**
     * Adds the ResponseChatMessage to the textArea
     *
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
            lobbyChatArea.insertText(lobbyChatArea.getLength(), msg.getMessage() +"\n");
        });
    }

    /**
     * Updates the lobby chat when a ResponseChatMessage was posted to the EventBus.
     *
     * @param message
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        // Only update Messages from used lobby chat
        if (message.getChat() == 1) {
            LOG.debug("Updated lobby chat area with new message..");
            updateChat(message);
        }
    }

    /**
     * Übergeben der Variablen joinedLobbyUser und currentLobby
     *
     * LobbyCreatedMessage wird abgefangen und die Variablen joinedLobbyUser und currentLobby werden übergeben
     *
     * @param message
     */
    @Subscribe
    public void lobbyCreatedSuccessful(LobbyCreatedMessage message) {
        this.joinedLobbyUser = message.getUser();
        this.currentLobby = message.getName();
    }

    /**
     * Übergeben der Variablen joinedLobbyUser und currentLobby
     *
     * UserJoinedLobbyMessage wird abgefangen und die Variablen joinedLobbyUser und currentLobby werden übergeben
     *
     * @param message
     */
    @Subscribe
    public void userJoinedSuccessful(UserJoinedLobbyMessage message) {
        this.joinedLobbyUser = message.getUser();
        this.currentLobby = message.getName();
    }
    //TODO: für User die, die nicht die Lobby erstellen muss noch eine Methode erstellt werden, die den aktuellen User und die aktuelle Lobby festlegt

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
            // ChatID = 1 means lobby chat
            var chatId = 1;
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
}
