package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.game.message.SummaryConfirmedMessage;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Manages the SummaryView
 * <p>
 * Class was build exactly like GamePresenter.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-18
 */
public class SummaryPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/SummaryView.fxml";
    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

    @Inject
    public GameService gameService;
    @FXML
    public Label winnerLabel;
    private User currentUser;
    private String gameName;
    private Game game;

    @FXML
    public void onBackToMainMenu(ActionEvent event) {
        eventBus.post(new SummaryConfirmedMessage(this.gameName, this.currentUser));
    }

    @Subscribe
    public void onGameFinishedMessage(GameFinishedMessage message) {
        this.gameName = message.GetGame().getName();
        if (message.getSession().isPresent()) {
            var session = message.getSession().get();
            this.currentUser = session.getUser();
        }
        this.game = message.GetGame();
        Platform.runLater(() -> {
            winnerLabel.setText("User " + getWinner().getUsername() + " won!");
        });
    }

    private User getWinner() {
        var users = game.getUsersList();
        Optional<User> winner = users.stream().filter(user -> {
            var inventory = game.getInventory(user);
            if (inventory.getVictoryPoints() >= 10) {
                return true;
            } else {
                return false;
            }
        }).findFirst();
        return winner.get();
    }

}
