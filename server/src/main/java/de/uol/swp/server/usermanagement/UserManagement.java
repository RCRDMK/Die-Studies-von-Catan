package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import de.uol.swp.server.usermanagement.store.UserStore;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * Handles most user related issues e.g. login/logout
 *
 * @author Marco Grawunder
 * @author Marius Birk
 * @see de.uol.swp.server.usermanagement.AbstractUserManagement
 * @since 2019-08-05
 * <p>
 * Enhanced this class to make it possible to work with our database at the servers of the university.
 * With that it is possible to retrieve, update, delete or insert users. Also it is possible to login/logout.
 * @since 2021-01-19
 * <p>
 * Enhanced the complete class with PreparedStatements. PreparedStatements do not affect the functionality, but they secure our database
 * from unwanted usernames for example.
 * @since 2021-02-26
 */
public class UserManagement extends AbstractUserManagement {

    private static final SortedMap<String, User> loggedInUsers = new TreeMap<>();
    private final UserStore storeInUse;

    /**
     * Constructor
     *
     * @author Marius Birk
     * @see de.uol.swp.server.usermanagement.store.UserStore
     * @since 2019-08-05
     * <p>
     * The constructor changed to an empty constructor.
     * @since 2021-01-19
     */

    @Inject
    public UserManagement(UserStore storeInUse) throws SQLException {
        this.storeInUse = storeInUse;
    }

    /**
     * Login the user
     * <p>
     * This function finds the user from the provided username and password and if the user is present,
     * it puts the user in the loggedInUsers SortedMap. It also returns the User object
     *
     * @author
     * @see Exception
     * @since
     */
    @Override
    public User login(String username, String password) throws Exception {
        Optional<User> user = storeInUse.findUser(username, password);
        if (user.isPresent()) {
            loggedInUsers.put(username, user.get());
            return user.get();
        } else {
            throw new SecurityException("Cannot auth user " + username);
        }
    }

    /**
     * Check if a user is logged in
     * <p>
     * This function checks if the user exists in the loggedInUsers SortedMap. If so it returns true.
     *
     * @author
     * @see Exception
     * @since
     */
    @Override
    public boolean isLoggedIn(User username) {
        return loggedInUsers.containsKey(username.getUsername());
    }

    /**
     * Creates a user
     * <p>
     * This function creates a user if the user doesnt exist already. If it does it throws a UserManagementException.
     *
     * @author
     * @see Exception
     * @since
     */
    @Override
    public User createUser(User userToCreate) throws Exception {
        Optional<User> user = storeInUse.findUser(userToCreate.getUsername());
        if (user.isPresent()) {
            throw new UserManagementException("Username already used!");
        }
        return storeInUse.createUser(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
    }

    /**
     * Deletes the user in the database.
     * <p>
     * This method drops the user from the database.
     * If the user is still logged in, he/she will be logged out before being deleted.
     *
     * @author Carsten Dekker
     * @see java.sql.SQLException
     * @since 2021-03-12
     */
    @Override
    public void dropUser(User userToDrop) throws Exception {
        Optional<User> user = storeInUse.findUser(userToDrop.getUsername());
        if (user.isEmpty()) {
            throw new UserManagementException("Username unknown!");
        }
        storeInUse.removeUser(userToDrop.getUsername());
    }

    /**
     * Updates the users email.
     * <p>
     * This method updates the mail from the User in the database. It throws an exception if the user is not present in
     * the database.
     *
     * @return A new UserDTO with the username and the mail address
     * @author Carsten Dekker
     * @see java.sql.SQLException
     * @since 2021-03-12
     */
    @Override
    public User updateUserMail(User toUpdateMail) throws Exception {
        Optional<User> user = storeInUse.findUser(toUpdateMail.getUsername());
        if (user.isEmpty()) {
            throw new UserManagementException("Username unknown!");
        }
        return storeInUse.updateUserMail(toUpdateMail.getUsername(), toUpdateMail.getEMail());
    }

    /**
     * Updates the users password.
     * <p>
     * This method updates the password from the User in the database. It throws an exception if the user is not present
     * in the database or if the password, that the user entered in the UserSettingsView, is not the same as the
     * currently used password.
     *
     * @return A new UserDTO with the username and the mail address
     * @author Carsten Dekker
     * @see java.sql.SQLException
     * @since 2021-03-12
     */
    @Override
    public User updateUserPassword(User toUpdatePassword, String currentPassword) throws Exception {
        Optional<User> user = storeInUse.findUser(toUpdatePassword.getUsername(), currentPassword);
        if (user.isEmpty()) {
            throw new UserManagementException("Username unknown!");
        }
        return storeInUse.updateUserPassword(toUpdatePassword.getUsername(), toUpdatePassword.getPassword());
    }

    /**
     * Updates the users pictureID
     * <p>
     * This method updates the pictureID from the user in the database. It shows an exception, if the user is not
     * present in the database.
     *
     * @author Carsten Dekker
     * @param toUpdatePicture the new user object that contains the new profilePictureID
     * @return A new UserDTO with the username and the profile pictureID
     * @see java.sql.SQLException
     * @since 2021-04-15
     */
    @Override
    public User updateUserPicture(User toUpdatePicture) throws Exception {
        Optional<User> user = storeInUse.findUser(toUpdatePicture.getUsername());
        if (user.isEmpty()) {
            throw new UserManagementException("Username unknown!");
        }
        return storeInUse.updateUserPicture(toUpdatePicture.getUsername(), toUpdatePicture.getProfilePictureID());
    }

    @Override
    public void logout(User user) {
        loggedInUsers.remove(user.getUsername());
    }

    /**
     * Selects the Mail and the profilePictureId from the database.
     * <p>
     * This method selects the mail and the profilePictureID from the User and creates a new UserDTO with the information
     * and an empty password. The UserDTO gets returned to the UserService.
     *
     * @return A new UserDTO with the username, the mail address and the profilePictureID
     * @author Carsten Dekker
     * @see java.sql.SQLException
     * @since 2021-03-12
     */
    @Override
    public User retrieveUserInformation(User toGetInformation) throws Exception {
        Optional<User> user = storeInUse.findUser(toGetInformation.getUsername());
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserManagementException("Username unknown!");
        }
    }

    /**
     * Retrieves all Users
     * <p>
     * This function returns a List of all Users.
     *
     * @author
     * @see Exception
     * @since
     */
    @Override
    public List<User> retrieveAllUsers() throws SQLException {
        return storeInUse.getAllUsers();
    }
}
