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
import java.util.Stack;

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

    Stack<TerrainField> tStack;

    public double cardSize() {
        return 50.0;
    }


    public Stack<TerrainField> getStandardStack() {
        Stack<TerrainField> s = new Stack<>();

        //Stack of cards get generated in same order as "spielfeld"-finespec in confluence. TODO: This should probably get done by the server in future. Think of this as a test-method wich can be adapted to server later.

        TerrainField f0 = new TerrainField("Ocean", 0, Vector.topRight(cardSize()));
        s.push(f0);
        TerrainField f1 = new TerrainField("Ocean", 0, Vector.topRight(cardSize()));
        s.push(f1);
        TerrainField f2 = new TerrainField("Ocean", 0, Vector.topRight(cardSize()));
        s.push(f2);
        TerrainField f3 = new TerrainField("Ocean", 0, Vector.bottomRight(cardSize()));
        s.push(f3);
        TerrainField f4 = new TerrainField("Ocean", 0, Vector.bottomRight(cardSize()));
        s.push(f4);
        TerrainField f5 = new TerrainField("Ocean", 0, Vector.bottomRight(cardSize()));
        s.push(f5);
        TerrainField f6 = new TerrainField("Ocean", 0, Vector.bottom((cardSize())));
        s.push(f6);
        TerrainField f7 = new TerrainField("Ocean", 0, Vector.bottom((cardSize())));
        s.push(f7);
        TerrainField f8 = new TerrainField("Ocean", 0, Vector.bottom((cardSize())));
        s.push(f8);
        TerrainField f9 = new TerrainField("Ocean", 0, Vector.bottomLeft((cardSize())));
        s.push(f9);
        TerrainField f10 = new TerrainField("Ocean", 0, Vector.bottomLeft((cardSize())));
        s.push(f10);
        TerrainField f11 = new TerrainField("Ocean", 0, Vector.bottomLeft((cardSize())));
        s.push(f11);
        TerrainField f12 = new TerrainField("Ocean", 0, Vector.topLeft((cardSize())));
        s.push(f12);
        TerrainField f13 = new TerrainField("Ocean", 0, Vector.topLeft((cardSize())));
        s.push(f13);
        TerrainField f14 = new TerrainField("Ocean", 0, Vector.topLeft((cardSize())));
        s.push(f14);
        TerrainField f15 = new TerrainField("Ocean", 0, Vector.top((cardSize())));
        s.push(f15);
        TerrainField f16 = new TerrainField("Ocean", 0, Vector.top((cardSize())));
        s.push(f16);
        TerrainField f17 = new TerrainField("Ocean", 0, Vector.topRight((cardSize())));
        s.push(f17);


        TerrainField f18 = new TerrainField("Forrest", 5, Vector.topRight(cardSize()));
        s.push(f18);
        TerrainField f19 = new TerrainField("Farmland", 2, Vector.topRight(cardSize()));
        s.push(f19);
        TerrainField f20 = new TerrainField("Forrest", 6, Vector.bottomRight(cardSize()));
        s.push(f20);
        TerrainField f21 = new TerrainField("Grassland", 3, Vector.bottomRight(cardSize()));
        s.push(f21);
        TerrainField f22 = new TerrainField("Grassland", 8, Vector.bottom(cardSize()));
        s.push(f22);
        TerrainField f23 = new TerrainField("Forrest", 10, Vector.bottom(cardSize()));
        s.push(f23);
        TerrainField f24 = new TerrainField("Farmland", 9, Vector.bottomLeft(cardSize()));
        s.push(f24);
        TerrainField f25 = new TerrainField("Grassland", 12, Vector.bottomLeft(cardSize()));
        s.push(f25);
        TerrainField f26 = new TerrainField("Hillside", 11, Vector.topLeft(cardSize()));
        s.push(f26);
        TerrainField f27 = new TerrainField("Grassland", 4, Vector.topLeft(cardSize()));
        s.push(f27);
        TerrainField f28 = new TerrainField("Hillside", 8, Vector.top(cardSize()));
        s.push(f28);
        TerrainField f29 = new TerrainField("Farmland", 10, Vector.topRight(cardSize()));
        s.push(f29);
        TerrainField f30 = new TerrainField("Hillside", 9, Vector.topRight(cardSize()));
        s.push(f30);
        TerrainField f31 = new TerrainField("Mountain", 4, Vector.bottomRight(cardSize()));
        s.push(f31);
        TerrainField f32 = new TerrainField("Farmland", 5, Vector.bottom(cardSize()));
        s.push(f32);
        TerrainField f33 = new TerrainField("Mountain", 6, Vector.bottomLeft(cardSize()));
        s.push(f33);
        TerrainField f34 = new TerrainField("Forrest", 3, Vector.top(cardSize()));
        s.push(f34);
        TerrainField f35 = new TerrainField("Desert", 0, null);
        s.push(f35);

        return s;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tStack = getStandardStack(); //in future -> tStack = (some stack send by server);

        draw();
    }


    public void draw() {
        //preparation
        Vector lastPosition;
        GraphicsContext g = this.canvas.getGraphicsContext2D();

        //paint background
        g.setFill(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //draw cards

        //draw middle card first for placement-reference of other cards
        g.setFill(tStack.peek().determineColorOfTerrain()); //determine color of upmost card of stack
        tStack.peek().setPosition(new Vector(canvas.getWidth() / 2 - (cardSize() / 2), canvas.getHeight() / 2 - (cardSize() / 2))); //Set Position of first Terrainfield
        g.fillOval(tStack.peek().getPosition().getX(), tStack.peek().getPosition().getY(), cardSize(), cardSize()); //draw circle with given color at given position TODO: implement actual graphics instead of cycles.
        lastPosition = tStack.peek().getPosition(); //save current position for placement of next card
        g.fillText(tStack.peek().getName(), lastPosition.getX() + (cardSize() / 2), lastPosition.getY() + (cardSize() / 2));
        tStack.pop(); //pop current card

        //draw other cards
        for (TerrainField tf : tStack) {
            g.setFill(tStack.peek().determineColorOfTerrain()); //determine color of upmost card of stack
            tf.setPosition(Vector.addVector(lastPosition, tf.getPlacementVector())); //Set position of current terrainfield based on predecessor position and placement-vector.
            g.fillOval(tf.getPosition().getX(), tf.getPosition().getY(), cardSize(), cardSize()); //draw circle with given color at given position
            lastPosition = tf.getPosition();  //save current position for placement of next card
            tStack.pop(); //pop current card
        }
    }

}
