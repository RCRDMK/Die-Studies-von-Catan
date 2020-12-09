package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.LobbyCreatedMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.client.lobby.LobbyService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Manages the lobby
 *
 * @author Ricardo Mook, Marc Hermes
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-19
 */

//Class was build exactly like MainMenuPresenter. Only ActionEvents were added
// TODO: Update the list of users in the lobbyUsersView list
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);
    public TextField lobbyChatInput;
    public TextArea lobbyChatArea;

    private ObservableList<String> lobbyUsers;

    private User joinedLobbyUser;

    private String currentLobby;

    @FXML
    private ListView<String> lobbyUsersView;

    @Inject
    private LobbyService lobbyService;

    /**
     * Übergeben der Variablen joinedLobbyUser und currentLobby
     * <p>
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
     * <p>
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

    @FXML
    public void onSendMessage(ActionEvent event) {
        //TODO:
    }
}
