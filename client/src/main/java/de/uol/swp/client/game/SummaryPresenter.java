package de.uol.swp.client.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.HelperObjects.DetailedTableStats;
import de.uol.swp.client.game.HelperObjects.GeneralTableStats;
import de.uol.swp.client.game.HelperObjects.InventoryTableStats;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.game.dto.StatsDTO;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.lobby.response.JoinOnGoingGameResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * Manages the SummaryView
 * <p>
 * Class was build exactly like GamePresenter.
 *
 * @author René Meyer, Sergej Tulnev
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-04-18
 */
@SuppressWarnings("UnstableApiUsage")
public class SummaryPresenter extends AbstractPresenter {
    public static final String fxml = "/fxml/SummaryView.fxml";
    @FXML
    public TableView<GeneralTableStats> generalTableStats;
    @FXML
    public TableView<DetailedTableStats> detailedTableStats;
    @FXML
    public TableView<InventoryTableStats> resourceTableStats;
    @FXML
    public Label winnerLabel;
    @FXML
    public ImageView winnerImage;
    @FXML
    public ImageView profileImage;
    @Inject
    private GameService gameService;
    @Inject
    private LobbyService lobbyService;
    private String gameName;

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
     * Subscribe to GameCreatedMessage to get the actual current user and corresponding gameName
     * <p>
     * This is needed so we have the currentUser and the gameName in the SummaryPresenter
     *
     * @param gcm GameCreatedMessage
     * @author René Meyer, Sergej Tulnev
     * @see GameCreatedMessage
     * @since 2021-05-01
     */
    @Subscribe
    public void onGameCreated(GameCreatedMessage gcm) {
        if (this.gameName == null) {
            this.currentUser = gcm.getUser();
            this.gameName = gcm.getName();
        }
    }

    /**
     * Subscribe to the JoinOnGoingGameResponse to get the actual current user and corresponding gameName
     *
     * @param joggr the JoinOnGoingGameResponse detected on the EventBus
     * @author Marc Hermes
     * @since 2021-06-19
     */
    @Subscribe
    public void onGameJoined(JoinOnGoingGameResponse joggr) {
        if (this.gameName == null && joggr.isJoinedSuccessful()) {
            this.currentUser = joggr.getUser();
            this.gameName = joggr.getGameName();
        }
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
        if (this.gameName != null) {
            if (this.gameName.equals(message.getStatsDTO().getGameName())) {
                this.statsDTO = message.getStatsDTO();
                setStatistics();
            }
        }
    }

    /**
     * Sets all ui elements
     * <p>
     * This method sets all ui elements for the SummaryPresenter
     * e.g. the winner label, profile picture and calls the initTable() functions to
     * initialize the tables on the fx thread
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
                winnerLabel.setText("Congratulations, you won the game " + gameName + "!");
                winnerLabel.setTextFill(Color.LIGHTGREEN);
            } else {
                this.winnerImage.setImage(new Image("/textures/summaryscreen/badge_loseScreenBg.png"));
                this.winnerImage.toBack();
                this.profileImage.setImage(new Image(profilePictureString));
                this.profileImage.toFront();
                winnerLabel.setText("Sorry, you lost the game " + gameName + " to the user " + statsDTO.getWinner());
                winnerLabel.setTextFill(Color.ORANGERED);
            }
            if ((long) generalTableStats.getItems().size() == 0) {
                initGeneralTable();
            }
            if ((long) detailedTableStats.getItems().size() == 0) {
                initDetailedTable();
            }
            if ((long) resourceTableStats.getItems().size() == 0) {
                initResourceTable();
            }
        });
    }

    /**
     * Initializes the generalTable
     * <p>
     * This method prepares the generalTable and loops the users from the game
     * to add general Stats to the first table
     *
     * @author René Meyer, Sergej Tulnev
     * @see TableColumn
     * @since 2021-05-08
     */
    private void initGeneralTable() {
        // Init Table
        TableColumn<GeneralTableStats, String> userColumn = new TableColumn<>("User - Stat");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        userColumn.setPrefWidth(200);
        TableColumn<GeneralTableStats, String> achievementColumn = new TableColumn<>("Achievement");
        achievementColumn.setPrefWidth(200);
        achievementColumn.setCellValueFactory(new PropertyValueFactory<>("achievement"));
        generalTableStats.getColumns().addAll(userColumn, achievementColumn);

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
            var achievement = "";
            if (inventory.isLargestArmy()) {
                achievement = achievement + "Largest Army";

            } else if (inventory.isLongestRoad()) {
                achievement = achievement + "Longest Road";
            } else {
                achievement = "-";
            }
            var item = new GeneralTableStats(thisUser, achievement);
            generalTableStats.getItems().add(item);
        });
        var item = new GeneralTableStats("Overall Trades", Integer.toString(statsDTO.getOverallTrades()));
        generalTableStats.getItems().add(item);
        item = new GeneralTableStats("Overall Turns", Integer.toString(statsDTO.getOverallTurns()));
        generalTableStats.getItems().add(item);
    }

    /**
     * Initializes the detailedTable
     * <p>
     * This method prepares the detailedTable and loops the users from the game
     * to add detailed Stats to the 2nd table
     *
     * @author René Meyer, Sergej Tulnev
     * @see TableColumn
     * @since 2021-05-08
     */
    private void initDetailedTable() {
        // Init Table
        TableColumn<DetailedTableStats, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        userColumn.setPrefWidth(100);
        TableColumn<DetailedTableStats, Integer> roadsColumn = new TableColumn<>("Roads");
        roadsColumn.setPrefWidth(100);
        roadsColumn.setCellValueFactory(new PropertyValueFactory<>("roads"));
        TableColumn<DetailedTableStats, Integer> knightsColumn = new TableColumn<>("Played Knights");
        knightsColumn.setCellValueFactory(new PropertyValueFactory<>("knights"));
        knightsColumn.setPrefWidth(100);
        TableColumn<DetailedTableStats, Integer> victoryPointsColumn = new TableColumn<>("VictoryPoints");
        victoryPointsColumn.setCellValueFactory(new PropertyValueFactory<>("victoryPoints"));
        victoryPointsColumn.setPrefWidth(100);
        detailedTableStats.getColumns().addAll(userColumn, roadsColumn, knightsColumn, victoryPointsColumn);

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
            var item = new DetailedTableStats(thisUser, inventory.getContinuousRoad(), inventory.getPlayedKnights(),
                    inventory.getVictoryPoints());
            detailedTableStats.getItems().add(item);
        });
    }

    /**
     * Initializes the resource Table
     * <p>
     * This method prepares the resourceTable and loops the users from the game
     * to add resource Stats to the 3rd table
     *
     * @author René Meyer, Sergej Tulnev
     * @see TableColumn
     * @since 2021-05-08
     */
    private void initResourceTable() {
        // Init Table
        TableColumn<InventoryTableStats, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        userColumn.setPrefWidth(66);
        TableColumn<InventoryTableStats, Integer> lumberColumn = new TableColumn<>("Lumber");
        lumberColumn.setPrefWidth(66);
        lumberColumn.setCellValueFactory(new PropertyValueFactory<>("lumber"));
        TableColumn<InventoryTableStats, Integer> brickColumn = new TableColumn<>("Brick");
        brickColumn.setCellValueFactory(new PropertyValueFactory<>("brick"));
        brickColumn.setPrefWidth(66);
        TableColumn<InventoryTableStats, Integer> grainColumn = new TableColumn<>("Grain");
        grainColumn.setCellValueFactory(new PropertyValueFactory<>("grain"));
        grainColumn.setPrefWidth(66);
        TableColumn<InventoryTableStats, Integer> woolColumn = new TableColumn<>("Wool");
        woolColumn.setCellValueFactory(new PropertyValueFactory<>("wool"));
        woolColumn.setPrefWidth(66);
        TableColumn<InventoryTableStats, Integer> oreColumn = new TableColumn<>("Ore");
        oreColumn.setCellValueFactory(new PropertyValueFactory<>("ore"));
        oreColumn.setPrefWidth(66);
        var columns = resourceTableStats.getColumns();
        columns.addAll(userColumn, lumberColumn, brickColumn, grainColumn, woolColumn, oreColumn);

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
            var item = new InventoryTableStats(thisUser, inventory.lumber.getNumber(), inventory.brick.getNumber(),
                    inventory.grain.getNumber(), inventory.wool.getNumber(), inventory.ore.getNumber());
            resourceTableStats.getItems().add(item);
        });
    }
}
