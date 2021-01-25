package de.uol.swp.server.usermanagement;

import java.util.Hashtable;

/**
 * Maintains a list of active users to log out inactive users
 *
 * @author Philip
 * @since 2021-01-22
 */

public class ActivUserList {

    private static Hashtable activUserTable = new Hashtable();

    /**
     * Handles a list of active users
     *
     * If a user does not send a ping message to the server for
     * more than 60 seconds, the user is logged out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void updateActivUser (String user, Long time){
        activUserTable.put(user, time);
        while (activUserTable.elements().hasMoreElements()) {
            long t1 = System.currentTimeMillis();
            long t2 = (long) activUserTable.elements().nextElement();
            if ((t1 - t2) >= 60000) {
              //  UserManagement.logout(user);
            }
        }
    }

    /**
     * Handles a list of active users
     *
     * Adds a user to the list when they log in.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void addActivUser (String user){
        activUserTable.put(user, System.currentTimeMillis());
    }

    /**
     * Handles a list of active users
     *
     * Adds a user to the list when they log out.
     *
     * @author Philip
     * @since 2021-01-22
     */

    public static void removeActivUser (String user){
        activUserTable.remove(user);
    }
}
