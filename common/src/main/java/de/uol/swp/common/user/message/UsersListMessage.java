package de.uol.swp.common.user.message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uol.swp.common.message.AbstractServerMessage;

/**
 * A message containing all current logged in usernames
 *
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public class UsersListMessage extends AbstractServerMessage {

    private static final long serialVersionUID = -7968574381977330152L;
    private final ArrayList<String> users;

    /**
     * Constructor
     *
     * @param users List containing all users currently logged in
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    public UsersListMessage(List<String> users) {
        this.users = new ArrayList<>(users);
    }

    /**
     * Getter for the List containing all users currently logged in
     *
     * @return List containing all users currently logged in
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    @Override
    public int hashCode() {
        return Objects.hash(users);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        UsersListMessage that = (UsersListMessage) o;
        return Objects.equals(users, that.users);
    }
}
