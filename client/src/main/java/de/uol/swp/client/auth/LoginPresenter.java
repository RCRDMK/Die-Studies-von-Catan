package de.uol.swp.client.auth;

import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Manages the login window
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2019-08-08
 */
public class LoginPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LoginView.fxml";

    private static final ShowRegistrationViewEvent showRegViewEvent = new ShowRegistrationViewEvent();

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    /**
     * Default Constructor
     *
     * @author Marco Grawunder
     * @since 2019-08-18
     */
    public LoginPresenter() {
    }

    /**
     * Method called when the login button is pressed
     * <p>
     * This Method is called when the login button is pressed. It takes the text
     * entered in the login and password field and gives the user service a request
     * to log in the user specified by those fields.
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.user.UserService
     * @since 2019-08-13
     */
    @FXML
    private void onLoginButtonPressed() throws InvalidKeySpecException, NoSuchAlgorithmException {
        userService.login(loginField.getText(), passwordField.getText());
        passwordField.clear();
        loginField.clear();
    }

    /**
     * Method called when the register button is pressed
     * <p>
     * This Method is called when the register button is pressed. It posts an instance
     * of the ShowRegistrationViewEvent to the EventBus the SceneManager is subscribed
     * to.
     *
     * @author Marco Grawunder
     * @see de.uol.swp.client.register.event.ShowRegistrationViewEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2019-08-13
     */
    @FXML
    private void onRegisterButtonPressed() {
        eventBus.post(showRegViewEvent);
    }
}
