package de.uol.swp.client.lobby;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.common.user.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyUsers;

    private User joinedLobbyUser;







    public void onStartGame(ActionEvent event) {
        //TODO:
    }

    public void onLeaveLobby(ActionEvent event) {
        //TODO:
    }
}
