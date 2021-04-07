package de.uol.swp.client.lobby;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

/**
 * Creates LobbyCells to populate the ListView fxml-element for the Lobbybrowser
 *
 * @author Pieter Vogt
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */

public class LobbyCell extends ListCell<LobbyDTO> {
    HBox hbox = new HBox();
    Label label= new Label("");
    Label label1= new Label("");
    Label label2= new Label("");
    Pane pane = new Pane();
    Pane pane1 = new Pane();
    Pane pane2 = new Pane();
    Button button = new Button("join");

    /**
     * Constructor for the Cells
     * <p>
     * If the Constructor is invoked, the Cell-Element is created and the Button is linked to the Lobby it belongs to.
     * Therefore we commit the lobbyservice and the userDTO from the invoking MainMenuPresenter so the constructor knows
     * what to link the button to. The Lobbyservice serves as a donor for the Lobby to join into.
     *
     * @param
     * @author Pieter Vogt
     * @see de.uol.swp.client.lobby.LobbyService
     * @see de.uol.swp.client.main.MainMenuPresenter
     * @since 2020-12-27
     */
    public LobbyCell(LobbyService lobbyservice, User user) {
        super();

        hbox.getChildren().addAll(label, pane, label1, pane1, label2, pane2,  button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(pane1, Priority.ALWAYS);
        HBox.setHgrow(pane2, Priority.ALWAYS);
        button.setOnAction(event -> lobbyservice.joinLobby(label.getText(), (UserDTO) user));
    }

    @Override
    protected void updateItem(LobbyDTO item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            label.setText(item.getName());
            label1.setText(item.getUsers().size() + "/4");
            label2.setText(Boolean.toString(item.getGameStarted()));
            setGraphic(hbox);
        }
    }
}

