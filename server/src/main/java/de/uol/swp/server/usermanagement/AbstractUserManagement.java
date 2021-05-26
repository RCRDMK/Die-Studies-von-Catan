package de.uol.swp.server.usermanagement;


import de.uol.swp.common.user.User;

import java.sql.SQLException;
import java.util.List;

abstract class AbstractUserManagement implements ServerUserService {
    public abstract List<User> retrieveAllUsers() throws SQLException;
}
