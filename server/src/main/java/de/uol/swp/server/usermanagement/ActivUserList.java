package de.uol.swp.server.usermanagement;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Maintains a list of active users to log out inactive users
 * The Timestamp comes from the PingRequest
 *
 * @author Philip
 * @since 2021-01-22
 */

public class ActivUserList {

    private static Hashtable activUserTable = new Hashtable();

    /**
     * Handles a list of active users
     * <p>
     * If a user does not send a ping message to the server for
     * more than 60 seconds, the user is logged out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void updateActivUser(String username, Long time) {
        activUserTable.put(username, time);
    }

    /**
     * Handles a list of active users
     * <p>
     * Adds a user to the list when they log in.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void addActivUser(String username) {
        activUserTable.put(username, System.currentTimeMillis());
    }

    /**
     * Handles a list of active users
     * <p>
     * Adds a user to the list when they log out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void removeActivUser(String username) {
        activUserTable.remove(username);
    }

    public static void checkActivUser() {
        Enumeration enu = activUserTable.keys();
        while (enu.hasMoreElements()) {
            String username = enu.nextElement().toString();
            long t2 = (long) activUserTable.get(username);
            if (t2 >= 60000) {
                UserManagement.pingLogout(username);
            }
        }
    }
}
