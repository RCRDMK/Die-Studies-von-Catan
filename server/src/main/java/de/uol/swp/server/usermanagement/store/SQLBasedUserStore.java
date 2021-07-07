package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * SQLBasedUserStore class
 * <p>
 * UserStoreClass for all database operations, like building and closing the connection,
 * creating, finding, updating or deleting a user.
 *
 * @author Marius Birk
 * @see de.uol.swp.server.usermanagement.store.AbstractUserStore
 * @see de.uol.swp.server.usermanagement.store.UserStore
 * @since 2021-01-15
 */
public class SQLBasedUserStore extends AbstractUserStore implements UserStore {

    private Connection connection;
    private Statement statement;
    private final String CONNECTION = "jdbc:mysql://178.238.232.242:3306?autoReconnect=true";
    private static final Logger LOG = LogManager.getLogger(SQLBasedUserStore.class);

    /**
     * Build Connection
     * <p>
     * This method will build up a connection to our database at the University Servers.
     * It can be used everywhere, where the userManagement is used and needed. But most times
     * the connection is already opened and would only close if the server is going to shut down.
     *
     * @author Marius Birk
     * @since 2021-01-15
     */
    public void buildConnection() throws SQLException {
        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        connection = DriverManager.getConnection(CONNECTION, "swpJ", "Uz3FLt2cgMmFCALY");
        statement = connection.createStatement();
        statement.execute("use swpJ;");
        LOG.debug("Building connection to database.");
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
        LOG.debug("Connection to the database closed.");
    }

    /**
     * Finds a user with given username and password
     * <p>
     * This function is building a connection to the database and is querying a select statement
     * to find a given user with the passed username and password arguments.
     * If the user is found this function returns the User object.
     * If not it will return an Optional.empty() object
     *
     * @author Marius Birk
     * @see Exception
     * @since 2021-01-15
     */
    @Override
    public Optional<User> findUser(String username, String password) throws Exception {
        ResultSet resultSet;
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select name, password, mail, pictureID from userData where name=? and password=?;");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (password.equals(resultSet.getString("password"))) {
                    User user = new UserDTO(username, password, resultSet.getString(3), resultSet.getInt(4));
                    return Optional.of(user.getWithoutPassword());
                }
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            LOG.debug("Fehler bei der Datenbankabfrage!");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return Optional.empty();
    }

    /**
     * Finds a user with given username
     * <p>
     * This function is building a connection to the database and is querying a select statement
     * to find a given user with the passed username argument.
     * If the user is found this function returns the User object.
     * If not it will return an Optional.empty() object
     *
     * @author Marius Birk
     * @see Exception
     * @since 2021-01-15
     */
    @Override
    public Optional<User> findUser(String username) throws Exception {
        ResultSet resultSet;
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select name, mail, pictureID from userData where name=?;");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new UserDTO(username, "", resultSet.getString(2), resultSet.getInt(3));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            LOG.debug("Fehler bei der Datenbankabfrage!");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return Optional.empty();
    }

    /**
     * Creates a New User
     * <p>
     * This method creates a new user in the database. It throws an exception if the username is already used in
     * the database.
     *
     * @return A new UserDTO with the username, password and the mail address
     * @author Marius Birk
     * @see java.sql.SQLException
     * @since 2021-01-15
     */

    @Override
    public User createUser(String username, String password, String eMail) throws Exception {
        try {
            buildConnection();
            PreparedStatement userName = connection.prepareStatement("insert into userData(name, password, mail) values (?,?,?);");
            userName.setString(1, username);
            userName.setString(2, password);
            userName.setString(3, eMail);
            userName.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("User could not be created.");
        } finally {
            closeConnection();
        }
        return new UserDTO(username, "", eMail);
    }

    /**
     * Removes a user from the database
     * <p>
     * This function is building a connection to the database and is querying a delete statement
     * to remove a user entry with the given username.
     *
     * @author Marius Birk
     * @see Exception
     * @since 2021-01-15
     */
    @Override
    public void removeUser(String username) throws Exception {
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("delete from userData where name=?;");
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new Exception("User could not be dropped!");
        } finally {
            closeConnection();
        }
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
    public User updateUserMail(String username, String eMail) throws Exception {
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("update userData set mail=? where name=?;");
            preparedStatement.setString(1, eMail);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("EMail could not be updated.");
        } finally {
            closeConnection();
        }
        return new UserDTO(username, "", eMail);
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
    public User updateUserPassword(String username, String password) throws Exception {
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("update userData set password=? where name=?;");
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new SQLException("Password could not be updated.");
        } finally {
            closeConnection();
        }
        return new UserDTO(username, "", "");
    }

    /**
     * Updates the users pictureID
     * <p>
     * This method updates the pictureID from the user in the database. It shows an exception, if the user is not
     * present in the database.
     *
     * @param username         name of the updated user
     * @param profilePictureID the new profilePictureID
     * @return A new UserDTO with the username and the profile pictureID
     * @author Carsten Dekker
     * @see java.sql.SQLException
     * @since 2021-04-15
     */
    @Override
    public User updateUserPicture(String username, int profilePictureID) throws Exception {
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("update userData set pictureID=? where name=?;");
            preparedStatement.setInt(1, profilePictureID);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new SQLException("Profile picture could not be updated.");
        } finally {
            closeConnection();
        }
        return new UserDTO(username, "", "", profilePictureID);
    }

    /**
     * Gets all Users from the database
     * <p>
     * This function is building a connection to the database and is querying a select statement
     * to find all userData from the database.
     * Foreach userData in the database it adds the corresponding user to an ArrayList and afterwards returns it.
     *
     * @author Marius Birk
     * @see SQLException
     * @since 2021-01-15
     */
    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        try {
            buildConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select * from userData;");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userList.add(new UserDTO(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4)));
            }
            return userList;
        } catch (SQLException e) {
            LOG.debug("");
            throw new SQLException();
        } finally {
            closeConnection();
        }
    }
}
