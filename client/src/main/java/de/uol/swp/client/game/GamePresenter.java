package de.uol.swp.client.game;

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
 *<p>
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
     * @param event The ActionEvent created by pressing the Roll Dice button
     * @author Kirstin, Pieter
     * @see de.uol.swp.client.game.GameService
     * @since 2021-01-07
     *
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     *
     * I have changed the place of the method to the new GamePresenter.
     */
    @FXML
    public void onRollDice (ActionEvent event) {
        gameService.rollDiceTest(this.currentLobby, this.joinedLobbyUser);
    }

    @FXML
    public void onTrade (ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuildStreet (ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuildSettlement (ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuildTown (ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onBuyDevelopmentCard (ActionEvent event) {
        //TODO:...
    }

    @FXML
    public void onLeaveGame(ActionEvent event) {
        if (this.currentLobby != null && this.joinedLobbyUser != null) {
            gameService.leaveGame(this.currentLobby, this.joinedLobbyUser);
        } else if (this.currentLobby == null && this.joinedLobbyUser != null) {
            throw new GamePresenterException("Name der jetzigen Lobby ist nicht vorhanden!");
        } else {
            throw new GamePresenterException("Der jetzige User ist nicht vorhanden");
        }


        if (this.currentLobby != null && this.joinedLobbyUser != null) {
            lobbyService.leaveLobby(this.currentLobby, (UserDTO) this.joinedLobbyUser);
        }
        else if (this.currentLobby == null && this.joinedLobbyUser != null) {
            throw new GamePresenterException("Name der jetzigen Lobby ist nicht vorhanden!");
        } else {
            throw new GamePresenterException("Der jetzige User ist nicht vorhanden");
        }
    }









    /**
     * Handles successful lobby creation
     * <p>
     * If a LobbyCreatedSuccessfulResponse is detected on the EventBus this method invokes createdSuccessfulLogic.
     *
     * @param message the LobbyCreatedSuccessfulResponse object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse
     * @since 2020-12-02
     */
    @Subscribe
    public void gameStartedSuccessful(GameCreatedMessage message) {
        gameStartedSuccessfulLogic(message);
    }

    /**
     * The Method invoked by createdSuccessful()
     * <p>
     * If the currentLobby is null, meaning this is an empty LobbyPresenter that is ready to be used for a new lobby tab,
     * the parameters of this LobbyPresenter are updated to the User and Lobby given by the lcsr Response.
     * An update of the Users in the currentLobby is also requested.
     *
     * @param gcm the LobbyCreatedSuccessfulResponse given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see LobbyCreatedSuccessfulResponse
     * @since 2021-01-20
     */
    public void gameStartedSuccessfulLogic(GameCreatedMessage gcm) {
        if (this.currentLobby == null) {
            LOG.debug("Requesting update of User list in lobby because lobby was created.");
            this.joinedLobbyUser = gcm.getUser();
            this.currentLobby = gcm.getName();
        }
    }
}
