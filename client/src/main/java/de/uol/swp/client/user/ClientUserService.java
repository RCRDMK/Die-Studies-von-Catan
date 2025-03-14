package de.uol.swp.client.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the client user service
 * <p>
 * As the communication with the server is based on events, the
 * returns of the call must be handled by events
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */

public interface ClientUserService {

    /**
     * Login with username and password
     *
     * @param username the name of the user
     * @param password the password of the user
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    void login(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException;

    /**
     * Log out from server
     *
     * @author Marco Grawunder
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2017-03-17
     */
    void logout(User user);

    /**
     * Create a new persistent user
     *
     * @param user The user to create
     * @author Marco Grawunder
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    void createUser(User user) throws InvalidKeySpecException, NoSuchAlgorithmException;

    /**
     * Removes a user from the sore
     * <p>
     * Remove the User specified by the User object.
     *
     * @param user The user to remove
     * @author Marco Grawunder
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-10-10
     */
    void dropUser(User user);

    /**
     * Update the password from a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2021-03-14
     */
    void updateUserPassword(User user, String currentPassword) throws InvalidKeySpecException, NoSuchAlgorithmException;

    /**
     * Update the mail from a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2021-03-14
     */
    void updateUserMail(User user);

    /**
     * Update the profilePicture from a user
     * <p>
     * Updates the user specified by the User object
     *
     * @param user the user object containing all info to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2021-04-15
     */
    void updateUserProfilePicture(User user);

    /**
     * Retrieve the list of all current logged in users
     *
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    void retrieveAllUsers();

    /**
     * Method used to start the timer for sending pings
     *
     * @param user the user of this client who is currently logged in
     * @author Philip Nitsche
     * @since 2021-01-21
     */
    void startTimerForPing(User user);

    /**
     * Method used to end the timer for sending pings
     *
     * @author Philip Nitsche
     * @since 2021-01-21
     */
    void endTimerForPing();
}
