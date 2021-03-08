package de.uol.swp.server.usermanagement;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Maintains a list of active users to log out inactive users
 * The Timestamp comes from the PingRequest
 *
 * @author Philip
 * @since 2021-01-22
 */

public class ActiveUserList {

    static Hashtable<String, Long> activeUserTable = new Hashtable<>();
    static List<String> userToDropList = new ArrayList<String>();

    /**
     * Handles a list of active users
     * <p>
     * If a user does not send a ping message to the server for
     * more than 60 seconds, the user is logged out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void updateActiveUser(String username, Long time) {
        activeUserTable.put(username, time);
    }

    /**
     * Handles a list of active users
     * <p>
     * Adds a user to the list when they log in.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void addActiveUser(String username) {
        activeUserTable.put(username, System.currentTimeMillis());
    }

    /**
     * Handles a list of active users
     * <p>
     * Adds a user to the list when they log out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void removeActiveUser(String username) {
        activeUserTable.remove(username);
    }

    /**
     * Handles a list of active users
     * <p>
     * Check whether a user has not sent a ping message for more than 60 seconds.
     *
     * @author Philip
     * @since 2021-01-22
     * @return
     */

    public static List<String> checkActiveUser() {
        Enumeration<String> enu = activeUserTable.keys();
        while (enu.hasMoreElements()) {
            String username = enu.nextElement();
            long t2 = activeUserTable.get(username);
            if ((System.currentTimeMillis() - t2) >= 60000) {
                userToDropList.add(username);
            }
        }
        return(userToDropList);
    }
}
