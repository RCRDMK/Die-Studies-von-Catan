package de.uol.swp.client.game;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.HashMap;


public class PublicInventoryCell extends ListCell <HashMap<String, Integer>> {
        HBox hbox = new HBox();
        Label PlayerName = new Label("");
        Label resourceCardsCount = new Label("");
        Label developmentCardsCount = new Label("");
        Label playedKnightCardsCount = new Label("");
        Label continuousRoad = new Label("");
        Label publicVictoryPoints = new Label("");
}
