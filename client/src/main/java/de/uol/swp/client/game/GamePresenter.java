package de.uol.swp.client.game;

import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.game.GameObjects.TerrainField;
import de.uol.swp.client.game.HelperObjects.Vector;
import de.uol.swp.common.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Inject
    private GameService gameService;
    @FXML
    private Canvas canvas = new Canvas();

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

    //Container for Terrainfields
    TerrainField[] tfArray;

    /**
     * this method holds the size of the terraincards in pixels
     * <p>
     * at the moment this function is used to define the width and height of a card because they are treated as a
     * circle. in future this may be modified to address a potentially scalable canvas. if the canvas gets scalable in
     * the future, the cardsizes need to scale along for this to be of any use.
     */

    public double cardSize() {
        double d = Math.min(canvas.getHeight(), canvas.getWidth()); //Determine minimum Pixels in height and length of the canvas (we dont want the playfield to scale out of canvas, so we orient at the smaller axis)
        return d / 8; // divide by 8 because the playfield is 7 cards wide and add 1/2 card each side for margin/borderspace. (looks nicer, trust me)
    }

    /**
     * Method for generating a stack of terraincards for a standard-ruleset-playfield
     *
     * @return stack of terraincards
     * @author pieter vogt
     * @since 24-01-2021
     */
    public TerrainField[] getStandardDeck() {

        TerrainField[] tempArray;

        //Stack of cards get generated in same order as "spielfeld"-finespec in confluence. TODO: This should probably get done by the server in future. Think of this as a test-method wich can be migrated to server later.

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

        for (int i = tempArray.length - 2; i >= 0; i--) { // tempArray.length - 2 because the desertfield (upmost "card") was already positioned, so we dont need to handle it again (index out of bounds when whe try to add tempArray[i+1]...)
            tempArray[i].setPosition(Vector.addVector(tempArray[i + 1].getPosition(), tempArray[i].getPlacementVector())); //add position of last terrainfield and current placementvector to determine position.
        }
        return tempArray;
    }

    /**
     * initializes everything that needs to be present before any playeraction
     *
     * @author pieter vogt
     * @since 24-01-2021
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfArray = getStandardDeck(); // In future this should be a deque send by the server.
        draw();
    }

    /**
     * The method that actually draws colored stuff to the screen.
     * <p>
     * This method draws its items from back to front, meaning backmost items need to be drawn first and so on.
     * </p>
     *
     * @author pieter vogt
     * @since 24-01-2021
     */
    public void draw() {

        //preparation
        GraphicsContext g = this.canvas.getGraphicsContext2D(); //this is the object thats doing the drawing and has all the methods for graphics related stuff.

        //paint black background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //draw terrainfields
        for (int i = tfArray.length - 1; i >= 0; i--) {
            g.setFill(tfArray[i].determineColorOfTerrain()); //determine color of upmost card of stack
            g.fillOval(tfArray[i].getPosition().getX(), tfArray[i].getPosition().getY(), cardSize(), cardSize()); //draw circle with given color at given position TODO: This - in combination with the Vector.vector-methods - SHOULD be already scaling with canvassize. If and when a scalable Canvas gets implemented, this should be checked.
        }
    }
}
