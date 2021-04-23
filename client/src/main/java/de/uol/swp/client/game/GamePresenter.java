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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    private TradeStartedMessage tradeStartedMessage;

    private ArrayList<HexagonContainer> hexagonContainers = new ArrayList<>();
    private ArrayList<MapGraphNodeContainer> mapGraphNodeContainers = new ArrayList<>();
    private Boolean itsMyTurn = false;
    @Inject
    private GameService gameService;
    @Inject
    private ChatService chatService;
    @FXML
    private Canvas canvas;

    @FXML
    private AnchorPane gameAnchorPane;

    @FXML
    private ListView<String> gameUsersView;

    @FXML
    private Button EndTurnButton;


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
    Button tradeButton;

    @FXML
    public void onTrade(ActionEvent event) {
        String tradeCode = UUID.randomUUID().toString().trim().substring(0, 7);
        eventBus.post(new TradeStartedMessage((UserDTO) this.joinedLobbyUser, this.currentLobby, tradeCode));
        tradeButton.setDisable(true);
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
    public void onBuyDevelopmentCard(ActionEvent event) {
        gameService.buyDevelopmentCard(this.joinedLobbyUser, this.currentLobby);
    }

    /**
     * Method called when the RollDice button is pressed
     * <p>
     * If the RollDice button is pressed, this methods tries to request the GameService to send a RollDiceRequest.
     *
     * @param event The ActionEvent created by pressing the Roll Dice button
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
    public void onRollDice(ActionEvent event) {
        if (this.currentLobby != null) {
            gameService.rollDice(this.currentLobby, this.joinedLobbyUser);
        }
    }

    @FXML
    public void onEndTurn(ActionEvent event) {
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
     * Users in the currentLobby is also requested.
     *
     * @param gcm the GameCreatedMessage given by the original subscriber method.
     * @author Alexander Losse, Ricardo Mook
     * @see GameCreatedMessage
     * @see de.uol.swp.common.game.GameField
     * @since 2021-03-05
     */
    public void gameStartedSuccessfulLogic(GameCreatedMessage gcm) {
        if (this.currentLobby == null) {
            LOG.debug("Requesting update of User list in game scene because game scene was created.");
            this.joinedLobbyUser = gcm.getUser();
            this.currentLobby = gcm.getName();
            updateGameUsersList(gcm.getUsers());
            initializeMatch(gcm.getMapGraph());
            Platform.runLater(this::setupRessourceAlert);
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
     * not, it becomes unclickable.</p>
     *
     * @param response
     * @author Pieter Vogt
     */
    @Subscribe
    public void nextPlayerTurn(NextTurnMessage response) {
        if (response.getGameName().equals(currentLobby)) {
            if (response.getPlayerWithCurrentTurn().equals(joinedLobbyUser.getUsername())) {
                itsMyTurn = true;
                EndTurnButton.setDisable(false);
            } else {
                itsMyTurn = false;
                EndTurnButton.setDisable(true);
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
     * @param event
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

        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //Drawing hexagons

        for (HexagonContainer hexagonContainer : this.hexagonContainers) {

            Vector placementVector = Vector.convertStringListToVector(hexagonContainer.getHexagon().getSelfPosition(), cardSize(), centerOfCanvasVector);

            hexagonContainer.getCircle().setLayoutX(placementVector.getX());
            hexagonContainer.getCircle().setLayoutY(placementVector.getY());
            hexagonContainer.getCircle().setFill(determineColorOfTerrain(hexagonContainer.getHexagon()));

            if (hexagonContainer.getHexagon().getDiceToken() != 0) {
                Text text = new Text(placementVector.getX(), placementVector.getY(), Integer.toString(hexagonContainer.getHexagon().getDiceToken()));
                text.setFill(Color.WHITE);
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
     *
     * @param mapGraph the MapGraph created by the Server
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.game.GameField
     * @see de.uol.swp.client.game.GameObjects.TerrainField
     * @see de.uol.swp.common.game.TerrainFieldContainer
     */
    public void initializeMatch(MapGraph mapGraph) {

        //Setting up the HexagonContainers
        System.out.println("Setting up " + mapGraph.getHexagonHashSet().size() + " HexagonContainers...");

        for (MapGraph.Hexagon hexagon : mapGraph.getHexagonHashSet()) {
            Circle circle = new Circle(cardSize() / 2);
            HexagonContainer hexagonContainer = new HexagonContainer(hexagon, circle);
            this.hexagonContainers.add(hexagonContainer);
            Platform.runLater(() -> gameAnchorPane.getChildren().add(hexagonContainer.getCircle()));
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
                    this.alert.setHeaderText("Yout have not enough Ressources!");
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

    /**
     * Updates the corresponding Node in the list of MapGraphNodes to represent the changes from the message.
     *
     * @param message The data about the changed properties of the MapGraph
     *
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
                    draw();
                    break;
                }
            }
        } else {
            for (MapGraphNodeContainer mapGraphNodeContainer : mapGraphNodeContainers) {
                if (mapGraphNodeContainer.getMapGraphNode().getUuid().equals(message.getUuid())) {
                    MapGraph.StreetNode streetNode = (MapGraph.StreetNode) mapGraphNodeContainer.getMapGraphNode();
                    streetNode.buildRoad(message.getPlayerIndex());
                    draw();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onPrivateInventoryChangeMessage(PrivateInventoryChangeMessage privateInventoryChangeMessage) {
        //TODO: Darstellung der Veränderung des Inventars
    }

    @Subscribe
    public void onPublicInventoryChangeMessage(PublicInventoryChangeMessage publicInventoryChangeMessage) {
        //TODO: Darstellung der Veränderung des Inventars
    }


    //informiert die Spieler die auf das Angebot bieten sollen
    @Subscribe
    public void onTradeOfferInformBiddersMessage(TradeOfferInformBiddersMessage toibm) {
        if (toibm.getName().equals(currentLobby)) {
            tradeButton.setDisable(true);
        }
    }

    //TODO: JavaDoc bitches
    @Subscribe
    public void onTradeSuccessfulMessage(TradeSuccessfulMessage tsm) {
        TradeSuccessfulMessage lastMessage = tsm;
        if (tsm.getName().equals((currentLobby)) ) {
            Platform.runLater(() -> {
                if (tsm.isTradeSuccessful() == true && joinedLobbyUser.getUsername().equals(tsm.getUser().getUsername())) {
                    try {
                        var chatMessageInfo = "The trade: " + tsm.getTradeCode() + " was successful between " + tsm.getUser().getUsername() + " and " + tsm.getBidder().getUsername();
                        var chatId = "game_" + currentLobby;
                        if (!chatMessageInfo.isEmpty()) {
                            RequestChatMessage message = new RequestChatMessage(chatMessageInfo, chatId, joinedLobbyUser.getUsername(),
                                    System.currentTimeMillis());
                            chatService.sendMessage(message);
                        }
                    } catch (Exception e) {
                        LOG.debug(e);
                    }
                } else {
                    try {
                        var chatMessageInfo = "The trade: " + tsm.getTradeCode() + " was  not successful! :(";
                        var chatId = "game_" + currentLobby;
                        if (!chatMessageInfo.isEmpty()) {
                            RequestChatMessage message = new RequestChatMessage(chatMessageInfo, chatId, joinedLobbyUser.getUsername(),
                                    System.currentTimeMillis());
                            chatService.sendMessage(message);
                        }
                    } catch (Exception e) {
                        LOG.debug(e);
                    }
                }
                Platform.runLater(() -> {
                    tradeButton.setDisable(false);
                });
            });
        }
    }

    /**
     * shows an alert if the trade user has not enough in inventory
     *
     * @param message
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
}
