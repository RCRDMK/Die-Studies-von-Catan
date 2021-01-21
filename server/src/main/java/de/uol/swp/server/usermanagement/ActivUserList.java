package de.uol.swp.server.usermanagement;

import de.uol.swp.common.user.User;
import java.util.Hashtable;
import java.util.Optional;

public class ActivUserList {

    private Hashtable activUserTable = new Hashtable();

    public void addActivUser (User user){
        activUserTable.put(user, System.currentTimeMillis());
    }

    public void updateActivUser (User user){
        activUserTable.put(user, System.currentTimeMillis());
        while (activUserTable.elements().hasMoreElements()) {
            long t1 = System.currentTimeMillis();
            long t2 = (long) activUserTable.elements().nextElement();
            if ((t1 - t2) >= 60000) {
                UserManagement.logout(user);
            }
        }
    }

    public void removeActivUser (User user){
        activUserTable.remove(user);
    }
}
