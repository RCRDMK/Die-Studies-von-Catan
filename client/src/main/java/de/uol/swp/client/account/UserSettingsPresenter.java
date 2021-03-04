package de.uol.swp.client.account;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.account.event.LeaveUserSettingsEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.user.ClientUserService;
import de.uol.swp.common.user.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

}
