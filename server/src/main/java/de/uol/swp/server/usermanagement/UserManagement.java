package de.uol.swp.server.usermanagement;

import com.google.common.base.Strings;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.sql.*;
import java.util.*;


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

    private final String CONNECTION = "jdbc:mysql://134.106.11.89:50102";
    private Connection connection;
    private Statement statement;
    private static final Logger LOG = LogManager.getLogger(UserManagement.class);
    private static final SortedMap<String, User> loggedInUsers = new TreeMap<>();


    /**
     * Constructor
     *
     * @author Marius Birk
     * @see de.uol.swp.server.usermanagement.store.UserStore
     * @since 2019-08-05
     * <p>
     * The constructor changed to an empty constructor. The usual store is not longer needed.
     * @since 2021-01-19
     */
    @Inject
    public UserManagement() {
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
        connection = DriverManager.getConnection(CONNECTION, "root", "SWP2020j");
        statement = connection.createStatement();
        statement.execute("use user_store;");
    }

    /**
     * Closes Connection
     * <p>
     * This method will be closing the connection between database and server application.
     * If the server is going to be shut down,
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
    public User login(String username, String password) throws SQLException {
        if (!password.isEmpty() || !password.isBlank() || password == null) {
            String login = "select name, mail from user where name=? and password=?;";
            ResultSet resultSet = null;
            try {
                PreparedStatement loginUser = connection.prepareStatement(login);
                loginUser.setString(1, username);
                loginUser.setString(2, password);
                resultSet = loginUser.executeQuery();
            } catch (SQLException e) {
                LOG.debug("Fehler bei der Datenbankabfrage");
                e.printStackTrace();
            }
            if (resultSet.next()) {
                User user = new UserDTO(username, password, resultSet.getString(2));
                this.loggedInUsers.put(username, user);
                return user;
            } else {
                throw new SecurityException("Cannot auth user " + username);
            }
        } else {
            throw new SecurityException("Cannot auth user " + username);
        }

    }

    @Override
    public boolean isLoggedIn(User username) {
        return loggedInUsers.containsKey(username.getUsername());
    }

    @Override
    public User createUser(User userToCreate) {
        String getUsername = "select name from user where name = ?;";
        ResultSet resultSet;
        try {
            PreparedStatement userName = connection.prepareStatement(getUsername);
            userName.setString(1, userToCreate.getUsername());
            resultSet = userName.executeQuery();

            if (!resultSet.next()) {
                String insertUser = "insert into user(name, password, mail) values (?,?,?);";
                userName = connection.prepareStatement(insertUser);
                userName.setString(1, userToCreate.getUsername());
                userName.setString(2, userToCreate.getPassword());
                userName.setString(3, userToCreate.getEMail());

                userName.executeUpdate();
            } else {
                throw new UserManagementException("Username already used!");
            }
        } catch (SQLException e) {
            LOG.error("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
        }
        return new UserDTO(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
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
    public User updateUserMail(User toUpdateMail) throws SQLException {
        ResultSet resultSet;
        PreparedStatement updateUserMail;
        try {
            String getUser = "select * from user where name=?;";
            updateUserMail = connection.prepareStatement(getUser);
            updateUserMail.setString(1, toUpdateMail.getUsername());
            resultSet = updateUserMail.executeQuery();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new UserManagementException("Username unknown!");
        }
        String newEMail = "";
        if (resultSet.next()) {
            try {
                newEMail = firstNotNull(toUpdateMail.getEMail(), resultSet.getString("mail"));
                String updateUserString = "update user set mail=? where name=?;";
                updateUserMail = connection.prepareStatement(updateUserString);
                updateUserMail.setString(1, toUpdateMail.getEMail());
                updateUserMail.setString(2, toUpdateMail.getUsername());
                updateUserMail.executeUpdate();
            } catch (SQLException e) {
                LOG.debug(e);
                throw new UserManagementException("Username unknown!");
            }
        } else {
            throw new UserManagementException("Username unknown!");
        }
        return new UserDTO(toUpdateMail.getUsername(), toUpdateMail.getPassword(), newEMail);
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
    public User updateUserPassword(User toUpdatePassword, String currentPassword) throws SQLException {
        ResultSet resultSet;
        PreparedStatement updateUserPassword;
        try {
            String getUser = "select * from user where name=?;";
            updateUserPassword = connection.prepareStatement(getUser);
            updateUserPassword.setString(1, toUpdatePassword.getUsername());
            resultSet = updateUserPassword.executeQuery();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new UserManagementException("Username unknown!");
        }
        String newPassword = "";
        if (resultSet.next()) {
            if (resultSet.getString(2).equals(currentPassword)) {
                try {
                    newPassword = firstNotNull(toUpdatePassword.getPassword(), resultSet.getString("password"));
                    String updateUserString = "update user set password=? where name=?;";
                    updateUserPassword = connection.prepareStatement(updateUserString);
                    updateUserPassword.setString(1, toUpdatePassword.getPassword());
                    updateUserPassword.setString(2, toUpdatePassword.getUsername());
                    updateUserPassword.executeUpdate();
                } catch (SQLException e) {
                    LOG.debug(e);
                    throw new UserManagementException("Username unknown!");
                }
            } else {
                throw new UserManagementException("The Send Password is not equal to the current password!");
            }
        } else {
            throw new UserManagementException("Username unknown!");
        }
        return new UserDTO(toUpdatePassword.getUsername(), newPassword, toUpdatePassword.getEMail());
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
    public void dropUser(User userToDrop) throws SQLException {
        String selectUserString = "select name from user where name =?;";
        if (isLoggedIn(userToDrop)) {
            logout(userToDrop);
        }
        try {
            PreparedStatement dropUser = connection.prepareStatement(selectUserString);
            dropUser.setString(1, userToDrop.getUsername());
            ResultSet resultSet = dropUser.executeQuery();
            if (!resultSet.next()) {
                throw new UserManagementException("Username unknown!");
            } else {
                dropUser = connection.prepareStatement("delete from user where name=?;");
                dropUser.setString(1, userToDrop.getUsername());
                dropUser.executeUpdate();
            }

        } catch (SQLException e) {
            LOG.debug(e);
            throw new UserManagementException("User could not be dropped!");
        }
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
     * @author Marco Grawunder
     * @since 2019-08-05
     */
    private String firstNotNull(String firstValue, String secondValue) {
        return Strings.isNullOrEmpty(firstValue) ? secondValue : firstValue;
    }

    @Override
    public void logout(User user) {
        loggedInUsers.remove(user.getUsername());
    }

    /**
     * Enhanced/Changed method
     * <p>
     * Changed some parts of this method to make it possible to work with our SQL database.
     * Now we are handeling a List of Users and retrieving all users out of our database.
     * The next step is to copy all of the retrieved users in a LinkedList and to return it.
     *
     * @return List of Users out of the database
     * @author Marius Birk
     * @see java.sql.SQLException
     * @see java.util.LinkedList
     */
    @Override
    public List<User> retrieveAllUsers() throws SQLException {
        List<User> userList = new LinkedList<>();
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            String selectAllUsers = "select * from user;";
            preparedStatement = connection.prepareStatement(selectAllUsers);
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            LOG.debug(e);
            throw new UserManagementException("Could not retrieve all users.");
        }
        while (resultSet.next()) {
            userList.add(new UserDTO(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
        }
        return userList;
    }

    /**
     * Selects the Mail from the database.
     * <p>
     * This method selects the mail from the User and creates a new UserDTO with the mail and an empty password.
     * The UserDTO gets returned to the UserService.
     *
     * @return A new UserDTO with the username and the mail address
     * @author Carsten Dekker
     * @see java.sql.SQLException
     * @since 2021-03-12
     */
    @Override
    public User retrieveUserMail(User toGetInformation) throws SQLException {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            String selectMail = "select mail from user where name = ? ;";
            preparedStatement = connection.prepareStatement(selectMail);
            preparedStatement.setString(1, toGetInformation.getUsername());
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            LOG.debug(e);
            throw new UserManagementException("Username unknown");
        }
        if (resultSet.next()) {
            return new UserDTO(toGetInformation.getUsername(), "", resultSet.getString(1));
        } else {
            return null;
        }
    }
}
