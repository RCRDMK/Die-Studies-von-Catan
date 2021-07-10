package de.uol.swp.server.usermanagement;


import java.sql.SQLException;
import java.util.List;

import de.uol.swp.common.user.User;

abstract class AbstractUserManagement implements ServerUserService {
    public abstract List<User> retrieveAllUsers() throws SQLException;
}
