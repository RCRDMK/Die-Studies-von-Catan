package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.game.GamePresenterException;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameDroppedMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the GameView
 * <p>
 * Class was build exactly like LobbyPresenter.
 *
 * @author Carsten Dekker
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-01-13
 */

public class GamePresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/GameView.fxml";

    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

    private User joinedLobbyUser;

    private String currentLobby;

    @Inject
    private GameService gameService;

    @Inject
    private LobbyService lobbyService;


    /**
     * Method called when the RollDice button is pressed
     * <p>
     * If the RollDice button is pressed,
     * this methods tries to request the GameService to send a RollDiceRequest.
     *
     * @param event The ActionEvent created by pressing the Roll Dice button
     * @author Kirstin, Pieter
     * @see de.uol.swp.client.game.GameService
     * @since 2021-01-07
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     * <p>
     * I have changed the place of the method to the new GamePresenter.
     */
    @FXML
    public void onRollDice(ActionEvent event) {
        gameService.rollDiceTest(this.currentLobby, this.joinedLobbyUser);
    }

    @FXML
    public void onTrade(ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuildStreet(ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuildSettlement(ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuildTown(ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuyDevelopmentCard(ActionEvent event) {
        //TODO:...
    }

    /**
     * Handles successful game creation
     * <p>
     * If a GameCreatedMessage is detected on the EventBus this method invokes gameStartedSuccessfulLogic.
     *
     * @param message the GameCreatedMessage object seen on the EventBus
     * @author Ricardo Mook, Alexander Losse
     * @see de.uol.swp.common.game.message.GameCreatedMessage
     * @since 2021-03-05
     */
    @Subscribe
    public void gameStartedSuccessful(GameCreatedMessage message) {
        gameStartedSuccessfulLogic(message);
    }

    /**
     * The Method invoked by gameStartedSuccessful()
     * <p>
     * If the currentLobby is null, meaning this is an empty GamePresenter that is ready to be used for a new game tab,
     * the parameters of this GamePresenter are updated to the User and Lobby given by the gcm Message.
     * An update of the Users in the currentLobby is also requested.
     *
     * @param gcm the GameCreatedMessage given by the original subscriber method.
     * @author Alexander Losse, Ricardo Mook
     * @see GameCreatedMessage
     * @since 2021-03-05
     */
    public void gameStartedSuccessfulLogic(GameCreatedMessage gcm) {
        if (this.currentLobby == null) {
            LOG.debug("Requesting update of User list in game scene because game scene was created.");
            this.joinedLobbyUser = gcm.getUser();
            this.currentLobby = gcm.getName();
        }
    }

    /**
     * Method called when the leaveGame Button is pressed
     * <p>
     * If the leaveGameButton is pressed,
     * the method tries to call the GameService method leaveGame
     * It throws a GamePresenterException if joinedLobbyUser and currentLobby are not initialised
     *
     * @param event
     * @author Ricardo Mook, Alexander Losse
     * @see de.uol.swp.client.game.GameService
     * @see de.uol.swp.client.game.GamePresenterException
     * @since 2021-03-04
     */
    @FXML
    public void onLeaveGame(ActionEvent event) {

        if (this.currentLobby != null && this.joinedLobbyUser != null) {
            lobbyService.leaveLobby(this.currentLobby, (UserDTO) this.joinedLobbyUser);
            gameService.leaveGame(this.currentLobby, this.joinedLobbyUser);
        }
        else if (this.currentLobby == null && this.joinedLobbyUser != null) {
            throw new GamePresenterException("Name of the current Lobby is not available!");
        } else {
            throw new GamePresenterException("User of the current Lobby is not available");
        }
    }


}
