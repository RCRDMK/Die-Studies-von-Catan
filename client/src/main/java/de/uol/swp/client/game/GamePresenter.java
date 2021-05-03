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
import de.uol.swp.common.game.MapGraph;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.ResourcesToDiscardRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.game.AllThisGameUsersResponse;
import de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;
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

public class GamePresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/GameView.fxml";

    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

    @FXML
    public TextField gameChatInput;

    @FXML
    public TextArea gameChatArea;

    private User joinedLobbyUser;

    private String currentLobby;

    private Alert alert;

    private ButtonType buttonTypeOkay;

    private Button btnOkay;

    private ObservableList<String> gameUsers;

    private ArrayList<HexagonContainer> hexagonContainers = new ArrayList<>();

    private ArrayList<MapGraphNodeContainer> mapGraphNodeContainers = new ArrayList<>();

    private Boolean itsMyTurn = false;

    private HashMap<String, Integer> privateInventory;

    @Inject
    private GameService gameService;

    @Inject
    private ChatService chatService;

    @FXML
    private Canvas canvas;

    private ArrayList<ImagePattern> profilePicturePatterns = new ArrayList<>();

    private ArrayList<Rectangle> rectangles = new ArrayList<>();

    @FXML
    private AnchorPane gameAnchorPane;

    @FXML
    private ListView<String> gameUsersView;

    @FXML
    private Button EndTurnButton;

    @FXML
    private Button tradeButton;

    @FXML
    private Pane picturePlayerView1;

    @FXML
    private Pane picturePlayerView2;

    @FXML
    private Pane picturePlayerView3;

    @FXML
    private Pane picturePlayerView4;

    @FXML
    private Button rollDiceButton;

    @FXML
    private GridPane playerOneDiceView;

    @FXML
    private GridPane playerTwoDiceView;

    @FXML
    private GridPane playerThreeDiceView;

    @FXML
    private GridPane playerFourDiceView;

    final private ArrayList<ImagePattern> diceImages = new ArrayList<>();

    final private Rectangle rectangleDie1 = new Rectangle(100, 100);

    final private Rectangle rectangleDie2 = new Rectangle(100, 100);

    @FXML
    private Button rollDice;

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

    @FXML
    private Rectangle robber;

    /**
     * Method called when the send Message button is pressed
     * <p>
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage If this will result in an exception, go log the exception
     *
     * @param event The ActionEvent created by pressing the send Message button
     * @author René, Sergej
     * @see de.uol.swp.client.chat.ChatService
     * @since 2021-03-08
     */
    @FXML
    void onSendMessage(ActionEvent event) {
        try {
            var chatMessage = gameChatInput.getCharacters().toString();
            // ChatID = game_lobbyname so we have seperate lobby and game chat separated by id
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
     * Adds the ResponseChatMessage to the textArea
     *
     * @param message
     * @author René Meyer
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-03-13
     */
    private void updateChat(ResponseChatMessage message) {
        updateChatLogic(message);
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
     */
    private void updateChatLogic(ResponseChatMessage rcm) {
        var time = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date((long) rcm.getTime().doubleValue());
        var readableTime = time.format(resultdate);
        gameChatArea.insertText(gameChatArea.getLength(), readableTime + " " + rcm.getUsername() + ": " + rcm.getMessage() + "\n");
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
     * This method is called when the Trade button is pressed
     * <p>
     * When the user presses the trade button a popup window appears. Within it the user can select which ressources
     * he wants to trade and which amount of it. With a click on the Start a Trade button the startTrade method from the
     * Gameservice on the client side gets called.
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */

    @FXML
    public void onTrade(ActionEvent event) {
        String tradeCode = UUID.randomUUID().toString().trim().substring(0, 7);
        gameService.sendTradeStartedRequest((UserDTO) this.joinedLobbyUser, this.currentLobby, tradeCode);

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
     */
    @FXML
    public void onRollDice() {
        if (this.currentLobby != null) {
            gameService.rollDice(this.currentLobby, (UserDTO) this.joinedLobbyUser);
        }
    }

    //TODO JavaDoc fehlt
    @FXML
    public void onEndTurn() {
        eventBus.post(new EndTurnRequest(this.currentLobby, (UserDTO) this.joinedLobbyUser));
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
     * @see de.uol.swp.common.game.GameField
     * @since 2021-03-05
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-04-22
     */
    public void gameStartedSuccessfulLogic(GameCreatedMessage gcm) {
        if (this.currentLobby == null) {
            LOG.debug("Requesting update of User list in game scene because game scene was created.");
            this.joinedLobbyUser = gcm.getUser();
            this.currentLobby = gcm.getName();
            updateGameUsersList(gcm.getUsers());
            initializeMatch(gcm.getMapGraph());
            for (int i = 1; i <= 64; i++) {
                Image image;
                image = new Image("img/profilePictures/" + i + ".png");
                ImagePattern imagePattern;
                imagePattern = new ImagePattern(image);
                profilePicturePatterns.add(imagePattern);
            }
            Platform.runLater(() -> {
                setupPlayerPictures(gcm.getUsers());
                setupRessourceAlert();
                initializeRobberResourceMenu();
                setupRobberAlert();
                setupDicesAtGameStart();
            });
        }
    }

    /**
     * This method initializes the menu where the player has to choose, which resource he wants to give to the player,
     * that moved the robber.
     * <p>
     * The method initializes an array of 5 rectangles and fills it with the pictures of the resources. After that,
     * it creates 10 buttons and sets some icons, to indicate the buttons.
     * If this is complete, the method puts the buttons and rectangles into a gridpane that is shown besides the chat.
     * After this initialization the pane gets invisible and will only be shown by the TooMuchResourceCarsMessage.
     *
     * @author Marius Birk
     * @since 2021-04-19
     */
    public void initializeRobberResourceMenu() {
        this.alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        chooseResource = new GridPane();
        this.privateInventory = new HashMap<>();
        this.privateInventory.put("Lumber", 0);
        this.privateInventory.put("Grain", 0);
        this.privateInventory.put("Wool", 0);
        this.privateInventory.put("Brick", 0);
        this.privateInventory.put("Ore", 0);


        //Initialize the robber menu
        Rectangle[] resources = new Rectangle[5];
        resources[0] = new Rectangle(30, 30);
        resources[1] = new Rectangle(30, 30);
        resources[2] = new Rectangle(30, 30);
        resources[3] = new Rectangle(30, 30);
        resources[4] = new Rectangle(30, 30);

        resources[0].setFill(new ImagePattern(new Image("textures/originals/RES_Holz.png")));
        resources[1].setFill(new ImagePattern(new Image("textures/originals/RES_Getreide.png")));
        resources[2].setFill(new ImagePattern(new Image("textures/originals/RES_Wolle.png")));
        resources[3].setFill(new ImagePattern(new Image("textures/originals/RES_Lehm.png")));
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
        woolLabelRobberMenu = new Label();
        oreLabelRobberMenu = new Label();
        grainLabelRobberMenu = new Label();
        toDiscardLabel = new Label();

        chooseResource.add(lumberLabelRobberMenu, 0, 2);
        chooseResource.add(grainLabelRobberMenu, 1, 2);
        chooseResource.add(woolLabelRobberMenu, 2, 2);
        chooseResource.add(brickLabelRobberMenu, 3, 2);
        chooseResource.add(oreLabelRobberMenu, 4, 2);
        chooseResource.add(new Label("Amount of Cards to discard:"), 0, 4);
        chooseResource.add(toDiscardLabel, 1, 4);

        chooseResource.setVgap(40);
        chooseResource.setHgap(30);

        initializedResourceButtons();

        //Initializing robber on the canvas
        robber.setLayoutX((canvas.getWidth() / 2 + canvas.getLayoutX()) - (robber.getWidth() / 2));
        robber.setLayoutY((canvas.getHeight() / 2 + canvas.getLayoutY()) - (robber.getHeight() / 2));
        Platform.runLater(() -> {
            gameAnchorPane.getChildren().add(robber);
        });
    }

    public void initializedResourceButtons() {
        choose[0].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (Integer.parseInt(lumberLabelRobberMenu.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())-1));
                    lumberLabelRobberMenu.setText(Integer.toString(Integer.parseInt(lumberLabelRobberMenu.getText()) - 1));
                }
            }
        });

        choose[5].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (privateInventory.get("Lumber") > Integer.parseInt(lumberLabelRobberMenu.getText())) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())+1));
                    lumberLabelRobberMenu.setText(Integer.toString(Integer.parseInt(lumberLabelRobberMenu.getText()) + 1));
                }
            }
        });

        choose[1].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (Integer.parseInt(grainLabelRobberMenu.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())-1));
                    grainLabelRobberMenu.setText(Integer.toString(Integer.parseInt(grainLabelRobberMenu.getText()) - 1));
                }
            }
        });
        choose[6].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (privateInventory.get("Grain") > Integer.parseInt(grainLabelRobberMenu.getText())) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())+1));
                    grainLabelRobberMenu.setText(Integer.toString(Integer.parseInt(grainLabelRobberMenu.getText()) + 1));
                }
            }
        });
        choose[2].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (Integer.parseInt(woolLabelRobberMenu.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())-1));
                    woolLabelRobberMenu.setText(Integer.toString(Integer.parseInt(woolLabelRobberMenu.getText()) - 1));
                }
            }
        });
        choose[7].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (privateInventory.get("Wool") > Integer.parseInt(woolLabelRobberMenu.getText())) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())+1));
                    woolLabelRobberMenu.setText(Integer.toString(Integer.parseInt(woolLabelRobberMenu.getText()) + 1));
                }
            }
        });
        choose[3].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (Integer.parseInt(brickLabelRobberMenu.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())-1));
                    brickLabelRobberMenu.setText(Integer.toString(Integer.parseInt(brickLabelRobberMenu.getText()) - 1));
                }
            }
        });
        choose[8].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (privateInventory.get("Brick") > Integer.parseInt(brickLabelRobberMenu.getText())) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())+1));
                    brickLabelRobberMenu.setText(Integer.toString(Integer.parseInt(brickLabelRobberMenu.getText()) + 1));
                }
            }
        });
        choose[4].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (Integer.parseInt(oreLabelRobberMenu.getText()) > 0) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())-1));
                    oreLabelRobberMenu.setText(Integer.toString(Integer.parseInt(oreLabelRobberMenu.getText()) - 1));
                }
            }
        });
        choose[9].setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (privateInventory.get("Ore") > Integer.parseInt(oreLabelRobberMenu.getText())) {
                    toDiscardLabel.setText(Integer.toString(Integer.parseInt(toDiscardLabel.getText())+1));
                    oreLabelRobberMenu.setText(Integer.toString(Integer.parseInt(oreLabelRobberMenu.getText()) + 1));
                }
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
    public void setupPlayerPictures(ArrayList<UserDTO> list) {
        for (UserDTO userDTO : list) {
            Rectangle rectangle = new Rectangle(100, 100);
            rectangle.setFill(profilePicturePatterns.get(userDTO.getProfilePictureID() - 1));
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
     *     First a alert is instanciated and the content text and the title are set.
     *     Now the amount of cards, that need to be discarded are set and the labels of the resources are set.
     *     After that the method checks if the buttons, which are needed to select, which resource the player wants to discard, are disabled
     *     or not.
     *     If the user clicked "OK" all values from the labels will be put into an HashMap and are send to the server.
     * @author Marius Birk
     * @since 2021-04-19
     */
    public void showRobberResourceMenu(TooMuchResourceCardsMessage tooMuchResourceCardsMessage) {
        Alert tooMuchAlert = new Alert(Alert.AlertType.INFORMATION);
        tooMuchAlert.setHeaderText("Choose the resources you want to discard!");
        tooMuchAlert.setTitle(tooMuchResourceCardsMessage.getName());
        toDiscardLabel.setText(Integer.toString(tooMuchResourceCardsMessage.getCards()));
        tooMuchAlert.getDialogPane().setContent(chooseResource);

        if (privateInventory.get("Lumber") != 0) {
            choose[0].setDisable(false);
            choose[5].setDisable(false);
        } else {
            choose[0].setDisable(true);
            choose[5].setDisable(true);
        }
        if (privateInventory.get("Grain") != 0) {
            choose[1].setDisable(false);
            choose[6].setDisable(false);
        } else {
            choose[1].setDisable(true);
            choose[6].setDisable(true);
        }
        if (privateInventory.get("Wool") != 0) {
            choose[2].setDisable(false);
            choose[7].setDisable(false);
        } else {
            choose[2].setDisable(true);
            choose[7].setDisable(true);
        }
        if (privateInventory.get("Brick") != 0) {
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
        grainLabelRobberMenu.setText(Integer.toString(privateInventory.get("Grain")));
        brickLabelRobberMenu.setText(Integer.toString(privateInventory.get("Brick")));
        oreLabelRobberMenu.setText(Integer.toString(privateInventory.get("Ore")));
        woolLabelRobberMenu.setText(Integer.toString(privateInventory.get("Wool")));

        tooMuchAlert.showAndWait();

        if (tooMuchAlert.getResult().getText().equals("OK")) {
            HashMap<String, Integer> inventory = new HashMap();
            inventory.put("Lumber", Integer.parseInt(lumberLabelRobberMenu.getText()));
            inventory.put("Grain", Integer.parseInt(grainLabelRobberMenu.getText()));
            inventory.put("Brick", Integer.parseInt(brickLabelRobberMenu.getText()));
            inventory.put("Ore", Integer.parseInt(oreLabelRobberMenu.getText()));
            inventory.put("Wool", Integer.parseInt(woolLabelRobberMenu.getText()));

            ResourcesToDiscardRequest resourcesToDiscard = new ResourcesToDiscardRequest(tooMuchResourceCardsMessage.getName(), (UserDTO) tooMuchResourceCardsMessage.getUser(), inventory);
            eventBus.post(resourcesToDiscard);
        }
    }


    /**
     * Handles successful leaving of game
     * <p>
     * If a GameLeftSuccessfulResponse is detected on the EventBus the method gameLeftSuccessfulLogic is invoked.
     *
     * @param glsr the GameLeftSuccessfulResponse object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse
     * @since 2021-03-15
     */
    @Subscribe
    public void gameLeftSuccessful(GameLeftSuccessfulResponse glsr) {
        gameLeftSuccessfulLogic(glsr);
    }

    /**
     * Changes the clickability of the button for ending your turn.
     *
     * <p>This method checks, if the the games name equals the name of the game in the message. If so, and if you are
     * the player with the current turn (transported in message), your button for ending your turn gets clickable. If
     * not, it becomes unclickable. It also invokes the passTheDice method and manages the visibility of the diceViews.</p>
     *
     * @param response //TODO JavaDoc
     * @author Pieter Vogt
     *
     * Enhanced by Carsten Dekker
     * @since 2021-04-30
     */
    @Subscribe
    public void nextPlayerTurn(NextTurnMessage response) {
        if (response.getGameName().equals(currentLobby)) {
            if (response.getPlayerWithCurrentTurn().equals(joinedLobbyUser.getUsername())) {
                itsMyTurn = true;
                EndTurnButton.setDisable(false);
                rollDice.setDisable(false);
                buyDevCard.setDisable(false);
                tradeButton.setDisable(false);
            } else {
                itsMyTurn = false;
                EndTurnButton.setDisable(true);
                rollDice.setDisable(true);
                buyDevCard.setDisable(true);
                tradeButton.setDisable(true);
            }
            if (!response.isInStartingTurn()) {
                if (response.getTurn() == 0) {
                    passTheDice(playerFourDiceView, playerOneDiceView);
                    playerOneDiceView.setVisible(true);
                    playerFourDiceView.setVisible(false);
                } else if (response.getTurn() == 1) {
                    passTheDice(playerOneDiceView, playerTwoDiceView);
                    playerOneDiceView.setVisible(false);
                    playerTwoDiceView.setVisible(true);
                } else if (response.getTurn() == 2) {
                    passTheDice(playerTwoDiceView, playerThreeDiceView);
                    playerTwoDiceView.setVisible(false);
                    playerThreeDiceView.setVisible(true);
                } else if (response.getTurn() == 3) {
                    passTheDice(playerThreeDiceView, playerFourDiceView);
                    playerThreeDiceView.setVisible(false);
                    playerFourDiceView.setVisible(true);
                }
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
     * @see de.uol.swp.common.user.response.game.GameLeftSuccessfulResponse
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
     * @param event \\TODO JavaDoc fehlt hier
     * @author Ricardo Mook, Alexander Losse
     * @see de.uol.swp.client.game.GameService
     * @see de.uol.swp.client.game.GamePresenterException
     * @since 2021-03-04
     */
    @FXML
    public void onLeaveGame(ActionEvent event) {

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
     * @see de.uol.swp.common.user.response.game.AllThisGameUsersResponse
     * @since 2021-03-14
     */
    public void gameUserListLogic(AllThisGameUsersResponse atgur) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(atgur.getName())) {
                LOG.debug("Update of user list " + atgur.getUsers());
                updateGameUsersList(atgur.getUsers());

            }
        }
    }

    /**
     * Updates the game menu user list of the current game according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user in the list given to the game menu
     * user list. If there ist no user list this creates one.
     *
     * @param gameUserList A list of UserDTO objects including all currently logged in users
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Iskander Yusupov , @design Marc Hermes, Ricardo Mook
     * @see de.uol.swp.common.user.UserDTO
     * @since 2020-03-14
     */
    private void updateGameUsersList(List<UserDTO> gameUserList) {
        updateGameUsersListLogic(gameUserList);
    }

    public void updateGameUsersListLogic(List<UserDTO> l) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (gameUsers == null) {
                gameUsers = FXCollections.observableArrayList();
                gameUsersView.setItems(gameUsers);
            }
            gameUsers.clear();
            l.forEach(u -> gameUsers.add(u.getUsername()));
        });
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
        double d = Math.min(canvas.getHeight(), canvas.getWidth()); //Determine minimum pixels in height and length of the canvas (we dont want the playfield to scale out of canvas, so we orient at the smaller axis)
        return d / 8; // Divide by 8 because the playfield is 7 cards wide and add 1/2 card each side for margin so the cards dont touch the boundaries of the canvas.
    }

    /**
     * Determines the color to draw its host-object.
     *
     * @return the color for the host-object
     * @author Pieter Vogt
     * @since 2021-01-04
     */
    public Color determineColorOfTerrain(MapGraph.Hexagon h) {
        Color c;
        //"Ocean" = 0; "Forest" = 1; "Farmland" = 2; "Grassland" = 3; "Hillside" = 4; "Mountain" = 5; "Desert" = 6;
        switch (h.getTerrainType()) {
            case 1:
                c = Color.OLIVEDRAB;
                break;
            case 2:
                c = Color.GOLDENROD;
                break;
            case 3:
                c = Color.LAWNGREEN;
                break;
            case 4:
                c = Color.LIGHTCORAL;
                break;
            case 5:
                c = Color.GREY;
                break;
            case 0:
                c = Color.DODGERBLUE;
                break;
            default:
                c = Color.BLANCHEDALMOND;
                break;
        }
        return c;
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
     * This method draws its items from back to front, meaning backmost items need to be drawn first and so on. This is
     * why the background is drawn first, etc.
     * </p>
     * <p>
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
            Platform.runLater(() -> {
                hexagonContainer.getHexagonShape().setFill(determinePictureOfTerrain(hexagonContainer.getHexagon()));
            });

            if (hexagonContainer.getHexagon().getDiceToken() != 0) {
                Text text = new Text(placementVector.getX(), placementVector.getY(), Integer.toString(hexagonContainer.getHexagon().getDiceToken()));
                text.setFill(Color.BLACK);
                Platform.runLater(() -> gameAnchorPane.getChildren().add(text));
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

                mapGraphNodeContainer.getCircle().setFill(determinePlayerColorByIndex(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer()));


            } else {
                double itemSize = cardSize() / 15;
                MapGraph.StreetNode streetNode = (MapGraph.StreetNode) mapGraphNodeContainer.getMapGraphNode();

                Vector parentVector = Vector.convertStringListToVector(streetNode.getParent().getSelfPosition(), cardSize(), centerOfCanvasVector);

                Vector selfVector = Vector.getVectorFromMapGraphNode(streetNode, cardSize());
                Vector drawVector = Vector.addVector(parentVector, selfVector);

                Circle circle = mapGraphNodeContainer.getCircle();
                circle.setRadius(itemSize);
                circle.setLayoutX(drawVector.getX());
                circle.setLayoutY(drawVector.getY());

                circle.setFill(determinePlayerColorByIndex(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer()));
            }
        }
    }

    /**
     * Determine the right color for a drawn, player-owned object.
     *
     * @return Correct Color for a player-owned Object.
     * @author Pieter Vogt
     * @since 2021-04-15
     */
    public Color determinePlayerColorByIndex(int playerIndex) {
        switch (playerIndex) {
            case 0:
                return Color.color(1.0, 0.4, 0.4);
            case 1:
                return Color.color(0.4, 0.5, 1.0);
            case 2:
                return Color.color(1.0, 1.0, 0.4);
            case 3:
                return Color.color(0.5, 1.0, 0.4);
        }
        return Color.color(0.5, 0.5, 0.5);
    }

    /**
     * Method to draw buildings to the screen.
     * <p>
     * Creates the Spots (Circles) for the Buildings. If a Circle is clicked, it changes it's colour.
     * <p>
     * enhanced by Marc Hermes 2021-03-31
     *
     * @author Kirstin
     * @since 2021-03-28
     */
    public void initializeNodeSpots(Object o) {

        EventHandler<MouseEvent> clickOnCircleHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                for (MapGraphNodeContainer container : mapGraphNodeContainers) {
                    if (mouseEvent.getSource().equals(container.getCircle()) && itsMyTurn == true) {
                        String typeOfNode;
                        if (container.getMapGraphNode() instanceof MapGraph.BuildingNode) {
                            typeOfNode = "BuildingNode";
                        } else {
                            typeOfNode = "StreetNode";
                        }
                        UserDTO user = new UserDTO(joinedLobbyUser.getUsername(), joinedLobbyUser.getPassword(), joinedLobbyUser.getEMail()); //Still sent with password because tight deadline. TODO: Change responsible Interface in future, to send Users without Passwords.
                        gameService.constructBuilding(user, currentLobby, container.getMapGraphNode().getUuid(), typeOfNode);
                    }
                }
            }
        };

        for (MapGraphNodeContainer container : mapGraphNodeContainers) {
            container.getCircle().setOnMouseClicked(clickOnCircleHandler);
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
     * @see de.uol.swp.common.game.GameField
     * @see de.uol.swp.client.game.GameObjects.TerrainField
     * @see de.uol.swp.common.game.TerrainFieldContainer
     * @since 2021-04-20
     */
    public void initializeMatch(MapGraph mapGraph) {

        //Setting up the HexagonContainers
        System.out.println("Setting up " + mapGraph.getHexagonHashSet().size() + " HexagonContainers...");

        for (MapGraph.Hexagon hexagon : mapGraph.getHexagonHashSet()) {
            HexagonContainer hexagonContainer = new HexagonContainer(hexagon, cardSize());
            this.hexagonContainers.add(hexagonContainer);
            Platform.runLater(() -> gameAnchorPane.getChildren().add(hexagonContainer.getHexagonShape()));
        }

        //Setting up the BuildingNodeContainers
        System.out.println("Setting up " + mapGraph.getBuildingNodeHashSet().size() + " BuildingNodeContainers...");

        for (MapGraph.BuildingNode buildingNode : mapGraph.getBuildingNodeHashSet()) {
            MapGraphNodeContainer mapGraphNodeContainer = new MapGraphNodeContainer(new Circle(cardSize() / 6), buildingNode);
            this.mapGraphNodeContainers.add(mapGraphNodeContainer);
            initializeNodeSpots(mapGraphNodeContainer);
            Platform.runLater(() -> gameAnchorPane.getChildren().add(mapGraphNodeContainer.getCircle()));
        }

        //Setting up the StreetNodeContainers
        System.out.println("Setting up " + mapGraph.getStreetNodeHashSet().size() + " StreetNodeContainers...");

        for (MapGraph.StreetNode streetNode : mapGraph.getStreetNodeHashSet()) {
            MapGraphNodeContainer mapGraphNodeContainer = new MapGraphNodeContainer(new Circle(cardSize() / 8), streetNode);
            this.mapGraphNodeContainers.add(mapGraphNodeContainer);
            initializeNodeSpots(mapGraphNodeContainer);
            Platform.runLater(() -> gameAnchorPane.getChildren().add(mapGraphNodeContainer.getCircle()));
        }


        //Draw robber
        //Initialize the robber graphics
        robber = new Rectangle(30, 30);
        robber.setFill(new ImagePattern(new Image("textures/originals/robbers.png")));
        robber.setVisible(true);

        draw();
    }

    @Subscribe
    public void onBuyDevelopmentCardMessage(BuyDevelopmentCardMessage buyDevelopmentCardMessage) {
        buyDevelopmentCardLogic(buyDevelopmentCardMessage.getDevCard());
    }

    public void buyDevelopmentCardLogic(String card) {
        // TODO Reaktion des Clients kann erst richtig implementiert werden, wenn die Nutzer auch Ressourcen haben.
    }

    @Subscribe
    public void onNotEnoughRessourcesMessages(NotEnoughRessourcesMessage notEnoughRessourcesMessage) {
        notEnoughRessourcesMessageLogic(notEnoughRessourcesMessage);
    }

    /**
     * The method invoked by NotEnoughRessourceMessage
     * <p>
     * This method reacts to the NotEnoughRessourcesMessage and shows the corresponding alert window.
     *
     * @param notEnoughRessourcesMessage
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Marius Birk
     * @see de.uol.swp.common.game.message.NotEnoughRessourcesMessage
     * @since 2021-04-03
     */
    public void notEnoughRessourcesMessageLogic(NotEnoughRessourcesMessage notEnoughRessourcesMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(notEnoughRessourcesMessage.getName())) {
                Platform.runLater(() -> {
                    this.alert.setTitle(notEnoughRessourcesMessage.getName());
                    this.alert.setHeaderText("You have not enough Ressources!");
                    this.alert.show();
                });
            }
        }
    }

    /**
     * The method invoked when the Game Presenter is first used.
     * <p>
     * The Alert tells the user, that he doesn't have enough ressources to buy a development card. The user can only
     * click the showed button to close the dialog.
     *
     * @author Marius Birk
     * @since 2021-04-03
     */
    public void setupRessourceAlert() {
        this.alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        this.buttonTypeOkay = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(buttonTypeOkay);
        this.btnOkay = (Button) alert.getDialogPane().lookupButton(buttonTypeOkay);
        btnOkay.setOnAction(event -> {
            alert.close();
            event.consume();
        });
    }

    @Subscribe
    public void onMoveRobberMessage(MoveRobberMessage moveRobberMessage) {
        moveRobberMessageLogic(moveRobberMessage);
    }

    /**
     * This method will be invoked if a MoveRobberMessage is detected on the eventBus.
     * <p>
     * At first it checks if the current lobby is null and if that, it checks if the current lobby is the lobby we want to work in.
     * After a successfull check the method calls an alert on another thread to inform the user, that he can move the robber.
     * To know, where the user has clicked, we need to create an evenhandler and override the handle method. in the handle
     * method we iterate over every hexagon and check if the mouse was pressed on it. Now it can call the movedRobber method
     * in the gameService and it can remove the eventhandler from the hexagons.
     *
     * @param moveRobberMessage
     * @author Marius Birk
     * @since 2021-04-20
     */
    public void moveRobberMessageLogic(MoveRobberMessage moveRobberMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(moveRobberMessage.getName())) {
                Platform.runLater(() -> {
                    this.alert.setTitle(moveRobberMessage.getName());
                    this.alert.setHeaderText("Click on a field to move the Robber!");
                    this.alert.show();
                });

                //adding a eventhandler to know where the user wants to set the robber
                EventHandler<MouseEvent> clickOnHexagonHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        for (HexagonContainer container : hexagonContainers) {
                            if (mouseEvent.getSource().equals(container.getHexagonShape()) && itsMyTurn == true) {
                                if (container.getHexagon().getTerrainType() != 6) {
                                    for (HexagonContainer container1 : hexagonContainers) {
                                        container1.getHexagonShape().removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                                    }
                                    gameService.movedRobber(moveRobberMessage.getName(), moveRobberMessage.getUser(), container.getHexagon().getUuid());
                                }
                            }

                        }
                    }
                };
                for (HexagonContainer container : hexagonContainers) {
                    container.getHexagonShape().addEventHandler(MouseEvent.MOUSE_PRESSED, clickOnHexagonHandler);
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
     * The Alert tells the user, that he has to move the robber to a new field. The user can only
     * click the showed button to close the dialog.
     *
     * @author Marius Birk
     * @since 2021-04-20
     */
    public void setupChoosePlayerAlert(ChoosePlayerMessage choosePlayerMessage) {
        Alert chooseAlert = new Alert(Alert.AlertType.WARNING);
        chooseAlert.getButtonTypes().setAll();
        chooseAlert.setTitle(choosePlayerMessage.getName());
        chooseAlert.setContentText("Choose a player to draw a card from!");
        for (int i = 0; i < choosePlayerMessage.getUserList().size(); i++) {
            if (!choosePlayerMessage.getUserList().get(i).equals(choosePlayerMessage.getUser().getUsername())) {
                chooseAlert.getButtonTypes().add(new ButtonType(choosePlayerMessage.getUserList().get(i)));
            }
        }
        chooseAlert.showAndWait();
        gameService.drawRandomCardFromPlayer(choosePlayerMessage.getName(), choosePlayerMessage.getUser(), chooseAlert.getResult().getText());
        chooseAlert.close();
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
            newGridPane.add(rectangleDie1, 0, 0);
            rectangleDie2.setFill(diceImages.get(0));
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
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
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
        if (message.getTypeOfNode().equals("BuildingNode")) {
            for (MapGraphNodeContainer mapGraphNodeContainer : mapGraphNodeContainers) {
                if (mapGraphNodeContainer.getMapGraphNode().getUuid().equals(message.getUuid())) {
                    MapGraph.BuildingNode buildingNode = (MapGraph.BuildingNode) mapGraphNodeContainer.getMapGraphNode();
                    buildingNode.buildOrDevelopSettlement(message.getPlayerIndex());
                    mapGraphNodeContainer.getCircle().setFill(determinePlayerColorByIndex(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer()));
                    break;
                }
            }
        } else {
            for (MapGraphNodeContainer mapGraphNodeContainer : mapGraphNodeContainers) {
                if (mapGraphNodeContainer.getMapGraphNode().getUuid().equals(message.getUuid())) {
                    MapGraph.StreetNode streetNode = (MapGraph.StreetNode) mapGraphNodeContainer.getMapGraphNode();
                    streetNode.buildRoad(message.getPlayerIndex());
                    mapGraphNodeContainer.getCircle().setFill(determinePlayerColorByIndex(mapGraphNodeContainer.getMapGraphNode().getOccupiedByPlayer()));
                    break;
                }
            }
        }
    }

    /**
     * This method will be invoked if the robber is successfully moved on the gamefield.
     * <p>
     * If the robber is successfully moved on the gamefield and needs to be moved.
     * The method iterates over every hexagon on the gamefield and checks if the uuid of the hexagon is the same
     * as the uuid in the SuccessfullMovedRobberMessage. If this is true, the robbers layout will be set to the
     * hexagons layout. From that layout we substract the half of the height/width of the robber, because the layout
     * is determined as the upper left edge of the robber.
     *
     * @param successfullMovedRobberMessage
     * @author Marius Birk
     * @since 2021-04-22
     */
    @Subscribe
    public void onSuccessfullMovedRobberMessage(SuccessfullMovedRobberMessage successfullMovedRobberMessage) {
        for (HexagonContainer hexagonContainer : hexagonContainers) {
            if (hexagonContainer.getHexagon().getUuid().equals(successfullMovedRobberMessage.getNewField())) {
                robber.setLayoutX(hexagonContainer.getHexagonShape().getLayoutX() - (robber.getWidth() / 2));
                robber.setLayoutY(hexagonContainer.getHexagonShape().getLayoutY() - (robber.getHeight() / 2));
            }
        }
    }

    @Subscribe
    public void onPrivateInventoryChangeMessage(PrivateInventoryChangeMessage privateInventoryChangeMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(privateInventoryChangeMessage.getName())) {
                this.privateInventory = privateInventoryChangeMessage.getPrivateInventory();
                lumberLabelRobberMenu.setText(Integer.toString((Integer) privateInventoryChangeMessage.getPrivateInventory().get("Lumber")));
                grainLabelRobberMenu.setText(Integer.toString((Integer) privateInventoryChangeMessage.getPrivateInventory().get("Grain")));
                woolLabelRobberMenu.setText(Integer.toString((Integer) privateInventoryChangeMessage.getPrivateInventory().get("Wool")));
                brickLabelRobberMenu.setText(Integer.toString((Integer) privateInventoryChangeMessage.getPrivateInventory().get("Brick")));
                oreLabelRobberMenu.setText(Integer.toString((Integer) privateInventoryChangeMessage.getPrivateInventory().get("Ore")));
            }
        }
    }

    @Subscribe
    public void onPublicInventoryChangeMessage(PublicInventoryChangeMessage publicInventoryChangeMessage) {
        //TODO: Darstellung der Veränderung des Inventars
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
     * This method is invoked if a TooMuchResourceCardsMessage is layed on the bus.
     *
     * @param tooMuchResourceCardsMessage
     */
    @Subscribe
    public void onTooMuchRessourceCardsMessage(TooMuchResourceCardsMessage tooMuchResourceCardsMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(tooMuchResourceCardsMessage.getName())) {
                Platform.runLater(() -> showRobberResourceMenu(tooMuchResourceCardsMessage));
            }
        }
    }

    /**
     * This method will be invoked if a choosePlayerMessage is layed on the bus.
     * <p>
     * The method sets up an alert to choose a player to draw a card from.
     * That will be done on another Thread.
     *
     * @param choosePlayerMessage
     * @author Marius Birk
     * @since 2021-05-01
     */
    @Subscribe
    public void onChoosePlayerMessage(ChoosePlayerMessage choosePlayerMessage) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(choosePlayerMessage.getName())) {
                Platform.runLater(() -> setupChoosePlayerAlert(choosePlayerMessage));
            }
        }
    }
}
