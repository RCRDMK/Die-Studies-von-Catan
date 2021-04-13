package de.uol.swp.client.account;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.account.event.ChangeToCertainSizeEvent;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.account.event.ShowUserSettingsViewEvent;
import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.RetrieveUserMailResponse;
import de.uol.swp.common.user.response.UpdateUserSuccessfulResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Manages the UserSettings window
 *
 * @author Carsten Dekker
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-03-04
 */
public class UserSettingsPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/UserSettingsView.fxml";

    private static final Logger LOG = LogManager.getLogger(UserSettingsPresenter.class);

    private static final LeaveUserSettingsEvent leaveUserSettingsEvent = new LeaveUserSettingsEvent();

    private User loggedInUser;

    private Alert alert;

    private ButtonType buttonTypeYes;

    private ButtonType buttonTypeNo;

    private Button btnYes;

    private Button btnNo;

    @FXML
    private Label currentPasswordLabel;

    @FXML
    private Label newPasswordLabel1;

    @FXML
    private Label newPasswordLabel2;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField1;

    @FXML
    private PasswordField newPasswordField2;

    @FXML
    private Button confirmPasswordButton;

    @FXML
    private Label currentEmailLabel;

    @FXML
    private Label newEmailLabel1;

    @FXML
    private Label newEmailLabel2;

    @FXML
    private TextField currentEmailField;

    @FXML
    private TextField newEmailField1;

    @FXML
    private TextField newEmailField2;

    @FXML
    private Button confirmEmailButton;

    @FXML
    private Button confirmProfilePictureButton;

    @FXML
    private Rectangle profilePictureRectangle;

    @FXML
    private GridPane profilePicturesView;

    Rectangle[][] rectangles = new Rectangle[8][8];

    @FXML
    private Button leaveButton;

    @Inject
    private UserSettingsService userSettingsService;

    private int selectedPictureID;

    private boolean pictureLocked;

    private final ArrayList<Image> profilePictures = new ArrayList<>();

    private final ArrayList<ImagePattern> profilePicturePatterns = new ArrayList<>();

    /**
     * Method called when the Leave button is pressed.
     * <p>
     * If the Leave button is pressed, this methods posts an instance
     * of the leaveUserSettingsEvent to the EventBus the SceneManager is subscribed to.
     *
     * @param event The ActionEvent created by pressing the Leave button
     * @author Carsten Dekker
     * @see de.uol.swp.client.user.UserService
     * @since 2021-03-04
     */
    @FXML
    void onLeaveButtonPressed(ActionEvent event) {
        currentEmailField.clear();
        eventBus.post(leaveUserSettingsEvent);
    }

    /**
     * Method called when the DeleteUser button is pressed.
     * <p>
     * If the delete User button is pressed, this methods tries to request the UserService to send a specified request.
     * The request is of type DropUserRequest
     *
     * @param event The ActionEvent created by pressing the DeleteUser button
     * @author Carsten Dekker
     * @see de.uol.swp.client.user.UserService
     * @since 2020-12-15
     */
    @FXML
    void onDropUserButtonPressed(ActionEvent event) {
        alert.show();
    }

    /**
     * Method called when the changePassword MenuItem is pressed.
     * <p>
     * If the changePassword MenuItem is pressed, this methods sets all the labels, password fields and the confirm button
     * that are used  to change the password, to visible. It also hides the elements that are used to change the eMail.
     *
     * @param event The ActionEvent created by pressing the changePassword MenuItem
     * @author Carsten Dekker
     * @since 2021-03-06
     */
    @FXML
    public void onChangePasswordButtonPressed(ActionEvent event) {
        for (Label label : Arrays.asList(currentPasswordLabel, newPasswordLabel1, newPasswordLabel2)) {
            label.setVisible(true);
        }
        for (PasswordField passwordField : Arrays.asList(currentPasswordField, newPasswordField1, newPasswordField2)) {
            passwordField.setVisible(true);
        }

        for (Label label : Arrays.asList(currentEmailLabel, newEmailLabel1, newEmailLabel2)) {
            label.setVisible(false);
        }
        for (TextField textField : Arrays.asList(currentEmailField, newEmailField1, newEmailField2)) {
            textField.setVisible(false);
        }
        currentEmailField.setDisable(false);
        confirmPasswordButton.setVisible(true);
        confirmEmailButton.setVisible(false);
        confirmProfilePictureButton.setVisible(false);
        profilePicturesView.setVisible(false);
        profilePictureRectangle.setVisible(false);
        leaveButton.setLayoutX(234);
        leaveButton.setLayoutY(219);
    }

    /**
     * Method called when the changeEmail MenuItem is pressed.
     * <p>
     * If the changeEmail MenuItem is pressed, this methods sets all the labels, text fields and the confirm button
     * that are used  to change the mail address, to visible. It also hides the elements that are used to change the
     * password.
     *
     * @param event The ActionEvent created by pressing the changeEmail MenuItem
     * @author Carsten Dekker
     * @since 2021-03-06
     */
    @FXML
    void onChangeEmailButtonPressed(ActionEvent event) {
        for (Label label : Arrays.asList(currentPasswordLabel, newPasswordLabel1, newPasswordLabel2)) {
            label.setVisible(false);
        }
        for (PasswordField passwordField : Arrays.asList(currentPasswordField, newPasswordField1, newPasswordField2)) {
            passwordField.setVisible(false);
        }
        for (Label label : Arrays.asList(currentEmailLabel, newEmailLabel1, newEmailLabel2)) {
            label.setVisible(true);
        }
        for (TextField textField : Arrays.asList(currentEmailField, newEmailField1, newEmailField2)) {
            textField.setVisible(true);
        }
        currentEmailField.setDisable(true);
        confirmPasswordButton.setVisible(false);
        confirmEmailButton.setVisible(true);
        confirmProfilePictureButton.setVisible(false);
        profilePicturesView.setVisible(false);
        profilePictureRectangle.setVisible(false);
        leaveButton.setLayoutX(234);
        leaveButton.setLayoutY(219);
    }

    @FXML
    void onChangeProfilePictureButtonPressed(ActionEvent event) {
        for (Label label : Arrays.asList(currentPasswordLabel, newPasswordLabel1, newPasswordLabel2)) {
            label.setVisible(false);
        }
        for (PasswordField passwordField : Arrays.asList(currentPasswordField, newPasswordField1, newPasswordField2)) {
            passwordField.setVisible(false);
        }
        for (Label label : Arrays.asList(currentEmailLabel, newEmailLabel1, newEmailLabel2)) {
            label.setVisible(false);
        }
        for (TextField textField : Arrays.asList(currentEmailField, newEmailField1, newEmailField2)) {
            textField.setVisible(false);
        }
        currentEmailField.setDisable(true);
        confirmPasswordButton.setVisible(false);
        confirmEmailButton.setVisible(false);
        confirmProfilePictureButton.setVisible(true);
        profilePicturesView.setVisible(true);
        profilePictureRectangle.setVisible(true);
        leaveButton.setLayoutX(600);
        leaveButton.setLayoutY(425);
    }

    /**
     * Method called when the confirm password button is pressed.
     * <p>
     * If the confirm password button is pressed, this methods checks if the input is valid. If the input is valid, this
     * method tries to update the User via the userService, else it creates an error event.
     *
     * @param event The ActionEvent created by pressing the confirm password button
     * @author Carsten Dekker
     * @since 2021-03-17
     */
    @FXML
    void onConfirmPasswordButtonPressed(ActionEvent event) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (Strings.isNullOrEmpty(currentPasswordField.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Please enter your current password"));
        } else if (!newPasswordField1.getText().equals(newPasswordField2.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Passwords are not equal"));
        } else if (Strings.isNullOrEmpty(newPasswordField1.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Password cannot be empty"));
        } else {
            userService.updateUserPassword(new UserDTO(loggedInUser.getUsername(), newPasswordField1.getText(), ""),
                    currentPasswordField.getText());
        }
    }

    /**
     * Method called when the confirm email button is pressed.
     * <p>
     * If the confirm email button is pressed, this methods checks if the input is valid. If the input is valid, this
     * method tries to update the User via the userService, else it creates an error event. After the confirm button
     * was successful pressed the email text fields are cleared and the new email is written to the current email
     * field.
     *
     * @param event The ActionEvent created by pressing the confirm email button
     * @author Carsten Dekker
     * @since 2021-03-17
     */
    @FXML
    void onConfirmEmailButtonPressed(ActionEvent event) {
        if (!newEmailField1.getText().equals(newEmailField2.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Email addresses are not equal"));
        } else if (Strings.isNullOrEmpty(newEmailField1.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Email address cannot be empty"));
        } else if (!userSettingsService.isValidEmailAddress(newEmailField1.getText())) {
            eventBus.post(new UserSettingsErrorEvent("E-Mail is not valid"));
        } else {
            userService.updateUserMail(new UserDTO(loggedInUser.getUsername(), "", newEmailField1.getText()));
            currentEmailField.clear();
            currentEmailField.setText(newEmailField1.getText());
            newEmailField1.clear();
            newEmailField2.clear();
        }
    }

    @FXML
    void onConfirmProfilePictureButtonPressed(ActionEvent event) {

    }

    /**
     * Handles successful retrieving of the user information.
     * <p>
     * If a RetrieveUserMailResponse is detected on the EventBus the method retrieveUserMailResponseLogic
     * is invoked.
     *
     * @param response the RetrieveUserMailResponse object seen on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.response.RetrieveUserMailResponse
     * @since 2021-03-18
     */
    @Subscribe
    public void onRetrieveUserMailResponse(RetrieveUserMailResponse response) {
        retrieveUserMailResponseLogic(response);
    }

    /**
     * The Method invoked by onRetrieveUserMailResponse().
     * <p>
     * If the retriever user information process was successful the user from the RetrieveUserMailResponse is set
     * to the loggedInUser in this presenter. The text in the currentEmailField is set to the mail given from the
     * RetrieveUserMailResponse.
     *
     * @param response the UpdateUserSuccessfulResponse given by the original subscriber method.
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.response.RetrieveUserMailResponse
     * @since 2021-03-18
     */
    public void retrieveUserMailResponseLogic(RetrieveUserMailResponse response) {
        LOG.debug("User mail received " + response.getUser().getUsername() + response.getUser().getEMail());
        this.loggedInUser = response.getUser();
        currentEmailField.setText(response.getUser().getEMail());
    }

    /**
     * Handles successful update of the user.
     * <p>
     * If a UpdateUserSuccessfulResponse is detected on the EventBus the method updateUserSuccessfulResponseLogic
     * is invoked.
     *
     * @param response the UpdateUserSuccessfulResponse object seen on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.response.UpdateUserSuccessfulResponse
     * @since 2021-03-18
     */
    @Subscribe
    public void onUpdateUserSuccessfulResponse(UpdateUserSuccessfulResponse response) {
        updateUserSuccessfulResponseLogic(response);
    }

    /**
     * The Method invoked by onUpdateUserSuccessfulResponse().
     * <p>
     * If the update user process was successful the content of the password fields gets cleared.
     *
     * @param response the UpdateUserSuccessfulResponse given by the original subscriber method.
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.response.UpdateUserSuccessfulResponse
     * @since 2021-03-18
     */
    public void updateUserSuccessfulResponseLogic(UpdateUserSuccessfulResponse response) {
        LOG.debug("User successful response received " + response);
        currentPasswordField.clear();
        newPasswordField1.clear();
        newPasswordField2.clear();
    }

    /**
     * Handles ShowUserSettingsViewEvent detected on the EventBus
     * <p>
     * If a ShowUserSettingsViewEvent is detected on the EventBus, this method gets
     * called. It calls the method to create the buttons and the alert for the drop user button.
     *
     * @param event The ShowUserSettingsViewEvent detected on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.event.ShowUserSettingsViewEvent
     * @since 2021-03-17
     */
    @Subscribe
    public void onShowUserSettingsViewEvent(ShowUserSettingsViewEvent event) {
        if (this.loggedInUser == null) {
            Platform.runLater(this::setupButtonsAndAlerts);
        }
    }

    /**
     * The method invoked when the UserSettingsPresenter is first used.
     * <p>
     * The Alert asking the user whether he wants to delete his account or not as well as its corresponding
     * buttons buttonTypeYes/No are created.
     * Also 2 more hidden buttons are created whose ActionEvents are linked to the buttonTypeYes/No buttons
     * of the Alert.
     *
     * @author Carsten Dekker and Marc Hermes
     * @since 2021-03-18
     */
    public void setupButtonsAndAlerts() {
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);
        this.buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        this.buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        this.btnYes = (Button) alert.getDialogPane().lookupButton(buttonTypeYes);
        btnYes.setOnAction(event -> {
            onBtnYesClicked();
            event.consume();
        });
        this.btnNo = (Button) alert.getDialogPane().lookupButton(buttonTypeNo);
        btnNo.setOnAction(event -> {
            onBtnNoClicked();
            event.consume();
        });
        for (int i = 1; i <= 64; i++) {
            Image image;
            image = new Image("img/profilePictures/" + i + ".png");
            profilePictures.add(image);
            ImagePattern imagePattern;
            imagePattern = new ImagePattern(image);
            profilePicturePatterns.add(imagePattern);
        }
        profilePictureRectangle.setFill(profilePicturePatterns.get(0));
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                rectangles[i][j] = new Rectangle(50, 50);
                rectangles[i][j].setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!pictureLocked) {
                            Rectangle rectangle = (Rectangle) event.getSource();
                            profilePictureRectangle.setFill(rectangle.getFill());
                        }
                    }
                });
                rectangles[i][j].setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Rectangle rectangle = (Rectangle) event.getSource();
                        profilePictureRectangle.setFill(rectangle.getFill());
                        for (int i = 0; i < profilePicturePatterns.size(); i++) {
                            if (profilePicturePatterns.get(i).equals(rectangle.getFill())) {
                                selectedPictureID = i;
                                pictureLocked = true;
                            }
                        }
                    }
                });
                rectangles[i][j].setFill(profilePicturePatterns.get(counter));
                counter++;
                profilePicturesView.add(rectangles[i][j], j, i);
            }
        }
        profilePicturesView.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pictureLocked = false;
            }
        });


    }

    /**
     * The method invoked when the Yes Button of the Alert is pressed.
     * <p>
     * When the Button "Yes" is pressed in the Alert, the Alert will be closed and the userService will be called
     * to send a DropUserRequest to the server. The user also switches to the login view.
     *
     * @author Carsten Dekker
     * @since 2021-03-18
     */
    public void onBtnYesClicked() {
        alert.close();
        LOG.debug("User pressed the yes button");
        userService.dropUser(this.loggedInUser);
        eventBus.post(new ShowLoginViewEvent());
    }

    /**
     * The method invoked when the No Button of the Alert is pressed.
     * <p>
     * When the Button "No" is pressed in the Alert, the Alert will be closed and the user turns back to the
     * UserSettingsView.
     *
     * @author Carsten Dekker
     * @since 2021-03-18
     */
    public void onBtnNoClicked() {
        alert.close();
        LOG.debug("User pressed the no button");
    }
    /*
    public void changeSize(double width, double height) {
        ChangeToCertainSizeEvent ctcse = new ChangeToCertainSizeEvent(width, height);
        eventBus.post(ctcse);
    }
    */
}

