package de.uol.swp.client.register;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.UserDTO;

/**
 * Manages the registration window
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-29
 */
public class RegistrationPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/RegistrationView.fxml";

    private static final RegistrationCanceledEvent registrationCanceledEvent = new RegistrationCanceledEvent();

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;

    @FXML
    private TextField emailField1;

    @FXML
    private TextField emailField2;

    @Inject
    private RegistrationService registrationService;


    /**
     * Constructor
     *
     * @param eventBus    The EventBus set in ClientModule
     * @param userService The injected ClientUserService
     * @author Marco Grawunder
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-09-18
     */
    @Inject
    public RegistrationPresenter(EventBus eventBus, ClientUserService userService) {
        setEventBus(eventBus);
    }

    /**
     * Method called when the cancel button is pressed
     * <p>
     * This Method is called when the cancel button is pressed. It posts an instance
     * of the RegistrationCanceledEvent to the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.event.RegistrationCanceledEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2019-09-02
     */
    @FXML
    void onCancelButtonPressed() {
        eventBus.post(registrationCanceledEvent);
    }

    /**
     * Method called when the register button is pressed
     * <p>
     * This Method is called when the register button is pressed. It posts an instance
     * of the RegistrationErrorEvent to the EventBus the SceneManager is subscribed
     * to, if one of the fields is empty, the password and E-Mail fields are not equal or the
     * E-Mail is not valid.
     * If everything is filled in correctly the user service is requested to create
     * a new user.
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @see de.uol.swp.client.register.RegistrationService
     * @since 2019-09-02
     * <p>
     * Enhanced by Carsten Dekker
     * @since 2021-01-15
     */
    @FXML
    void onRegisterButtonPressed() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (Strings.isNullOrEmpty(loginField.getText())) {
            eventBus.post(new RegistrationErrorEvent("Username cannot be empty"));
        } else if (!passwordField1.getText().equals(passwordField2.getText())) {
            eventBus.post(new RegistrationErrorEvent("Passwords are not equal"));
        } else if (Strings.isNullOrEmpty(passwordField1.getText())) {
            eventBus.post(new RegistrationErrorEvent("Password cannot be empty"));
        } else if (!emailField1.getText().equals(emailField2.getText())) {
            eventBus.post(new RegistrationErrorEvent("E-Mail Addresses are not equal"));
        } else if (Strings.isNullOrEmpty(emailField1.getText())) {
            eventBus.post(new RegistrationErrorEvent("E-Mail cannot be empty"));
        } else if (!registrationService.isValidEmailAddress(emailField1.getText())) {
            eventBus.post(new RegistrationErrorEvent("E-Mail is not valid"));
        } else {
            userService.createUser(new UserDTO(loginField.getText(), passwordField1.getText(), emailField1.getText()));
            loginField.clear();
            passwordField1.clear();
            passwordField2.clear();
            emailField1.clear();
            emailField2.clear();
        }
    }


}
