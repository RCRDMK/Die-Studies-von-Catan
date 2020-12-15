package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * A request send from client to server, trying to delete the users
 * account
 *
 * @author Carsten Dekker
 * @since  2020-12-15
 */
public class DropUserRequest extends AbstractRequestMessage {

    final private User user;

    /**
     * Constructor
     *
     * @param user the deleted user
     * @since  2020-12-15
     */
    public DropUserRequest(User user) {
        this.user = user;
    }

    /**
     * Getter for the user
     *
     * @return user that gets deleted
     * @since  2020-12-15
     */

    public User getUser() {
        return user;
    }

}
