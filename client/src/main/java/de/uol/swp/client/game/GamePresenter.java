package de.uol.swp.client.game;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.GameObjects.TerrainField;
import de.uol.swp.client.game.HelperObjects.Vector;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.response.GameCreatedSuccessfullyResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages the GameView
 * <p>
 * Class was build exactly like LobbyPresenter.
 *
 * @author Carsten Dekker
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-01-13
 */

public class GamePresenter extends AbstractPresenter implements Initializable {

    public static final String fxml = "/fxml/GameView.fxml";

    private static final Logger LOG = LogManager.getLogger(GamePresenter.class);

    private User joinedLobbyUser;

    private String currentLobby;

    //Container for Terrainfields
    TerrainField[] tfArray;

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

    /**
     * Method called when the send Message button is pressed
     * <p>
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage If this will result in an exception, go log the exception
     *
     * @param event The ActionEvent created by pressing the send Message button
     * @author  René, Sergej
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
                RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, joinedLobbyUser.getUsername(), System.currentTimeMillis());
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
     * @author ?
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since ?
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        onResponseChatMessageLogic(message);
    }

    /**
     * The Method invoked by onResponseChatMessage()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Response, the method updateChat is invoked
     * to update the chat of the currentLobby in regards to the input given by the response.
     *
     * @param rcm the ResponseChatMessage given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-01-20
     */
    public void onResponseChatMessageLogic(ResponseChatMessage rcm) {
        // Only update Messages from used game chat
        if (this.currentLobby != null) {
            if (rcm.getChat().equals("game_"+currentLobby)) {
                LOG.debug("Updated game chat area with new message..");
                updateChat(rcm);
            }
        }
    }

    /**
     * Adds the ResponseChatMessage to the textArea
     *
     * @param message
     */
    private void updateChat(ResponseChatMessage message) {
        updateChatLogic(message);
    }

    private void updateChatLogic(ResponseChatMessage rcm) {
        var time = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date((long) rcm.getTime().doubleValue());
        var readableTime = time.format(resultdate);
        gameChatArea.insertText(gameChatArea.getLength(), readableTime + " " + rcm.getUsername() + ": " + rcm.getMessage() + "\n");
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
        gameService.rollDiceTest(this.currentLobby, this.joinedLobbyUser);
    }

    @FXML
    public void onTrade(ActionEvent event) {
        //TODO:...
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
        //TODO:...
    }

    @FXML
    public void onLeaveGame(ActionEvent event) {
        //TODO:...
    }

    /**
     * This method holds the size of the terrainfields in pixels.
     * <p>
     * The cardsize is not a fixed value, because if the canvas becomes scalabe in a future update, the cards need to
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
     * Method for generating a stack of terrainfields for a standard-ruleset-playfield.
     *
     * @return Array with TerrainFields in the standard-rulebook manner.
     * @author Pieter Vogt
     * @see <a href="https://confluence.swl.informatik.uni-oldenburg.de/display/SWP2020J/SpecCatan_1004+Spielfeld">Specification
     * 1004</a>
     * @since 2021-01-24
     */
    public TerrainField[] getStandardDeck() {

        TerrainField[] tempArray;

        //Array of cards get generated in same order as "spielfeld"-finespec in confluence. TODO: This should probably get done by the server in future. Think of this as a test-method wich can be migrated to server later.

        //beginning of oceans
        TerrainField f0 = new TerrainField("Ocean", 0, Vector.bottomLeft(cardSize()));
        TerrainField f1 = new TerrainField("Ocean", 0, Vector.bottomLeft(cardSize()));
        TerrainField f2 = new TerrainField("Ocean", 0, Vector.bottomLeft(cardSize()));
        TerrainField f3 = new TerrainField("Ocean", 0, Vector.topLeft(cardSize()));
        TerrainField f4 = new TerrainField("Ocean", 0, Vector.topLeft(cardSize()));
        TerrainField f5 = new TerrainField("Ocean", 0, Vector.topLeft(cardSize()));
        TerrainField f6 = new TerrainField("Ocean", 0, Vector.top((cardSize())));
        TerrainField f7 = new TerrainField("Ocean", 0, Vector.top((cardSize())));
        TerrainField f8 = new TerrainField("Ocean", 0, Vector.top((cardSize())));
        TerrainField f9 = new TerrainField("Ocean", 0, Vector.topRight((cardSize())));
        TerrainField f10 = new TerrainField("Ocean", 0, Vector.topRight((cardSize())));
        TerrainField f11 = new TerrainField("Ocean", 0, Vector.topRight((cardSize())));
        TerrainField f12 = new TerrainField("Ocean", 0, Vector.bottomRight((cardSize())));
        TerrainField f13 = new TerrainField("Ocean", 0, Vector.bottomRight((cardSize())));
        TerrainField f14 = new TerrainField("Ocean", 0, Vector.bottomRight((cardSize())));
        TerrainField f15 = new TerrainField("Ocean", 0, Vector.bottom((cardSize())));
        TerrainField f16 = new TerrainField("Ocean", 0, Vector.bottom((cardSize())));
        TerrainField f17 = new TerrainField("Ocean", 0, Vector.bottomLeft((cardSize())));

        //beginning of landmasses
        TerrainField f18 = new TerrainField("Forest", 5, Vector.bottomLeft(cardSize()));
        TerrainField f19 = new TerrainField("Farmland", 2, Vector.bottomLeft(cardSize()));
        TerrainField f20 = new TerrainField("Forest", 6, Vector.topLeft(cardSize()));
        TerrainField f21 = new TerrainField("Grassland", 3, Vector.topLeft(cardSize()));
        TerrainField f22 = new TerrainField("Grassland", 8, Vector.top(cardSize()));
        TerrainField f23 = new TerrainField("Forest", 10, Vector.top(cardSize()));
        TerrainField f24 = new TerrainField("Farmland", 9, Vector.topRight(cardSize()));
        TerrainField f25 = new TerrainField("Grassland", 12, Vector.topRight(cardSize()));
        TerrainField f26 = new TerrainField("Hillside", 11, Vector.bottomRight(cardSize()));
        TerrainField f27 = new TerrainField("Grassland", 4, Vector.bottomRight(cardSize()));
        TerrainField f28 = new TerrainField("Hillside", 8, Vector.bottom(cardSize()));
        TerrainField f29 = new TerrainField("Farmland", 10, Vector.bottomLeft(cardSize()));
        TerrainField f30 = new TerrainField("Hillside", 9, Vector.bottomLeft(cardSize()));
        TerrainField f31 = new TerrainField("Mountain", 4, Vector.topLeft(cardSize()));
        TerrainField f32 = new TerrainField("Farmland", 5, Vector.top(cardSize()));
        TerrainField f33 = new TerrainField("Mountain", 6, Vector.topRight(cardSize()));
        TerrainField f34 = new TerrainField("Forest", 3, Vector.bottomRight(cardSize()));
        TerrainField f35 = new TerrainField("Mountain", 3, Vector.bottomLeft(cardSize()));
        TerrainField f36 = new TerrainField("Desert", 0, new Vector(0, 0));
        f36.setPosition(new Vector(((canvas.getWidth() / 2) - cardSize() / 2), ((canvas.getHeight() / 2)) - cardSize() / 2));

        tempArray = new TerrainField[]{f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14, f15, f16, f17, f18, f19, f20, f21, f22, f23, f24, f25, f26, f27, f28, f29, f30, f31, f32, f33, f34, f35, f36};

        for (int i = tempArray.length - 2; i >= 0; i--) { // TempArray.length - 2 because the desert-field (upmost "card") was already positioned, so we dont need to handle it again (index out of bounds when whe try to add tempArray[i+1]...)
            tempArray[i].setPosition(Vector.addVector(tempArray[i + 1].getPosition(), tempArray[i].getPlacementVector())); //Add position of last terrainfield and current placement-vector to determine position.
        }
        return tempArray;
    }

    /**
     * Initializes everything that needs to be done before the first player-action takes place.
     *
     * @author Pieter Vogt
     * @since 2021-01-24
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfArray = getStandardDeck(); // In future this should be a deque send by the server.
        draw();
    }

    /**
     * The method that actually draws graphical objects to the screen.
     * <p>
     * This method draws its items from back to front, meaning backmost items need to be drawn first and so on. This is
     * why the background is drawn first, etc.
     * </p>
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
            g.setFill(tfArray[i].determineColorOfTerrain()); //Determine draw-color of current Terrainfield.
            g.fillOval(tfArray[i].getPosition().getX(), tfArray[i].getPosition().getY(), cardSize(), cardSize()); //Draw circle with given color at given position TODO: This - in combination with the Vector.vector-methods - SHOULD be already scaling with canvassize. If and when a scalable Canvas gets implemented, this should be checked.
        }
    }

    /**
     * Handles GameCreatedSuccessfullyResponse
     * <p>
     * If GameCreatedSuccessfullyResponse is detected on the EventBus the method createdSuccessfulLogic is invoked.
     *
     * @param response the GameCreatedSuccessfullyResponse object seen on the EventBus
     * @author René Meyer
     * @see de.uol.swp.common.game.response.GameCreatedSuccessfullyResponse
     * @since 2021-03-08
     */
    @Subscribe
    public void createdSuccessful(GameCreatedSuccessfullyResponse response) {
        createdSuccessfulLogic(response);
    }

    /**
     * The Method invoked by createdSuccessful()
     * <p>
     * After the game is successfully created it retrieves the joinedLobbyUser and the currentLobby from the
     * GameCreatedSuccessfullyResponse.
     *
     * @param lcsr the GameCreatedSuccessfullyResponse given by the original subscriber method.
     * @author René Meyer
     * @see de.uol.swp.common.game.message.GameCreatedMessage
     * @since 2021-03-08
     */
    public void createdSuccessfulLogic(GameCreatedSuccessfullyResponse lcsr) {
        if (this.currentLobby == null) {
            this.joinedLobbyUser = lcsr.getJoinedUser();
            this.currentLobby = lcsr.getLobbyName();
        }
    }

}
