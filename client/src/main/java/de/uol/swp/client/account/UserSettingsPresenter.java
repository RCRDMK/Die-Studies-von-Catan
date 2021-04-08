package de.uol.swp.client.account;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.account.event.ShowUserSettingsViewEvent;
import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.response.RetrieveUserMailResponse;
import de.uol.swp.common.user.response.UpdateUserSuccessfulResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 * Manages the UserSettings window
 *
 * @author Carsten Dekker
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2021-03-04
 *
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
    private ListView<String> profilePictureListView;

    @FXML
    private ImageView profilePictureImageView;

    @Inject
    private UserSettingsService userSettingsService;

    Image image1 = new Image("img/001.png");

    Image image2 = new Image("img/002.png");

    Image image3 = new Image("img/003.png");

    Image image4 = new Image("img/004.png");

    Image image5 = new Image("img/005.png");

    Image image6 = new Image("img/006.png");

    Image image7 = new Image("img/007.png");

    Image image8 = new Image("img/008.png");

    Image image9 = new Image("img/009.png");

    /**
     * Method called when the Leave button is pressed.
     * <p>
     * If the Leave button is pressed, this methods posts an instance
     * of the leaveUserSettingsEvent to the EventBus the SceneManager is subscribed to.
     *
     * @param event The ActionEvent created by pressing the Leave button
     *
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
     *
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
     *
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
        profilePictureListView.setVisible(false);
        profilePictureImageView.setVisible(false);
    }

    /**
     * Method called when the changeEmail MenuItem is pressed.
     * <p>
     * If the changeEmail MenuItem is pressed, this methods sets all the labels, text fields and the confirm button
     * that are used  to change the mail address, to visible. It also hides the elements that are used to change the
     * password.
     *
     * @param event The ActionEvent created by pressing the changeEmail MenuItem
     *
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
        profilePictureListView.setVisible(false);
        profilePictureImageView.setVisible(false);
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
        profilePictureListView.setVisible(true);
        profilePictureImageView.setVisible(true);
    }

    /**
     * Method called when the confirm password button is pressed.
     * <p>
     * If the confirm password button is pressed, this methods checks if the input is valid. If the input is valid, this
     * method tries to update the User via the userService, else it creates an error event.
     *
     * @param event The ActionEvent created by pressing the confirm password button
     *
     * @author Carsten Dekker
     * @since 2021-03-17
     */
    @FXML
    void onConfirmPasswordButtonPressed(ActionEvent event) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (Strings.isNullOrEmpty(currentPasswordField.getText())){
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
     *
     * @author Carsten Dekker
     * @since 2021-03-17
     */
    @FXML
    void onConfirmEmailButtonPressed(ActionEvent event)  {
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
     * @see de.uol.swp.client.account.event.ShowUserSettingsViewEvent
     * @author Carsten Dekker
     * @since 2021-03-17
     */
    @Subscribe
    public void onShowUserSettingsViewEvent(ShowUserSettingsViewEvent event) {
        if(this.loggedInUser == null) {
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
        this.btnYes = (Button) alert.getDialogPane().lookupButton( buttonTypeYes );
        btnYes.setOnAction( event -> {
            onBtnYesClicked();
            event.consume();
        } );
        this.btnNo = (Button) alert.getDialogPane().lookupButton( buttonTypeNo );
        btnNo.setOnAction( event -> {
            onBtnNoClicked();
            event.consume();
        } );
        //Rectangle rectangle = new Rectangle();
        profilePictureImageView.setImage(image1);
        //profilePictureImageView.setClip(rectangle);
        ObservableList<String> profilePictures;
        profilePictures = FXCollections.observableArrayList();
        profilePictureListView.setItems(profilePictures);
        profilePictureListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + profilePictureListView.getSelectionModel().getSelectedItem());
                try {
                    profilePictureImageView.setImage(showPicturePreview(profilePictureListView.getSelectionModel().getSelectedItem()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        for(int i = 1; i <= 20; i++) {
            profilePictures.add("picture" + i);
        }
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

    public Image showPicturePreview(String value) throws FileNotFoundException {
        switch (value) {
            case "picture1":
                return image1;
            case "picture2":
                return image2;
            case "picture3":
                return image3;
            case "picture4":
                return image4;
            case "picture5":
                return image5;
            case "picture6":
                return image6;
            case "picture7":
                return image7;
            case "picture8":
                return image8;
            case "picture9":
                return image9;
            case "picture10":
                return image1;
            case "picture11":
                return image1;
            case "picture12":
                return image1;
            case "picture13":
                return image1;
            case "picture14":
                return image1;
            case "picture15":
                return image1;
            case "picture16":
                return image1;
            case "picture17":
                return image1;
            case "picture18":
                return image1;
            case "picture19":
                return image1;
            case "picture20":
                return image1;
            default:
                throw new IllegalStateException("Unexpected value: " + value);
        }

    }
}
