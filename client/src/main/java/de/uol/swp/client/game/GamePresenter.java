package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.game.HelperObjects.HexagonContainer;
import de.uol.swp.client.game.HelperObjects.MapGraphNodeContainer;
import de.uol.swp.client.game.HelperObjects.Vector;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.response.AllThisGameUsersResponse;
import de.uol.swp.common.game.response.GameLeftSuccessfulResponse;
import de.uol.swp.common.game.response.PlayDevelopmentCardResponse;
import de.uol.swp.common.game.response.ResolveDevelopmentCardNotSuccessfulResponse;
import de.uol.swp.common.lobby.message.JoinOnGoingGameMessage;
import de.uol.swp.common.lobby.response.JoinOnGoingGameResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages the GameView
 * <p>
 * Class was build exactly like LobbyPresenter.
 * <p>
 * enhanced by Pieter Vogt 2021-03-26
 *
 * @author Carsten Dekker
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-01-13
 */

@SuppressWarnings("UnstableApiUsage")
public class GamePresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/GameView.fxml";

    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

    @FXML
    private final TabPane tabPane = new TabPane();

    @FXML
    public TextField gameChatInput;

    @FXML
    public MenuButton buildMenu;
    @FXML
    public TextArea gameChatArea;

    public Dialog<Object> tooMuchAlert;

    public Alert chooseAlert;

    private User joinedLobbyUser;

    private String currentLobby;

    private Alert alert;

    private ButtonType buttonTypeOkay;

    private Button btnOkay;

    private ObservableList<String> gameUsers;

    private String gameFieldVariant;

    private final ArrayList<HexagonContainer> hexagonContainers = new ArrayList<>();
    private ObservableList<HashMap.Entry<String, Integer>> publicInventory1;
    private ObservableList<HashMap.Entry<String, Integer>> publicInventory2;
    private ObservableList<HashMap.Entry<String, Integer>> publicInventory3;
    private ObservableList<HashMap.Entry<String, Integer>> publicInventory4;

    private final ArrayList<MapGraphNodeContainer> mapGraphNodeContainers = new ArrayList<>();

    private Boolean itsMyTurn = false;

    private HashMap<String, Integer> privateInventory;

    @Inject
    private GameService gameService;

    @Inject
    private ChatService chatService;

    @FXML
    private Canvas canvas;

    // Used for the DevelopmentCard alerts and functionality
    private Alert resolveDevelopmentCardAlert;
    private final ImagePattern lumber = new ImagePattern(new Image("textures/resized/RES_Holz.png"));
    private final ImagePattern brick = new ImagePattern(new Image("textures/resized/RES_Lehm.png"));
    private final ImagePattern grain = new ImagePattern(new Image("textures/resized/RES_Getreide.png"));
    private final ImagePattern wool = new ImagePattern(new Image("textures/resized/RES_Wolle.png"));
    private final ImagePattern ore = new ImagePattern(new Image("textures/resized/RES_Erz.png"));
    private final ArrayList<Rectangle> resourceRectangles = new ArrayList<>();
    private String currentDevelopmentCard = "";
    private String resource1 = "";
    private String resource2 = "";
    private UUID street1 = null;
    private UUID street2 = null;
    private final Circle selectedStreet1 = new Circle();
    private final Circle selectedStreet2 = new Circle();
    private final Circle selectedResource1 = new Circle();
    private final Circle selectedResource2 = new Circle();

    private final ArrayList<ImagePattern> profilePicturePatterns = new ArrayList<>();

    private final ArrayList<Rectangle> rectangles = new ArrayList<>();
    private final ArrayList<Rectangle> rectanglesLargestArmy = new ArrayList<>();
    private final ArrayList<Rectangle> rectanglesLongestRoad = new ArrayList<>();

    @FXML
    private AnchorPane gameAnchorPane;

    @FXML
    private Label gameUserView1;
    @FXML
    private Label gameUserView2;
    @FXML
    private Label gameUserView3;
    @FXML
    private Label gameUserView4;

    @FXML
    private Button endTurnButton;

    @FXML
    private Button tradeButton;

    @FXML
    public Label buildingNotSuccessfulLabel;

    @FXML
    private Button kickPlayerOneButton;
    @FXML
    private Button kickPlayerTwoButton;
    @FXML
    private Button kickPlayerThreeButton;
    @FXML
    private Button kickPlayerFourButton;

    @FXML
    private Pane picturePlayerView1;

    @FXML
    private Pane picturePlayerView2;

    @FXML
    private Pane picturePlayerView3;

    @FXML
    private Pane picturePlayerView4;

    @FXML
    private GridPane playerOneDiceView;

    @FXML
    private GridPane playerTwoDiceView;

    @FXML
    private GridPane playerThreeDiceView;

    @FXML
    private GridPane playerFourDiceView;

    @FXML
    private Pane playerOneLargestArmyView;
    @FXML
    private Pane playerTwoLargestArmyView;
    @FXML
    private Pane playerThreeLargestArmyView;
    @FXML
    private Pane playerFourLargestArmyView;

    @FXML
    private Pane playerOneLongestRoadView;
    @FXML
    private Pane playerTwoLongestRoadView;
    @FXML
    private Pane playerThreeLongestRoadView;
    @FXML
    private Pane playerFourLongestRoadView;

    @FXML
    private Pane pricesView;


    @FXML
    private ListView<HashMap.Entry<String, Integer>> publicInventory1View;
    @FXML
    private ListView<HashMap.Entry<String, Integer>> publicInventory2View;
    @FXML
    private ListView<HashMap.Entry<String, Integer>> publicInventory3View;
    @FXML
    private ListView<HashMap.Entry<String, Integer>> publicInventory4View;

    @FXML
    private GridPane privateInventoryView;
    @FXML
    Label privateLumberLabel = new Label("0");
    @FXML
    Label privateBrickLabel = new Label("0");
    @FXML
    Label privateGrainLabel = new Label("0");
    @FXML
    Label privateWoolLabel = new Label("0");
    @FXML
    Label privateOreLabel = new Label("0");
    @FXML
    Label privateKnightCardLabel = new Label("0");
    @FXML
    Label privateMonopolyCardLabel = new Label("0");
    @FXML
    Label privateRoadBuildingCardLabel = new Label("0");
    @FXML
    Label privateYearOfPlentyCardLabel = new Label("0");
    @FXML
    Label privateVictoryPointCardLabel = new Label("0");
    @FXML
    Label privateCitiesLabel = new Label("0");
    @FXML
    Label privateRoadsLabel = new Label("0");
    @FXML
    Label privateSettlementsLabel = new Label("0");

    final private ArrayList<ImagePattern> diceImages = new ArrayList<>();

    final private Rectangle rectangleDie1 = new Rectangle(60, 60);

    final private Rectangle rectangleDie2 = new Rectangle(60, 60);

    @FXML
    private Button rollDiceButton;

    @FXML
    private Button buyDevCard;

    @FXML
    private GridPane chooseResource;

    @FXML
    private Label lumberLabelRobberMenu;
    @FXML
    private Label brickLabelRobberMenu;
    @FXML
    private Label woolLabelRobberMenu;
    @FXML
    private Label oreLabelRobberMenu;
    @FXML
    private Label grainLabelRobberMenu;
    @FXML
    private Label toDiscardLabel;

    @FXML
    private Button[] choose;

    private Rectangle robber;

    private boolean rolledDice = false;

    private boolean startingTurn;

    private int myPlayerNumber;

    private final HashMap<UUID, MapGraphNodeContainer> nodeContainerHashMap = new HashMap<>();

    /**
     * Method called when the send Message button is pressed
     * <p>
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage If this will result in an exception, go log the exception
     *
     * @author René, Sergej
     * @see de.uol.swp.client.chat.ChatService
     * @since 2021-03-08
     */
    @FXML
    void onSendMessage() {
        try {
            var chatMessage = gameChatInput.getCharacters().toString();
            // ChatID = game_lobbyName so we have separate lobby and game chat separated by id
            var chatId = "game_" + currentLobby;
            if (!chatMessage.isEmpty()) {
                RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, joinedLobbyUser.getUsername(),
                        System.currentTimeMillis());
                chatService.sendMessage(message);
            }
            this.gameChatInput.setText("");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * Updates the game chat when a ResponseChatMessage was posted to the EventBus.
     * <p>
     * If a ResponseChatMessage is detected on the EventBus the method onResponseChatMessageLogic is invoked.
     *
     * @param message the ResponseChatMessage object seen on the EventBus
     * @author René Meyer
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-03-13
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        onResponseChatMessageLogic(message);
    }

    /**
     * The Method invoked by onResponseChatMessage()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored in this
     * LobbyPresenter equals the one in the received Response, the method updateChat is invoked to update the chat of
     * the currentLobby in regards to the input given by the response.
     *
     * @param rcm the ResponseChatMessage given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-01-20
     */
    public void onResponseChatMessageLogic(ResponseChatMessage rcm) {
        // Only update Messages from used game chat
        if (this.currentLobby != null) {
            if (rcm.getChat().equals("game_" + currentLobby)) {
                LOG.debug("Updated game chat area with new message..");
                updateChat(rcm);
            }
        }
    }

    /**
     * Adds the ResponseChatMessage to the textArea
     * <p>
     * First the message gets formatted with the readableTime.
     * After the formatting the Message gets added to the textArea.
     * The formatted Message contains the username, readableTime and message
     *
     * @param rcm the ResponseChatMessage given by the original subscriber method.
     * @author René Meyer
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-03-13
     * <p>
     * Enhanced by Sergej Tulnev
     * @since 2021-06-17
     * <p>
     * If the user has a long message, it will have a line break
     */
    private void updateChat(ResponseChatMessage rcm) {
        var time = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date((long) rcm.getTime().doubleValue());
        var readableTime = time.format(resultDate);
        gameChatArea.insertText(gameChatArea.getLength(), readableTime + " " + rcm.getUsername() + ": " + rcm.getMessage() + "\n");
        gameChatArea.setWrapText(true);
    }

    /**
     * This method is called when the Trade button is pressed
     * <p>
     * When the user presses the trade button a teb appears. Within it the user can select which resources
     * he wants to trade and which amount of it. With a click on the Start a Trade button the startTrade method from the
     * GameService on the client side gets called.
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */
    @FXML
    public void onTrade() {
        this.buildMenu.setDisable(true);
        this.buyDevCard.setDisable(true);
        this.endTurnButton.setDisable(true);
        this.tradeButton.setDisable(true);
        String tradeCode = UUID.randomUUID().toString().trim().substring(0, 7);
        gameService.sendTradeStartedRequest((UserDTO) this.joinedLobbyUser, this.currentLobby, tradeCode);
    }

    /**
     * Enabled the buttons when the trade ended
     *
     * @param message TradeEndedMessage
     * @author Anton Nikiforov
     * @since 2021-05-29
     */
    @Subscribe
    public void onTradeEndedMessage(TradeEndedMessage message) {
        if (itsMyTurn) {
            this.buildMenu.setDisable(false);
            this.buyDevCard.setDisable(false);
            this.endTurnButton.setDisable(false);
            this.tradeButton.setDisable(false);
        }
    }


    /**
     * Method called when the buildRoad button is pressed.
     * <p>
     * makes all the buildings and roads visible that are occupied by players, as well as the empty roads spots.
     *
     * @author Marc Hermes
     * @since 2021-05-04
     */
    @FXML
    public void onBuildRoad() {
        Platform.runLater(() -> updatePossibleBuildingSpots(0));
    }

    /**
     * Method called when a build button is pressed
     * <p>
     * This method decides what building- or streetNode circles are displayed on the playing field.
     *
     * @param mode selects if streetNodes or buildingNodes are displayed
     * @author Carsten Dekker
     * @since 2021-06-04
     */
    public void updatePossibleBuildingSpots(int mode) {
        if (mode == 0) {
            for (MapGraphNodeContainer container : mapGraphNodeContainers) {
                if (container.getMapGraphNode().getOccupiedByPlayer() == 666 && container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                    container.getCircle().setVisible(false);
                } else if (container.getMapGraphNode().getOccupiedByPlayer() == myPlayerNumber && container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                    container.getCircle().setDisable(true);
                }
                if (container.getMapGraphNode().getOccupiedByPlayer() == myPlayerNumber && container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                    for (MapGraph.StreetNode streetNode : ((MapGraph.BuildingNode) container.getMapGraphNode()).getConnectedStreetNodes()) {
                        if (streetNode.getOccupiedByPlayer() != myPlayerNumber && streetNode.getOccupiedByPlayer() == 666) {
                            MapGraphNodeContainer container1 = nodeContainerHashMap.get(streetNode.getUuid());
                            container1.getCircle().setVisible(true);
                        }
                    }
                } else if (container.getMapGraphNode().getOccupiedByPlayer() == myPlayerNumber && container.getMapGraphNode() instanceof MapGraph.StreetNode) {
                    for (MapGraph.BuildingNode buildingNode : ((MapGraph.StreetNode) container.getMapGraphNode()).getConnectedBuildingNodes()) {
                        if (buildingNode.getOccupiedByPlayer() == 420 || buildingNode.getOccupiedByPlayer() == 666) {
                            for (MapGraph.StreetNode streetNode1 : buildingNode.getConnectedStreetNodes()) {
                                if (streetNode1.getOccupiedByPlayer() == 666) {
                                    MapGraphNodeContainer container1 = nodeContainerHashMap.get(streetNode1.getUuid());
                                    container1.getCircle().setVisible(true);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (MapGraphNodeContainer container : mapGraphNodeContainers) {
                if (container.getMapGraphNode().getOccupiedByPlayer() == 666 && container.getMapGraphNode() instanceof MapGraph.StreetNode) {
                    container.getCircle().setVisible(false);
                }
                if (container.getMapGraphNode().getOccupiedByPlayer() == 666 && container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                    for (MapGraph.StreetNode streetNode : ((MapGraph.BuildingNode) container.getMapGraphNode()).getConnectedStreetNodes()) {
                        if (streetNode.getOccupiedByPlayer() == myPlayerNumber) {
                            container.getCircle().setVisible(true);
                        }
                    }
                } else if (container.getMapGraphNode().getOccupiedByPlayer() == myPlayerNumber && container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                    container.getCircle().setDisable(false);
                }
            }
        }
    }


    /**
     * Method called when the buildSettlement button is pressed.
     * <p>
     * Makes all the buildings and streets visible that are occupied by players, as well as the empty building spots
     *
     * @author Marc Hermes
     * @since 2021-05-04
     */
    @FXML
    public void onBuildSettlement() {
        updatePossibleBuildingSpots(1);
    }

    /**
     * Method called when the buildTown button is pressed.
     * <p>
     * makes all the buildings and streets visible that are occupied by players.
     *
     * @author Marc Hermes
     * @since 2021-05-04
     */
    @FXML
    public void onBuildTown() {
        for (MapGraphNodeContainer container : mapGraphNodeContainers) {
            if (container.getMapGraphNode().getOccupiedByPlayer() == myPlayerNumber && container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                container.getCircle().setVisible(true);
                container.getCircle().setDisable(false);
            } else if (container.getMapGraphNode().getOccupiedByPlayer() == 666 || container.getMapGraphNode().getOccupiedByPlayer() == 420) {
                container.getCircle().setVisible(false);
            }
        }
    }

    @FXML
    public void onBuyDevelopmentCard() {
        gameService.buyDevelopmentCard(this.joinedLobbyUser, this.currentLobby);
    }

    /**
     * Method called when the RollDice button is pressed
     * <p>
     * If the RollDice button is pressed, this methods tries to request the GameService to send a RollDiceRequest.
     *
     * @author Kirstin, Pieter
     * @see de.uol.swp.client.game.GameService
     * @since 2021-01-07
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-01-13
     * <p>
     * I have changed the place of the method to the new GamePresenter.
     * Enhanced by Alexander Losse on 2021-05-30
     * <p>
     * remembers that the dice was rolled and changes the buttons
     */
    @FXML
    public void onRollDice() {
        if (this.currentLobby != null) {
            gameService.rollDice(this.currentLobby, (UserDTO) this.joinedLobbyUser);
            rolledDice = true;
            switchTurnPhaseButtons();
        }
    }

    /**
     * Method called when the endTurn button is pressed
     * <p>
     * Calls the gameService to send an EndTurnRequest to the server and
     * disables the visibility of the remaining possible building spots
     *
     * @author Marc Hermes
     * @since 2021-06-15
     */
    @FXML
    public void onEndTurn() {
        gameService.endTurn((UserDTO) this.joinedLobbyUser, this.currentLobby);
        for (MapGraphNodeContainer mapGraphNode : mapGraphNodeContainers) {
            if (mapGraphNode.getMapGraphNode().getOccupiedByPlayer() == 666) {
                mapGraphNode.getCircle().setVisible(false);
            }
        }

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
     * the parameters of this GamePresenter are updated to the User and Lobby given by the gcm Message. An update of the
     * Users in the currentLobby is also requested. After that the player pictures and the needed objects for the dice
     * are created in the game.
     *
     * @param gcm the GameCreatedMessage given by the original subscriber method.
     * @author Alexander Losse, Ricardo Mook
     * @see GameCreatedMessage
     * @since 2021-03-05
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-04-22
     */
    public void gameStartedSuccessfulLogic(GameCreatedMessage gcm) {
        if (this.currentLobby == null) {
            LOG.debug("Updating User list in game scene because game was created.");
            this.joinedLobbyUser = gcm.getUser();
            this.currentLobby = gcm.getName();
            this.gameFieldVariant = gcm.getGameFieldVariant();
            updateGameUsersList(gcm.getUsers(), gcm.getHumans());
            updateKickButtons(gcm.getUsers(), gcm.getHumans(), gcm.getGameOwner());
            for (int i = 1; i <= 67; i++) {
                Image image;
                image = new Image("img/profilePictures/" + i + ".png");
                ImagePattern imagePattern;
                imagePattern = new ImagePattern(image);
                profilePicturePatterns.add(imagePattern);
            }
            Platform.runLater(() -> {
                initializeMatch(gcm.getMapGraph());
                setupPlayerPictures(gcm.getUsers());
                setupResourceAlert();
                initializeRobberResourceMenu();
                setupRobberAlert();
                setupDicesAtGameStart();
                setUpPrivateInventoryView();
                setupResolveDevelopmentCardAlert();
                setupChoosePlayerAlert();
                setUpTabs();
                setUpPrices();
                setUpLargestArmyAndLongestRoadPanes(gcm.getUsers());
                setUpKickButtons(gcm.getUsers());
                updateKickButtons(gcm.getUsers(), gcm.getHumans(), gcm.getGameOwner());
            });
            evaluateMyPlayerNumber(gcm.getUsers());
            buyDevelopmentCardMessageLogic(25);
        }
    }

    /**
     * Method invoked by gameStartedSuccessfulLogic()
     * <p>
     * This method sets the turn int for this client.
     *
     * @param users the users in the game
     * @author Carsten Dekker
     * @since 2021-06-4
     */
    public void evaluateMyPlayerNumber(ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            if (joinedLobbyUser.getUsername().equals(users.get(i).getUsername())) {
                myPlayerNumber = i;
            }
        }
    }

    /**
     * When a JoinOnGoingGameResponse is detected on the EventBus this method is invoked
     * <p>
     * First the usual actions when joining a game are done to initialize the visuals of the presenter.
     * Then the method updateGameField() is called to update the mapGraphNodes that are already built by players.
     *
     * @param joggr the JoinOnGoingGameResponse detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void onJoinOnGoingGameResponse(JoinOnGoingGameResponse joggr) {
        if (this.currentLobby == null && joggr.isJoinedSuccessful()) {
            LOG.debug("Updating User list in game scene because game was joined.");
            this.joinedLobbyUser = joggr.getUser();
            this.currentLobby = joggr.getGameName();
            this.gameFieldVariant = joggr.getGameFieldVariant();
            updateGameUsersList(joggr.getUsers(), joggr.getHumans());
            updateKickButtons(joggr.getUsers(), joggr.getHumans(), joggr.getGameOwner());
            for (int i = 1; i <= 67; i++) {
                Image image;
                image = new Image("img/profilePictures/" + i + ".png");
                ImagePattern imagePattern;
                imagePattern = new ImagePattern(image);
                profilePicturePatterns.add(imagePattern);
            }
            Platform.runLater(() -> {
                initializeMatch(joggr.getMapGraph());
                setupPlayerPictures(joggr.getUsers());
                setupResourceAlert();
                initializeRobberResourceMenu();
                setupRobberAlert();
                setupDicesAtGameStart();
                setUpPrivateInventoryView();
                setupResolveDevelopmentCardAlert();
                setUpTabs();
                setUpPrices();
                setUpLargestArmyAndLongestRoadPanes(joggr.getUsers());
                setUpKickButtons(joggr.getUsers());
                updateKickButtons(joggr.getUsers(), joggr.getHumans(), joggr.getGameOwner());
                updateGameField();
            });
        }
    }


    /**
     * This method initializes the menu where the player has to choose, which resource he wants to give to the player,
     * that moved the robber.
     * <p>
     * The method initializes an array of 5 rectangles and fills it with the pictures of the resources. After that,
     * it creates 10 buttons and sets some icons, to indicate the buttons.
     * If this is complete, the method puts the buttons and rectangles into a gridPane that is shown besides the chat.
     * After this initialization the pane gets invisible and will only be shown by the TooMuchResourceCarsMessage.
     *
     * @author Marius Birk
     * @since 2021-04-19
     */
    public void initializeRobberResourceMenu() {
        this.alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        chooseResource = new GridPane();
        this.privateInventory = new HashMap<>();

        //Initialize the robber menu
        Rectangle[] resources = new Rectangle[5];
        resources[0] = new Rectangle(30, 30);
        resources[1] = new Rectangle(30, 30);
        resources[2] = new Rectangle(30, 30);
        resources[3] = new Rectangle(30, 30);
        resources[4] = new Rectangle(30, 30);

        resources[0].setFill(new ImagePattern(new Image("textures/originals/RES_Holz.png")));
        resources[1].setFill(new ImagePattern(new Image("textures/originals/RES_Lehm.png")));
        resources[2].setFill(new ImagePattern(new Image("textures/originals/RES_Getreide.png")));
        resources[3].setFill(new ImagePattern(new Image("textures/originals/RES_Wolle.png")));
        resources[4].setFill(new ImagePattern(new Image("textures/originals/RES_Erz.png")));

        choose = new Button[10];
        for (int i = 0; i < choose.length; i++) {
            choose[i] = new Button();
            if (i <= 4) {
                Rectangle imageView = new Rectangle(30, 30);
                imageView.setFill(new ImagePattern(new Image("img/icons/arrow_up.png")));
                choose[i].setGraphic(imageView);
            }
            if (i >= 5) {
                Rectangle imageView = new Rectangle(30, 30);
                imageView.setFill(new ImagePattern(new Image("img/icons/arrow_down.png")));
                choose[i].setGraphic(imageView);
            }
        }
        for (int i = 0; i <= 4; i++) {
            chooseResource.add(choose[i], i, 0);
            chooseResource.add(resources[i], i, 1);
            chooseResource.add(choose[5 + i], i, 3);
        }
        lumberLabelRobberMenu = new Label();
        brickLabelRobberMenu = new Label();
        grainLabelRobberMenu = new Label();
        woolLabelRobberMenu = new Label();
        oreLabelRobberMenu = new Label();
        toDiscardLabel = new Label();

        chooseResource.add(lumberLabelRobberMenu, 0, 2);
        chooseResource.add(brickLabelRobberMenu, 1, 2);
        chooseResource.add(grainLabelRobberMenu, 2, 2);
        chooseResource.add(woolLabelRobberMenu, 3, 2);
        chooseResource.add(oreLabelRobberMenu, 4, 2);
        chooseResource.add(new Label("Amount of Cards to discard:"), 0, 4, 3, 1);
        chooseResource.add(toDiscardLabel, 3, 4);

        chooseResource.setVgap(40);
        chooseResource.setHgap(30);

        initializedResourceButtons();

    }

    public void initializedResourceButtons() {
        choose[0].setOnAction(event -> {
            if (Integer.parseInt(lumberLabelRobberMenu.getText()) > 0) {
                if (Integer.parseInt(toDiscardLabel.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) - 1));
                    lumberLabelRobberMenu.setText(Integer.toString(Integer.parseInt(lumberLabelRobberMenu.getText()) - 1));
                }
            }
        });

        choose[1].setOnAction(event -> {
            if (Integer.parseInt(brickLabelRobberMenu.getText()) > 0) {
                if (Integer.parseInt(toDiscardLabel.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) - 1));
                    brickLabelRobberMenu.setText(Integer.toString(Integer.parseInt(brickLabelRobberMenu.getText()) - 1));
                }
            }
        });

        choose[2].setOnAction(event -> {
            if (Integer.parseInt(grainLabelRobberMenu.getText()) > 0) {
                if (Integer.parseInt(toDiscardLabel.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) - 1));
                    grainLabelRobberMenu.setText(Integer.toString(Integer.parseInt(grainLabelRobberMenu.getText()) - 1));
                }
            }
        });

        choose[3].setOnAction(event -> {
            if (Integer.parseInt(woolLabelRobberMenu.getText()) > 0) {
                if (Integer.parseInt(toDiscardLabel.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) - 1));
                    woolLabelRobberMenu.setText(Integer.toString(Integer.parseInt(woolLabelRobberMenu.getText()) - 1));
                }
            }
        });

        choose[4].setOnAction(event -> {
            if (Integer.parseInt(oreLabelRobberMenu.getText()) > 0) {
                if (Integer.parseInt(toDiscardLabel.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) - 1));
                    oreLabelRobberMenu.setText(Integer.toString(Integer.parseInt(oreLabelRobberMenu.getText()) - 1));
                }
            }
        });


        choose[5].setOnAction(event -> {
            if (privateInventory.get("Lumber") > Integer.parseInt(lumberLabelRobberMenu.getText())) {
                toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) + 1));
                lumberLabelRobberMenu.setText(Integer.toString(Integer.parseInt(lumberLabelRobberMenu.getText()) + 1));
            }
        });

        choose[6].setOnAction(event -> {
            if (privateInventory.get("Brick") > Integer.parseInt(brickLabelRobberMenu.getText())) {
                toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) + 1));
                brickLabelRobberMenu.setText(Integer.toString(Integer.parseInt(brickLabelRobberMenu.getText()) + 1));
            }
        });

        choose[7].setOnAction(event -> {
            if (privateInventory.get("Grain") > Integer.parseInt(grainLabelRobberMenu.getText())) {
                toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) + 1));
                grainLabelRobberMenu.setText(Integer.toString(Integer.parseInt(grainLabelRobberMenu.getText()) + 1));
            }
        });

        choose[8].setOnAction(event -> {
            if (privateInventory.get("Wool") > Integer.parseInt(woolLabelRobberMenu.getText())) {
                toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) + 1));
                woolLabelRobberMenu.setText(Integer.toString(Integer.parseInt(woolLabelRobberMenu.getText()) + 1));
            }
        });

        choose[9].setOnAction(event -> {
            if (privateInventory.get("Ore") > Integer.parseInt(oreLabelRobberMenu.getText())) {
                toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText()) + 1));
                oreLabelRobberMenu.setText(Integer.toString(Integer.parseInt(oreLabelRobberMenu.getText()) + 1));
            }
        });
    }

    /**
     * Handles the creation of the profile pictures at game start
     * <p>
     * At the start of a game this method gets called. It uses an arrayList, containing all the users in the game,
     * and creates the right profile picture for each user.
     *
     * @param list an ArrayList with UserDTOs
     * @author Carsten Dekker
     * @since 2021-04-18
     */
    public void setupPlayerPictures(ArrayList<User> list) {
        for (User user : list) {
            Rectangle rectangle = new Rectangle(90, 90);
            rectangle.setFill(profilePicturePatterns.get(user.getProfilePictureID() - 1));
            rectangles.add(rectangle);
        }
        picturePlayerView1.getChildren().add(rectangles.get(0));
        picturePlayerView2.getChildren().add(rectangles.get(1));
        if (rectangles.size() > 2)
            picturePlayerView3.getChildren().add(rectangles.get(2));
        if (rectangles.size() > 3)
            picturePlayerView4.getChildren().add(rectangles.get(3));
    }

    /**
     * This method is invoked if a TooMuchResourcesMessage is send to the client.
     * <p>
     * First a alert is instantiated and the content text and the title are set.
     * Now the amount of cards, that need to be discarded are set and the labels of the resources are set.
     * After that the method checks if the buttons, which are needed to select, which resource the player wants to discard, are disabled
     * or not.
     * If the user clicked "OK" all values from the labels will be put into an HashMap and are send to the server.
     *
     * @author Marius Birk
     * @since 2021-04-19
     */
    public void showRobberResourceMenu(TooMuchResourceCardsMessage tooMuchResourceCardsMessage) {
        if (tooMuchAlert == null || !tooMuchAlert.isShowing()) {
            tooMuchAlert = new Dialog<>();
            tooMuchAlert.initStyle(StageStyle.UNDECORATED);

            Rectangle2D center = Screen.getPrimary().getVisualBounds();
            tooMuchAlert.setX(center.getWidth() / 4);
            tooMuchAlert.setY(center.getHeight() / 3);
            Platform.setImplicitExit(false);

            tooMuchAlert.setOnCloseRequest(windowEvent -> {
                if (Integer.parseInt(toDiscardLabel.getText()) == 0) {
                    HashMap<String, Integer> inventory = new HashMap<>();
                    inventory.put("Lumber", Integer.parseInt(lumberLabelRobberMenu.getText()));
                    inventory.put("Brick", Integer.parseInt(brickLabelRobberMenu.getText()));
                    inventory.put("Grain", Integer.parseInt(grainLabelRobberMenu.getText()));
                    inventory.put("Wool", Integer.parseInt(woolLabelRobberMenu.getText()));
                    inventory.put("Ore", Integer.parseInt(oreLabelRobberMenu.getText()));

                    if (itsMyTurn && rolledDice) {
                        tradeButton.setDisable(false);
                        buildMenu.setDisable(false);
                        buyDevCard.setDisable(false);
                        endTurnButton.setDisable(false);
                    }
                    if (itsMyTurn && !rolledDice) {
                        rollDiceButton.setDisable(false);
                    }
                    gameService.discardResources(this.currentLobby, this.joinedLobbyUser, inventory);
                } else {
                    windowEvent.consume();
                }
            });

            tooMuchAlert.setHeaderText("Choose the resources you want to discard in the " + tooMuchResourceCardsMessage.getName() + " lobby!");
            tooMuchAlert.setTitle(tooMuchResourceCardsMessage.getName());
            toDiscardLabel.setText(Integer.toString(tooMuchResourceCardsMessage.getCards()));
            tooMuchAlert.getDialogPane().getButtonTypes().add(new ButtonType("Send"));
            tooMuchAlert.getDialogPane().setContent(chooseResource);

            this.privateInventory.remove("Lumber");
            this.privateInventory.remove("Brick");
            this.privateInventory.remove("Grain");
            this.privateInventory.remove("Wool");
            this.privateInventory.remove("Ore");


            this.privateInventory.put("Lumber", tooMuchResourceCardsMessage.getInventory().get("Lumber"));
            this.privateInventory.put("Brick", tooMuchResourceCardsMessage.getInventory().get("Brick"));
            this.privateInventory.put("Grain", tooMuchResourceCardsMessage.getInventory().get("Grain"));
            this.privateInventory.put("Wool", tooMuchResourceCardsMessage.getInventory().get("Wool"));
            this.privateInventory.put("Ore", tooMuchResourceCardsMessage.getInventory().get("Ore"));


            if (privateInventory.get("Lumber") != 0) {
                choose[0].setDisable(false);
                choose[5].setDisable(false);
            } else {
                choose[0].setDisable(true);
                choose[5].setDisable(true);
            }
            if (privateInventory.get("Brick") != 0) {
                choose[1].setDisable(false);
                choose[6].setDisable(false);
            } else {
                choose[1].setDisable(true);
                choose[6].setDisable(true);
            }
            if (privateInventory.get("Grain") != 0) {
                choose[2].setDisable(false);
                choose[7].setDisable(false);
            } else {
                choose[2].setDisable(true);
                choose[7].setDisable(true);
            }
            if (privateInventory.get("Wool") != 0) {
                choose[3].setDisable(false);
                choose[8].setDisable(false);
            } else {
                choose[3].setDisable(true);
                choose[8].setDisable(true);
            }
            if (privateInventory.get("Ore") != 0) {
                choose[4].setDisable(false);
                choose[9].setDisable(false);
            } else {
                choose[4].setDisable(true);
                choose[9].setDisable(true);
            }

            lumberLabelRobberMenu.setText(Integer.toString(privateInventory.get("Lumber")));
            brickLabelRobberMenu.setText(Integer.toString(privateInventory.get("Brick")));
            grainLabelRobberMenu.setText(Integer.toString(privateInventory.get("Grain")));
            woolLabelRobberMenu.setText(Integer.toString(privateInventory.get("Wool")));
            oreLabelRobberMenu.setText(Integer.toString(privateInventory.get("Ore")));

            Window window = tooMuchAlert.getDialogPane().getScene().getWindow();
            window.setOnCloseRequest(Event::consume);

            tooMuchAlert.initModality(Modality.APPLICATION_MODAL);
            tooMuchAlert.show();
            tradeButton.setDisable(true);
            rollDiceButton.setDisable(true);
            buildMenu.setDisable(true);
            buyDevCard.setDisable(true);
            endTurnButton.setDisable(true);
        } else {
            lumberLabelRobberMenu.setText(Integer.toString(privateInventory.get("Lumber")));
            brickLabelRobberMenu.setText(Integer.toString(privateInventory.get("Brick")));
            grainLabelRobberMenu.setText(Integer.toString(privateInventory.get("Grain")));
            woolLabelRobberMenu.setText(Integer.toString(privateInventory.get("Wool")));
            oreLabelRobberMenu.setText(Integer.toString(privateInventory.get("Ore")));
            toDiscardLabel.setText(String.valueOf((tooMuchResourceCardsMessage.getCards())));
        }
    }


    /**
     * Handles successful leaving of game
     * <p>
     * If a GameLeftSuccessfulResponse is detected on the EventBus the method gameLeftSuccessfulLogic is invoked.
     *
     * @param glsr the GameLeftSuccessfulResponse object seen on the EventBus
     * @author Marc Hermes
     * @see GameLeftSuccessfulResponse
     * @since 2021-03-15
     */
    @Subscribe
    public void gameLeftSuccessful(GameLeftSuccessfulResponse glsr) {
        gameLeftSuccessfulLogic(glsr);
    }

    /**
     * Changes the click-ability of the button for ending your turn.
     *
     * <p>This method checks, if the the games name equals the name of the game in the message. If so, and if you are
     * the player with the current turn (transported in message), your button for ending your turn gets clickable. If
     * not, it becomes un-clickable. It also invokes the passTheDice method and manages the visibility of the diceViews.</p>
     *
     * @param message the NextTurnMessage detected on the EventBus
     * @author Pieter Vogt
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-04-30
     * <p>
     * Enhanced by Alexander Losse on 2021-05-30
     */
    @Subscribe
    public void nextPlayerTurn(NextTurnMessage message) {
        if (message.getGameName().equals(currentLobby)) {
            rolledDice = false;
            if (message.getPlayerWithCurrentTurn().equals(joinedLobbyUser.getUsername())) {
                startingTurn = message.isInStartingTurn();
                itsMyTurn = true;

            } else {
                itsMyTurn = false;
            }
            switchTurnPhaseButtons();
            if (!message.isInStartingTurn()) {
                if (message.getTurn() == 0) {
                    passTheDice(playerFourDiceView, playerOneDiceView);
                    playerOneDiceView.setVisible(true);
                    playerFourDiceView.setVisible(false);
                } else if (message.getTurn() == 1) {
                    passTheDice(playerOneDiceView, playerTwoDiceView);
                    playerOneDiceView.setVisible(false);
                    playerTwoDiceView.setVisible(true);
                } else if (message.getTurn() == 2) {
                    passTheDice(playerTwoDiceView, playerThreeDiceView);
                    playerTwoDiceView.setVisible(false);
                    playerThreeDiceView.setVisible(true);
                } else if (message.getTurn() == 3) {
                    passTheDice(playerThreeDiceView, playerFourDiceView);
                    playerThreeDiceView.setVisible(false);
                    playerFourDiceView.setVisible(true);
                }
            } else {
                endTurnButton.setDisable(true);
            }
        }
    }

    /**
     * The method invoked by gameLeftSuccessful()
     * <p>
     * If the Game is left, meaning this Game Presenter is no longer needed, this presenter will no longer be registered
     * on the event bus and no longer be reachable for responses, messages etc.
     *
     * @param glsr the GameLeftSuccessfulResponse given by the original subscriber method
     * @author Marc Hermes
     * @see GameLeftSuccessfulResponse
     * @since 2021-03-15
     */
    public void gameLeftSuccessfulLogic(GameLeftSuccessfulResponse glsr) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(glsr.getName())) {
                this.currentLobby = null;
                clearEventBus();
            }
        }
    }


    /**
     * Method called when the leaveGame Button is pressed
     * <p>
     * If the leaveGameButton is pressed, the method tries to call the GameService method leaveGame It throws a
     * GamePresenterException if joinedLobbyUser and currentLobby are not initialised
     *
     * @author Ricardo Mook, Alexander Losse
     * @see de.uol.swp.client.game.GameService
     * @see de.uol.swp.client.game.GamePresenterException
     * @since 2021-03-04
     */
    @FXML
    public void onLeaveGame() {

        if (this.currentLobby != null && this.joinedLobbyUser != null) {
            gameService.leaveGame(this.currentLobby, this.joinedLobbyUser);
        } else if (this.currentLobby == null && this.joinedLobbyUser != null) {
            throw new GamePresenterException("Name of the current Lobby is not available!");
        } else {
            throw new GamePresenterException("User of the current Lobby is not available");
        }
    }

    /**
     * Handles successful game leave of the user
     * <p>
     * If a UserLeftGameMessage is detected on the EventBus the method otherUserLeftSuccessfulLogic is invoked.
     *
     * @param message the UserLeftGameMessage object seen on the EventBus
     * @author Iskander Yusupov
     * @see de.uol.swp.common.game.message.UserLeftGameMessage
     * @since 2021-03-17
     */
    @Subscribe
    public void otherUserLeftSuccessful(UserLeftGameMessage message) {
        otherUserLeftSuccessfulLogic(message);
    }

    /**
     * The Method invoked by otherUserLeftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty GamePresenter and the game/lobby name stored in
     * this GamePresenter equals the one in the received Message, an update of the Users in the currentLobby(current
     * game) is requested.
     *
     * @param ulgm the UserLeftGameMessage given by the original subscriber method.
     * @author Iskander Yusupov
     * @see de.uol.swp.common.game.message.UserLeftGameMessage
     * @since 2021-03-17
     */
    public void otherUserLeftSuccessfulLogic(UserLeftGameMessage ulgm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(ulgm.getName())) {
                LOG.debug("Requesting update of User list in lobby because a User left the lobby.");
                gameService.retrieveAllThisGameUsers(ulgm.getName());
            }
        }
    }

    @Subscribe
    public void gameUserList(AllThisGameUsersResponse allThisGameUsersResponse) {
        gameUserListLogic(allThisGameUsersResponse);
    }

    /**
     * The Method invoked by gameUserList()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored in this
     * GamePresenter equals the one in the received Response, the method updateGameUsersList is invoked to update the
     * List of the Users in the currentLobby in regards to the list given by the response.
     *
     * @param atgur the AllThisLobbyUsersResponse given by the original subscriber method.
     * @author Iskander Yusupov
     * @see AllThisGameUsersResponse
     * @since 2021-03-14
     */
    public void gameUserListLogic(AllThisGameUsersResponse atgur) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(atgur.getName())) {
                LOG.debug("Update of user list " + atgur.getUsers());
                updateGameUsersList(atgur.getUsers(), atgur.getHumanUsers());
                updateKickButtons(atgur.getUsers(), atgur.getHumanUsers(), atgur.getGameOwner());
            }
        }
    }

    /**
     * When a JoinOnGoingGameMessage is detected on the EventBus this method is invoked
     * <p>
     * If the currentLobby is not null, meaning this is not an empty presenter and the currentLobby equals
     * one in the JoinOnGoingGameMessage an update of the users in this presenter is done
     *
     * @param joggm the JoinOnGoingGameMessage detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void onJoinOnGoingGameMessage(JoinOnGoingGameMessage joggm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(joggm.getName())) {
                LOG.debug("The user " + joggm.getUser().getUsername() + " joined the game!");
                updateGameUsersList(joggm.getUsers(), joggm.getHumans());
                updateKickButtons(joggm.getUsers(), joggm.getHumans(), joggm.getGameOwner());
            }
        }
    }


    /**
     * Updates the game menu user list of the current game according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user in the list given to the game menu
     * user list. If there is no user list this creates one.
     * <p>
     * enhanced by Iskander Yusupov, 2021-06-04
     * enhanced by Marc Hermes, 2021-05-27
     *
     * @param l      A list of User objects including all users in the game
     * @param humans a set of Users that contains all human users in the game
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Iskander Yusupov, Marc Hermes, Ricardo Mook
     * @see de.uol.swp.common.user.UserDTO
     * @since 2020-03-14
     */
    public void updateGameUsersList(ArrayList<User> l, Set<User> humans) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (gameUsers == null) {
                gameUsers = FXCollections.observableArrayList();
            }
            gameUsers.clear();
            l.forEach(u -> {
                if (humans.contains(u)) {
                    gameUsers.add(u.getUsername());
                } else {
                    gameUsers.add(u.getUsername() + " (KI)");
                }
            });
            gameUserView1.setText(gameUsers.get(0));
            gameUserView1.setAlignment(Pos.CENTER);
            gameUserView2.setText(gameUsers.get(1));
            gameUserView2.setAlignment(Pos.CENTER);
            if (gameUsers.size() > 2) {
                gameUserView3.setText(gameUsers.get(2));
                gameUserView3.setAlignment(Pos.CENTER);
                gameUserView3.setVisible(true);
            }
            if (gameUsers.size() > 3) {
                gameUserView4.setText(gameUsers.get(3));
                gameUserView4.setAlignment(Pos.CENTER);
                gameUserView4.setVisible(true);
            }
        });
    }

    /**
     * @param list
     * @author Iskander Yusupov
     * @since 2021-06-21
     */
    public void setUpKickButtons(ArrayList<User> list) {
        Image kickIcon1 = new Image("textures/resized/Kick_Icon.jpg");
        ImageView kickView1 = new ImageView(kickIcon1);
        kickView1.setFitHeight(25);
        kickView1.setPreserveRatio(true);
        kickPlayerOneButton.setPrefSize(25, 25);
        kickPlayerOneButton.setGraphic(kickView1);
        kickPlayerOneButton.setAlignment(Pos.CENTER);
        kickPlayerOneButton.getStyleClass().add("kick-button");
        Tooltip hoverKickPlayerOne = new Tooltip("");

        hoverKickPlayerOne.setText("Kick Player One");
        Tooltip.install(kickPlayerOneButton, hoverKickPlayerOne);
        hoverKickPlayerOne.setShowDelay(Duration.millis(0));
        Image kickIcon2 = new Image("textures/resized/Kick_Icon.jpg");
        ImageView kickView2 = new ImageView(kickIcon2);
        kickView2.setFitHeight(25);
        kickView2.setPreserveRatio(true);
        kickPlayerTwoButton.setPrefSize(25, 25);
        kickPlayerTwoButton.setGraphic(kickView2);
        kickPlayerTwoButton.setAlignment(Pos.CENTER);
        kickPlayerTwoButton.getStyleClass().add("kick-button");
        Tooltip hoverKickPlayerTwo = new Tooltip("");

        hoverKickPlayerTwo.setText("Kick Player Two");
        Tooltip.install(kickPlayerTwoButton, hoverKickPlayerTwo);
        hoverKickPlayerTwo.setShowDelay(Duration.millis(0));
        if (list.size() > 2) {
            Image kickIcon3 = new Image("textures/resized/Kick_Icon.jpg");
            ImageView kickView3 = new ImageView(kickIcon3);
            kickView3.setFitHeight(25);
            kickView3.setPreserveRatio(true);
            kickPlayerThreeButton.setPrefSize(25, 25);
            kickPlayerThreeButton.setGraphic(kickView3);
            kickPlayerThreeButton.setAlignment(Pos.CENTER);
            kickPlayerThreeButton.getStyleClass().add("kick-button");
            Tooltip hoverKickPlayerThree = new Tooltip("");

            hoverKickPlayerThree.setText("Kick Player Three");
            Tooltip.install(kickPlayerThreeButton, hoverKickPlayerThree);
            hoverKickPlayerThree.setShowDelay(Duration.millis(0));
        }
        if (list.size() > 3) {
            Image kickIcon4 = new Image("textures/resized/Kick_Icon.jpg");
            ImageView kickView4 = new ImageView(kickIcon4);
            kickView4.setFitHeight(25);
            kickView4.setPreserveRatio(true);
            kickPlayerFourButton.setPrefSize(25, 25);
            kickPlayerFourButton.setGraphic(kickView4);
            kickPlayerFourButton.setAlignment(Pos.CENTER);
            kickPlayerFourButton.getStyleClass().add("kick-button");
            Tooltip hoverKickPlayerFour = new Tooltip("");

            hoverKickPlayerFour.setText("Kick Player Four");
            Tooltip.install(kickPlayerFourButton, hoverKickPlayerFour);
            hoverKickPlayerFour.setShowDelay(Duration.millis(0));
        }
    }

    /**
     * @param list
     * @param humans
     * @param gameOwner
     * @author Iskander Yusupov
     * @since 2021-06-21
     */
    public void updateKickButtons(ArrayList<User> list, Set<User> humans, User gameOwner) {
        if (joinedLobbyUser == gameOwner) {
            kickPlayerOneButton.setVisible(true);
            if (!humans.contains(list.get(0)) || list.get(0) == gameOwner) {
                kickPlayerOneButton.setDisable(true);
            } else {
                kickPlayerOneButton.setDisable(false);
            }
            kickPlayerTwoButton.setVisible(true);
            if (!humans.contains(list.get(1)) && list.get(1) == gameOwner) {
                kickPlayerTwoButton.setDisable(true);
            } else {
                kickPlayerTwoButton.setDisable(false);
            }
            if (list.size() > 2) {
                kickPlayerThreeButton.setVisible(true);
                if (!humans.contains(list.get(2)) && list.get(2) == gameOwner) {
                    kickPlayerThreeButton.setDisable(true);
                } else {
                    kickPlayerThreeButton.setDisable(false);
                }
            }
            if (list.size() > 3) {
                kickPlayerFourButton.setVisible(true);
                if (!humans.contains(list.get(3)) && list.get(3) == gameOwner) {
                    kickPlayerFourButton.setDisable(true);
                } else {
                    kickPlayerFourButton.setDisable(false);
                }
            }
        } else {
            kickPlayerOneButton.setVisible(false);
            kickPlayerTwoButton.setVisible(false);
            kickPlayerThreeButton.setVisible(false);
            kickPlayerFourButton.setVisible(false);
        }
    }

    /**
     * This method holds the size of the terrainFields in pixels.
     * <p>
     * The card size is not a fixed value, because if the canvas becomes scalable in a future update, the cards need to
     * scale with it. So if we start to work with textures, they need to be scaled as well. Slight modifications to this
     * method can do this.
     *
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    public double cardSize() {
        double d = Math.min(canvas.getHeight(), canvas.getWidth()); //Determine minimum pixels in height and length of the canvas (we don't want the game field to scale out of canvas, so we orient at the smaller axis)
        if (!gameFieldVariant.equals("VeryRandom")) {
            return d / 5.5; // Divide by 8 because the game field is 7 cards wide and add 1/2 card each side for margin so the cards don't touch the boundaries of the canvas.
        } else return d / 7;
    }

    /**
     * Determines the paint (image) to draw its host-object.
     *
     * @return the paint for the host-object
     * @author Marc Hermes
     * @since 2021-04-28
     */
    public Paint determinePictureOfTerrain(MapGraph.Hexagon h) {
        ImagePattern imagePattern;
        Paint paint;
        //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
        switch (h.getTerrainType()) {
            case 1:
                imagePattern = new ImagePattern(new Image("textures/resized/HEX_Wald.png"));
                paint = imagePattern;
                break;
            case 2:
                imagePattern = new ImagePattern(new Image("textures/resized/HEX_Felder.png"));
                paint = imagePattern;
                break;
            case 3:
                imagePattern = new ImagePattern(new Image("textures/resized/HEX_Weideland.png"));
                paint = imagePattern;
                break;
            case 4:
                imagePattern = new ImagePattern(new Image("textures/resized/HEX_Lehm.png"));
                paint = imagePattern;
                break;
            case 5:
                imagePattern = new ImagePattern(new Image("textures/resized/HEX_Minen.png"));
                paint = imagePattern;
                break;
            case 0:
                paint = Color.DODGERBLUE;
                break;
            default:
                imagePattern = new ImagePattern(new Image("/textures/resized/HEX_Wueste.png"));
                paint = imagePattern;
                break;
        }
        return paint;
    }

    /**
     * The method that actually draws graphical objects to the screen.
     * <p>
     * This method draws its items from back to front, meaning back most items need to be drawn first and so on. This is
     * why the background is drawn first, etc.
     * </p>
     * enhanced by Marc Hermes 2021-03-31 enhanced by Pieter Vogt 2021-04-07
     *
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    public void draw() {

        Vector centerOfCanvasVector = new Vector((canvas.getWidth() / 2 + canvas.getLayoutX()), canvas.getHeight() / 2 + canvas.getLayoutY());
        GraphicsContext g = this.canvas.getGraphicsContext2D(); //This is the object that is doing the drawing and has all the graphics related methods.

        //Drawing background.

        g.setFill(new ImagePattern(new Image("textures/blaues-meer-sicht-von-oben.jpg")));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //Drawing hexagons

        for (HexagonContainer hexagonContainer : this.hexagonContainers) {

            Vector placementVector = Vector.convertStringListToVector(hexagonContainer.getHexagon().getSelfPosition(), cardSize(), centerOfCanvasVector);
            hexagonContainer.getHexagonShape().setLayoutX(placementVector.getX());
            hexagonContainer.getHexagonShape().setLayoutY(placementVector.getY());
            hexagonContainer.getHexagonShape().setFill(determinePictureOfTerrain(hexagonContainer.getHexagon()));

            if (hexagonContainer.getHexagon().getDiceToken() != 0) {
                Text text = new Text(placementVector.getX(), placementVector.getY(), Integer.toString(hexagonContainer.getHexagon().getDiceToken()));
                text.setFill(Color.BLACK);
                text.setMouseTransparent(true);
                gameAnchorPane.getChildren().add(text);
            } else {
                //Draw robber
                //Initialize the robber graphics
                robber = new Rectangle(25, 25);
                robber.setFill(new ImagePattern(new Image("textures/originals/robbers.png")));
                robber.setVisible(true);
                robber.setLayoutX(placementVector.getX() - robber.getWidth() / 2);
                robber.setLayoutY(placementVector.getY() - robber.getHeight() / 2);
                gameAnchorPane.getChildren().add(robber);

            }
        }

        //Draw buildings

        for (MapGraphNodeContainer mapGraphNodeContainer : mapGraphNodeContainers) {

            if (mapGraphNodeContainer.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                double itemSize = cardSize() / 10;
                MapGraph.BuildingNode buildingNode = (MapGraph.BuildingNode) mapGraphNodeContainer.getMapGraphNode();

                Vector parentVector = Vector.convertStringListToVector(buildingNode.getParent().getSelfPosition(), cardSize(), centerOfCanvasVector);
                Vector selfVector = Vector.getVectorFromMapGraphNode(buildingNode, cardSize());
                Vector drawVector = Vector.addVector(parentVector, selfVector);

                Circle circle = mapGraphNodeContainer.getCircle();
                circle.setRadius(itemSize);
                circle.setLayoutX(drawVector.getX());
                circle.setLayoutY(drawVector.getY());
                circle.setVisible(false);

                circle.setFill(Color.color(0.5, 0.5, 0.5));
                if (buildingNode.getTypeOfHarbor() != 0) {
                    //Creating Symbols and Tooltips for harbors.
                    double harborSymbolSize = 25.0;
                    ImagePattern harborTexture = new ImagePattern(new Image("textures/hafen.png"));
                    Rectangle harborSymbol = new Rectangle(mapGraphNodeContainer.getCircle().getLayoutX() - harborSymbolSize / 2, mapGraphNodeContainer.getCircle().getLayoutY() - harborSymbolSize, harborSymbolSize, harborSymbolSize);
                    harborSymbol.setFill(harborTexture);

                    gameAnchorPane.getChildren().add(harborSymbol);
                    harborSymbol.toFront();

                    Tooltip tooltip = new Tooltip("");

                    switch (buildingNode.getTypeOfHarbor()) {
                        case 1:
                            tooltip.setText("2:1\n Wool");
                            Tooltip.install(harborSymbol, tooltip);
                            tooltip.setShowDelay(Duration.millis(100));
                            break;
                        case 2:
                            tooltip.setText("2:1\n Brick");
                            Tooltip.install(harborSymbol, tooltip);
                            tooltip.setShowDelay(Duration.millis(100));
                            break;
                        case 3:
                            tooltip.setText("2:1\n Lumber");
                            Tooltip.install(harborSymbol, tooltip);
                            tooltip.setShowDelay(Duration.millis(100));
                            break;
                        case 4:
                            tooltip.setText("2:1\n Grain");
                            Tooltip.install(harborSymbol, tooltip);
                            tooltip.setShowDelay(Duration.millis(100));
                            break;
                        case 5:
                            tooltip.setText("2:1\n Ore");
                            Tooltip.install(harborSymbol, tooltip);
                            tooltip.setShowDelay(Duration.millis(100));
                            break;
                        case 6:
                            tooltip.setText("3:1\n Any");
                            Tooltip.install(harborSymbol, tooltip);
                            tooltip.setShowDelay(Duration.millis(100));
                            break;
                    }
                }
            } else {
                double itemSize = cardSize() / 15;
                MapGraph.StreetNode streetNode = (MapGraph.StreetNode) mapGraphNodeContainer.getMapGraphNode();

                Vector parentVector = Vector.convertStringListToVector(streetNode.getParent().getSelfPosition(), cardSize(), centerOfCanvasVector);

                Vector selfVector = Vector.getVectorFromMapGraphNode(streetNode, cardSize());
                Vector drawVector = Vector.addVector(parentVector, selfVector);

                Rectangle rectangle = mapGraphNodeContainer.getRectangle();
                rectangle.setLayoutX(drawVector.getX() - rectangle.getWidth() / 2);
                rectangle.setLayoutY(drawVector.getY() - rectangle.getHeight() / 2);
                rectangle.setVisible(false);

                if (mapGraphNodeContainer.getMapGraphNode().getPositionToParent().equals("topRight") || mapGraphNodeContainer.getMapGraphNode().getPositionToParent().equals("bottomLeft")) {
                    rectangle.setRotate(120);
                } else if (mapGraphNodeContainer.getMapGraphNode().getPositionToParent().equals("topLeft") || mapGraphNodeContainer.getMapGraphNode().getPositionToParent().equals("bottomRight")) {
                    rectangle.setRotate(60);
                }

                Circle circle = mapGraphNodeContainer.getCircle();
                circle.setRadius(itemSize);
                circle.setLayoutX(drawVector.getX());
                circle.setLayoutY(drawVector.getY());
                circle.setVisible(false);

                circle.setFill(Color.color(0.5, 0.5, 0.5));
            }
        }
    }

    /**
     * Method to initialize the GameField of this GamePresenter of this client
     * <p>
     * First creates the tfArray, then iterates over the terrainFieldContainers of the gameField to get the diceTokens
     * values and copies them to the tfArray of this GamePresenter. Then the values of the fieldTypes are checked and
     * translated into the correct String names of the tfArray TerrainFields.
     * <p>
     * Enhanced, with a drawing of a robber
     *
     * @param mapGraph the MapGraph created by the Server
     * @author Marius Birk
     * @author Marc Hermes
     * @since 2021-04-20
     */
    public void initializeMatch(MapGraph mapGraph) {

        //Setting up the HexagonContainers
        LOG.debug("Setting up " + mapGraph.getHexagonHashSet().size() + " HexagonContainers...");

        for (MapGraph.Hexagon hexagon : mapGraph.getHexagonHashSet()) {
            HexagonContainer hexagonContainer = new HexagonContainer(hexagon, cardSize());
            this.hexagonContainers.add(hexagonContainer);
            gameAnchorPane.getChildren().add(hexagonContainer.getHexagonShape());
        }

        //Setting up the BuildingNodeContainers
        LOG.debug("Setting up " + mapGraph.getBuildingNodeHashSet().size() + " BuildingNodeContainers...");

        for (MapGraph.BuildingNode buildingNode : mapGraph.getBuildingNodeHashSet()) {
            MapGraphNodeContainer mapGraphNodeContainer = new MapGraphNodeContainer(new Circle(cardSize() / 6), buildingNode);
            this.mapGraphNodeContainers.add(mapGraphNodeContainer);
            nodeContainerHashMap.put(buildingNode.getUuid(), mapGraphNodeContainer);
            gameAnchorPane.getChildren().add(mapGraphNodeContainer.getCircle());
        }

        //Setting up the StreetNodeContainers
        LOG.debug("Setting up " + mapGraph.getStreetNodeHashSet().size() + " StreetNodeContainers...");

        for (MapGraph.StreetNode streetNode : mapGraph.getStreetNodeHashSet()) {
            MapGraphNodeContainer mapGraphNodeContainer = new MapGraphNodeContainer(new Circle(cardSize() / 8), streetNode, new Rectangle(cardSize() / 8.4, cardSize() / 1.9));
            this.mapGraphNodeContainers.add(mapGraphNodeContainer);
            gameAnchorPane.getChildren().add(mapGraphNodeContainer.getCircle());
            gameAnchorPane.getChildren().add(mapGraphNodeContainer.getRectangle());
            nodeContainerHashMap.put(streetNode.getUuid(), mapGraphNodeContainer);
        }
        initializeNodeSpots();

        draw();
    }

    /**
     * Method to draw buildings to the screen.
     * <p>
     * Creates the Spots (Circles) for the Buildings and streets. If a Circle is clicked, the gameService will be called
     * to request the building of a street/building. If a circle is clicked during the resolution of the Road Building
     * developmentCard, a bigger circle(black/red) will be temporarily placed above the street building spot.
     *
     * <p>
     * enhanced by Marc Hermes 2021-03-31 enhanced by Marc Hermes 2021-05-04
     *
     * @author Kirstin
     * @since 2021-03-28
     */
    public void initializeNodeSpots() {

        EventHandler<MouseEvent> clickOnCircleHandler = mouseEvent -> {
            for (MapGraphNodeContainer container : mapGraphNodeContainers) {
                if (mouseEvent.getSource().equals(container.getCircle()) && itsMyTurn) {
                    String typeOfNode;
                    if (container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                        typeOfNode = "BuildingNode";
                    } else {
                        typeOfNode = "StreetNode";
                    }
                    if (currentDevelopmentCard.equals("")) {
                        gameService.constructBuilding((UserDTO) joinedLobbyUser.getWithoutPassword(), currentLobby, container.getMapGraphNode().getUuid(), typeOfNode);
                    } else if (currentDevelopmentCard.equals("Road Building") && typeOfNode.equals("StreetNode")) {
                        if (street1 == null) {
                            street1 = container.getMapGraphNode().getUuid();
                            selectedStreet1.setLayoutX(container.getCircle().getLayoutX());
                            selectedStreet1.setLayoutY(container.getCircle().getLayoutY());
                            selectedStreet1.setRadius(container.getCircle().getRadius() * 2);
                            selectedStreet1.setVisible(true);
                            MapGraphNodeContainer streetNode = nodeContainerHashMap.get(street1);
                            streetNode.getMapGraphNode().setOccupiedByPlayer(myPlayerNumber);
                            updatePossibleBuildingSpots(0);
                        } else if (street2 == null) {
                            street2 = container.getMapGraphNode().getUuid();
                            selectedStreet2.setLayoutX(container.getCircle().getLayoutX());
                            selectedStreet2.setLayoutY(container.getCircle().getLayoutY());
                            selectedStreet2.setRadius(container.getCircle().getRadius() * 2);
                            selectedStreet2.setVisible(true);
                            MapGraphNodeContainer streetNode = nodeContainerHashMap.get(street1);
                            MapGraph.StreetNode streetNode1 = (MapGraph.StreetNode) streetNode.getMapGraphNode();
                            for (MapGraph.BuildingNode buildingNode : streetNode1.getConnectedBuildingNodes()) {
                                for (MapGraph.StreetNode streetNode2 : buildingNode.getConnectedStreetNodes()) {
                                    if (streetNode2.getOccupiedByPlayer() == 666) {
                                        MapGraphNodeContainer mapGraphNodeContainer = nodeContainerHashMap.get(streetNode2.getUuid());
                                        mapGraphNodeContainer.getCircle().setVisible(false);
                                    }
                                }
                            }
                            streetNode.getMapGraphNode().setOccupiedByPlayer(666);
                            updatePossibleBuildingSpots(0);
                        } else {
                            street2 = null;
                            street1 = container.getMapGraphNode().getUuid();
                            selectedStreet2.setVisible(false);
                            selectedStreet1.setLayoutX(container.getCircle().getLayoutX());
                            selectedStreet1.setLayoutY(container.getCircle().getLayoutY());
                            MapGraphNodeContainer streetNode = nodeContainerHashMap.get(street1);
                            streetNode.getMapGraphNode().setOccupiedByPlayer(myPlayerNumber);
                            updatePossibleBuildingSpots(0);
                        }
                    }
                }
            }
        };

        for (MapGraphNodeContainer container : mapGraphNodeContainers) {
            container.getCircle().setOnMouseClicked(clickOnCircleHandler);
        }
    }

    /**
     * Updates the gameField based on the information currently available in this gamePresenter
     * <p>
     * This method is primarily used to update the gameField after (re)-joining an ongoing game.
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    public void updateGameField() {

        for (MapGraphNodeContainer mapGraphNodeContainer : mapGraphNodeContainers) {
            if (mapGraphNodeContainer.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                MapGraph.BuildingNode buildingNode = (MapGraph.BuildingNode) mapGraphNodeContainer.getMapGraphNode();
                if (buildingNode.getSizeOfSettlement() == 1) {
                    mapGraphNodeContainer.getCircle().setFill(determineBuildingPicture(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer(), 1));
                    if (mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer() != 666) {
                        mapGraphNodeContainer.getCircle().setRadius(cardSize() / 3.5);
                        mapGraphNodeContainer.getCircle().setVisible(true);
                    }

                } else if (buildingNode.getSizeOfSettlement() == 2) {
                    mapGraphNodeContainer.getCircle().setFill(determineBuildingPicture(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer(), 2));
                    if (mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer() != 666) {
                        mapGraphNodeContainer.getCircle().setRadius(cardSize() / 3.5);
                        mapGraphNodeContainer.getCircle().setVisible(true);
                    }

                }
            } else if (mapGraphNodeContainer.getMapGraphNode() instanceof MapGraph.StreetNode) {
                mapGraphNodeContainer.getRectangle().setFill(determineBuildingPicture(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer(), 0));
                if (mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer() != 666) {
                    mapGraphNodeContainer.getRectangle().setVisible(true);
                }
            }
        }
        for (MapGraphNodeContainer mapGraphNodeContainer1 : mapGraphNodeContainers) {
            if (mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() != 666 && mapGraphNodeContainer1.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                mapGraphNodeContainer1.getCircle().toFront();
            }
        }
        for (HexagonContainer hexagonContainer : hexagonContainers) {
            if (hexagonContainer.getHexagon().isOccupiedByRobber()) {
                robber.setLayoutX(hexagonContainer.getHexagonShape().getLayoutX());
                robber.setLayoutY(hexagonContainer.getHexagonShape().getLayoutY());
                robber.setVisible(true);
            }
        }
    }

    /**
     * Handles the BuyDevelopmentCardMessage
     * <p>
     * If a BuyDevelopmentCardMessage is detected on the EventBus the method invokes the buyDevelopmentCardMessageLogic
     *
     * @param buyDevelopmentCardMessage GameMessage
     * @author Marius Birk
     * @see BuyDevelopmentCardMessage
     * @since 2021-05-27
     */
    @Subscribe
    public void onBuyDevelopmentCardMessage(BuyDevelopmentCardMessage buyDevelopmentCardMessage) {
        buyDevelopmentCardMessageLogic(buyDevelopmentCardMessage.getDevCardsNumber());
    }

    /**
     * The Method invoked by onBuyDevelopmentCardMessage()
     * <p>
     * Set the hover text from buyDevCard button with the parameter
     *
     * @param devCardsNumber the AllThisLobbyUsersResponse given by the original subscriber method.
     * @author Anton Nikiforov
     * @since 2021-06-14
     */
    public void buyDevelopmentCardMessageLogic(int devCardsNumber) {
        Tooltip hover = new Tooltip(devCardsNumber + " Cards left");
        if (devCardsNumber == 1) hover.setText("1 Card left");
        hover.setShowDelay(Duration.millis(50));
        buyDevCard.setTooltip(hover);
    }

    @Subscribe
    public void onNotEnoughResourcesMessages(NotEnoughResourcesMessage notEnoughResourcesMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(notEnoughResourcesMessage.getName())) {
                Platform.runLater(() -> {
                    this.alert.setTitle(notEnoughResourcesMessage.getName());
                    this.alert.setHeaderText("You have not enough Resources!");
                    this.alert.show();
                });
            }
        }
    }

    /**
     * This method reacts to the onSettlementFullyDevelopedMessage and shows the alert window.
     *
     * @param sfdm the SettlementFullyDevelopedMessage found on the bus
     * @author Carsten Dekker
     * @since 2021-06-07
     */
    @Subscribe
    public void onSettlementFullyDevelopedMessage(SettlementFullyDevelopedMessage sfdm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(sfdm.getName())) {
                Platform.runLater(() -> {
                    this.alert.setTitle(sfdm.getName());
                    this.alert.setHeaderText("This settlement is fully developed!");
                    this.alert.show();
                });
            }
        }
    }

    /**
     * The method invoked when the Game Presenter is first used.
     * <p>
     * The Alert tells the user, that he doesn't have enough resources to buy a development card. The user can only
     * click the showed button to close the dialog.
     *
     * @author Marius Birk
     * @since 2021-04-03
     */
    public void setupResourceAlert() {
        this.alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        this.buttonTypeOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeOkay);
        this.btnOkay = (Button) alert.getDialogPane().lookupButton(buttonTypeOkay);
        btnOkay.setOnAction(event -> {
            alert.close();
            event.consume();
        });
    }

    /**
     * Handles the MoveRobberMessage
     * <p>
     * If a MoveRobberMessage is detected on the EventBus the method moveRobberMessageLogic is invoked.
     *
     * @param moveRobberMessage GameMessage
     * @author Marius Birk
     * @see MoveRobberMessage
     * @since 2021-04-20
     */
    @Subscribe
    public void onMoveRobberMessage(MoveRobberMessage moveRobberMessage) {
        moveRobberMessageLogic(moveRobberMessage);
    }

    /**
     * This method will be invoked if a MoveRobberMessage is detected on the eventBus.
     * <p>
     * At first it checks if the current lobby is null and if that, it checks if the current lobby is the lobby we want to work in.
     * After a successful check the method calls an alert on another thread to inform the user, that he can move the robber.
     * To know, where the user has clicked, we need to create an eventHandler and override the handle method. in the handle
     * method we iterate over every hexagon and check if the mouse was pressed on it. Now it can call the movedRobber method
     * in the gameService and it can remove the eventHandler from the hexagons.
     *
     * @param moveRobberMessage the MoveRobberMessage that was detected on the EventBus
     * @author Marius Birk
     * @since 2021-04-20
     */
    public void moveRobberMessageLogic(MoveRobberMessage moveRobberMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(moveRobberMessage.getName())) {
                Platform.runLater(() -> {
                    this.alert.setTitle(moveRobberMessage.getName());
                    this.alert.setHeaderText("Click on a field to move the Robber!");
                    Rectangle2D center = Screen.getPrimary().getVisualBounds();
                    this.alert.setX(center.getWidth() / 4);
                    this.alert.setY(center.getHeight() / 5);
                    this.alert.show();
                });

                //adding a eventHandler to know where the user wants to set the robber
                EventHandler<MouseEvent> clickOnHexagonHandler = new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        for (HexagonContainer container : hexagonContainers) {
                            if (mouseEvent.getSource().equals(container.getHexagonShape()) && itsMyTurn) {
                                if (container.getHexagon().getTerrainType() != 6) {
                                    for (HexagonContainer container1 : hexagonContainers) {
                                        container1.getHexagonShape().removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                                        switchTurnPhaseButtons();
                                    }
                                    if (currentDevelopmentCard.equals("Knight")) {
                                        gameService.resolveDevelopmentCardKnight((UserDTO) moveRobberMessage.getUser(), moveRobberMessage.getName(), currentDevelopmentCard, container.getHexagon().getUuid());
                                        currentDevelopmentCard = "";
                                    } else {
                                        gameService.movedRobber(moveRobberMessage.getName(), moveRobberMessage.getUser(), container.getHexagon().getUuid());
                                    }
                                }
                            }

                        }
                    }
                };
                for (HexagonContainer container : hexagonContainers) {
                    container.getHexagonShape().addEventHandler(MouseEvent.MOUSE_PRESSED, clickOnHexagonHandler);
                    rollDiceButton.setDisable(true);
                    buildMenu.setDisable(true);
                    tradeButton.setDisable(true);
                    endTurnButton.setDisable(true);
                    buyDevCard.setDisable(true);
                }
            }
        }
    }

    /**
     * The method invoked when the Game Presenter is first used.
     * <p>
     * The Alert tells the user, that he has to move the robber to a new field. The user can only
     * click the showed button to close the dialog.
     *
     * @author Marius Birk
     * @since 2021-04-20
     */
    public void setupRobberAlert() {
        this.alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        this.buttonTypeOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeOkay);
        this.btnOkay = (Button) alert.getDialogPane().lookupButton(buttonTypeOkay);
        btnOkay.setOnAction(event -> {
            alert.close();
            event.consume();
        });
    }

    /**
     * The method invoked when the Game Presenter is first used.
     * <p>
     * The alert is setup and show, when the ChoosePlayerMessage is detected on the eventbus. The method runs on an JavaFX Thread.
     * At first the alert will be created and gets the type "warning", after that all buttons get set and a title is chosen. Now,
     * the list of users in the game will be check if one of the user is the one user, who created the message. This user will be ignored.
     * The rest is added to the alert and the alert shows and waits for result. If one user is chosen the drawRandomCardFromPlayer method is invoked.
     * The alert closes.
     *
     * @author Marius Birk
     * @since 2021-04-20
     */
    public void setupChoosePlayerAlert() {
        chooseAlert = new Alert(Alert.AlertType.CONFIRMATION);
        chooseAlert.setContentText("Choose a player to draw a card from!");
    }

    /**
     * This method shows the choosePlayerAlert.
     * <p>
     * The method sets the title of the alert to the gameName and then configures the buttons in it. The buttons
     * are getting named after the names of the user in the choosePlayerMessage.
     * Then the alert will be shown and the user can choose a player, so the method can call the drawRandomResource Method
     * in the gameService.
     *
     * @param choosePlayerMessage the choosePlayerMessage that was detected on the EventBus
     * @author Marius Birk
     * @since 2021-06-02
     */
    public void showChoosePlayerAlert(ChoosePlayerMessage choosePlayerMessage) {
        if (choosePlayerMessage.getName() != null) {
            chooseAlert.setTitle(choosePlayerMessage.getName());
        } else {
            chooseAlert.setTitle("Choose a Player");
        }
        chooseAlert.getButtonTypes().setAll();
        for (int i = 0; i < choosePlayerMessage.getUserList().size(); i++) {
            if (!choosePlayerMessage.getUserList().get(i).equals(choosePlayerMessage.getUser().getUsername())) {
                chooseAlert.getButtonTypes().add(new ButtonType(choosePlayerMessage.getUserList().get(i)));
            }
        }
        if (!chooseAlert.isShowing()) {
            chooseAlert.showAndWait();
        }
        gameService.drawRandomCardFromPlayer(choosePlayerMessage.getName(), choosePlayerMessage.getUser(), chooseAlert.getResult().getText());
        chooseAlert.close();
    }

    /**
     * Method used for setting up the Alert for the ResolveDevelopmentCard functionality, as well as the pictures and functionality of the buttons and user interaction.
     * <p>
     * Depending on the current developmentCard the alert will show different elements. Pressing the "Ok" button will call the corresponding gameService method that sends the Request
     * to resolve the developmentCard.
     *
     * @author Marc Hermes
     * @since 2021-05-04
     */
    public void setupResolveDevelopmentCardAlert() {

        EventHandler<MouseEvent> clickOnResourceRectangleHandler = mouseEvent -> {
            Rectangle rect = (Rectangle) mouseEvent.getSource();
            if (rect.getFill().equals(lumber)) {
                if (resource1.equals("")) {
                    resource1 = "Lumber";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource1.setVisible(true);
                } else if (resource2.equals("") && currentDevelopmentCard.equals("Year of Plenty")) {
                    resource2 = "Lumber";
                    selectedResource2.setLayoutX(rect.getLayoutX() + selectedResource2.getRadius() / 2 + rect.getWidth() - selectedResource2.getRadius());
                    selectedResource2.setLayoutY(rect.getLayoutY() + selectedResource2.getRadius());
                    selectedResource2.setVisible(true);
                } else {
                    resource1 = "Lumber";
                    resource2 = "";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource2.setVisible(false);
                }
            } else if (rect.getFill().equals(brick)) {
                if (resource1.equals("")) {
                    resource1 = "Brick";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource1.setVisible(true);
                } else if (resource2.equals("") && currentDevelopmentCard.equals("Year of Plenty")) {
                    resource2 = "Brick";
                    selectedResource2.setLayoutX(rect.getLayoutX() + selectedResource2.getRadius() / 2 + rect.getWidth() - selectedResource2.getRadius());
                    selectedResource2.setLayoutY(rect.getLayoutY() + selectedResource2.getRadius());
                    selectedResource2.setVisible(true);
                } else {
                    resource1 = "Brick";
                    resource2 = "";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource2.setVisible(false);
                }
            } else if (rect.getFill().equals(grain)) {
                if (resource1.equals("")) {
                    resource1 = "Grain";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource1.setVisible(true);
                } else if (resource2.equals("") && currentDevelopmentCard.equals("Year of Plenty")) {
                    resource2 = "Grain";
                    selectedResource2.setLayoutX(rect.getLayoutX() + selectedResource2.getRadius() / 2 + rect.getWidth() - selectedResource2.getRadius());
                    selectedResource2.setLayoutY(rect.getLayoutY() + selectedResource2.getRadius());
                    selectedResource2.setVisible(true);
                } else {
                    resource1 = "Grain";
                    resource2 = "";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource2.setVisible(false);
                }
            } else if (rect.getFill().equals(wool)) {
                if (resource1.equals("")) {
                    resource1 = "Wool";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource1.setVisible(true);
                } else if (resource2.equals("") && currentDevelopmentCard.equals("Year of Plenty")) {
                    resource2 = "Wool";
                    selectedResource2.setLayoutX(rect.getLayoutX() + selectedResource2.getRadius() / 2 + rect.getWidth() - selectedResource2.getRadius());
                    selectedResource2.setLayoutY(rect.getLayoutY() + selectedResource2.getRadius());
                    selectedResource2.setVisible(true);
                } else {
                    resource1 = "Wool";
                    resource2 = "";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource2.setVisible(false);
                }
            } else if (rect.getFill().equals(ore)) {
                if (resource1.equals("")) {
                    resource1 = "Ore";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource1.setVisible(true);
                } else if (resource2.equals("") && currentDevelopmentCard.equals("Year of Plenty")) {
                    resource2 = "Ore";
                    selectedResource2.setLayoutX(rect.getLayoutX() + selectedResource2.getRadius() / 2 + rect.getWidth() - selectedResource2.getRadius());
                    selectedResource2.setLayoutY(rect.getLayoutY() + selectedResource2.getRadius());
                    selectedResource2.setVisible(true);
                } else {
                    resource1 = "Ore";
                    resource2 = "";
                    selectedResource1.setLayoutX(rect.getLayoutX() + selectedResource1.getRadius());
                    selectedResource1.setLayoutY(rect.getLayoutY() + selectedResource1.getRadius());
                    selectedResource2.setVisible(false);
                }
            }
        };

        this.resolveDevelopmentCardAlert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        ButtonType resolveButtonType = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        Button resolveButton;
        selectedResource1.setFill(Color.BLACK);
        selectedResource2.setFill(Color.RED);
        selectedResource1.setRadius(5);
        selectedResource2.setRadius(5);
        Rectangle2D center = Screen.getPrimary().getVisualBounds();
        this.resolveDevelopmentCardAlert.setX(center.getWidth() / 4);
        this.resolveDevelopmentCardAlert.setY(center.getHeight() / 3);
        double rectHeight = 100.0;
        double rectWidth = 50.0;
        Rectangle lumberRectangle = new Rectangle(rectWidth, rectHeight);
        lumberRectangle.setFill(lumber);
        resourceRectangles.add(lumberRectangle);
        Rectangle brickRectangle = new Rectangle(rectWidth, rectHeight);
        brickRectangle.setFill(brick);
        resourceRectangles.add(brickRectangle);
        Rectangle grainRectangle = new Rectangle(rectWidth, rectHeight);
        grainRectangle.setFill(grain);
        resourceRectangles.add(grainRectangle);
        Rectangle woolRectangle = new Rectangle(rectWidth, rectHeight);
        woolRectangle.setFill(wool);
        resourceRectangles.add(woolRectangle);
        Rectangle oreRectangle = new Rectangle(rectWidth, rectHeight);
        oreRectangle.setFill(ore);
        resourceRectangles.add(oreRectangle);
        double position = 0;
        for (Rectangle rectangle : resourceRectangles) {
            rectangle.setOnMouseClicked(clickOnResourceRectangleHandler);
            rectangle.setLayoutY(rectWidth);
            rectangle.setLayoutX(position);
            position = position + rectWidth;
        }
        resolveDevelopmentCardAlert.getButtonTypes().setAll(resolveButtonType);
        resolveButton = (Button) resolveDevelopmentCardAlert.getDialogPane().lookupButton(resolveButtonType);
        resolveButton.setOnAction(event -> {
            switch (this.currentDevelopmentCard) {
                case "Year of Plenty":
                    gameService.resolveDevelopmentCardYearOfPlenty((UserDTO) joinedLobbyUser, currentLobby, currentDevelopmentCard, resource1, resource2);
                    resource1 = "";
                    resource2 = "";
                    selectedResource1.setVisible(false);
                    selectedResource2.setVisible(false);
                    currentDevelopmentCard = "";
                    break;
                case "Monopoly":
                    gameService.resolveDevelopmentCardMonopoly((UserDTO) joinedLobbyUser, currentLobby, currentDevelopmentCard, resource1);
                    resource1 = "";
                    selectedResource1.setVisible(false);
                    currentDevelopmentCard = "";
                    break;
                case "Road Building":
                    gameService.resolveDevelopmentCardRoadBuilding((UserDTO) joinedLobbyUser, currentLobby, currentDevelopmentCard, street1, street2);
                    street1 = null;
                    street2 = null;
                    selectedStreet1.setVisible(false);
                    selectedStreet2.setVisible(false);
                    currentDevelopmentCard = "";
                    break;
            }
            event.consume();

            if (itsMyTurn) {
                tradeButton.setDisable(false);
                buildMenu.setDisable(false);
                buyDevCard.setDisable(false);
                endTurnButton.setDisable(false);
            }
        });
        resolveDevelopmentCardAlert.initModality(Modality.NONE);
        resolveDevelopmentCardAlert.getDialogPane().getChildren().addAll(resourceRectangles);
        resolveDevelopmentCardAlert.getDialogPane().getChildren().addAll(selectedResource1, selectedResource2);
        gameAnchorPane.getChildren().add(selectedStreet1);
        gameAnchorPane.getChildren().add(selectedStreet2);
        selectedResource1.setVisible(false);
        selectedResource2.setVisible(false);
        selectedStreet1.setVisible(false);
        selectedStreet2.setVisible(false);
        selectedStreet1.setFill(Color.BLACK);
        selectedStreet2.setFill(Color.RED);
    }

    /**
     * The method gets invoked when the Game Presenter is created.
     * <p>
     * This method creates six imagePatterns and adds them to the ArrayList diceImages. Then it fills rectangleDie1 and
     * rectangleDie2 with the die picture one. Both rectangles are added to the playerOneDiceView.
     *
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public void setupDicesAtGameStart() {
        for (int i = 1; i <= 6; i++) {
            Image image = new Image("img/dice/dice_" + i + ".png");
            ImagePattern imagePattern = new ImagePattern(image);
            diceImages.add(imagePattern);
        }
        rectangleDie1.setFill(diceImages.get(0));
        playerOneDiceView.add(rectangleDie1, 0, 0);
        rectangleDie2.setFill(diceImages.get(0));
        playerOneDiceView.add(rectangleDie2, 1, 0);
    }

    /**
     * Handles the removing and adding of the two rectangleDie from one gridPane to another
     * <p>
     * This method removes the rectangles from the current GridPane and adds them to the new GridPane. Then it fills
     * both rectangles with the imagePatterns for the die with eyes one.
     *
     * @param oldGridPane the gridPane, where the rectangles currently are
     * @param newGridPane the next gridPane, where the rectangles going to be added
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public void passTheDice(GridPane oldGridPane, GridPane newGridPane) {
        Platform.runLater(() -> {
            oldGridPane.getChildren().remove(rectangleDie1);
            oldGridPane.getChildren().remove(rectangleDie2);
            rectangleDie1.setFill(diceImages.get(0));
            rectangleDie2.setFill(diceImages.get(0));
            if (!newGridPane.getChildren().contains(rectangleDie1))
                newGridPane.add(rectangleDie1, 0, 0);
            if (!newGridPane.getChildren().contains(rectangleDie2))
                newGridPane.add(rectangleDie2, 1, 0);
        });
    }

    /**
     * The method invoked by the RollDiceResultMessage
     * <p>
     * This method calls the shuffleTheDice method and passes the DiceEyes1 and DiceEyes2.
     *
     * @param message the RollDiceResultMessage object seen on the eventBus
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    @Subscribe
    public void onRollDiceResultMessage(RollDiceResultMessage message) {
        if (this.currentLobby != null) {
            if (message.getName().equals(currentLobby)) {
                shuffleTheDice(message.getDiceEyes1(), message.getDiceEyes2());
            }
        }
    }

    /**
     * This method handles the dice animation
     * <p>
     * This method uses an ExecutorService that can schedule commands to run after a given delay, or to execute
     * periodically. With this service we can delay the for-loop for 125 milliseconds. For every iteration we generate two
     * random numbers between zero and five. We use these numbers and the diceImages ArrayList to fill both rectangleDie
     * with random imagePattern. In the 12th iteration the executorService gets shutdown and the imagePattern equal to
     * the rollDiceResult are shown.
     *
     * @param diceEyes1 the eyes from die one
     * @param diceEyes2 the eyes from die two
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    public void shuffleTheDice(int diceEyes1, int diceEyes2) {
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        final int[] i = {0};
        executorService.scheduleAtFixedRate(() -> {
            i[0]++;
            int randomNumber = randomInt(0, 5);
            rectangleDie1.setFill(diceImages.get(randomNumber));
            randomNumber = randomInt(0, 5);
            rectangleDie2.setFill(diceImages.get(randomNumber));
            if (i[0] == 12) {
                executorService.shutdown();
                rectangleDie1.setFill(diceImages.get(diceEyes1 - 1));
                rectangleDie2.setFill(diceImages.get(diceEyes2 - 1));
            }
        }, 0, 125, TimeUnit.MILLISECONDS);
    }

    /**
     * Randomizer for an int
     * <p>
     * This method returns a random number between int min and int max.
     *
     * @author Carsten Dekker
     * @since 2021-04-30
     */
    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }

    /**
     * Updates the corresponding Node in the list of MapGraphNodes to represent the changes from the message.
     *
     * @param message The data about the changed properties of the MapGraph
     * @author Pieter Vogt
     * @since 2021-04-15
     */
    @Subscribe
    public void onSuccessfulConstructionMessage(SuccessfulConstructionMessage message) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(message.getName())) {
                MapGraphNodeContainer mapGraphNodeContainer = nodeContainerHashMap.get(message.getUuid());
                if (message.getTypeOfNode().equals("BuildingNode")) {
                    MapGraph.BuildingNode buildingNode = (MapGraph.BuildingNode) mapGraphNodeContainer.getMapGraphNode();
                    buildingNode.setOccupiedByPlayer(message.getPlayerIndex());
                    buildingNode.incSizeOfSettlement();
                    for (MapGraph.StreetNode streetNode : ((MapGraph.BuildingNode) mapGraphNodeContainer.getMapGraphNode()).getConnectedStreetNodes()) {
                        for (MapGraph.BuildingNode buildingNode1 : streetNode.getConnectedBuildingNodes()) {
                            if (!buildingNode1.equals(buildingNode)) {
                                buildingNode1.setOccupiedByPlayer(420);
                            }
                        }
                    }
                    for (MapGraphNodeContainer mapGraphNodeContainer1 : mapGraphNodeContainers) {
                        if ((mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() == 420 || mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() == 666) && mapGraphNodeContainer1.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                            mapGraphNodeContainer1.getCircle().setVisible(false);
                        }
                    }
                    if (startingTurn && mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer() == myPlayerNumber) {
                        for (MapGraph.StreetNode streetNode : ((MapGraph.BuildingNode) mapGraphNodeContainer.getMapGraphNode()).getConnectedStreetNodes()) {
                            MapGraphNodeContainer mapGraphNodeContainer1 = nodeContainerHashMap.get(streetNode.getUuid());
                            mapGraphNodeContainer1.getCircle().setVisible(true);
                        }
                    }
                    if (itsMyTurn && !startingTurn) {
                        updatePossibleBuildingSpots(1);
                    }
                    if (mapGraphNodeContainer.getCircle().getFill().equals(Color.color(0.5, 0.5, 0.5))) {
                        Platform.runLater(() -> {
                            mapGraphNodeContainer.getCircle().setRadius(cardSize() / 3.5);
                            mapGraphNodeContainer.getCircle().setFill(determineBuildingPicture(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer(), 1));
                            mapGraphNodeContainer.getCircle().setVisible(true);
                            for (MapGraphNodeContainer mapGraphNodeContainer1 : mapGraphNodeContainers) {
                                if (mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() != 666 && mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() != 420 && mapGraphNodeContainer1.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                                    mapGraphNodeContainer1.getCircle().toFront();
                                }
                            }
                        });
                    } else {
                        for (MapGraphNodeContainer mapGraphNodeContainer1 : mapGraphNodeContainers) {
                            if ((mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() == 420 || mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() == 666) && mapGraphNodeContainer1.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                                mapGraphNodeContainer1.getCircle().setVisible(false);
                            }
                        }

                        Platform.runLater(() -> {
                            mapGraphNodeContainer.getCircle().setRadius(cardSize() / 3.5);
                            mapGraphNodeContainer.getCircle().setFill(determineBuildingPicture(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer(), 2));
                            for (MapGraphNodeContainer mapGraphNodeContainer1 : mapGraphNodeContainers) {
                                if (mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() != 666 && mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() != 420 && mapGraphNodeContainer1.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                                    mapGraphNodeContainer1.getCircle().toFront();
                                }
                            }
                        });
                    }
                } else {
                    MapGraph.StreetNode streetNode = (MapGraph.StreetNode) mapGraphNodeContainer.getMapGraphNode();
                    streetNode.setOccupiedByPlayer(message.getPlayerIndex());
                    if (startingTurn) {
                        for (MapGraphNodeContainer mapGraphNodeContainer1 : mapGraphNodeContainers) {
                            if (mapGraphNodeContainer1.getMapGraphNode().getOccupiedByPlayer() == 666 && mapGraphNodeContainer1.getMapGraphNode() instanceof MapGraph.StreetNode) {
                                mapGraphNodeContainer1.getCircle().setVisible(false);
                            }
                        }
                    }
                    if (itsMyTurn && !startingTurn) {
                        updatePossibleBuildingSpots(0);
                    }
                    Platform.runLater(() -> {
                        mapGraphNodeContainer.getCircle().setVisible(false);
                        mapGraphNodeContainer.getRectangle().setFill(determineBuildingPicture(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer(), 0));
                        mapGraphNodeContainer.getRectangle().setVisible(true);
                        for (MapGraphNodeContainer mapGraphNodeContainer2 : mapGraphNodeContainers) {
                            if (mapGraphNodeContainer2.getMapGraphNode().getOccupiedByPlayer() != 666 && mapGraphNodeContainer2.getMapGraphNode().getOccupiedByPlayer() != 420 && mapGraphNodeContainer2.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                                mapGraphNodeContainer2.getCircle().toFront();
                            }
                        }
                    });


                }
            }
        }
    }

    /**
     * This method selects the right picture or color for the building/street-nodes
     *
     * @param playerIndex    the currently playing player
     * @param typeOfBuilding int with the type of the building
     * @return this paint gets filled into the circles or rectangles
     * @author Carsten Dekker
     * @since 2021-06-01
     */
    public Paint determineBuildingPicture(int playerIndex, int typeOfBuilding) {
        if (typeOfBuilding == 1) {
            switch (playerIndex) {
                case 0:
                    return new ImagePattern(new Image("img/buildings/settlement_red.png"));
                case 1:
                    return new ImagePattern(new Image("img/buildings/settlement_blue.png"));
                case 2:
                    return new ImagePattern(new Image("img/buildings/settlement_purple.png"));
                default:
                    return new ImagePattern(new Image("img/buildings/settlement_green.png"));
            }
        } else if (typeOfBuilding == 2) {
            switch (playerIndex) {
                case 0:
                    return new ImagePattern(new Image("img/buildings/city_red.png"));
                case 1:
                    return new ImagePattern(new Image("img/buildings/city_blue.png"));
                case 2:
                    return new ImagePattern(new Image("img/buildings/city_purple.png"));
                default:
                    return new ImagePattern(new Image("img/buildings/city_green.png"));
            }
        } else {
            switch (playerIndex) {
                case 0:
                    return Color.color(0.5, 0, 0);
                case 1:
                    return Color.color(0.11, 0.56, 1);
                case 2:
                    return Color.color(0.29, 0, 0.5);
                default:
                    return Color.color(0, 0.5, 0);
            }
        }
    }


    /**
     * This method will be invoked if the robber is successfully moved on the game field.
     * <p>
     * If the robber is successfully moved on the game field and needs to be moved.
     * The method iterates over every hexagon on the game field and checks if the uuid of the hexagon is the same
     * as the uuid in the SuccessfulMovedRobberMessage. If this is true, the robbers layout will be set to the
     * hexagons layout. From that layout we subtract the half of the height/width of the robber, because the layout
     * is determined as the upper left edge of the robber.
     *
     * @param successfulMovedRobberMessage the successfulMovedRobberMessage detected on the EventBus
     * @author Marius Birk
     * @since 2021-04-22
     */
    @Subscribe
    public void onSuccessfulMovedRobberMessage(SuccessfulMovedRobberMessage successfulMovedRobberMessage) {
        for (HexagonContainer hexagonContainer : hexagonContainers) {
            if (hexagonContainer.getHexagon().getUuid().equals(successfulMovedRobberMessage.getNewField())) {
                robber.setLayoutX(hexagonContainer.getHexagonShape().getLayoutX() - robber.getWidth() / 2);
                robber.setLayoutY(hexagonContainer.getHexagonShape().getLayoutY() - robber.getHeight() / 2);
            }
        }
    }

    /**
     * The method called when a PrivateInventoryChangeMessage is received.
     * If lobby is not null and if current lobby is equal to lobby from received message,
     * updates privateInventory.
     * enhanced by Anton Nikiforov, Alexander Losse, Iskander Yusupov
     *
     * @param privateInventoryChangeMessage the PrivateInventoryChangeMessage received from the server
     * @author Marc Hermes
     * @since 2021-05-16
     * @since 2021-05-02
     */
    @Subscribe
    public void onPrivateInventoryChangeMessage(PrivateInventoryChangeMessage privateInventoryChangeMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(privateInventoryChangeMessage.getName())) {
                if (tooMuchAlert != null) {
                    Platform.runLater(() -> {
                        lumberLabelRobberMenu.setText(String.valueOf(privateInventoryChangeMessage.getPrivateInventory().get("Lumber")));
                        brickLabelRobberMenu.setText(String.valueOf(privateInventoryChangeMessage.getPrivateInventory().get("Brick")));
                        grainLabelRobberMenu.setText(String.valueOf(privateInventoryChangeMessage.getPrivateInventory().get("Grain")));
                        woolLabelRobberMenu.setText(String.valueOf(privateInventoryChangeMessage.getPrivateInventory().get("Wool")));
                        oreLabelRobberMenu.setText(String.valueOf(privateInventoryChangeMessage.getPrivateInventory().get("Ore")));
                        int toDiscard = Integer.parseInt(lumberLabelRobberMenu.getText()) + Integer.parseInt(grainLabelRobberMenu.getText()) + Integer.parseInt(woolLabelRobberMenu.getText()) + Integer.parseInt(brickLabelRobberMenu.getText()) + Integer.parseInt(oreLabelRobberMenu.getText());
                        if (toDiscard % 2 == 0) {
                            toDiscardLabel.setText(String.valueOf(toDiscard / 2));
                        } else {
                            toDiscardLabel.setText(String.valueOf((toDiscard - 1) / 2));
                        }
                    });
                }
                updatePrivateInventory(privateInventoryChangeMessage.getPrivateInventory());
            }
        }
    }

    /**
     * Checks whether each value of each label is equal to the value from received HashMap,
     * if it is not, then replaces existing value of label with the value from the HashMap.
     * <p>
     * enhanced by Anton Nikiforov, Alexander Losse, Iskander Yusupov
     *
     * @param pr HashMap<String, Integer>, which was provided by onPrivateInventoryChangeMessage
     * @author Carsten Dekker, Iskander Yusupov
     * @since 2021-05-16
     * @since 2021-05-14
     */

    private void updatePrivateInventory(HashMap<String, Integer> pr) {
        Platform.runLater(() -> {
            if (!privateLumberLabel.getText().equals(pr.get("Lumber").toString())) {
                privateLumberLabel.setText(pr.get("Lumber").toString());
            }
            if (!privateBrickLabel.getText().equals(pr.get("Brick").toString())) {
                privateBrickLabel.setText(pr.get("Brick").toString());
            }
            if (!privateGrainLabel.getText().equals(pr.get("Grain").toString())) {
                privateGrainLabel.setText(pr.get("Grain").toString());
            }
            if (!privateWoolLabel.getText().equals(pr.get("Wool").toString())) {
                privateWoolLabel.setText(pr.get("Wool").toString());
            }
            if (!privateOreLabel.getText().equals(pr.get("Ore").toString())) {
                privateOreLabel.setText(pr.get("Ore").toString());
            }
            if (!privateVictoryPointCardLabel.getText().equals(pr.get("Victory Point Card").toString())) {
                privateVictoryPointCardLabel.setText(pr.get("Victory Point Card").toString());
            }
            if (!privateKnightCardLabel.getText().equals(pr.get("Knight").toString())) {
                privateKnightCardLabel.setText(pr.get("Knight").toString());
            }
            if (!privateMonopolyCardLabel.getText().equals(pr.get("Monopoly").toString())) {
                privateMonopolyCardLabel.setText(pr.get("Monopoly").toString());
            }
            if (!privateRoadBuildingCardLabel.getText().equals(pr.get("Road Building").toString())) {
                privateRoadBuildingCardLabel.setText(pr.get("Road Building").toString());
            }
            if (!privateYearOfPlentyCardLabel.getText().equals(pr.get("Year of Plenty").toString())) {
                privateYearOfPlentyCardLabel.setText(pr.get("Year of Plenty").toString());
            }
            if (!privateSettlementsLabel.getText().equals(pr.get("Settlements").toString())) {
                privateSettlementsLabel.setText(pr.get("Settlements").toString());
            }
            if (!privateRoadsLabel.getText().equals(pr.get("Roads").toString())) {
                privateRoadsLabel.setText(pr.get("Roads").toString());
            }
            if (!privateCitiesLabel.getText().equals(pr.get("Cities").toString())) {
                privateCitiesLabel.setText(pr.get("Cities").toString());
            }
        });
    }

    /**
     * Handles new PublicInventoryChangeMessage
     * <p>
     * If a PublicInventoryChangeMessage is detected on the EventBus the method onPublicInventoryChangeMessageLogic is invoked.
     *
     * @param publicInventoryChangeMessage the PublicInventoryChangeMessage object seen on the EventBus
     * @author Iskander Yusupov
     * @see PublicInventoryChangeMessage
     * @since 2021-05-28
     */
    @Subscribe
    public void onPublicInventoryChangeMessage(PublicInventoryChangeMessage publicInventoryChangeMessage) {
        onPublicInventoryChangeMessageLogic(publicInventoryChangeMessage);
    }

    /**
     * The Method invoked by onPublicInventoryChangeMessage()
     * <p>
     * If the currentLobby is not null, meaning this is not an empty GamePresenter and the game name stored in this
     * GamePresenter equals the one in the received Message, the method updatePublicInventory is invoked to update the
     * public inventories in the currentLobby(current game) in regards to the arrayLists given by the message.
     *
     * @param puicm the PublicInventoryChangeMessage given by the original subscriber method.
     * @author Iskander Yusupov
     * @see de.uol.swp.common.game.message.PublicInventoryChangeMessage
     * @since 2021-05-28
     */
    private void onPublicInventoryChangeMessageLogic(PublicInventoryChangeMessage puicm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(puicm.getName())) {
                updatePublicInventory(puicm.getPublicInventories());
            }
        }
    }

    /**
     * Updates the publicInventoryViews according to the Arraylists given
     * <p>
     * If there is no publicInventory (ObservableList), method creates one.
     * Also creates PublicInventoryCells depending on current number of players in game.
     * Manages visibility of public inventory ListViews in GameView.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Iskander Yusupov
     * @see Inventory
     * @since 2021-05-28
     */
    public void updatePublicInventory(ArrayList<HashMap<String, Integer>> publicInventoriesList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (publicInventory1 == null) {
                publicInventory1 = FXCollections.observableArrayList();
                publicInventory1View.setItems(publicInventory1);
            }
            if (publicInventory2 == null) {
                publicInventory2 = FXCollections.observableArrayList();
                publicInventory2View.setItems(publicInventory2);
            }
            if (publicInventory3 == null && gameUsers.size() > 2) {
                publicInventory3 = FXCollections.observableArrayList();
                publicInventory3View.setItems(publicInventory3);
                publicInventory3View.setVisible(true);
            }
            if (publicInventory4 == null && gameUsers.size() > 3) {
                publicInventory4 = FXCollections.observableArrayList();
                publicInventory4View.setItems(publicInventory4);
                publicInventory4View.setVisible(true);
            }
            fillInPublicInventory(publicInventory1, publicInventoriesList.get(0));
            publicInventory1View.setCellFactory(y -> new PublicInventoryCell(publicInventoriesList.get(0)));
            fillInPublicInventory(publicInventory2, publicInventoriesList.get(1));
            publicInventory2View.setCellFactory(y -> new PublicInventoryCell(publicInventoriesList.get(1)));
            if (gameUsers.size() > 2) {
                fillInPublicInventory(publicInventory3, publicInventoriesList.get(2));
                publicInventory3View.setCellFactory(y -> new PublicInventoryCell(publicInventoriesList.get(2)));
                publicInventory3View.setVisible(true);
            }
            if (gameUsers.size() > 3) {
                fillInPublicInventory(publicInventory4, publicInventoriesList.get(3));
                publicInventory4View.setCellFactory(y -> new PublicInventoryCell(publicInventoriesList.get(3)));
                publicInventory4View.setVisible(true);
            }
        });
    }

    /**
     * Manages visibility of longest road and largest army cards as well as the other inventory items in the GameView.
     * <p>
     * This method clears hashMapEntriesList (public Inventory Observable List) and then adds the
     * contents of public inventory from hashmap in each publicInventory(1-4), using switch-case.
     *
     * @param hashMapEntriesList the observableList for the items of the inventory
     * @param hashMap            the hashMap containing the public inventory
     * @author Iskander Yusupov
     * @see Inventory
     * @since 2021-06-02
     */
    public void fillInPublicInventory(ObservableList<HashMap.Entry<String, Integer>> hashMapEntriesList, HashMap<String, Integer> hashMap) {
        hashMapEntriesList.clear();
        hashMapEntriesList.add(null);
        hashMapEntriesList.add(null);
        hashMapEntriesList.add(null);
        hashMapEntriesList.add(null);
        hashMapEntriesList.add(null);
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            switch (entry.getKey()) {
                case "Public Victory Points":
                    hashMapEntriesList.add(0, entry);
                    hashMapEntriesList.remove(1);
                    break;
                case "Resource":
                    hashMapEntriesList.add(1, entry);
                    hashMapEntriesList.remove(2);
                    break;
                case "Development Cards":
                    hashMapEntriesList.add(2, entry);
                    hashMapEntriesList.remove(3);
                    break;
                case "Played Knights":
                    hashMapEntriesList.add(3, entry);
                    hashMapEntriesList.remove(4);
                    break;
                case "Continuous Road":
                    hashMapEntriesList.add(4, entry);
                    hashMapEntriesList.remove(5);
                    break;

                case "Largest Army":
                    if (entry.getValue() == 1 && hashMapEntriesList == publicInventory1) {
                        playerOneLargestArmyView.setVisible(true);
                        playerTwoLargestArmyView.setVisible(false);
                        playerThreeLargestArmyView.setVisible(false);
                        playerFourLargestArmyView.setVisible(false);
                    } else if (entry.getValue() == 1 && hashMapEntriesList == publicInventory2) {
                        playerOneLargestArmyView.setVisible(false);
                        playerTwoLargestArmyView.setVisible(true);
                        playerThreeLargestArmyView.setVisible(false);
                        playerFourLargestArmyView.setVisible(false);
                    } else if (entry.getValue() == 1 && hashMapEntriesList == publicInventory3) {
                        playerOneLargestArmyView.setVisible(false);
                        playerTwoLargestArmyView.setVisible(false);
                        playerThreeLargestArmyView.setVisible(true);
                        playerFourLargestArmyView.setVisible(false);
                    } else if (entry.getValue() == 1 && hashMapEntriesList == publicInventory4) {
                        playerOneLargestArmyView.setVisible(false);
                        playerTwoLargestArmyView.setVisible(false);
                        playerThreeLargestArmyView.setVisible(false);
                        playerFourLargestArmyView.setVisible(true);
                    }
                    break;
                case "Longest Road":
                    if (entry.getValue() == 1 && hashMapEntriesList == publicInventory1) {
                        playerOneLongestRoadView.setVisible(true);
                    } else if (entry.getValue() == 1 && hashMapEntriesList == publicInventory2) {
                        playerTwoLongestRoadView.setVisible(true);
                    } else if (entry.getValue() == 1 && hashMapEntriesList == publicInventory3) {
                        playerThreeLongestRoadView.setVisible(true);
                    } else if (entry.getValue() == 1 && hashMapEntriesList == publicInventory4) {
                        playerFourLongestRoadView.setVisible(true);
                    }
                    if (entry.getValue() == 0 && hashMapEntriesList == publicInventory1) {
                        playerOneLongestRoadView.setVisible(false);
                    }
                    if (entry.getValue() == 0 && hashMapEntriesList == publicInventory2) {
                        playerTwoLongestRoadView.setVisible(false);
                    }
                    if (entry.getValue() == 0 && hashMapEntriesList == publicInventory3) {
                        playerThreeLongestRoadView.setVisible(false);
                    }
                    if (entry.getValue() == 0 && hashMapEntriesList == publicInventory4) {
                        playerFourLongestRoadView.setVisible(false);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * The method gets invoked when the Game Presenter is created.
     * <p>
     * This method creates two imagePatterns. Then it creates and fills rectangleLargestArmy and
     * rectangleLongestRoad with corresponding ImagePattern.
     * <p>
     * Rectangles of each player are added to the ArrayList rectangles.
     * <p>
     * After this, hover and exit methods are being executed. When the user is entering the rectangle
     * of a card, a tooltip, with what the card under the mouse cursor represents, appear. If the user clicks
     * on the card, the onClickOnDevelopmentCard method gets executed and the user can read and close window with card image.
     * <p>
     * Lastly it adds rectangles in the corresponding FXML Panes, depending on number of players in game
     *
     * @author Iskander Yusupov
     * @since 2021-06-05
     */
    private void setUpLargestArmyAndLongestRoadPanes(ArrayList<User> list) {
        for (int i = 0; i < list.size(); i++) {
            Image imageLargestArmy = new Image("textures/inventory/ACHVMNT_GroessteRittermacht.png");
            Rectangle rectangleLargestArmy = new Rectangle(60, 60);
            rectangleLargestArmy.setFill(new ImagePattern(imageLargestArmy));
            rectanglesLargestArmy.add(rectangleLargestArmy);
            Image imageLongestRoad = new Image("textures/inventory/ACHVMNT_LaengsteStrasse.png");
            Rectangle rectangleLongestRoad = new Rectangle(60, 60);
            rectangleLongestRoad.setFill(new ImagePattern(imageLongestRoad));
            rectanglesLongestRoad.add(rectangleLongestRoad);

            Tooltip hoverLargestArmy = new Tooltip("");

            hoverLargestArmy.setText("Largest Army");
            Tooltip.install(rectangleLargestArmy, hoverLargestArmy);
            hoverLargestArmy.setShowDelay(Duration.millis(0));

            rectangleLargestArmy.setOnMouseClicked(new EventHandler<>() {
                final String title = hoverLargestArmy.getText();
                final String description = "A card that you receive upon reaching largest army, longer than 2";
                final Boolean isDevelopmentCard = false;

                @Override
                public void handle(MouseEvent mouseEvent) {
                    onClickOnDevelopmentCard(title, description, imageLargestArmy, isDevelopmentCard);

                }
            });

            Tooltip hoverLongestRoad = new Tooltip("");

            hoverLongestRoad.setText("Longest Road");
            Tooltip.install(rectangleLongestRoad, hoverLongestRoad);
            hoverLongestRoad.setShowDelay(Duration.millis(0));
            rectangleLongestRoad.setOnMouseClicked(new EventHandler<>() {
                final String title = hoverLongestRoad.getText();
                final String description = "A card that you receive upon having longest road, longer than 4";
                final Boolean isDevelopmentCard = false;

                @Override
                public void handle(MouseEvent mouseEvent) {
                    onClickOnDevelopmentCard(title, description, imageLongestRoad, isDevelopmentCard);

                }
            });
        }
        playerOneLargestArmyView.getChildren().add(rectanglesLargestArmy.get(0));
        playerOneLongestRoadView.getChildren().add(rectanglesLongestRoad.get(0));
        playerTwoLargestArmyView.getChildren().add(rectanglesLargestArmy.get(1));
        playerTwoLongestRoadView.getChildren().add(rectanglesLongestRoad.get(1));
        if (list.size() > 2) {
            playerThreeLargestArmyView.getChildren().add(rectanglesLargestArmy.get(2));
            playerThreeLongestRoadView.getChildren().add(rectanglesLongestRoad.get(2));
        }
        if (list.size() > 3) {
            playerFourLargestArmyView.getChildren().add(rectanglesLargestArmy.get(3));
            playerFourLongestRoadView.getChildren().add(rectanglesLongestRoad.get(3));
        }
    }

    /**
     * The method gets invoked when the Game Presenter is created.
     * <p>
     * This method creates Tabs with game chat, game event log and pricesView. Then it fills TabPane with tabs.
     *
     * @author Iskander Yusupov
     * @since 2021-06-14
     */
    private void setUpTabs() {
        Tab tabChat = new Tab();
        tabChat.setText("Chat");
        tabChat.setContent(gameChatArea);
        Tab tabGameLog = new Tab();
        tabGameLog.setText("Log");
        Tab tabPrices = new Tab();
        tabPrices.setText("Prices");
        tabPrices.setContent(pricesView);
        tabPane.getTabs().addAll(tabChat, tabGameLog, tabPrices);
    }

    /**
     * The method gets invoked when the Game Presenter is created.
     * <p>
     * This method creates image and imagePattern. Then it creates and fills rectanglePrices  with
     * <p>
     * ImagePattern. Rectangle is added to the FXML Pane.
     * <p>
     *
     * @author Iskander Yusupov
     * @since 2021-06-08
     */
    private void setUpPrices() {
        Image imagePrices = new Image("textures/resized/Baukosten.png");
        Rectangle rectanglePrices = new Rectangle(225, 225);
        rectanglePrices.setFill(new ImagePattern(imagePrices));
        pricesView.getChildren().add(rectanglePrices);
    }


    /**
     * The method called when a ResolveDevelopmentCardNotSuccessfulResponse is received
     * <p>
     * The resolveDevelopmentCardAlert will be shown in which the error description is displayed.
     *
     * @param rdcns the ResolveDevelopmentCardNotSuccessfulResponse received from the server
     * @author Marc Hermes
     * @since 2021-05-02
     */
    @Subscribe
    public void onResolveCardNotSuccessfulResponse(ResolveDevelopmentCardNotSuccessfulResponse rdcns) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(rdcns.getGameName())) {
                this.currentDevelopmentCard = rdcns.getDevCard();
                Platform.runLater(() -> {
                    this.resolveDevelopmentCardAlert.setTitle(currentDevelopmentCard + " in " + rdcns.getGameName());
                    this.resolveDevelopmentCardAlert.setHeaderText(rdcns.getErrorDescription());
                    this.resolveDevelopmentCardAlert.show();
                    this.tradeButton.setDisable(true);
                    this.rollDiceButton.setDisable(true);
                    this.buildMenu.setDisable(true);
                    this.buyDevCard.setDisable(true);
                    this.endTurnButton.setDisable(true);
                });

            }
        }

    }

    /**
     * The method called when a PlayDevelopmentCardResponse is received
     * <p>
     * Depending on which developmentCard is played the resolveDevelopmentCardAlert is shown.
     * Also if the currently played DevelopmentCard is "Year of Plenty" or "Monopoly" the visibility of the rectangles with the pictures of the resources is set to "true".
     * Furthermore the circles displaying the empty building spots are hidden.
     * <p>
     * In case the developmentCard is "Road Building", the resourceRectangles are hidden and the empty street building will be shown on the game field.
     *
     * @param pdcr the PlayDevelopmentCardResponse received from the server
     * @author Marc Hermes
     * @since 2021-05-02
     */
    @Subscribe
    public void onPlayDevelopmentCardResponse(PlayDevelopmentCardResponse pdcr) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(pdcr.getGameName())) {
                if (pdcr.isCanPlayCard()) {
                    this.currentDevelopmentCard = pdcr.getDevCard();
                    if (!pdcr.getDevCard().equals("Knight")) {
                        LOG.debug("The card " + pdcr.getDevCard() + " was played by the user " + pdcr.getUserName());
                        this.currentDevelopmentCard = pdcr.getDevCard();
                        Platform.runLater(() -> {
                            this.resolveDevelopmentCardAlert.setTitle(currentDevelopmentCard + " in " + pdcr.getGameName());
                            if (this.currentDevelopmentCard.equals("Year of Plenty") || this.currentDevelopmentCard.equals("Monopoly")) {
                                this.resolveDevelopmentCardAlert.setHeaderText("Select Resource/s");
                                for (Rectangle rect : resourceRectangles) {
                                    rect.setVisible(true);
                                }
                                for (MapGraphNodeContainer container : mapGraphNodeContainers) {
                                    if (container.getMapGraphNode().getOccupiedByPlayer() == 666) {
                                        container.getCircle().setVisible(false);
                                    }
                                }
                            } else if (this.currentDevelopmentCard.equals("Road Building")) {
                                updatePossibleBuildingSpots(0);
                                this.resolveDevelopmentCardAlert.setHeaderText("Select 2 building spots for the streets");
                                for (Rectangle rect : resourceRectangles) {
                                    rect.setVisible(false);
                                }
                            }
                            this.resolveDevelopmentCardAlert.show();
                            this.tradeButton.setDisable(true);
                            this.rollDiceButton.setDisable(true);
                            this.buildMenu.setDisable(true);
                            this.buyDevCard.setDisable(true);
                            this.endTurnButton.setDisable(true);

                        });

                    } else {
                        LOG.debug("The user " + pdcr.getUserName() + " cannot play the card " + pdcr.getDevCard());
                    }
                }
            }
        }
    }

    /**
     * The method called when a ResolveDevelopmentCardMessage is received
     *
     * @param rdcm the ResolveDevelopmentCardMessage received from the server
     * @author Marc Hermes
     * @since 2021-05-02
     */
    @Subscribe
    public void onResolveDevelopmentCardMessage(ResolveDevelopmentCardMessage rdcm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(rdcm.getName())) {
                LOG.debug("The user " + rdcm.getUser().getUsername() + " successfully resolved the card " + rdcm.getDevCard());
            }
        }
    }

    /**
     * Shows an alert if the trade user has not enough in inventory
     *
     * @param message TradeCardErrorMessage
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-25
     */
    @Subscribe
    public void notEnoughResTrade(TradeCardErrorMessage message) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(message.getName())) {
                Platform.runLater(() -> {
                    this.alert.setTitle(message.getName());
                    this.alert.setHeaderText("You have not enough Resources for the trade in: " + message.getTradeCode());
                    this.alert.show();
                });
            }
        }
    }

    /**
     * The method gets invoked when the Game Presenter is created.
     * <p>
     * This method creates thirteen images and rectangles. Then it creates and fills imagePatterns
     * with the images from first to thirteens. Every imagePattern is added to the privateInventoryView.
     * <p>
     * After this, hover, exits and drag methods are being executed. When the user is entering the rectangle
     * of a card, a tooltip, with what the card under the mouse cursor represents, appear. If the user clicks
     * on the card, the onClickOnDevelopmentCard method gets executed and the user can choose to play the card.
     * Alternatively, the user can also just drag the card to play it.
     * <p>
     * Lastly it adds thirteen corresponding labels to the privateInventoryView.
     * <p>
     * Enhanced by Ricardo Mook, 2021-05-27
     * added Tooltip.install method for the mouse
     * <p>
     * Enhanced by Ricardo Mook, 2021-05-30
     * added drag method and method call by clicking on a card
     *
     * @author Carsten Dekker, Iskander Yusupov
     * @since 2021-05-14
     */
    public void setUpPrivateInventoryView() {
        for (int i = 1; i < 14; i++) {
            Image image = new Image("textures/inventory/privateInventoryImage" + i + ".png");
            Rectangle r = new Rectangle(50, 80);
            r.setFill(new ImagePattern(image));
            privateInventoryView.add(r, i - 1, 0);
            privateInventoryView.setAlignment(Pos.CENTER);

            Tooltip hover = new Tooltip("");

            switch (i) {

                case 1:
                    hover.setText("Lumber");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "A resource you can get from forest fields";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;

                case 2:
                    hover.setText("Brick");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "This resource can be found on hill fields";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;

                case 3:
                    hover.setText("Grain");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "This resource can be harvested on grain fields";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;

                case 4:
                    hover.setText("Wool");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "This resource is getting produced on pasture fields";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;

                case 5:
                    hover.setText("Ore");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "This resource can be won from mountain fields";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;
                case 6:
                    hover.setText("Victory Points");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "Cards which aren't playable but increase your victory points at the end of game";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;


                case 7:
                    hover.setText("Knight");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "If you play this card, you can move the robber to another field and can draw one card from an affected player";
                        final Boolean isDevelopmentCard = true;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    r.setOnDragDetected(new EventHandler<>() {
                        final String title = hover.getText();

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            gameService.playDevelopmentCard((UserDTO) joinedLobbyUser, currentLobby, title);
                        }
                    });

                    break;

                case 8:
                    hover.setText("Monopoly");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "When you play out this card, you can choose a resource. Every player who currently has the chosen resource on their hand must give you all of them.";
                        final Boolean isDevelopmentCard = true;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    r.setOnDragDetected(new EventHandler<>() {
                        final String title = hover.getText();

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            gameService.playDevelopmentCard((UserDTO) joinedLobbyUser, currentLobby, title);
                        }
                    });

                    break;

                case 9:
                    hover.setText("Road Building");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "The Road Building development card. With this card played out, you can build two roads this turn free of charge";
                        final Boolean isDevelopmentCard = true;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    r.setOnDragDetected(new EventHandler<>() {
                        final String title = hover.getText();

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            gameService.playDevelopmentCard((UserDTO) joinedLobbyUser, currentLobby, title);
                        }
                    });

                    break;

                case 10:
                    hover.setText("Year of Plenty");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "The Year of Plenty development card. When this card is played out, you immediately get two resource cards of your choice from the bank";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    r.setOnDragDetected(new EventHandler<>() {
                        final String title = hover.getText();

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            gameService.playDevelopmentCard((UserDTO) joinedLobbyUser, currentLobby, title);
                        }
                    });

                    break;

                case 11:
                    hover.setText("Settlements");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "You can build these to get resources from tiles";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;

                case 12:
                    hover.setText("Roads");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "With these you can connect your settlements and cities with each other";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;

                case 13:
                    hover.setText("Cities");
                    Tooltip.install(r, hover);
                    hover.setShowDelay(Duration.millis(0));

                    r.setOnMouseClicked(new EventHandler<>() {
                        final String title = hover.getText();
                        final String description = "A bigger version of the settlements";
                        final Boolean isDevelopmentCard = false;

                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            onClickOnDevelopmentCard(title, description, image, isDevelopmentCard);

                        }
                    });

                    break;


            }

        }
        privateInventoryView.add(privateLumberLabel, 0, 1);
        privateInventoryView.add(privateBrickLabel, 1, 1);
        privateInventoryView.add(privateGrainLabel, 2, 1);
        privateInventoryView.add(privateWoolLabel, 3, 1);
        privateInventoryView.add(privateOreLabel, 4, 1);
        privateInventoryView.add(privateVictoryPointCardLabel, 5, 1);
        privateInventoryView.add(privateKnightCardLabel, 6, 1);
        privateInventoryView.add(privateMonopolyCardLabel, 7, 1);
        privateInventoryView.add(privateRoadBuildingCardLabel, 8, 1);
        privateInventoryView.add(privateYearOfPlentyCardLabel, 9, 1);
        privateInventoryView.add(privateSettlementsLabel, 10, 1);
        privateInventoryView.add(privateRoadsLabel, 11, 1);
        privateInventoryView.add(privateCitiesLabel, 12, 1);
    }

    /**
     * This method can be called when the user clicks on a card in the private inventory.
     * <p>
     * When this method is called, a close-up of the clicked card with a short description appears. If the card is a
     * development card, it can be played directly from the close-up.
     *
     * @param cardName          The name of the clicked card
     * @param description       A short description of the played card
     * @param cardImage         An enlarged image of the clicked card
     * @param isDevelopmentCard Boolean value if the card is a development card or not
     * @author Ricardo Mook
     * @since 2021-05-30
     */

    public void onClickOnDevelopmentCard(String cardName, String description, Image cardImage, Boolean isDevelopmentCard) {
        Alert clickAlert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType ok = new ButtonType("Ok", ButtonBar.ButtonData.NO);
        clickAlert.getButtonTypes().setAll(ok);

        if (isDevelopmentCard) {
            ButtonType playThisCard = new ButtonType("Play this card", ButtonBar.ButtonData.YES);
            clickAlert.getButtonTypes().setAll(ok, playThisCard);

            Button playCard = (Button) clickAlert.getDialogPane().lookupButton(playThisCard);
            playCard.setOnAction(event -> gameService.playDevelopmentCard((UserDTO) joinedLobbyUser, currentLobby, cardName));
        }
        clickAlert.setHeaderText(cardName);
        clickAlert.setContentText(description);
        clickAlert.setGraphic(new ImageView(cardImage));

        clickAlert.show();


    }

    /**
     * This method is invoked if a TooMuchResourceCardsMessage detected on the bus.
     *
     * @param tooMuchResourceCardsMessage the tooMuchResourceCardsMessage detected on the EventBus
     * @author Marius Birk
     * @since 2021-05-01
     */
    @Subscribe
    public void onTooMuchResourceCardsMessage(TooMuchResourceCardsMessage tooMuchResourceCardsMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(tooMuchResourceCardsMessage.getName())) {
                Platform.runLater(() -> showRobberResourceMenu(tooMuchResourceCardsMessage));
            }
        }
    }

    /**
     * This method will be invoked if a choosePlayerMessage is detected on the bus.
     * <p>
     * The method sets up an alert to choose a player to draw a card from.
     * That will be done on another Thread.
     *
     * @param choosePlayerMessage the choosePlayerMessage detected on the EventBus
     * @author Marius Birk
     * @since 2021-05-01
     */
    @Subscribe
    public void onChoosePlayerMessage(ChoosePlayerMessage choosePlayerMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(choosePlayerMessage.getName())) {
                if (!choosePlayerMessage.getUserList().isEmpty()) {
                    Platform.runLater(() -> showChoosePlayerAlert(choosePlayerMessage));

                }
            }
        }
    }

    /**
     * help method to disable/enable buttons
     * <p>
     * method is used to check for specific variables and enables/disables button accordingly
     * e.g it is not users turn
     *
     * @author Alexander Losse
     * @since 2021-05-30
     */
    private void switchTurnPhaseButtons() {
        Platform.runLater(() -> {
            if (itsMyTurn) { //it's users turn
                if (!startingTurn) { //not in opening phase
                    if (!rolledDice) { //part 1(resources)
                        buildMenu.setDisable(true);
                        endTurnButton.setDisable(true);
                        tradeButton.setDisable(true);
                        rollDiceButton.setDisable(false);
                        buyDevCard.setDisable(true);
                    } else { //part 2 trading, building
                        buildMenu.setDisable(false);
                        endTurnButton.setDisable(false);
                        tradeButton.setDisable(false);
                        rollDiceButton.setDisable(true);
                        buyDevCard.setDisable(false);
                    }

                } else { //opening phase
                    for (MapGraphNodeContainer mapGraphNodeContainer : mapGraphNodeContainers) {
                        if (mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer() == 666 && mapGraphNodeContainer.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                            mapGraphNodeContainer.getCircle().setVisible(true);
                        }
                    }
                    buildMenu.setDisable(true);
                    endTurnButton.setDisable(true);
                    tradeButton.setDisable(true);
                    rollDiceButton.setDisable(true);
                    buyDevCard.setDisable(true);
                }
            } else { //not users turn
                endTurnButton.setDisable(true);
                rollDiceButton.setDisable(true);
                buyDevCard.setDisable(true);
                tradeButton.setDisable(true);
                buyDevCard.setDisable((true));
                buildMenu.setDisable(true);
            }
        });
    }

}