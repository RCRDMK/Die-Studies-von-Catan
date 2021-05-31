package de.uol.swp.server.usermanagement;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.exception.DropUserExceptionMessage;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.exception.RetrieveUserInformationExceptionMessage;
import de.uol.swp.common.user.exception.UpdateUserExceptionMessage;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.DropUserSuccessfulResponse;
import de.uol.swp.common.user.response.RegistrationSuccessfulResponse;
import de.uol.swp.common.user.response.RetrieveUserInformationResponse;
import de.uol.swp.common.user.response.UpdateUserSuccessfulResponse;
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
     * @author Marco Grawunder
     * @see de.uol.swp.server.usermanagement.UserManagement
     * @since 2019-08-05
     */
    @Inject
    public UserService(EventBus eventBus, UserManagement userManagement) throws SQLException {
        super(eventBus);
        this.userManagement = userManagement;
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
     * @author Marco Grawunder
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
            returnMessage = new RegistrationExceptionMessage("Cannot create user " + msg.getUser() + " " +
                    e.getMessage());
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
            returnMessage = new DropUserExceptionMessage("Cannot drop user " + dropUserRequest.getUser() + " " +
                    e.getMessage());
        }
        returnMessage.setMessageContext(dropUserRequest.getMessageContext().get());
        post(returnMessage);
    }

    /**
     * Handles RetrieveUserInformationRequest found on the EventBus
     * <p>
     * If a RetrieveUserInformationRequest is detected on the EventBus, this method is called.
     * It tries to get the eMail from the user via the UserManagement. If this succeeds a
     * RetrieveUserInformationResponse is posted on the EventBus otherwise a RetrieveUserInformationExceptionMessage
     * gets posted there.
     *
     * @param retrieveUserInformationRequest The RetrieveUserInformationRequest found on the EventBus
     * @author Carsten Dekker
     * @see RetrieveUserInformationRequest
     * @since 2021-03-12
     */
    @Subscribe
    private void onRetrieveUserInformation(RetrieveUserInformationRequest retrieveUserInformationRequest) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got a new retrieveUserMail request with " + retrieveUserInformationRequest.getUser());
        }
        ResponseMessage returnMessage;
        try {
            returnMessage = new RetrieveUserInformationResponse(userManagement.retrieveUserInformation(retrieveUserInformationRequest.getUser()));
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new RetrieveUserInformationExceptionMessage("Cannot get user information "
                    + retrieveUserInformationRequest.getUser() + " " + e.getMessage());
        }
        if (retrieveUserInformationRequest.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(retrieveUserInformationRequest.getMessageContext().get());
        }
        post(returnMessage);
    }

    /**
     * This method gets invoked from the startGame method in the gameService
     * <p>
     * This method adds the profilePictureID to the users, that get added to the userInGame ArrayList
     * in the GameService.
     *
     * @param user the user to get the profilePictureID from
     * @return user with the profilePictureID
     * @author Carsten Dekker
     * @since 2021-04-18
     */
    public User retrieveUserInformation(User user) {
        try {
            return userManagement.retrieveUserInformation(user);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return user;
    }

    /**
     * Handles UpdateUserProfilePictureRequest found on the eventBus
     * <p>
     * If an UpdateUserProfilePictureRequest is detected on zhe eventBus, this method is called.
     * It tries to update the users profilePictureID via the UserManagement. If this succeeds a
     * UpdateUserSuccessfulResponse is posted on the EventBus otherwise a UpdateUserExceptionMessage
     * gets posted there.
     *
     * @param uuppr the UpdateUserProfilePictureRequest found on the eventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.UpdateUserProfilePictureRequest
     * @since 2021.04.15
     */
    @Subscribe
    private void onUpdateUserPictureRequest(UpdateUserProfilePictureRequest uuppr) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got a new updateUserPictureRequest with " + uuppr.getUser());
            ResponseMessage returnMessage;
            try {
                userManagement.updateUserPicture(uuppr.getUser());
                returnMessage = new UpdateUserSuccessfulResponse();
            } catch (Exception e) {
                LOG.error(e);
                returnMessage = new UpdateUserExceptionMessage("Cannot update user " + uuppr.getUser() + " " +
                        e.getMessage());
            }
            if (uuppr.getMessageContext().isPresent()) {
                returnMessage.setMessageContext(uuppr.getMessageContext().get());
            }
            post(returnMessage);
        }
    }


    /**
     * Handles UpdateUserMailRequest found on the EventBus
     * <p>
     * If a UpdateUserMailRequest is detected on the EventBus, this method is called.
     * It tries to update the users mail address via the UserManagement. If this succeeds a
     * UpdateUserSuccessfulResponse is posted on the EventBus otherwise a UpdateUserExceptionMessage
     * gets posted there.
     *
     * @param updateUserMailRequest The UpdateUserRequest found on the EventBus
     * @param updateUserMailRequest The UpdateUserRequest found on the EventBus
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.UpdateUserMailRequest
     * @since 2021-03-14
     */
    @Subscribe
    private void onUpdateUserMailRequest(UpdateUserMailRequest updateUserMailRequest) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got a new updateUserMail request with " + updateUserMailRequest.getUser());
        }
        ResponseMessage returnMessage;
        try {
            userManagement.updateUserMail(updateUserMailRequest.getUser());
            returnMessage = new UpdateUserSuccessfulResponse();
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new UpdateUserExceptionMessage("Cannot update user " + updateUserMailRequest.getUser() + " " +
                    e.getMessage());
        }
        if (updateUserMailRequest.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(updateUserMailRequest.getMessageContext().get());
        }
        post(returnMessage);
    }

    /**
     * Handles UpdateUserPasswordRequest found on the EventBus
     * <p>
     * If a UpdateUserPasswordRequest is detected on the EventBus, this method is called.
     * It tries to update the user via the UserManagement. If this succeeds a
     * UpdateUserSuccessfulResponse is posted on the EventBus otherwise a UpdateUserExceptionMessage
     * gets posted there.
     *
     * @param updateUserPasswordRequest The UpdateUserRequest found on the EventBus
     * @author Carsten Dekker
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.UpdateUserPasswordRequest
     * @since 2021-03-14
     */
    @Subscribe
    private void onUpdateUserPasswordRequest(UpdateUserPasswordRequest updateUserPasswordRequest) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got a new updateUserPassword request with " + updateUserPasswordRequest.getUser());
        }
        ResponseMessage returnMessage;
        try {
            userManagement.updateUserPassword(updateUserPasswordRequest.getUser(), updateUserPasswordRequest.getCurrentPassword());
            returnMessage = new UpdateUserSuccessfulResponse();
        } catch (Exception e) {
            LOG.error(e);
            returnMessage = new UpdateUserExceptionMessage("Cannot update user " + updateUserPasswordRequest.getUser() + " " +
                    e.getMessage());
        }
        if (updateUserPasswordRequest.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(updateUserPasswordRequest.getMessageContext().get());
        }
        post(returnMessage);
    }
}
