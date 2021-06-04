package de.uol.swp.client.game;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.HashMap;


public class PublicInventoryCell extends ListCell<HashMap.Entry<String, Integer>> {
    HBox hbox = new HBox();
    Label inventoryEntry = new Label("");
    Pane pane1 = new Pane();
    HashMap<String, Integer> publicInventory;

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

