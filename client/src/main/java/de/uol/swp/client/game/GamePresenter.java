package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.game.GamePresenterException;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.game.message.GameDroppedMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse;
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
    }
}
