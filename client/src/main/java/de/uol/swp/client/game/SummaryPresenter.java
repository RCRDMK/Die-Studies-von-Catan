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

    /**
     * Gets triggered when the back to main menu button gets pressed
     * <p>
     * Posts a new SummaryConfirmedMessage on the eventBus.
     * SummaryConfirmedMessage gets subscribed in SceneManager
     *
     * @param event actionEvent
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.client.SceneManager
     * @since 2021-05-01
     */
    @FXML
    public void onBackToMainMenu(ActionEvent event) {
        //@TODO: Add functionality to this logic so user actually leaves/ends game
        eventBus.post(new SummaryConfirmedMessage(this.gameName, this.currentUser));
    }

    /**
     * Subscribe to GameCreatedMessage to get the actual current user
     * <p>
     * This is needed so we have the currentUser in the SummaryPresenter
     *
     * @param gcm GameCreatedMessage
     * @author René Meyer, Sergej Tulnev
     * @see GameCreatedMessage
     * @since 2021-05-01
     */
    @Subscribe
    public void onGameCreated(GameCreatedMessage gcm) {
        this.currentUser = gcm.getUser();
    }

    /**
     * Subscribe to GameFinishedMessage to get the game object
     * <p>
     * This is needed so we have the game object in the SummaryPresenter
     * This method also calls the function setStatistics to trigger the statistics logic.
     *
     * @param message GameFinishedMessage
     * @author René Meyer, Sergej Tulnev
     * @see GameFinishedMessage
     * @since 2021-05-01
     */
    @Subscribe
    public void onGameFinishedMessage(GameFinishedMessage message) {
        this.gameName = message.GetGame().getName();
        this.game = message.GetGame();
        setStatistics();
    }

    /**
     * Sets all ui elements
     * <p>
     * This method sets all ui elements for the SummaryPresenter
     * e.g. the winner label, profile picture and calls the initTable() function to
     * initialize the statsTable on the fx thread
     *
     * @author René Meyer, Sergej Tulnev
     * @see ImageView
     * @since 2021-05-01
     */
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

    /**
     * Initializes the statsTable
     * <p>
     * This method prepares the statsTable and loops the users from the game
     * to add their stats to the table.
     *
     * @author René Meyer, Sergej Tulnev
     * @see TableColumn
     * @since 2021-05-01
     */
    private void initTable() {
        // Init Table
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
        // Loop users to add table items for stats
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

    /**
     * Gets the Winner
     * <p>
     * This function loops the users from the game and then returns the winner (player with >= 10 victory points)
     * Since this function is readonly it is safe from possible malicious attacks and client-sided to reduce server-client traffic and code readability.
     *
     * @return winner as User object
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.common.game.inventory.Inventory
     * @since 2021-05-01
     */
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
