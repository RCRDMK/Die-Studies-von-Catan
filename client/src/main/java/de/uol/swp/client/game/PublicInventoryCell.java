package de.uol.swp.client.game;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;


public class PublicInventoryCell extends ListCell<HashMap.Entry<String, Integer>> {
    VBox vbox = new VBox();
    Label inventoryEntry = new Label("");
    Pane pane1 = new Pane();
    HashMap<String, Integer> publicInventory;

    public PublicInventoryCell(HashMap<String, Integer> publicInventory) {
        super();
        this.publicInventory = publicInventory;
        vbox.getChildren().addAll(inventoryEntry, pane1);
        VBox.setVgrow(pane1, Priority.ALWAYS);
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
                    inventoryEntry.setText("Knights: " + item.getValue().toString());
                    break;
                case "Continuous Road":
                    inventoryEntry.setText("Roads: " + item.getValue().toString());
                    break;
            }
        }
        setGraphic(vbox);
    }
}

