package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;

import de.uol.swp.common.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.Objects;
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
    public Optional<User> findUser(String username, String password) {

    }


    @Override
    public Optional<User> findUser(String username, String password) {
        User usr = users.get(username);
        if (usr != null && Objects.equals(usr.getPassword(), hash(password))) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public User login(String username, String password) throws SQLException {
        if (!password.isEmpty() && !password.isBlank()) {
            String login = "select name, mail, pictureID from userData where name=? and password=?;";
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
                User user = new UserDTO(username, password, resultSet.getString(2), resultSet.getInt(3));
                this.loggedInUsers.put(username, user);
                return user;
            } else {
                throw new SecurityException("Cannot auth user " + username);
            }
        } else {
            throw new SecurityException("Cannot auth user " + username);
        }

    }

}
