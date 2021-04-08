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
     * @author Marco Grawunder
     * @param username username of the user
     * @param password password the user uses
     * @param eMail email address the user is registered to
     * @since 2019-08-13
     */
    public UserDTO(String username, String password, String eMail) {
        assert Objects.nonNull(username);
        assert Objects.nonNull(password);
        assert Objects.nonNull(eMail);
        this.username = username;
        this.password = password;
        this.eMail = eMail;
        this.profilePictureID = 1;
    }

    /**
     * Copy constructor
     *
     * @author Marco Grawunder
     * @param user User object to copy the values of
     * @return UserDTO copy of User object
     * @since 2019-08-13
     */
    public static UserDTO create(User user) {
        return new UserDTO(user.getUsername(), user.getPassword(), user.getEMail());
    }

    /**
     * Copy constructor leaving password variable empty
     *
     * This constructor is used for the user list, because it would be a major security
     * flaw to send all user data including passwords to everyone connected.
     *
     * @author Marco Grawunder
     * @param user User object to copy the values of
     * @return UserDTO copy of User object having the password variable left empty
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
        return new UserDTO(username, "", eMail);
    }

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.getUsername());
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof UserDTO)){
            return false;
        }
        return Objects.equals(this.username, ((UserDTO)obj).username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }

    public int getProfilePictureID() {
        return profilePictureID;
    }

    public void setProfilePictureID(int profilePictureID) {
        this.profilePictureID = profilePictureID;
    }
}
