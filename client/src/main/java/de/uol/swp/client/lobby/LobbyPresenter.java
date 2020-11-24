package de.uol.swp.client.lobby;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.user.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



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

    private User joinedLobbyUser;

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
}
