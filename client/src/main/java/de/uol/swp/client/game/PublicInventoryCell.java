package de.uol.swp.client.game;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.HashMap;

/**
 * Creates PublicInventoryCells to populate the ListView fxml-element with information
 * <p>
 * about  content in the public inventories in game.
 *
 * @author Iskander Yusupov
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-05-28
 */

public class PublicInventoryCell extends ListCell<HashMap.Entry<String, Integer>> {
    HBox hbox = new HBox();
    Label inventoryEntry = new Label("");
    Pane pane1 = new Pane();
    HashMap<String, Integer> publicInventory;

    /**
     * Constructor for the Cells
     * <p>
     * If the Constructor is invoked, the Cell-Element is created.
     * It shows amount of public victory points, resources, development cards,
     * total amount of played knight during the game, and current longest continuos road.
     *
     * @param publicInventory
     * @author Iskander Yusupov
     * @see de.uol.swp.client.game.GamePresenter
     * @since 2021-05-28
     */
    public PublicInventoryCell(HashMap<String, Integer> publicInventory) {
        super();
        this.publicInventory = publicInventory;
        hbox.getChildren().addAll(inventoryEntry, pane1);
        HBox.setHgrow(pane1, Priority.ALWAYS);
    }

    @Override
    protected void updateItem(HashMap.Entry<String, Integer> item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item != null && !empty) {
            switch (item.getKey()) {
                case "Public Victory Points":
                    inventoryEntry.setText("Points: " + item.getValue().toString());
                    break;
                case "Resource":
                    inventoryEntry.setText("Resources: " + item.getValue().toString());
                    break;
                case "Development Cards":
                    inventoryEntry.setText("Cards: " + item.getValue().toString());
                    break;
                case "Played Knights":
                    inventoryEntry.setText("Pl.Knights: " + item.getValue().toString());
                    break;
                case "Continuous Road":
                    inventoryEntry.setText("Con.Road: " + item.getValue().toString());
                    break;
            }
        }
        setGraphic(hbox);
    }
}

