package de.uol.swp.client.game;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;


public class PublicInventoryCell extends ListCell<String> {
    HBox hbox = new HBox();
    HBox hbox1 = new HBox();
    HBox hbox2 = new HBox();
    Label playerName = new Label("");
    Label resourceCardsCount = new Label("R:");
    Label developmentCardsCount = new Label("DC:");
    Label playedKnightCardsCount = new Label("K:");
    Label continuousRoad = new Label("CR:");
    Label publicVictoryPoints = new Label("VP:");

    public PublicInventoryCell() {
        super();
        hbox.getChildren().add(playerName);
        hbox1.getChildren().addAll(resourceCardsCount, developmentCardsCount, playedKnightCardsCount);
        hbox2.getChildren().addAll(continuousRoad, publicVictoryPoints);
    }

  /*  @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);
        if (item != null && !empty) {
            playerName.setText(item);
        }
        setGraphic(hbox);
    } */

}
