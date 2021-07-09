package de.uol.swp.server.usermanagement;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import de.uol.swp.common.user.User;

/**
 * Maintains a list of active users to log out inactive users
 * The Timestamp comes from the PingRequest
 *
 * @author Philip
 * @since 2021-01-22
 */

public class ActiveUserList {

    Hashtable<User, Long> activeUserTable = new Hashtable<>();
    List<User> userToLogoutList = new ArrayList<>();

    /**
     * Handles a list of active users
     * <p>
     * If a user does not send a ping message to the server for
     * more than 60 seconds, the user is logged out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public void updateActiveUser(User user, Long time) {
        activeUserTable.put(user, time);
    }

    /**
     * Handles a list of active users
     * <p>
     * Adds a user to the list when they log in.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public void addActiveUser(User user) {
        activeUserTable.put(user, System.currentTimeMillis());
    }

    /**
     * Handles a list of active users
     * <p>
     * Adds a user to the list when they log out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public void removeActiveUser(User user) {
        activeUserTable.remove(user);
    }

    /**
     * Handles a list of active users
     * <p>
     * Check whether a user has not sent a ping message for more than 60 seconds and creates a list from these.
     * This list is then returned.
     *
     * @return List of Users to Drop
     * @author Philip
     * @since 2021-01-22
     */

    public List<User> checkActiveUser() {
        userToLogoutList.clear();
        Enumeration<User> enu = activeUserTable.keys();
        while (enu.hasMoreElements()) {
            User user = enu.nextElement();
            long t2 = activeUserTable.get(user);
            if ((System.currentTimeMillis() - t2) >= 60000) {
                userToLogoutList.add(user);
            }
        }
        return (userToLogoutList);
    }
}
