package de.uol.swp.client.lobby;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.Optional;

/**
 * Creates LobbyCells to populate the ListView fxml-element for the lobbyBrowser
 * <p>
 *
 * @author Pieter Vogt
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 * <p>
 * Enhanced by Carsten Dekker
 * Enhanced by René Meyer
 * @since 2021-04-08
 */

public class LobbyCell extends ListCell<LobbyDTO> {
    LobbyService lobbyService;
    User user;
    HBox hbox = new HBox();
    Label lobbyName = new Label("");
    Label userCount = new Label("");
    Label lobbyStatus = new Label("");
    Label lobbyProtected = new Label("");
    Pane pane = new Pane();
    Pane pane1 = new Pane();
    Pane pane2 = new Pane();
    Pane pane3 = new Pane();
    Button button = new Button("join");

    /**
     * Constructor for the Cells
     * <p>
     * If the Constructor is invoked, the Cell-Element is created and the Button is linked to the Lobby it belongs to.
     * It shows the lobbyName, the amount of user in the lobby and the status of the lobby.
     * Therefore we commit the lobbyService and the userDTO from the invoking MainMenuPresenter so the constructor knows
     * what to link the button to. The lobbyService serves as a donor for the Lobby to join into.
     *
     * @param lobbyService The lobbyService
     * @param user         The loggedIn user
     * @author Pieter Vogt
     * @see de.uol.swp.client.lobby.LobbyService
     * @see de.uol.swp.client.main.MainMenuPresenter
     * @since 2020-12-27
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-04-08
     */
    public LobbyCell(LobbyService lobbyService, User user) {
        super();
        hbox.getChildren().addAll(lobbyName, pane, userCount, pane1, lobbyStatus, pane2, lobbyProtected, pane3, button);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(pane1, Priority.ALWAYS);
        HBox.setHgrow(pane2, Priority.ALWAYS);
        HBox.setHgrow(pane3, Priority.ALWAYS);
        this.lobbyService = lobbyService;
        this.user = user;
    }

    @Override
    protected void updateItem(LobbyDTO item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        if (item != null && !empty) {
            lobbyName.setText(item.getName());
            if (item.getUsers().size() == 4) {
                userCount.setText("full");
                button.setDisable(true);
            } else {
                userCount.setText(item.getUsers().size() + "/4");
                button.setDisable(false);
            }
            if (item.getGameStarted()) {
                lobbyStatus.setText("started");
                button.setDisable(true);
            } else {
                lobbyStatus.setText("waiting");
                button.setDisable(false);
            }
            if (item.getPasswordHash() != 0) {
                lobbyProtected.setText("protected");
                button.setOnAction(event -> Platform.runLater(() -> {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Password");
                    dialog.setHeaderText("Please enter your password.");
                    ButtonType passwordButtonType = new ButtonType("Join", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().addAll(passwordButtonType, ButtonType.CANCEL);

                    var passwordField = new PasswordField();
                    passwordField.setPromptText("Password");

                    HBox hBox = new HBox();
                    hBox.getChildren().add(passwordField);
                    hBox.setPadding(new Insets(20));

                    HBox.setHgrow(passwordField, Priority.ALWAYS);

                    dialog.getDialogPane().setContent(hBox);
                    Platform.runLater(passwordField::requestFocus);
                    dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == passwordButtonType) {
                            return passwordField.getText();
                        }
                        return null;
                    });
                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(pw -> lobbyService.joinProtectedLobby(lobbyName.getText(), (UserDTO) user, pw));
                }));
            } else {
                lobbyProtected.setText("not protected");
                button.setOnAction(event -> lobbyService.joinLobby(lobbyName.getText(), (UserDTO) user));
            }
            setGraphic(hbox);
        }
    }
}

