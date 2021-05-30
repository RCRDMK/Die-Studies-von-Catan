package de.uol.swp.client.game;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;


public class PublicInventoryCell extends ListCell<HashMap.Entry<String, Integer>> {
    VBox vbox = new VBox();
    Label playerName = new Label("");
    Label victoryPoints = new Label("");
    Label resource = new Label("");
    Label developmentCards = new Label("");
    Label playedKnights = new Label("");
    Label continuousRoad = new Label("");
    Pane pane = new Pane();
    Pane pane1 = new Pane();
    Pane pane2 = new Pane();
    Pane pane3 = new Pane();
    Pane pane4 = new Pane();
    Pane pane5 = new Pane();

    public PublicInventoryCell() {
        super();
        vbox.getChildren().addAll(playerName, pane,victoryPoints , pane1, resource, pane2, developmentCards, pane3,
                playedKnights, pane4, continuousRoad, pane5);
        VBox.setVgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(pane1, Priority.ALWAYS);
        VBox.setVgrow(pane2, Priority.ALWAYS);
        VBox.setVgrow(pane3, Priority.ALWAYS);
        VBox.setVgrow(pane4, Priority.ALWAYS);
        VBox.setVgrow(pane4, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(HashMap.Entry<String, Integer> item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
           // playerName.setText(item.);
        }
    }

}
