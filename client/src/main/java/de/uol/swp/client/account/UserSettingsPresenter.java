package de.uol.swp.client.account;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.account.event.UserSettingsErrorEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    /**
     * Default Constructor
     *
     * @since 2021-03-04
     */
    public UserSettingsPresenter() {
    }

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @param userService The injected ClientUserService
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-03-04
     */
    @Inject
    public UserSettingsPresenter(EventBus eventBus, ClientUserService userService) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the Leave button is pressed
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
        eventBus.post(leaveUserSettingsEvent);
    }

    /**
     * Method called when the DeleteUser button is pressed
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
        userService.dropUser(this.loggedInUser);
        eventBus.post(new ShowLoginViewEvent());
    }

    /**
     * Method called when the changePassword MenuItem is pressed
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
        confirmPasswordButton.setVisible(true);
        confirmEmailButton.setVisible(false);
    }

    /**
     * Method called when the changeEmail MenuItem is pressed
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
        confirmPasswordButton.setVisible(false);
        confirmEmailButton.setVisible(true);
    }

    @FXML
    void onConfirmPasswordButtonPressed(ActionEvent event) {
        if (Strings.isNullOrEmpty(currentPasswordField.getText())){
            eventBus.post(new UserSettingsErrorEvent("Please enter your current password"));
        } else if (!newPasswordField1.getText().equals(newPasswordField2.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Passwords are not equal"));
        } else if (Strings.isNullOrEmpty(newPasswordField1.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Password cannot be empty"));
        } else {
            userService.updateUser(new UserDTO(loggedInUser.getUsername(), newPasswordField1.getText(), ""));
        }
    }

    @FXML
    void onConfirmEmailButtonPressed(ActionEvent event) {
        if (Strings.isNullOrEmpty(currentPasswordField.getText())){
            eventBus.post(new UserSettingsErrorEvent("Please enter your current password"));
        } else if (!newPasswordField1.getText().equals(newPasswordField2.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Passwords are not equal"));
        } else if (Strings.isNullOrEmpty(newPasswordField1.getText())) {
            eventBus.post(new UserSettingsErrorEvent("Password cannot be empty"));
        } else {
            userService.updateUser(new UserDTO(loggedInUser.getUsername(), "", ""));
        }
    }
}
