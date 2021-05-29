package de.uol.swp.client.game;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


/**
 * Creates PublicInventoryCells to populate the ListView fxml-element for
 * in-game public inventories.
 * <p>
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-05-28
 */
public class PublicInventoryCell extends ListCell<String> {
    VBox vbox = new VBox();
    Label playerName = new Label("");
    Label resource = new Label("Resources: ");
    Label developmentCards = new Label("Cards: ");
    Label playedKnights = new Label("Knights: ");
    Label continuousRoad = new Label("Roads: ");
    Pane pane = new Pane();
    Pane pane1 = new Pane();
    Pane pane2 = new Pane();
    Pane pane3 = new Pane();
    Pane pane4 = new Pane();

    public PublicInventoryCell() {
        super();
        vbox.getChildren().addAll(playerName, pane, resource, pane1, developmentCards, pane2,
                playedKnights, pane3, continuousRoad, pane4);
        VBox.setVgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(pane1, Priority.ALWAYS);
        VBox.setVgrow(pane2, Priority.ALWAYS);
        VBox.setVgrow(pane3, Priority.ALWAYS);
        VBox.setVgrow(pane4, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            //   playerName.setText(item.);
        }
    }

}
