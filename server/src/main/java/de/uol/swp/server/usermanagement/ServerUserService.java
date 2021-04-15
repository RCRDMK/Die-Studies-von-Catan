package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface for all methods of the server user service
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */

public interface ServerUserService {

    /**
     * Login with username and password
     *
     * @author Marco Grawunder
     * @param username the name of the user
     * @param password the password of the user
     * @return a new user object
     * @since 2017-03-17
     */
    User login(String username, String password) throws SQLException;


    /**
     * Test, if given user is logged in
     *
     * @author Marco Grawunder
     * @param user the user to check for
     * @return true if the User is logged in
     * @since 2019-09-04
     */
    boolean isLoggedIn(User user);

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
     * @author Marco Grawunder
     * @param user The user to create
     * @return the new created user
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    User createUser(User user) throws SQLException;

    /**
     * Removes a user from the sore
     * <p>
     * Remove the User specified by the User object.
     *
     * @author Marco Grawunder
     * @param user The user to remove
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-10-10
     */
    void dropUser(User user) throws SQLException;

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @author Carsten Dekker
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @return the updated user object
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    User updateUserMail(User user) throws SQLException;

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * 			 update the correct user
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @return the updated user object
     * @since 2019-09-02
     */
    User updateUserPassword(User user, String password) throws SQLException;

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @param user the user object containing all infos to update
     * @return the updated user object
     * @since 2021-04-15
     */
    User updateUserPicture(User user) throws SQLException;

    /**
     * Retrieve the list of all current logged in users
     *
     * @author Marco Grawunder
     * @return a list of users
     * @since 2017-03-17
     */
    List<User> retrieveAllUsers() throws SQLException;

    /**
     * Retrieve the user information of the currently logged in user
     *
     * @author Carsten Dekker
     * @return user information
     * @since 2021-03-11
     */
    User retrieveUserInformation(User user) throws SQLException;
}
