package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;

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
     * @param username the name of the user
     * @param password the password of the user
     * @return a new user object
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    User login(String username, String password) throws Exception;


    /**
     * Test, if given user is logged in
     *
     * @param user the user to check for
     * @return true if the User is logged in
     * @author Marco Grawunder
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
     * @param user The user to create
     * @return the new created user
     * @author Marco Grawunder
     * @implNote the User Object has to contain a unique identifier in order to
     * remove the correct user
     * @since 2019-09-02
     */
    User createUser(User user) throws Exception;

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
    void dropUser(User user) throws Exception;

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @return the updated user object
     * @author Carsten Dekker
     * @author Marco Grawunder
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    User updateUserMail(User user) throws Exception;

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to
     *             update, if some values are not set, (e.g. password is "")
     *             these fields are not updated
     * @return the updated user object
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @author Marco Grawunder
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2019-09-02
     */
    User updateUserPassword(User user, String password) throws Exception;

    /**
     * Update a user
     * <p>
     * Updates the User specified by the User object.
     *
     * @param user the user object containing all infos to update
     * @return the updated user object
     * @author Carsten Dekker
     * @implNote the User Object has to contain a unique identifier in order to
     * update the correct user
     * @since 2021-04-15
     */
    User updateUserPicture(User user) throws Exception;

    /**
     * Retrieve the user information of the currently logged in user
     *
     * @return user information
     * @author Carsten Dekker
     * @since 2021-03-11
     */
    User retrieveUserInformation(User user) throws Exception;
}
