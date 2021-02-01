package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.DropUserExceptionMessage;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.request.DropUserRequest;
import de.uol.swp.common.user.request.PingRequest;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.response.*;
import de.uol.swp.server.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * Mapping vom event bus calls to user management calls
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.AbstractService
 * @since 2019-08-05
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class UserService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final UserManagement userManagement;

    /**
     * Constructor
     *
     * @param eventBus       the EventBus used throughout the entire server (injected)
     * @param userManagement object of the UserManagement to use
     * @see de.uol.swp.server.usermanagement.UserManagement
     * @since 2019-08-05
     */
    @Inject
    public UserService(EventBus eventBus, UserManagement userManagement) throws SQLException {
        super(eventBus);
        this.userManagement = userManagement;
        this.userManagement.buildConnection();
    }

    /**
     * Handles RegisterUserRequests found on the EventBus
     * <p>
     * If a RegisterUserRequest is detected on the EventBus, this method is called.
     * It tries to create a new user via the UserManagement. If this succeeds a
     * RegistrationSuccessfulResponse is posted on the EventBus otherwise a RegistrationExceptionMessage
     * gets posted there.
     *
     * @param msg The RegisterUserRequest found on the EventBus
     * @see de.uol.swp.server.usermanagement.UserManagement#createUser(User)
     * @see de.uol.swp.common.user.request.RegisterUserRequest
     * @see de.uol.swp.common.user.response.RegistrationSuccessfulResponse
     * @see de.uol.swp.common.user.exception.RegistrationExceptionMessage
     * @since 2019-09-02
     */
    @Subscribe
    private void onRegisterUserRequest(RegisterUserRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new registration message with " + msg.getUser());
        }
        ResponseMessage returnMessage;
        try {
            User newUser = userManagement.createUser(msg.getUser());
            returnMessage = new RegistrationSuccessfulResponse();
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new RegistrationExceptionMessage("Cannot create user " + msg.getUser() + " " + e.getMessage());
        }
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
        post(returnMessage);
    }

    /**
     * Handles DropUserRequests found on the EventBus
     * <p>
     * If a DropUserRequest is detected on the EventBus, this method is called.
     * It tries to delete the user via the UserManagement. If this succeeds a
     * DropUserSuccessfulResponse is posted on the EventBus otherwise a DropUserExceptionMessage
     * gets posted there.
     *
     * @param dropUserRequest The DropUserRequest found on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.DropUserRequest
     * @since 2020-12-15
     */
    @Subscribe
    private void onDropUserRequest(DropUserRequest dropUserRequest) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new dropUser request with " + dropUserRequest.getUser());
        }
        ResponseMessage returnMessage;
        try {
            userManagement.dropUser(dropUserRequest.getUser());
            returnMessage = new DropUserSuccessfulResponse();
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new DropUserExceptionMessage("Cannot drop user " + dropUserRequest.getUser() + " " + e.getMessage());
        }
        post(returnMessage);
    }

    /**
     * Handles PingRequests found on the EventBus
     * <p>
     * If a PingRequest is detected on the EventBus, this method is called.
     * It sends a PingResponse back to the User.
     * It tells the ActivUserList the last send Ping time from this User.
     *
     * @param pingRequest The PingRequest found on the EventBus
     * @author Philip Nitsche
     * @see de.uol.swp.common.user.request.PingRequest
     * @since 2021-01-22
     */

    @Subscribe
    private void onPingRequest(PingRequest pingRequest) {
        ActivUserList.updateActivUser(pingRequest.getUsername(), pingRequest.getTime());
        ResponseMessage returnMessage;
        returnMessage = new PingResponse(pingRequest.getUsername(), pingRequest.getTime());
        post(returnMessage);
    }

}
