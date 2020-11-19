package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.response.AllOnlineUsersResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


/**
 * Manages the lobby
 *
 * @author Ricardo Mook, Marc Hermes
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-19
 *
 */

public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyUsers;

    private User joinedLobbyUser;

    @FXML
    private ListView<String> lobbyUsersView;

    @Subscribe
    public void newUser(UserLoggedInMessage message) {

        LOG.debug("New user " + message.getUsername() + " joined lobby");
        Platform.runLater(() -> {
            if (lobbyUsers != null && joinedLobbyUser != null && !joinedLobbyUser.getUsername().equals(message.getUsername()))
                lobbyUsers.add(message.getUsername());
        });
    }


    @Subscribe
    public void userList(LobbyCreatedMessage lobbyCreatedMessage) {
        LOG.debug("Update of user list " + lobbyCreatedMessage.getUsers());
        updateUsersList(lobbyCreatedMessage.getUsers());
    } //TODO:

    @Subscribe
    public void userList(UserJoinedLobbyMessage userJoinedLobbyMessage) {
        LOG.debug("Update of user list " + userJoinedLobbyMessage.getUsers());

        updateUsersList(userJoinedLobbyMessage.getUsers());
    } //TODO:

    @Subscribe
    public void userList(UserLeftLobbyMessage userLeftLobbyMessage) {
        LOG.debug("Update of user list " + userLeftLobbyMessage.getUsers());
        updateUsersList(userLeftLobbyMessage.getUsers());
    }


    private void updateUsersList(List<UserDTO> userList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyUsers == null) {
                lobbyUsers = FXCollections.observableArrayList();
                lobbyUsersView.setItems(lobbyUsers);
            }
            lobbyUsers.clear();
            userList.forEach(u -> lobbyUsers.add(u.getUsername()));
        });
    }





    public void onStartGame(ActionEvent event) {
        //TODO:
    }

    public void onLeaveLobby(ActionEvent event) {
        //TODO:
    }
}
