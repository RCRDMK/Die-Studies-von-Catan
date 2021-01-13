package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.user.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GamePresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/LobbyView.fxml";
    //public static final String fxml = "/fxml/GameView.fxml";

    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

    private User joinedLobbyUser;

    private String currentLobby;

    @Inject
    private GameService gameService;


    /**
     * Method called when the RollDice button is pressed
     * <p>
     * If the RollDice button is pressed,
     * this methods tries to request the lobbyService to send a RollDiceRequest.
     * @param event The ActionEvent created by pressing the send Message button
     * @author Kirstin, Pieter
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-01-07
     */
    @FXML
    public void onRollDice (ActionEvent event) {
        gameService.rollDiceTest(this.currentLobby, this.joinedLobbyUser);
    }
}
