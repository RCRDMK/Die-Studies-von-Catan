package de.uol.swp.server.usermanagement.store;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class SQLBasedUserStore extends AbstractUserStore implements UserStore {


    private Connection connection;
    private Statement statement;
    private static final Logger LOG = LogManager.getLogger(SQLBasedUserStore.class);

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
        String CONNECTION = "jdbc:mysql://134.106.11.89:50102";
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
    public Optional<User> findUser(String username, String password) throws Exception {
        String findUser = "select name, mail, pictureID from userData where name=? and password=?;";
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(findUser);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOG.debug("Fehler bei der Datenbankabfrage!");
            e.printStackTrace();
        }
        if (resultSet.next()) {
            User user = new UserDTO(username,"", resultSet.getString(2), resultSet.getInt(3));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findUser(String username) throws Exception {
        String findUser = "select name, mail, pictureID from userData where name=?;";
        ResultSet resultSet = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(findUser);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            LOG.debug("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
        }
        if (resultSet.next()) {
            User user = new UserDTO(username,"", resultSet.getString(2), resultSet.getInt(3));
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Creates a New User
     * <p>
     * This method creates a new User in the Database. It throws an exception if the username is already used in
     * the database.
     *
     * @return A new UserDTO with the username, password and the mail address
     * @see java.sql.SQLException
     */

    @Override
    public User createUser(String username, String password, String eMail) throws Exception {
        String getUsername = "select name from userData where name = ?;";
        ResultSet resultSet;
        try {
            PreparedStatement userName = connection.prepareStatement(getUsername);
            userName.setString(1, username);
            resultSet = userName.executeQuery();

            if (!resultSet.next()) {
                String insertUser = "insert into userData(name, password, mail) values (?,?,?);";
                userName = connection.prepareStatement(insertUser);
                userName.setString(1, username);
                userName.setString(2, password);
                userName.setString(3, eMail);

                userName.executeUpdate();
            } else {
                throw new Exception("Username already used!");
            }
        } catch (SQLException e) {
            LOG.error("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
        }
        return new UserDTO(username, "", eMail);
    }

    @Override
    public void removeUser(String username) throws Exception {
        String selectUserString = "select name from userData where name =?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(selectUserString);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                throw new Exception("Username unknown!");
            } else {
                preparedStatement = connection.prepareStatement("delete from userData where name=?;");
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            LOG.debug(e);
            throw new Exception("User could not be dropped!");
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
    public User updateUserMail(String username, String eMail) throws SQLException {
        ResultSet resultSet;
        PreparedStatement updateUserMail;
        try {
            String getUser = "select * from userData where name=?;";
            updateUserMail = connection.prepareStatement(getUser);
            updateUserMail.setString(1, username);
            resultSet = updateUserMail.executeQuery();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new SQLException("Username unknown!");
        }
        if (resultSet.next()) {
            try {
                String updateUserString = "update userData set mail=? where name=?;";
                updateUserMail = connection.prepareStatement(updateUserString);
                updateUserMail.setString(1, eMail);
                updateUserMail.setString(2, username);
                updateUserMail.executeUpdate();
            } catch (SQLException e) {
                LOG.debug(e);
                throw new SQLException("Username unknown!");
            }
        } else {
            throw new SQLException("Username unknown!");
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
    public User updateUserPassword(String username, String password) throws SQLException {
        ResultSet resultSet;
        PreparedStatement updateUserPassword;
        try {
            String getUser = "select * from userData where name=?;";
            updateUserPassword = connection.prepareStatement(getUser);
            updateUserPassword.setString(1, username);
            resultSet = updateUserPassword.executeQuery();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new SQLException("Username unknown!");
        }
        if (resultSet.next()) {
                try {
                    String updateUserString = "update userData set password=? where name=?;";
                    updateUserPassword = connection.prepareStatement(updateUserString);
                    updateUserPassword.setString(1, password);
                    updateUserPassword.setString(2, username);
                    updateUserPassword.executeUpdate();
                } catch (SQLException e) {
                    LOG.debug(e);
                    throw new SQLException("Username unknown!");
                }
        } else {
            throw new SQLException("Username unknown!");
        }
        return new UserDTO(username, "", "");
    }

    /**
     * Updates the users pictureID
     * <p>
     * This method updates the pictureID from the user in the database. It shows an exception, if the user is not
     * present in the database.
     *
     * @author Carsten Dekker
     * @param username name of the updated user
     * @param profilePictureID the new profilePictureID
     * @return A new UserDTO with the username and the profile pictureID
     * @see java.sql.SQLException
     * @since 2021-04-15
     */
    @Override
    public User updateUserPicture(String username, int profilePictureID) throws SQLException {
        ResultSet resultSet;
        PreparedStatement updateUserPicture;
        try {
            String getUser = "select * from userData where name=?;";
            updateUserPicture = connection.prepareStatement(getUser);
            updateUserPicture.setString(1, username);
            resultSet = updateUserPicture.executeQuery();
        } catch (SQLException e) {
            LOG.debug(e);
            throw new SQLException("Username unknown!");
        }
        int newPictureID = 1;
        if (resultSet.next()) {
            try {

                String updateUserString = "update userData set pictureID=? where name=?;";
                updateUserPicture = connection.prepareStatement(updateUserString);
                updateUserPicture.setInt(1, profilePictureID);
                updateUserPicture.setString(2, username);
                updateUserPicture.executeUpdate();
            } catch (SQLException e) {
                LOG.debug(e);
                throw new SQLException("Username unknown!");
            }
        } else {
            throw new SQLException("Username unknown!");
        }
        return new UserDTO(username, "", "", profilePictureID);
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
    public User retrieveUserInformation(String username) throws Exception {
        ResultSet resultSet;
        PreparedStatement preparedStatement;
        try {
            String selectMail = "select mail, pictureID from userData where name = ? ;";
            preparedStatement = connection.prepareStatement(selectMail);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            LOG.debug(e);
            throw new Exception("Username unknown");
        }
        if (resultSet.next())
            return new UserDTO(username, "", resultSet.getString(1), resultSet.getInt(2));
        else {
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }
}
