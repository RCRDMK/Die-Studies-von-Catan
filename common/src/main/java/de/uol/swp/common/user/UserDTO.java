package de.uol.swp.common.user;

import java.util.Objects;

/**
 * Objects of this class are used to transfer user data between the server and the
 * clients.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.request.RegisterUserRequest
 * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
 * @since 2019-08-13
 */
public class UserDTO implements User {

    private final String username;
    private final String password;
    private final String eMail;
    private int profilePictureID;

    /**
     * Constructor
     *
     * @param username username of the user
     * @param password password the user uses
     * @param eMail    email address the user is registered to
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    public UserDTO(String username, String password, String eMail) {
        assert Objects.nonNull(username);
        assert Objects.nonNull(password);
        assert Objects.nonNull(eMail);
        this.username = username;
        this.password = password;
        this.eMail = eMail;
    }

    /**
     * Overloaded Constructor
     * <p>
     *
     * @param username  username of the user
     * @param password  password of the user
     * @param eMail     email address of the user
     * @param pictureID pictureID of the chosen profilePicture
     * @author Carsten Dekker
     * @since 2021-04-15
     */
    public UserDTO(String username, String password, String eMail, int pictureID) {
        assert Objects.nonNull(username);
        assert Objects.nonNull(password);
        assert Objects.nonNull(eMail);
        this.username = username;
        this.password = password;
        this.eMail = eMail;
        this.profilePictureID = pictureID;
    }

    /**
     * Copy constructor
     *
     * @param user User object to copy the values of
     * @return UserDTO copy of User object
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    public static UserDTO create(User user) {
        return new UserDTO(user.getUsername(), user.getPassword(), user.getEMail());
    }

    /**
     * Copy constructor leaving password variable empty
     * <p>
     * This constructor is used for the user list, because it would be a major security
     * flaw to send all user data including passwords to everyone connected.
     *
     * @param user User object to copy the values of
     * @return UserDTO copy of User object having the password variable left empty
     * @author Marco Grawunder
     * @since 2019-08-13
     */
    public static UserDTO createWithoutPassword(User user) {
        return new UserDTO(user.getUsername(), "", user.getEMail());
    }


    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEMail() {
        return eMail;
    }

    @Override
    public User getWithoutPassword() {
        return new UserDTO(username, "", eMail, profilePictureID);
    }

    @Override
    public int getProfilePictureID() {
        return profilePictureID;
    }

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserDTO)) {
            return false;
        }
        return Objects.equals(this.username, ((UserDTO) obj).username);
    }

}
