package de.uol.swp.server.usermanagement;


import de.uol.swp.common.user.User;

import java.sql.SQLException;

abstract class AbstractUserManagement implements ServerUserService {
    public abstract User updateUserPicture(User toUpdatePicture) throws SQLException;
}
