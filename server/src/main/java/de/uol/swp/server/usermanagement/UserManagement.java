package de.uol.swp.server.usermanagement;

import com.google.common.base.Strings;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.store.UserStore;
import io.netty.handler.logging.LogLevel;
import org.checkerframework.checker.nullness.Opt;

import javax.inject.Inject;
import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Handles most user related issues e.g. login/logout
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.usermanagement.AbstractUserManagement
 * @since 2019-08-05
 */
public class UserManagement extends AbstractUserManagement {

    private final String CONNECTION = "jdbc:mysql://134.106.11.89:50101/user_store";
    private Connection connection;
    private Statement statement;
    private UserStore userStore;
    private final SortedMap<String, User> loggedInUsers = new TreeMap<>();

    /**
     * Constructor
     *
     * @param userStore object of the UserStore to be used
     * @see de.uol.swp.server.usermanagement.store.UserStore
     * @since 2019-08-05
     */
    @Inject
    public UserManagement(UserStore userStore) {
        this.userStore = userStore;
    }


    @Override
    public User login(String username, String password) throws SQLException {
        if (!password.isEmpty() || !password.isBlank() || password == null) {
            ResultSet resultSet = statement.executeQuery("select name from user where name='" + username + "' and password='" + password + "';");
            User user = new UserDTO(username, password, "");
            if (resultSet.next()) {
                this.loggedInUsers.put(username, user);
                return user;
            } else {
                throw new SecurityException("Cannot auth user " + username);
            }

        } else {
            throw new SecurityException("Cannot auth user " + username);
        }
    }

    /**
     * Build Connection
     * <p>
     * This method will build up a connection to our database at the University Servers.
     * It can be used everywhere, where the usermanagement is used and needed. But most times
     * the connection is already opened and would only close if the server is going to shut down.
     *
     * @author Marius Birk
     * @since 2021-01-15
     */

    public void buildConnection() throws SQLException {
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        connection = DriverManager.getConnection("jdbc:mysql://134.106.11.89:50101", "root", "SWP2020j");
        statement = connection.createStatement();
        statement.execute("use user_store;");
    }

    /**
     * Closes Connection
     * <p>
     * This method will be closing the connection between database and server application. If the server is going to be shut down,
     * it will close the connection to the database.
     *
     * @author Marius Birk
     * @since 2021-01-15
     */
    public void closeConnection() throws SQLException {
        statement.close();
        connection.close();
    }

    @Override
    public boolean isLoggedIn(User username) {
        return loggedInUsers.containsKey(username.getUsername());
    }

    @Override
    public User createUser(User userToCreate) throws SQLException {

        ResultSet resultSet = statement.executeQuery("select name from user where name = '" + userToCreate.getUsername() + "';");

        if (!resultSet.next()) {
            statement.executeUpdate("insert into user(name, password) values ('" + userToCreate.getUsername() + "','" + userToCreate.getPassword() + "');");
        } else {
            throw new UserManagementException("Username already used!");
        }
        return new UserDTO(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
    }

    @Override
    public User updateUser(User userToUpdate) throws SQLException {
        Optional<User> user = userStore.findUser(userToUpdate.getUsername());
        if (!user.isPresent()) {
            throw new UserManagementException("Username unknown!");
        }
        // Only update if there are new values
        String newPassword = firstNotNull(userToUpdate.getPassword(), user.get().getPassword());
        String newEMail = firstNotNull(userToUpdate.getEMail(), user.get().getEMail());
        return userStore.updateUser(userToUpdate.getUsername(), newPassword, newEMail);

    }

    @Override
    public void dropUser(User userToDrop) throws SQLException {
        Optional<User> user = userStore.findUser(userToDrop.getUsername());
        if (!user.isPresent()) {
            throw new UserManagementException("Username unknown!");
        }
        userStore.removeUser(userToDrop.getUsername());
    }

    /**
     * Sub-function of update user
     * <p>
     * This method is used to set the new user values to the old ones if the values
     * in the update request were empty.
     *
     * @param firstValue  value to update to, empty String or null
     * @param secondValue the old value
     * @return String containing the value to be used in the update command
     * @since 2019-08-05
     */
    private String firstNotNull(String firstValue, String secondValue) {
        return Strings.isNullOrEmpty(firstValue) ? secondValue : firstValue;
    }

    @Override
    public void logout(User user) {
        loggedInUsers.remove(user.getUsername());
    }

    @Override
    public List<User> retrieveAllUsers() throws SQLException {
        return userStore.getAllUsers();
    }
}
