package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.HelperObjects.StatsDTO;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.game.message.SummaryConfirmedMessage;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    @FXML
    public Label winnerLabel;
    @FXML
    public ImageView winnerImage;
    @FXML
    public ImageView profileImage;
    @FXML
    public TableView statsTable;

    private User currentUser;
    private String gameName;
    private Game game;

    @FXML
    public void onBackToMainMenu(ActionEvent event) {
        eventBus.post(new SummaryConfirmedMessage(this.gameName, this.currentUser));
    }

    @Subscribe
    public void onGameCreated(GameCreatedMessage gcm) {
        this.currentUser = gcm.getUser();
    }

    @Subscribe
    public void onGameFinishedMessage(GameFinishedMessage message) {
        this.gameName = message.GetGame().getName();
        this.game = message.GetGame();
        setStatistics();
    }

    private void setStatistics() {
        var profilePictureId = currentUser.getProfilePictureID();
        var profilePictureString = String.format("/img/profilePictures/%d.png", profilePictureId);
        Platform.runLater(() -> {
            if (this.currentUser.getUsername().equals(getWinner().getUsername())) {
                this.winnerImage.setImage(new Image("/textures/summaryscreen/badge_winScreenBg.png"));
                this.winnerImage.toBack();
                this.profileImage.setImage(new Image(profilePictureString));
                this.profileImage.toFront();
                winnerLabel.setText("Congratulations, you won!");
                winnerLabel.setTextFill(Color.LIGHTGREEN);
            } else {
                this.winnerImage.setImage(new Image("/textures/summaryscreen/badge_loseScreenBg.png"));
                this.winnerImage.toBack();
                this.profileImage.setImage(new Image(profilePictureString));
                this.profileImage.toFront();
                winnerLabel.setText("Sorry, you lost! User " + getWinner().getUsername() + " won!");
                winnerLabel.setTextFill(Color.ORANGERED);
            }
            initTable();
        });
    }

    private void initTable() {
        TableColumn userColumn = new TableColumn("User");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        userColumn.setPrefWidth(100);
        TableColumn roadsColumn = new TableColumn("Roads");
        roadsColumn.setPrefWidth(100);
        roadsColumn.setCellValueFactory(new PropertyValueFactory<>("roads"));
        TableColumn knightsColumn = new TableColumn("Knights");
        knightsColumn.setCellValueFactory(new PropertyValueFactory<>("knights"));
        knightsColumn.setPrefWidth(100);
        TableColumn victoryPointsColumn = new TableColumn("VictoryPoints");
        victoryPointsColumn.setCellValueFactory(new PropertyValueFactory<>("victoryPoints"));
        victoryPointsColumn.setPrefWidth(100);
        statsTable.getColumns().addAll(userColumn, roadsColumn, knightsColumn, victoryPointsColumn);

        // Get all User Data from game to display it in the tableView
        var usersFromGame = game.getUsersList();
        usersFromGame.forEach((user) -> {
            var inventory = game.getInventory(user);
            String thisUser;
            if (currentUser.getUsername().equals(user.getUsername())) {
                thisUser = "You";
            } else {
                thisUser = user.getUsername();
            }
            var item = new StatsDTO(thisUser, inventory.getContinuousRoad(), inventory.getPlayedKnights(), inventory.getVictoryPoints());
            statsTable.getItems().add(item);
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
