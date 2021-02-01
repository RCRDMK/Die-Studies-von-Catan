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
 * @see de.uol.swp.server.usermanagement.AbstractUserManagement
 * @since 2019-08-05
 *
 * Enhanced this class to make it possible to work with our database at the servers of the university.
 * With that it is possible to retrieve, update, delete or insert users. Also it is possible to login/logout.
 * @since 2021-01-19
 * @author Marius Birk
 */
public class UserManagement extends AbstractUserManagement {

    private final String CONNECTION = "jdbc:mysql://134.106.11.89:50102/user_store";
    private Connection connection;
    private Statement statement;
    private static final Logger LOG = LogManager.getLogger(UserManagement.class);
    private final SortedMap<String, User> loggedInUsers = new TreeMap<>();

    /**
     * Constructor
     *
     * @see de.uol.swp.server.usermanagement.store.UserStore
     * @since 2019-08-05
     *
     * The constructor changed to an empty constructor. The usual store is not longer needed.
     * @since 2021-01-19
     * @author Marius Birk
     */
    @Inject
    public UserManagement() {
    }


    @Override
    public User login(String username, String password) throws SQLException {
        if (!password.isEmpty() || !password.isBlank() || password == null) {
            ResultSet resultSet = statement.executeQuery("select name, mail from user where name='" + username + "' and password='" + password + "';");
            if (resultSet.next()) {
                User user = new UserDTO(username, password, resultSet.getString(2));
                this.loggedInUsers.put(username, user);
                ActivUserList.addActivUser(username);
                return user;
            } else {
                throw new SecurityException("Cannot auth user " + username);
            }

        } else {

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
        connection = DriverManager.getConnection("jdbc:mysql://134.106.11.89:50102", "root", "SWP2020j");
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
            statement.executeUpdate("insert into user(name, password, mail) values ('" + userToCreate.getUsername() + "','" + userToCreate.getPassword() + "','"+userToCreate.getEMail()+"');");
        } else {
            throw new UserManagementException("Username already used!");
        }
        return new UserDTO(userToCreate.getUsername(), userToCreate.getPassword(), userToCreate.getEMail());
    }

    @Override
    public User updateUser(User userToUpdate) throws SQLException {
        ResultSet resultSet;
        try{
            resultSet = statement.executeQuery("select * from user where name='" + userToUpdate.getUsername() + "';");
        }catch (Exception e){
            LOG.debug(e);
            throw new UserManagementException("Username unknown!");
        }
        // Only update if there are new values
        String newPassword="";
        String newEMail="";
        if(resultSet.next()){
            newPassword = firstNotNull(userToUpdate.getPassword(), resultSet.getString("password"));
            newEMail = firstNotNull(userToUpdate.getEMail(), resultSet.getString("mail"));
        }
        statement.executeUpdate("update user set password='"+newPassword+"', mail='"+newEMail+"' where name='"+userToUpdate.getUsername()+"';");

        return new UserDTO(userToUpdate.getUsername(), newPassword, newEMail);

    }

    @Override
    public void dropUser(User userToDrop) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select name from user where name ='" + userToDrop.getUsername() + "';");
        if (!resultSet.next()) {
            throw new UserManagementException("Username unknown!");
        }else{
            statement.executeUpdate("delete from user where name='"+userToDrop.getUsername()+"';");
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
        ActivUserList.removeActivUser(user.toString());
    }

    /**
     * Enhanced/Changed method
     * <p>
     *  Changed some parts of this method to make it possible to work with our SQL database.
     *  Now we are handeling a List of Users and retrieving all users out of our database.
     *  The next step is to copy all of the retrieved users in a LinkedList and to return it.
     *
     * @see java.sql.SQLException
     * @see java.util.LinkedList
     * @return List of Users out of the database
     * @author Marius Birk
     */
    @Override
    public List<User> retrieveAllUsers() throws SQLException {
        List<User> userList = new LinkedList<>();
        ResultSet resultSet;
        try{
            resultSet = statement.executeQuery("select * from user;");
        }catch (Exception e){
            LOG.debug(e);
            throw new UserManagementException("Could not retrieve all users.");
        }
        while(resultSet.next()){
            userList.add(new UserDTO(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
        }
        return userList;
    }
}
