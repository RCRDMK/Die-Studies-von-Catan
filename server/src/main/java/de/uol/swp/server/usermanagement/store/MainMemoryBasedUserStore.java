package de.uol.swp.server.usermanagement.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.base.Strings;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

/**
 * This is a user store.
 * <p>
 * This is the user store that is used for the start of the software project. The
 * user accounts in this user store only reside within the RAM of your computer
 * and only for as long as the server is running. Therefore the users have to be
 * added every time the server is started.
 *
 * @author Marco Grawunder
 * @implNote This store will never return the password of a user!
 * @see de.uol.swp.server.usermanagement.store.AbstractUserStore
 * @see de.uol.swp.server.usermanagement.store.UserStore
 * @since 2019-08-05
 */

public class MainMemoryBasedUserStore extends AbstractUserStore implements UserStore {

    private final Map<String, User> users = new HashMap<>();

    @Override
    public Optional<User> findUser(String username, String password) {
        User usr = users.get(username);
        if (usr != null && Objects.equals(usr.getPassword(), hash(password))) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findUser(String username) {
        User usr = users.get(username);
        if (usr != null) {
            return Optional.of(usr.getWithoutPassword());
        }
        return Optional.empty();
    }

    @Override
    public User createUser(String username, String password, String eMail) {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalArgumentException("Username must not be null");
        }
        User usr = new UserDTO(username, hash(password), eMail, 1);
        users.put(username, usr);
        return usr.getWithoutPassword();
    }

    @Override
    public void removeUser(String username) {
        users.remove(username);
    }

    @Override
    public User updateUserMail(String username, String eMail) {
        User usr = users.get(username);
        users.put(username, new UserDTO(username, usr.getPassword(), eMail, usr.getProfilePictureID()));
        return users.get(username).getWithoutPassword();
    }

    @Override
    public User updateUserPassword(String username, String password) {
        User usr = users.get(username);
        users.put(username, new UserDTO(username, hash(password), usr.getEMail(), usr.getProfilePictureID()));
        return users.get(username).getWithoutPassword();
    }

    @Override
    public User updateUserPicture(String username, int profilePictureID) {
        User usr = users.get(username);
        users.put(username, new UserDTO(username, usr.getPassword(), usr.getEMail(), profilePictureID));
        return users.get(username).getWithoutPassword();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> retUsers = new ArrayList<>();
        users.values().forEach(u -> retUsers.add(u.getWithoutPassword()));
        return retUsers;
    }
}
