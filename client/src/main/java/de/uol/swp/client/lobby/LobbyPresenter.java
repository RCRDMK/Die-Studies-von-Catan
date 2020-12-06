package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
import de.uol.swp.common.user.User;
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
 * @author Ricardo Mook, Marc Hermes
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-19
 *
 */

//Class was build exactly like MainMenuPresenter. Only ActionEvents were added
// TODO: Update the list of users in the lobbyUsersView list
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyUsers;

    private ObservableList<String> messages;

    private User joinedLobbyUser;

    @FXML
    public TextField lobbyChatInput;

    @FXML
    public TextArea lobbyChatArea;


    @Inject
    private ChatService chatService;

    /**
     * Handles join user to lobby
     *
     * If a LobbyJoinUserRequest is posted to the EventBus the joinedLobbyUser
     * of this lobby is set to the one in the message received
     *
     * Not finished yet! (the owner chats with himself)
     *
     * @param message the LobbyJoinUserRequest object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
     * @author Anton
     * @since 2020-12-06
     */

    @Subscribe
    public void onLobbyJoinUserRequest(CreateLobbyRequest message) {
        this.joinedLobbyUser = message.getOwner();
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
            lobbyChatArea.insertText(lobbyChatArea.getLength(), msg.getMessage() +"\n");
        });
    }

    /**
     * Updates the lobby chat when a ResponseChatMessage was posted to the EventBus.
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

    @FXML
    private ListView<String> lobbyUsersView;

    @FXML
    public void onStartGame(ActionEvent event) {
        //TODO:
    }

    @FXML
    public void onLeaveLobby(ActionEvent event) {
        //TODO:
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
