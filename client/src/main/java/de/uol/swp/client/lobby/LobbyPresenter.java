package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.user.response.AllThisLobbyUsersResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse;
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
 * Manages the lobby menu
 *
 * @author Ricardo Mook, Marc Hermes
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-19
 *
 */

//Class was build exactly like MainMenuPresenter.

public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyUsers;

    private User joinedLobbyUser;

    @Inject
    private LobbyService lobbyService;

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
     * Handles successful lobby creation
     *
     * If a LobbyCreatedSuccessfulResponse is posted to the EventBus the loggedInUser
     * of this client is set to the one in the message received and the full
     * list of users currently in the lobby is requested.
     *
     * @param message the LobbyCreatedSuccessfulResponse object seen on the EventBus
     * @see de.uol.swp.common.user.response.LobbyCreatedSuccessfulResponse
     * @since 2020-12-02
     */
    @Subscribe
    public void createdSuccessful(LobbyCreatedSuccessfulResponse message) {
        LOG.debug("Requesting update of User list in lobby because lobby was created.");
        this.joinedLobbyUser = message.getUser();
        lobbyService.retrieveAllThisLobbyUsers(message.getName());
    }

    /**
     * Handles successful lobby leave of the user
     *
     * If a UserleftLobbyMessage is posted to the EventBus the joinedLobbyUser
     * of this client is set to the one in the message received and the full
     * list of users currently remaining in the lobby is requested.
     *
     * @param message the UserLeftLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2020-12-03
     */
    @Subscribe
    public void leftSuccessful(UserLeftLobbyMessage message) {
        LOG.debug("Requesting update of User list in lobby because a User left the lobby.");
        lobbyService.retrieveAllThisLobbyUsers(message.getName());
    }

    /**
     * Handles successful lobby join from the user
     *
     * If a UserJoinedLobbyMessage is posted to the EventBus the joinedLobbyUser
     * of this client is set to the one in the message received and the full
     * list of users currently in the lobby is requested.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-03
     */
    @Subscribe
    public void joinedSuccessful(UserJoinedLobbyMessage message) {
        LOG.debug("Requesting update of User list in lobby because a User joined the lobby.");
        this.joinedLobbyUser = message.getUser();
        lobbyService.retrieveAllThisLobbyUsers(message.getName());
    }

    /**
     * Handles new list of users
     *
     * If a new AllThisLobbyUsersResponse object is posted to the EventBus the names
     * of users currently in this lobby are put onto the user list in the lobby menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
     * list" with the names of all current users in the lobby is displayed in the
     * log.
     *
     * @param allThisLobbyUsersResponse the AllThisLobbyUsersResponse object seen on the EventBus
     * @see AllThisLobbyUsersResponse
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
}
