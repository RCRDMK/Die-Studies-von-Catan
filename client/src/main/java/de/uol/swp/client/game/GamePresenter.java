package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.game.GameObjects.BuildingField;
import de.uol.swp.client.game.GameObjects.TerrainField;
import de.uol.swp.client.game.HelperObjects.Vector;
import de.uol.swp.client.game.event.ShowBidderTradeViewEvent;
import de.uol.swp.client.game.event.ShowSellerTradeViewEvent;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.GameField;
import de.uol.swp.common.game.TerrainFieldContainer;
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

    private User joinedLobbyUser;

    private String currentLobby;

    private Alert alert;

    private ButtonType buttonTypeOkay;

    private Button btnOkay;

    private ObservableList<String> gameUsers;

    private TradeStartedMessage tradeStartedMessage;

    //Container for TerrainFields
    private TerrainField[] tfArray;

    //Container for BuildingFields
    private BuildingField[] buildArray;

    //Container for StreetFields
    private BuildingField[] streetArray;

    @Inject
    private GameService gameService;

    @Inject
    private ChatService chatService;

    @FXML
    public TextField gameChatInput;

    @FXML
    public TextArea gameChatArea;

    @FXML
    private Canvas canvas = new Canvas();

    @FXML
    private AnchorPane gameAnchorPane;

    @Inject
    private LobbyService lobbyService;

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
     *
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
     *  When the user presses the trade button a popup window appears. Within it the user can select which ressources
     *  he wants to trade and which amount of it. With a click on the Start a Trade button the startTrade method from the
     *  Gameservice on the client side gets called.
     *
     * @author Alexander Losse, Ricardo Mook
     * @since 2021-04-07
     */
    @FXML
    public void onTrade(ActionEvent event) {//TODO TradeCode schon hier generieren
        String tradeCode = this.joinedLobbyUser +  UUID.randomUUID().toString();
        eventBus.post(new TradeStartedMessage((UserDTO)this.joinedLobbyUser, this.currentLobby, tradeCode));
    }

   /* @Subscribe //TODO JavaDoc
    public void onTradeRegistered(TradeOfferInformBiddersMessage toibm){
        eventBus.post(showBidderViewEvent);
    }*/

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
     *
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
     *
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
            initializeGameField(gcm.getGameField());
            Platform.runLater(this::setupRessourceAlert);
        }
    }

    /**
     * Handles successful leaving of game
     * <p>
     * If a GameLeftSuccessfulResponse is detected on the EventBus the method gameLeftSuccessfulLogic is invoked.
     *
     * @param glsr the GameLeftSuccessfulResponse object seen on the EventBus
     *
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
     *
     * @author Pieter Vogt
     */
    @Subscribe
    public void nextPlayerTurn(NextTurnMessage response) {
        if (response.getGameName().equals(currentLobby)) {
            if (response.getPlayerWithCurrentTurn().equals(joinedLobbyUser.getUsername())) {
                EndTurnButton.setDisable(false);
            } else EndTurnButton.setDisable(true);
        }
    }

    /**
     * The method invoked by gameLeftSuccessful()
     * <p>
     * If the Game is left, meaning this Game Presenter is no longer needed, this presenter will no longer be registered
     * on the event bus and no longer be reachable for responses, messages etc.
     *
     * @param glsr the GameLeftSuccessfulResponse given by the original subscriber method
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     * Method for generating an array of terrainFields, buildFields and streetFields that have the correct relative and
     * absolute positions to one another
     * <p>
     * enhanced by Marc Hermes - 2021-03-13 enhanced by Kirstin Beyer - 2021-03-28 enhanced by Pieter Vogt 2021-04-07
     *
     * @return Object containing array with TerrainFields, array with BuildingFields (for streets), array with
     * BuildingFields (for buildings) having the correct positions.
     * @author Pieter Vogt, Kirstin Beyer
     * @see <a href="https://confluence.swl.informatik.uni-oldenburg.de/display/SWP2020J/SpecCatan_1004+Spielfeld">Specification
     * 1004</a>
     * @since 2021-01-24
     */
    public Object[] getCorrectPositionsOfFields() {

        //TerrainFields

        TerrainField[] tempArray;

        //Array of cards get generated in same order as "spielfeld"-finespec in confluence.

        //beginning of oceans
        TerrainField f0 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f1 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f2 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f3 = new TerrainField(Vector.left(cardSize()));
        TerrainField f4 = new TerrainField(Vector.left(cardSize()));
        TerrainField f5 = new TerrainField(Vector.left(cardSize()));
        TerrainField f6 = new TerrainField(Vector.topLeft((cardSize())));
        TerrainField f7 = new TerrainField(Vector.topLeft((cardSize())));
        TerrainField f8 = new TerrainField(Vector.topLeft((cardSize())));
        TerrainField f9 = new TerrainField(Vector.topRight((cardSize())));
        TerrainField f10 = new TerrainField(Vector.topRight((cardSize())));
        TerrainField f11 = new TerrainField(Vector.topRight((cardSize())));
        TerrainField f12 = new TerrainField(Vector.right((cardSize())));
        TerrainField f13 = new TerrainField(Vector.right((cardSize())));
        TerrainField f14 = new TerrainField(Vector.right((cardSize())));
        TerrainField f15 = new TerrainField(Vector.bottomRight(cardSize()));
        TerrainField f16 = new TerrainField(Vector.bottomRight((cardSize())));
        TerrainField f17 = new TerrainField(Vector.bottomLeft((cardSize())));

        //beginning of landmasses
        TerrainField f18 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f19 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f20 = new TerrainField(Vector.left(cardSize()));
        TerrainField f21 = new TerrainField(Vector.left(cardSize()));
        TerrainField f22 = new TerrainField(Vector.topLeft(cardSize()));
        TerrainField f23 = new TerrainField(Vector.topLeft(cardSize()));
        TerrainField f24 = new TerrainField(Vector.topRight(cardSize()));
        TerrainField f25 = new TerrainField(Vector.topRight(cardSize()));
        TerrainField f26 = new TerrainField(Vector.right(cardSize()));
        TerrainField f27 = new TerrainField(Vector.right(cardSize()));
        TerrainField f28 = new TerrainField(Vector.bottomRight(cardSize()));
        TerrainField f29 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f30 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f31 = new TerrainField(Vector.left(cardSize()));
        TerrainField f32 = new TerrainField(Vector.topLeft(cardSize()));
        TerrainField f33 = new TerrainField(Vector.topRight(cardSize()));
        TerrainField f34 = new TerrainField(Vector.right(cardSize()));
        TerrainField f35 = new TerrainField(Vector.bottomLeft(cardSize()));
        TerrainField f36 = new TerrainField(new Vector(0, 0));
        f36.setPosition(new Vector(((canvas.getWidth() / 2) - cardSize() / 2), ((canvas.getHeight() / 2)) - cardSize() / 2));

        tempArray = new TerrainField[]{f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21, f22, f23, f24, f25, f26, f27, f28, f29, f30, f31, f32, f33, f34, f35, f36};

        for (int i = tempArray.length - 2; i >= 0; i--) { // TempArray.length - 2 because the desert-field (upmost "card") was already positioned, so we dont need to handle it again (index out of bounds when whe try to add tempArray[i+1]...)
            tempArray[i].setPosition(Vector.addVector(tempArray[i + 1].getPosition(), tempArray[i].getPlacementVector())); //Add position of last terrainfield and current placement-vector to determine position.
        }


        //BuildingFields

        BuildingField[] tempStreetArray = new BuildingField[72];
        BuildingField[] tempBuildArray = new BuildingField[54];

        int l = 0;
        int m = 0;
        Vector tempVec;

        // loop over all terrainFields (except ocean)
        for (int i = 18; i < 37; i++) {

            // loop over 12 positions for each terrainField, even positions j mark buildFields, odd positions j mark streetFields
            fieldLoop:
            for (int j = 0; j < 12; j++) {
                tempVec = Vector.addVector(tempArray[i].getPosition(), Vector.generalVector(cardSize() / Math.sqrt(2), 315));

                if (j % 2 != 0) {
                    tempVec = Vector.addVector(tempVec, Vector.generalVector(cardSize() / Math.sqrt(3), 30 * j));

                    // check if field is already in array
                    for (int k = 0; k < l; k++) {
                        if (Math.abs(tempVec.getX() - tempBuildArray[k].getPosition().getX()) < (cardSize() / 100) && Math.abs(tempVec.getY() - tempBuildArray[k].getPosition().getY()) < (cardSize() / 100)) {
                            continue fieldLoop;
                        }
                    }
                    // add new BuildingField to buildArray
                    BuildingField b = new BuildingField(tempVec);
                    b.setName("Settlement");
                    b.setUsed(false);
                    tempBuildArray[l] = b;
                    l++;

                } else {
                    tempVec = Vector.addVector(tempVec, Vector.generalVector(cardSize() * 0.5, 30 * j));

                    // check if field is already in array
                    for (int k = 0; k < m; k++) {
                        if (Math.abs(tempVec.getX() - tempStreetArray[k].getPosition().getX()) < cardSize() / 100 && Math.abs(tempVec.getY() - tempStreetArray[k].getPosition().getY()) < cardSize() / 100) {
                            continue fieldLoop;
                        }
                    }
                    // add new BuildingField to streetArray
                    BuildingField s = new BuildingField(tempVec);
                    s.setName("Street");
                    s.setUsed(false);
                    tempStreetArray[m] = s;
                    m++;
                }
            }
        }

        return new Object[]{tempArray, tempStreetArray, tempBuildArray};
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

        //Setup
        GraphicsContext g = this.canvas.getGraphicsContext2D(); //This is the object that is doing the drawing and has all the graphics related methods.

        //Paint black background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //Draw TerrainFields
        for (int i = tfArray.length - 1; i >= 0; i--) {
            Vector drawPosition = Vector.subVector(tfArray[i].getPosition(), Vector.generalVector(cardSize() / Math.sqrt(2), 135));
            Circle terrainFieldNode = new Circle(cardSize() / 2);
            terrainFieldNode.setLayoutX(drawPosition.getX());
            terrainFieldNode.setLayoutY(drawPosition.getY() + canvas.getLayoutY());
            terrainFieldNode.setFill(tfArray[i].determineColorOfTerrain());
            tfArray[i].setNode(terrainFieldNode);
            Platform.runLater(() -> gameAnchorPane.getChildren().add(terrainFieldNode));
            if (tfArray[i].getDiceToken() != 0) {
                Text text = new Text(drawPosition.getX(), drawPosition.getY() + canvas.getLayoutY(), Integer.toString(tfArray[i].getDiceToken()));
                text.setFill(Color.WHITE);
                Platform.runLater(() -> gameAnchorPane.getChildren().add(text));
            }
        }

        //Draw Buildings
        for (int i = 0; i < 72; i++) {
            initializeBuildingSpots("Street", streetArray[i]);
        }
        for (int i = 0; i < 54; i++) {
            initializeBuildingSpots("Settlement", buildArray[i]);
        }
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
    public void initializeBuildingSpots(String building, BuildingField buildingField) {
        EventHandler<MouseEvent> circleOnMousePressedEventHandler = t -> {
            Circle circle = (Circle) t.getSource();
            circle.setFill(Color.RED);
        };
        GraphicsContext g = this.canvas.getGraphicsContext2D();
        g.setLineWidth(cardSize() / 10);
        double x = buildingField.getPosition().getX();
        double y = buildingField.getPosition().getY();

        switch (building) {
            case "Street":
                double itemSize = cardSize() / 8;
                Circle street = new Circle();
                street.setRadius(itemSize / 2);
                street.setLayoutX(x);
                street.setLayoutY(y + canvas.getLayoutY());
                street.setFill(Color.GHOSTWHITE);
                street.setOnMouseClicked(circleOnMousePressedEventHandler);
                buildingField.setNode(street);
                Platform.runLater(() -> gameAnchorPane.getChildren().add(street));
                break;
            case "Settlement":
                itemSize = cardSize() / 4;
                Circle settlement = new Circle(itemSize / 2);
                settlement.setLayoutX(x);
                settlement.setLayoutY(y + canvas.getLayoutY());
                settlement.setFill(Color.GHOSTWHITE);
                settlement.setOnMouseClicked(circleOnMousePressedEventHandler);
                buildingField.setNode(settlement);
                Platform.runLater(() -> gameAnchorPane.getChildren().add(settlement));
                break;
            case "Town":
                itemSize = cardSize() / 3;
                Circle town = new Circle(itemSize / 2);
                town.setLayoutX(x);
                town.setLayoutY(y + canvas.getLayoutY());
                town.setFill(Color.GHOSTWHITE);
                town.setOnMouseClicked(circleOnMousePressedEventHandler);
                buildingField.setNode(town);
                Platform.runLater(() -> gameAnchorPane.getChildren().add(town));
                break;
        }
    }

    /**
     * Method to initialize the GameField of this GamePresenter of this client
     * <p>
     * First creates the tfArray, then iterates over the terrainFieldContainers of the gameField to get the diceTokens
     * values and copies them to the tfArray of this GamePresenter. Then the values of the fieldTypes are checked and
     * translated into the correct String names of the tfArray TerrainFields.
     *
     * @param gameField the gameField given by the Server
     *
     * @author Marc Hermes
     * @see de.uol.swp.common.game.GameField
     * @see de.uol.swp.client.game.GameObjects.TerrainField
     * @see de.uol.swp.common.game.TerrainFieldContainer
     */
    public void initializeGameField(GameField gameField) {
        Object[] obj = getCorrectPositionsOfFields();
        tfArray = (TerrainField[]) obj[0];
        streetArray = (BuildingField[]) obj[1];
        buildArray = (BuildingField[]) obj[2];

        TerrainFieldContainer[] terrainFieldContainers = gameField.getTFCs();
        for (int i = 0; i < terrainFieldContainers.length; i++) {
            tfArray[i].setDiceToken(terrainFieldContainers[i].getDiceTokens());
            int fieldType = terrainFieldContainers[i].getFieldType();
            String translatedFieldType;
            switch (fieldType) {
                case 0:
                    translatedFieldType = "Ocean";
                    break;
                case 1:
                    translatedFieldType = "Forest";
                    break;
                case 2:
                    translatedFieldType = "Farmland";
                    break;
                case 3:
                    translatedFieldType = "Grassland";
                    break;
                case 4:
                    translatedFieldType = "Hillside";
                    break;
                case 5:
                    translatedFieldType = "Mountain";
                    break;
                default:
                    translatedFieldType = "Desert";
                    break;
            }
            tfArray[i].setName(translatedFieldType);
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
     *
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
    public void onTradeOfferInformBiddersMessage(TradeOfferInformBiddersMessage toibm){
        if(toibm.getName().equals(currentLobby)){
            //TODO: client chooses what to do
        }
    }

    //informiert den Verkäufer über die Gebote.
    @Subscribe
    public void onTradeInformSellerAboutBidsMessage(TradeInformSellerAboutBidsMessage tisabm){
            if(tisabm.getName().equals(currentLobby)){
                //TODO: client chooses which bid to accept
            }
    }

    @Subscribe
    public void onTradeSuccessfulMessage(TradeSuccessfulMessage tsm){
        if(tsm.getName().equals((currentLobby))){
            if(tsm.isTradeSuccessful() == true){
                //TODO: Show which trade was successful
            }else{
                //TODO: Show no trade successful
            }
            //TODO: Close tradewindow
        }
    }
}
