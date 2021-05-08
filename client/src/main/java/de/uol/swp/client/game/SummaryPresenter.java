package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.HelperObjects.TableStats;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.game.dto.StatsDTO;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
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
    private GameService gameService;
    @Inject
    private LobbyService lobbyService;

    @FXML
    public Label winnerLabel;
    @FXML
    public ImageView winnerImage;
    @FXML
    public ImageView profileImage;
    @FXML
    public TableView statsTable;

    private User currentUser;
    private StatsDTO statsDTO;

    /**
     * Gets triggered when the back to main menu button is pressed
     * <p>
     * Calls the returnFromSummaryScreen function in the gameService
     * Also leaves the specific lobby and game so the game and lobby get removed
     *
     * @author René Meyer, Sergej Tulnev
     * @see de.uol.swp.client.SceneManager
     * @since 2021-05-01
     */
    @FXML
    public void onBackToMainMenu() {
        gameService.returnFromSummaryScreen(statsDTO, currentUser);
        lobbyService.leaveLobby(statsDTO.getGameName(), (UserDTO) currentUser);
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
        this.statsDTO = message.getStatsDTO();
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
            if (this.currentUser.getUsername().equals(statsDTO.getWinner())) {
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
                winnerLabel.setText("Sorry, you lost! User " + statsDTO.getWinner() + " won!");
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
        var inventories = statsDTO.getInventoryArrayList();
        // Loop users to add table items for stats
        inventories.forEach((inventory) -> {
            String thisUser;
            if (currentUser.getUsername().equals(inventory.getUser().getUsername())) {
                thisUser = "You";
            } else {
                thisUser = inventory.getUser().getUsername();
            }
            var item = new TableStats(thisUser, inventory.getContinuousRoad(), inventory.getPlayedKnights(), inventory.getVictoryPoints());
            statsTable.getItems().add(item);
        });
    }
}
