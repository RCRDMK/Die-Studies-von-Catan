package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This is a user store.
 *
 * This user store needs a connection to the database at the university servers at duemmer.informatik.uni-oldenburg.de.
 * The user data will not longer be deleted if the server needs to restart and will not longer be saved in the RAM of the
 * computer where the server runs.
 *
 * @implNote This store will never return the password of a user!
 * @see de.uol.swp.server.usermanagement.store.AbstractUserStore
 * @see de.uol.swp.server.usermanagement.store.UserStore
 * @since 2021-01-18
 * @author Marius Birk
 * */

public class SqlUserStore extends AbstractUserStore implements UserStore {
    private Statement statement;

    @Override
    public Optional<User> findUser(String username, String password) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select name, password from user where name='"+username+"' and password='"+password+"';");
        User newUser = new UserDTO(resultSet.getString("name"), resultSet.getString("password"), resultSet.getString("mail"));
        return Optional.of(newUser);
    }

    @Override
    public Optional<User> findUser(String username) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select name from user where name='"+username+"';");
        User newUser = new UserDTO(resultSet.getString("name"),"","");
        return Optional.of(newUser);
    }

    @Override
    public User createUser(String username, String password, String eMail) throws SQLException {
        statement.executeUpdate("insert into user values ('"+username+"', '"+password+"', '"+eMail+"');");
        User newUser = new UserDTO(username, password, eMail);
        return newUser;
    }

    @Override
    public User updateUser(String username, String password, String eMail) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select name from user where name='"+username+"';");
        if(resultSet.next()){
            statement.executeUpdate("update user set name='"+username+"', password='"+password+"', mail='"+eMail+"' where name='"+username+"';");
        }else {

        }
        User newUser = new UserDTO(username, password, eMail);
        return newUser;
    }

    @Override
    public void removeUser(String username) throws SQLException {
        statement.executeUpdate("delete from user where name='"+username+"';");
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from user;");
        List<User> userList = new LinkedList<>();

        while(resultSet.next()){
            userList.add(new UserDTO(resultSet.getString("name"), resultSet.getString("password"), resultSet.getString("mail")));
        }

        return userList;
    }
}
