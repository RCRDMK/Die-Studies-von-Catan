package de.uol.swp.client.lobby;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.chat.ChatService;
import de.uol.swp.common.chat.RequestChatMessage;
import de.uol.swp.common.chat.ResponseChatMessage;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.game.message.GameDroppedMessage;
import de.uol.swp.common.game.message.GameFinishedMessage;
import de.uol.swp.common.game.message.GameStartedMessage;
import de.uol.swp.common.game.message.NotEnoughPlayersMessage;
import de.uol.swp.common.game.response.GameAlreadyExistsResponse;
import de.uol.swp.common.game.response.NotLobbyOwnerResponse;
import de.uol.swp.common.lobby.message.StartGameMessage;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.response.JoinOnGoingGameResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse;
import de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse;
import de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse;

/**
 * Manages the lobby menu
 * <p>
 * Class was build exactly like MainMenuPresenter.
 *
 * @author Ricardo Mook, Marc Hermes
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-19
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);
    @FXML
    public ToggleGroup minimumAmountOfPlayersToggleButtons;
    @FXML
    public RadioButton minimum2Players;
    @FXML
    public RadioButton minimum3Players;
    @FXML
    public RadioButton minimum4Players;
    @FXML
    public TextField lobbyChatInput;
    @FXML
    public TextArea lobbyChatArea;
    @FXML
    public Label notEnoughPlayersLabel;
    @FXML
    public Label notLobbyOwnerLabel;
    @FXML
    public Label gameAlreadyExistsLabel;
    @FXML
    public Label reasonWhyNotAbleToJoinGame;
    private ObservableList<String> lobbyUsers;
    private User joinedLobbyUser;
    private String currentLobby;
    private Alert alert;
    private String lobbyOwnerName;
    private boolean isLobbyOwner = false;
    private String gameFieldVariant = "Standard";
    private int minimumAmountOfPlayers = 2;
    @FXML
    private RadioButton standardGameField;
    @FXML
    private RadioButton randomGameField;
    @FXML
    private RadioButton veryRandomGameField;
    @FXML
    private Button startGameButton;
    @FXML
    private Button joinGameButton;
    @FXML
    private ListView<String> lobbyUsersView;
    @Inject
    private LobbyService lobbyService;

    @Inject
    private ChatService chatService;


    /**
     * Method called when the StandardGameField radioButton is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    @FXML
    public void onStandardGameField() {
        gameFieldVariant = "Standard";
    }

    /**
     * Method called when the RandomGameField radioButton is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    @FXML
    public void onRandomGameField() {
        gameFieldVariant = "Random";
    }

    /**
     * Method called when the VeryRandomGameField radioButton is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-14
     */
    @FXML
    public void onVeryRandomGameField() {
        gameFieldVariant = "VeryRandom";
    }

    /**
     * Method called when the 2Players radioButton is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @FXML
    public void on2Players() {
        minimumAmountOfPlayers = 2;
    }

    /**
     * Method called when the 3Players radioButton is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @FXML
    public void on3Players() {
        minimumAmountOfPlayers = 3;
    }


    /**
     * Method called when the 4Players radioButton is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @FXML
    public void on4Players() {
        minimumAmountOfPlayers = 4;
    }

    /**
     * Method called when the StartGame button is pressed
     * <p>
     *
     * @author Kirstin Beyer und Iskander Yusupov
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-01-23
     */
    @FXML
    public void onStartGame() {
        LOG.debug("StartGame Button pressed");
        lobbyService
                .startGame(this.currentLobby, (UserDTO) this.joinedLobbyUser, gameFieldVariant, minimumAmountOfPlayers);
        gameAlreadyExistsLabel.setVisible(false);
        notLobbyOwnerLabel.setVisible(false);
        notEnoughPlayersLabel.setVisible(false);
        reasonWhyNotAbleToJoinGame.setVisible(false);
    }

    /**
     * Method called when the JoinGame button is pressed
     *
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @FXML
    public void onJoinGame() {
        LOG.debug("JoinGame Button Pressed");
        lobbyService.joinGame(this.currentLobby, (UserDTO) this.joinedLobbyUser);
        gameAlreadyExistsLabel.setVisible(false);
        notLobbyOwnerLabel.setVisible(false);
        notEnoughPlayersLabel.setVisible(false);
        reasonWhyNotAbleToJoinGame.setVisible(false);
    }

    /**
     * Method called when the LeaveLobby button is pressed
     *
     * @author Marc Hermes
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2020-12-02
     */
    @FXML
    public void onLeaveLobby() {
        if (this.currentLobby != null && this.joinedLobbyUser != null) {
            lobbyService.leaveLobby(this.currentLobby, (UserDTO) this.joinedLobbyUser);
        } else if (this.currentLobby == null && this.joinedLobbyUser != null) {
            throw new LobbyPresenterException("Name der jetzigen Lobby ist nicht vorhanden!");
        } else {
            throw new LobbyPresenterException("Der jetzige User ist nicht vorhanden");
        }
    }


    /**
     * Method called when the send Message button is pressed
     * <p>
     * If the send Message button is pressed, this methods tries to request the chatService to send a specified message.
     * The message is of type RequestChatMessage If this will result in an exception, go log the exception
     *
     * @author Anton, René, Sergej
     * @see de.uol.swp.client.chat.ChatService
     * @since 2020-12-06
     */
    @FXML
    void onSendMessage() {
        try {
            var chatMessage = lobbyChatInput.getCharacters().toString();
            // ChatID = gets lobby name
            var chatId = currentLobby;
            if (!chatMessage.isEmpty()) {
                RequestChatMessage message = new RequestChatMessage(chatMessage, chatId, joinedLobbyUser.getUsername(),
                        System.currentTimeMillis());
                chatService.sendMessage(message);
            }
            this.lobbyChatInput.setText("");
        } catch (Exception e) {
            LOG.debug(e);
        }
    }

    /**
     * Handles successful lobby creation
     * <p>
     * If a LobbyCreatedSuccessfulResponse is detected on the EventBus this method invokes createdSuccessfulLogic.
     *
     * @param message the LobbyCreatedSuccessfulResponse object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse
     * @since 2020-12-02
     */
    @Subscribe
    public void createdSuccessful(LobbyCreatedSuccessfulResponse message) {
        createdSuccessfulLogic(message);
    }

    /**
     * Sets the click-ability of the lobbies options-buttons.
     * <p>
     * If a User joins a lobby, hes automatically denied to click any options-buttons regarding game-settings like which
     * game-field to chose and so on. When the User has created the lobby, hes automatically enabled to change
     * game-settings. If the lobbyOwner leaves, the variable "isLobbyOwner" gets updated on every client and after that
     * this method is called again.
     * </p>
     *
     * @author Pieter Vogt
     * @since 2021-03-21
     */
    public void setGameOptionsButtonsVisibility() {
        if (isLobbyOwner) {
            randomGameField.setDisable(false);
            standardGameField.setDisable(false);
            veryRandomGameField.setDisable(false);
            minimum2Players.setDisable(false);
            minimum3Players.setDisable(false);
            minimum4Players.setDisable(false);
        } else {
            standardGameField.setDisable(true);
            randomGameField.setDisable(true);
            veryRandomGameField.setDisable(true);
            minimum2Players.setDisable(true);
            minimum3Players.setDisable(true);
            minimum4Players.setDisable(true);
        }
    }


    /**
     * The Method invoked by createdSuccessful()
     * <p>
     * If the currentLobby is null, meaning this is an empty LobbyPresenter that is ready to be used for a new lobby tab,
     * the parameters of this LobbyPresenter are updated to the User and Lobby given by the lcsr Response.
     * An update of the Users in the currentLobby is also done.
     *
     * @param lcsr the LobbyCreatedSuccessfulResponse given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyCreatedSuccessfulResponse
     * @since 2021-01-20
     */
    public void createdSuccessfulLogic(LobbyCreatedSuccessfulResponse lcsr) {
        if (this.currentLobby == null) {
            LOG.debug("Requesting update of User list in lobby because lobby was created.");
            this.joinedLobbyUser = lcsr.getUser();
            ArrayList<UserDTO> onlyLobbyOwner = new ArrayList<>();
            onlyLobbyOwner.add((UserDTO) joinedLobbyUser);
            this.lobbyOwnerName = joinedLobbyUser.getUsername();
            updateLobbyUsersList(onlyLobbyOwner);
            this.currentLobby = lcsr.getName();
            this.lobbyChatInput.setText("");
            lobbyChatArea.deleteText(0, lobbyChatArea.getLength());
            isLobbyOwner = true;
            Platform.runLater(this::setupButtonsAndAlerts);
        }
    }

    /**
     * Handles successful joining in the lobby
     * <p>
     * If a LobbyJoinedSuccessfulResponse is detected on the EventBus this method invokes userJoinedSuccessfulLogic
     *
     * @param message the LobbyJoinedSuccessfulResponse object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse
     * @since 2020-12-10
     */
    @Subscribe
    public void userJoinedSuccessful(LobbyJoinedSuccessfulResponse message) {
        userJoinedSuccessfulLogic(message);
    }

    /**
     * The Method invoked by userJoinedSuccessful()
     * <p>
     * If the currentLobby is null, meaning this is an empty LobbyPresenter that is ready to be used for a new lobby
     * tab, the parameters of this LobbyPresenter are updated to the User and Lobby given by the ljsr Response. An
     * update of the Users in the currentLobby is also requested. Furthermore the method setupButtonsAndAlerts is called
     * to create the buttons and the alert for the pop-up Alert that shows up when the User is asked whether he is ready
     * to start the game or not.
     * <p>
     * enhanced by Marc Hermes - 2021-02-08
     *
     * @param ljsr the LobbyJoinedSuccessfulResponse given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyJoinedSuccessfulResponse
     * @since 2021-01-20
     */
    public void userJoinedSuccessfulLogic(LobbyJoinedSuccessfulResponse ljsr) {
        if (this.currentLobby == null) {
            LOG.debug("LobbyJoinedSuccessfulResponse successfully received");
            this.joinedLobbyUser = ljsr.getUser();
            this.currentLobby = ljsr.getName();
            this.lobbyChatInput.setText("");
            lobbyChatArea.deleteText(0, lobbyChatArea.getLength());
            lobbyService.retrieveAllThisLobbyUsers(ljsr.getName());
            isLobbyOwner = false;
            Platform.runLater(this::setupButtonsAndAlerts);
        }
    }

    /**
     * The method invoked when the Lobby Presenter is first used: when a lobby is joined/created.
     * <p>
     * The Alert asking the user whether he is ready to start the game or not as well as its corresponding buttons
     * buttonTypeYes/No are created. Also 2 more hidden buttons are created whose ActionEvents are linked to the
     * buttonTypeYes/No buttons of the Alert. When either of those buttons is pressed onBtnYes/NoClicked will be called.
     * The initial Modality of the Alert is also changed so that the Main Window can still be used even when the Alert
     * is shown.
     *
     * @author Marc Hermes
     * @since 2021-02-08
     */
    public void setupButtonsAndAlerts() {
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Button btnYes = (Button) alert.getDialogPane().lookupButton(buttonTypeYes);
        btnYes.setOnAction(event -> {
            onBtnYesClicked();
            event.consume();
        });
        Button btnNo = (Button) alert.getDialogPane().lookupButton(buttonTypeNo);
        btnNo.setOnAction(event -> {
            onBtnNoClicked();
            event.consume();
        });
        this.alert.initModality(Modality.NONE);

        setGameOptionsButtonsVisibility();
        joinGameButton.setVisible(false);
        startGameButton.setVisible(true);
        standardGameField.fire();
        minimum2Players.fire();


    }

    /**
     * The method invoked when the Yes Button of the Alert is pressed
     * <p>
     * When the Button "Yes" is pressed in the Alert the Alert will be closed and the lobbyService will be called to
     * send a PlayerReadyRequest with "true" to the Server.
     *
     * @author Marc Hermes
     * @since 2021-02-08
     */
    public void onBtnYesClicked() {
        alert.close();
        lobbyService.sendPlayerReadyRequest(this.currentLobby, (UserDTO) this.joinedLobbyUser, true);
    }

    /**
     * The method invoked when the No Button of the Alert is pressed
     * <p>
     * When the Button "No" is pressed in the Alert the Alert will be closed and the lobbyService will be called to send
     * a PlayerReadyRequest with "false" to the Server.
     *
     * @author Marc Hermes
     * @since 2021-02-08
     */
    public void onBtnNoClicked() {
        alert.close();
        lobbyService.sendPlayerReadyRequest(this.currentLobby, (UserDTO) this.joinedLobbyUser, false);
    }

    /**
     * Handles successful leaving of lobby
     * <p>
     * If a LobbyLeftSuccessfulResponse is detected on the EventBus the method userLeftSuccessfulLogic is invoked.
     *
     * @param message the LobbyLeftSuccessfulResponse object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse
     * @since 2020-12-10
     */
    @Subscribe
    public void userLeftSuccessful(LobbyLeftSuccessfulResponse message) {
        userLeftSuccessfulLogic(message);
    }

    /**
     * The method invoked by userLeftSuccessful()
     * <p>
     * If the Lobby is left, meaning this Lobby Presenter is no longer needed, this presenter will no longer be
     * registered on the event bus and no longer be reachable for responses, messages etc.
     *
     * @param llsr the LobbyLeftSuccessfulResponse given by the original subscriber method
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.LobbyLeftSuccessfulResponse
     * @since 2021-01-20
     */
    public void userLeftSuccessfulLogic(LobbyLeftSuccessfulResponse llsr) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(llsr.getName())) {
                this.currentLobby = null;
                clearEventBus();
            }
        }
    }

    /**
     * Handles successful lobby join from the user
     * <p>
     * If a UserJoinedLobbyMessage is detected on the EventBus the method joinedSuccessfulLogic is invoked.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-03
     */
    @Subscribe
    public void joinedSuccessful(UserJoinedLobbyMessage message) {
        joinedSuccessfulLogic(message);
    }

    /**
     * The Method invoked by joinedSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Message, an update of the Users in the currentLobby
     * is done.
     *
     * @param ujlm the UserJoinedLobbyMessage given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2021-01-20
     */
    public void joinedSuccessfulLogic(UserJoinedLobbyMessage ujlm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(ujlm.getName())) {
                LOG.debug("Requesting update of User list in lobby because a User joined the lobby.");
                updateLobbyUsersList(ujlm.getUsers());
            }
        }
    }

    /**
     * Handles successful lobby leave of the user
     * <p>
     * If a UserJoinedLobbyMessage is detected on the EventBus the method leftSuccessfulLogic is invoked.
     *
     * @param message the UserLeftLobbyMessage object seen on the EventBus
     * @author Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2020-12-03
     */
    @Subscribe
    public void leftSuccessful(UserLeftLobbyMessage message) {
        leftSuccessfulLogic(message);
    }

    /**
     * The Method invoked by leftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored
     * in this LobbyPresenter equals the one in the received Message, an update of the Users in the currentLobby
     * is done.
     *
     * @param ullm the UserLeftLobbyMessage given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-20
     */
    public void leftSuccessfulLogic(UserLeftLobbyMessage ullm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(ullm.getName())) {
                LOG.debug("Requesting update of User list in lobby because a User left the lobby.");
                this.lobbyOwnerName = ullm.getLobbyOwner();
                updateLobbyUsersList(ullm.getUsers());
                if (ullm.getLobbyOwner().equals(joinedLobbyUser.getUsername())) {
                    isLobbyOwner = true;
                    setGameOptionsButtonsVisibility();
                }
            }
        }
    }

    /**
     * Handles new list of users
     * <p>
     * If a AllThisLobbyUsersResponse is detected on the EventBus the method lobbyUserListLogic is invoked.
     *
     * @param allThisLobbyUsersResponse the AllThisLobbyUsersResponse object seen on the EventBus
     * @author Marc Hermes, Ricardo Mook
     * @see AllThisLobbyUsersResponse
     * @since 2020-12-02
     */
    @Subscribe
    public void lobbyUserList(AllThisLobbyUsersResponse allThisLobbyUsersResponse) {
        lobbyUserListLogic(allThisLobbyUsersResponse);
    }

    /**
     * The Method invoked by leftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored in this
     * LobbyPresenter equals the one in the received Response, the method updateLobbyUsersList is invoked to update the
     * List of the Users in the currentLobby in regards to the list given by the response.
     *
     * @param atlur the AllThisLobbyUsersResponse given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.user.response.lobby.AllThisLobbyUsersResponse
     * @since 2021-01-20
     */
    public void lobbyUserListLogic(AllThisLobbyUsersResponse atlur) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(atlur.getName())) {
                LOG.debug("Update of user list " + atlur.getUsers());
                this.lobbyOwnerName = atlur.getLobbyOwnerName();
                updateLobbyUsersList(atlur.getUsers());

            }
        }
    }

    /**
     * Updates the lobby menu user list of the current lobby according to the list given
     * <p>
     * This method clears the entire user list and then adds the name of each user in the list given to the lobby menu
     * user list. If there ist no user list this creates one. Also if a user is identified as the lobby owner, "(Owner)" will be shown after their name.
     *
     * @param lobbyUserList A list of UserDTO objects including all currently logged in users
     * @implNote The code inside this Method has to run in the JavaFX-application thread. Therefore it is crucial not to
     * remove the {@code Platform.runLater()}
     * @author Marc Hermes, Ricardo Mook
     * @see de.uol.swp.common.user.UserDTO
     * @since 2020-12-02
     */
    private void updateLobbyUsersList(List<UserDTO> lobbyUserList) {
        updateLobbyUsersListLogic(lobbyUserList);
    }

    /**
     * This method gets invoked by the updateLobbyUsersList method
     *
     * @param l a list with UserDTOs
     * @author Marc Hermes, Ricardo Mook
     * @since 2020-12-02
     */
    public void updateLobbyUsersListLogic(List<UserDTO> l) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyUsers == null) {
                lobbyUsers = FXCollections.observableArrayList();
                lobbyUsersView.setItems(lobbyUsers);
            }
            lobbyUsers.clear();
            l.forEach(
                    u -> lobbyUsers.add(u.getUsername() + (u.getUsername().equals(lobbyOwnerName) ? " (Owner)" : "")));
        });
    }

    /**
     * Updates the lobby chat when a ResponseChatMessage was posted to the EventBus.
     * <p>
     * If a ResponseChatMessage is detected on the EventBus the method onResponseChatMessageLogic is invoked.
     *
     * @param message the ResponseChatMessage object seen on the EventBus
     * @author Rene
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2020-12-02
     */
    @Subscribe
    public void onResponseChatMessage(ResponseChatMessage message) {
        onResponseChatMessageLogic(message);
    }

    /**
     * The Method invoked by leftSuccessful()
     * <p>
     * If the currentLobby is not null, meaning this is an not an empty LobbyPresenter and the lobby name stored in this
     * LobbyPresenter equals the one in the received Response, the method updateChat is invoked to update the chat of
     * the currentLobby in regards to the input given by the response.
     *
     * @param rcm the ResponseChatMessage given by the original subscriber method.
     * @author Alexander Losse, Marc Hermes
     * @see de.uol.swp.common.chat.ResponseChatMessage
     * @since 2021-01-20
     */
    public void onResponseChatMessageLogic(ResponseChatMessage rcm) {
        // Only update Messages from used lobby chat
        if (this.currentLobby != null) {
            if (rcm.getChat().equals(currentLobby)) {
                LOG.debug("Updated lobby chat area with new message..");
                updateChat(rcm);
            }
        }
    }

    /**
     * Adds the ResponseChatMessage to the textArea
     *
     * @param message the chatMessage to update the chat with
     * @param message <p>
     *                <p>
     *                Enhanced by Sergej Tulnev
     * @author Alexander Losse, Marc Hermes
     * @since 2021-06-15
     * @since 2021-06-17
     * <p>
     * If the user has a long message, it will have a line break
     */
    private void updateChat(ResponseChatMessage message) {
        var time = new SimpleDateFormat("HH:mm");
        Date resultDate = new Date((long) message.getTime().doubleValue());
        var readableTime = time.format(resultDate);
        lobbyChatArea.insertText(lobbyChatArea.getLength(),
                readableTime + " " + message.getUsername() + ": " + message.getMessage() + "\n");
        lobbyChatArea.setWrapText(true);
    }


    /**
     * Method called when the StartGame button is pressed
     * <p>
     * If StartGameMessage is detected on the EventBus the method startGamePopupLogic is invoked.
     *
     * @param message The ActionEvent created by pressing the StartGame button
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.lobby.message.StartGameMessage
     * @since 2021-01-23
     */
    @Subscribe
    public void startGamePopup(StartGameMessage message) {
        startGamePopupLogic(message);
    }

    /**
     * The Method invoked by startGamePopup()
     * <p>
     * Method opens confirmation window with two options: Yes & No Which asks if each player is ready to start the
     * game.
     * <p>
     * enhanced by Marc Hermes - 2021-02-08
     *
     * @param sgm the startGamePopup given by the original subscriber method.
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.lobby.message.StartGameMessage
     * @since 2021-01-23
     */
    public void startGamePopupLogic(StartGameMessage sgm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(sgm.getName())) {
                gameAlreadyExistsLabel.setVisible(false);
                notLobbyOwnerLabel.setVisible(false);
                notEnoughPlayersLabel.setVisible(false);
                reasonWhyNotAbleToJoinGame.setVisible(false);
                Platform.runLater(() -> {
                    this.alert.setTitle("Start Game " + sgm.getName());
                    this.alert.setHeaderText("Ready to play?");
                    this.alert.show();
                });
            }
        }
    }

    /**
     * Handles unsuccessful start of the game.
     * <p>
     * If NotEnoughPlayersMessage is detected on the EventBus the method onNotEnoughPlayersMessageLogic is invoked.
     *
     * @param message the NotEnoughPlayersMessage object seen on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.message.NotEnoughPlayersMessage
     * @since 2021-01-23
     */
    @Subscribe
    public void onNotEnoughPlayersMessage(NotEnoughPlayersMessage message) {
        onNotEnoughPlayersMessageLogic(message);
    }

    /**
     * The Method invoked by onNotEnoughPlayersMessage()
     * <p>
     * Notifies player that not enough players are inside the lobby to start the game.
     *
     * @param nepm the NotEnoughPlayersMessage given by the original subscriber method.
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.message.NotEnoughPlayersMessage
     * @since 2021-01-23
     */
    public void onNotEnoughPlayersMessageLogic(NotEnoughPlayersMessage nepm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(nepm.getName())) {
                LOG.debug("Not enough Players in Lobby to start game");
                Platform.runLater(() ->
                        alert.close()
                );
                gameAlreadyExistsLabel.setVisible(false);
                notLobbyOwnerLabel.setVisible(false);
                notEnoughPlayersLabel.setVisible(true);
                reasonWhyNotAbleToJoinGame.setVisible(false);
            }
        }
    }

    /**
     * Handles unsuccessful start of the game.
     * <p>
     * If NotLobbyOwnerResponse is detected on the EventBus the method onNotLobbyOwnerResponseLogic is invoked.
     *
     * @param message the NotLobbyOwnerResponse object seen on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.response.NotLobbyOwnerResponse
     * @since 2021-01-23
     */
    @Subscribe
    public void onNotLobbyOwnerResponse(NotLobbyOwnerResponse message) {
        onNotLobbyOwnerResponseLogic(message);
    }

    /**
     * The Method invoked by onNotLobbyOwnerResponse()
     * <p>
     * Notifies player that he is not the lobby owner and therefore not allowed to start the game.
     *
     * @param nlor the NotLobbyOwnerResponse given by the original subscriber method.
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.response.NotLobbyOwnerResponse
     * @since 2021-01-23
     */
    public void onNotLobbyOwnerResponseLogic(NotLobbyOwnerResponse nlor) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(nlor.getLobbyName())) {
                notEnoughPlayersLabel.setVisible(false);
                gameAlreadyExistsLabel.setVisible(false);
                notLobbyOwnerLabel.setVisible(true);
                reasonWhyNotAbleToJoinGame.setVisible(false);
            }
        }
    }

    /**
     * Handles unsuccessful start of the game.
     * <p>
     * If GameAlreadyExistsResponse is detected on the EventBus the method onGameAlreadyExistsResponseLogic is invoked.
     *
     * @param message the GameAlreadyExistsResponse object seen on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.response.GameAlreadyExistsResponse
     * @since 2021-01-23
     */
    @Subscribe
    public void onGameAlreadyExistsResponse(GameAlreadyExistsResponse message) {
        onGameAlreadyExistsResponseLogic(message);
    }

    /**
     * The Method invoked by onGameAlreadyExistsResponse()
     * <p>
     * Notifies player that game already exists.
     *
     * @param gaer the GameAlreadyExistsResponse given by the original subscriber method.
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.response.GameAlreadyExistsResponse
     * @since 2021-01-23
     */
    public void onGameAlreadyExistsResponseLogic(GameAlreadyExistsResponse gaer) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(gaer.getLobbyName())) {
                LOG.debug("Game already exists.");
                notEnoughPlayersLabel.setVisible(false);
                notLobbyOwnerLabel.setVisible(false);
                gameAlreadyExistsLabel.setVisible(true);
                reasonWhyNotAbleToJoinGame.setVisible(false);
            }
        }
    }

    /**
     * When a JoinOnGoingGameResponse is detected on the EventBus this method is invoked.
     * <p>
     * If this is not an empty lobbyPresenter, this lobbyPresenter corresponds to the Response detected, and the user didn't successfully join the game
     * the reasonWhyNotAbleToJoinGame label is shown containing the reason for the failure.
     *
     * @param joggr the JoinOnGoingGameResponse detected on the EventBus
     * @author Marc Hermes
     * @since 2021-06-01
     */
    @Subscribe
    public void onJoinGameOnGoingResponse(JoinOnGoingGameResponse joggr) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(joggr.getGameName()) && !joggr.isJoinedSuccessful()) {
                LOG.debug("Couldn't join ongoing game because: " + joggr.getReasonForFailedJoin());
                notEnoughPlayersLabel.setVisible(false);
                notLobbyOwnerLabel.setVisible(false);
                gameAlreadyExistsLabel.setVisible(false);
                Platform.runLater(() -> reasonWhyNotAbleToJoinGame.setText(joggr.getReasonForFailedJoin()));
                reasonWhyNotAbleToJoinGame.setVisible(true);
            }
        }
    }

    /**
     * Handles successful creation of the game.
     * <p>
     * If GameCreatedMessage is detected on the EventBus the method gameCreatedSuccessfulLogic is invoked.
     *
     * @param message the NotEnoughPlayersResponse object seen on the EventBus
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.message.GameCreatedMessage
     * @since 2021-01-23
     */
    @Subscribe
    public void gameCreatedSuccessful(GameCreatedMessage message) {
        gameCreatedSuccessfulLogic(message);
    }

    /**
     * The Method invoked by gameCreatedSuccessful()
     * <p>
     * Notifies player that game is created. An update of the existing Games is also requested.
     *
     * @param gcm the GameCreatedMessage given by the original subscriber method.
     * @author Kirstin Beyer, Iskander Yusupov
     * @see de.uol.swp.common.game.message.GameCreatedMessage
     * @since 2021-01-23
     */
    public void gameCreatedSuccessfulLogic(GameCreatedMessage gcm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(gcm.getName())) {
                LOG.debug("New game " + gcm.getName() + " created");
                startGameButton.setVisible(false);
                joinGameButton.setVisible(true);
            }
        }
    }

    /**
     * When a GameDroppedMessage is detected on the EventBus this method is invoked
     * <p>
     * If the currentLobby is not null, meaning this isn't an empty presenter and the currentLobby equals the
     * lobby mentioned in the GameDroppedMessage button visibility is adjusted
     *
     * @param gdm the GameDroppedMessage detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void onGameDroppedMessage(GameDroppedMessage gdm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(gdm.getName())) {
                LOG.debug("The game " + gdm.getName() + " was dropped");
                startGameButton.setVisible(true);
                joinGameButton.setVisible(false);
            }
        }
    }

    /**
     * When a GameFinishedMessage is detected on the EventBus this method is invoked
     * <p>
     * If the currentLobby is not null, meaning this isn't an empty presenter and the currentLobby equals the
     * lobby mentioned in the GameFinishedMessage button visibility is adjusted
     *
     * @param gfm the GameFinishedMessage detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void onGameFinishedMessage(GameFinishedMessage gfm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(gfm.getName())) {
                LOG.debug("The game " + gfm.getName() + " has concluded");
                startGameButton.setVisible(true);
                joinGameButton.setVisible(false);
            }
        }
    }

    /**
     * When a GameStartedMessage is detected on the EventBus this method is invoked
     * <p>
     * If the currentLobby is not null, meaning this isn't an empty presenter and the currentLobby equals the
     * lobby mentioned in the GameStartedMessage button visibility is adjusted
     *
     * @param gsm the GameDroppedMessage detected on the EventBus
     * @author Marc Hermes
     * @since 2021-05-27
     */
    @Subscribe
    public void onGameStartedMessage(GameStartedMessage gsm) {
        if (this.currentLobby != null) {
            if (this.currentLobby.equals(gsm.getLobbyName())) {
                LOG.debug("New game " + gsm.getName() + " of this lobby started");
                startGameButton.setVisible(false);
                joinGameButton.setVisible(true);
            }
        }
    }
}
