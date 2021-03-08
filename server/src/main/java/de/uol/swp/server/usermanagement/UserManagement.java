package de.uol.swp.server.usermanagement;

import com.google.common.base.Strings;
import com.mysql.cj.log.Log;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.store.UserStore;
import io.netty.handler.logging.LogLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.Opt;

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
    private static Timer timer = new Timer();

    /**
     * Constructor
     *
     * @author Marius Birk
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
                ActiveUserList.addActiveUser(username);
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

    @Override
    public User updateUser(User userToUpdate) throws SQLException {
        ResultSet resultSet;
        PreparedStatement updateUser;
        try {
            String getUser = "select * from user where name=?;";
            updateUser = connection.prepareStatement(getUser);
            updateUser.setString(1, userToUpdate.getUsername());
            resultSet = updateUser.executeQuery();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new UserManagementException("Username unknown!");
        }
        // Only update if there are new values
        String newPassword = "";
        String newEMail = "";

        if (resultSet.next()) {
            try {
                newPassword = firstNotNull(userToUpdate.getPassword(), resultSet.getString("password"));
                newEMail = firstNotNull(userToUpdate.getEMail(), resultSet.getString("mail"));
                String updateUserString = "update user set password=?, mail=? where name=?;";
                updateUser = connection.prepareStatement(updateUserString);
                updateUser.setString(1, userToUpdate.getPassword());
                updateUser.setString(2, userToUpdate.getEMail());
                updateUser.setString(3, userToUpdate.getUsername());

                updateUser.executeUpdate();
            } catch (SQLException e) {
                LOG.debug(e);
                throw new UserManagementException("Username unknown!");
            }
        } else {
            throw new UserManagementException("User unknown!");
        }
        return new UserDTO(userToUpdate.getUsername(), newPassword, newEMail);
    }

    @Override
    public void dropUser(User userToDrop) throws SQLException {
        String selectUserString = "select name from user where name =?;";
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
     * @since 2019-08-05
     */
    private String firstNotNull(String firstValue, String secondValue) {
        return Strings.isNullOrEmpty(firstValue) ? secondValue : firstValue;
    }

    @Override
    public void logout(User user) {
        loggedInUsers.remove(user.getUsername());
        ActiveUserList.removeActiveUser(user.getUsername());
    }

    /**
     * Logout User
     * <p>
     * Logs a User out. This methode is called when a User sends for more than 60 seconds no Ping message.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void pingLogout(String username) {
        ActiveUserList.removeActiveUser(username);
        loggedInUsers.remove(username);
    }

    /**
     * Starts a Ping Timer
     * <p>
     * Starts a Ping Timer which checks every 30 seconds if the Users
     * are still Online with a start delay of 30 seconds.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void startTimerForActivUserList() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                List<String> userToDrop = ActiveUserList.checkActiveUser();
                if (userToDrop.size() >= 1) {
                    for (int i = 0; i < userToDrop.size(); i++) {
                        pingLogout(userToDrop.get(i));
                    }
                }
            }
        }, 30000, 30000);
    }

    /**
     * Stops the Ping Timer
     * <p>
     * Stops the Ping Timer which checks every 30 seconds if the Users are still Online.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public void endTimerForPing() {
        timer.cancel();
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
}
