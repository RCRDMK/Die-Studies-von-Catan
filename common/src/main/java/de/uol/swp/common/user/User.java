package de.uol.swp.common.user;

import java.io.Serializable;

/**
 * Interface for different kinds of user objects.
 * <p>
 * This interface is for unifying different kinds of user objects throughout the
 * project. With this being the base project it is currently only used for the UserDTO
 * objects.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.UserDTO
 * @since 2019-08-05
 */
public interface User extends Serializable, Comparable<User> {

    /**
     * Getter for the username variable
     *
     * @author Marco Grawunder
     * @return username of the user as String
     * @author Marco Grawunder
     * @since 2019-08-05
     */
    String getUsername();

    /**
     * Getter for the password variable
     *
     * @author Marco Grawunder
     * @return password of the user as String
     * @author Marco Grawunder
     * @since 2019-08-05
     */
    String getPassword();

    /**
     * Getter for the email variable
     *
     * @author Marco Grawunder
     * @return email address of the user as String
     * @author Marco Grawunder
     * @since 2019-08-05
     */
    String getEMail();

    /**
     * Creates a duplicate of this object leaving its password empty
     *
     * @author Marco Grawunder
     * @return Copy of this with empty password field
     * @author Marco Grawunder
     * @since 2019-08-05
     */
    User getWithoutPassword();

    int getProfilePictureID();

    void setProfilePictureID(int profilePictureID);
}
