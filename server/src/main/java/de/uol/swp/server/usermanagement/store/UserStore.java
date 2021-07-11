package de.uol.swp.server.usermanagement.store;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import de.uol.swp.common.user.User;

/**
 * Interface to unify different kinds of UserStores in order to able to exchange
 * them easily.
 *
 * @author Marco Grawunder
 * @since 2019-08-13
 */
public interface UserStore {

    /**
     * Find a user by username and password
     *
     * @param username username of the user to find
     * @param password password of the user to find
     * @return The User without password information, if found
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    Optional<User> findUser(String username, String password) throws Exception;

    /**
     * Find a user only by name
     *
     * @param username username of the user to find
     * @return The User without password information, if found
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    Optional<User> findUser(String username) throws Exception;

    /**
     * Create a new user
     *
     * @param username username of the new user
     * @param password password the user wants to use
     * @param eMail    email address of the new user
     * @return The User without password information
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    User createUser(String username, String password, String eMail) throws Exception;

    /**
     * Remove user from store
     *
     * @param username the username of the user to remove
     * @author Marco Grawunder
     * @since 2019-10-10
     */
    void removeUser(String username) throws Exception;

    /**
     * Updates the users eMail
     *
     * @param username username of the user
     * @param eMail    the new eMail
     * @return the updated user without password
     * @author Carsten Dekker
     * @since 2021-05-26
     */
    User updateUserMail(String username, String eMail) throws Exception;

    /**
     * Updates the users password
     *
     * @param username username of the user
     * @param password the new password
     * @return the updated user without password
     * @author Carsten Dekker
     * @since 2021-05-26
     */
    User updateUserPassword(String username, String password) throws Exception;

    /**
     * Updates the users profilePictureID
     *
     * @param username         username of the user
     * @param profilePictureID the new profilePictureID
     * @return the updated user without password
     * @author Carsten Dekker
     * @since 2021-05-26
     */
    User updateUserPicture(String username, int profilePictureID) throws Exception;

    /**
     * Retrieves the list of all users.
     *
     * @return A list of all users without password information
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    List<User> getAllUsers() throws SQLException;
}
