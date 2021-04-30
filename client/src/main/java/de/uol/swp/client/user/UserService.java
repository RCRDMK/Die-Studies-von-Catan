package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used to hide the communication details
 * implements de.uol.common.user.UserService
 *
 * @author Marco Grawunder
 * @see ClientUserService
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class UserService implements ClientUserService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);
    private final EventBus bus;
    private static Timer timer = new Timer();

    /**
     * Constructor
     *
     * @author Marco Grawunder
     * @param bus The  EventBus set in ClientModule
     * @author Marco Grawunder
     * @see de.uol.swp.client.di.ClientModule
     * @since 2017-03-17
     */
    @Inject
    public UserService(EventBus bus) {
        this.bus = bus;
        // Currently not need, will only post on bus
        //bus.register(this);
    }

    /**
     * Posts a login request to the EventBus
     *
     * @author Marco Grawunder
     * @param username the name of the user
     * @param password the password of the user
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    @Override
    public void login(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        password = convertStringToHash(password);
        LoginRequest msg = new LoginRequest(username, password);
        bus.post(msg);
    }

    /**
     * Posts a logout request to the EventBus
     *
     * @param username the name of the user
     */

    @Override
    public void logout(User username) {
        LogoutRequest msg = new LogoutRequest();
        bus.post(msg);
    }

    /**
     * Posts a create user request to the EventBus
     *
     * @param user the name of the user
     */


    @Override
    public void createUser(User user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User hashedPassword = new UserDTO(user.getUsername(), convertStringToHash(user.getPassword()), user.getEMail());
        RegisterUserRequest request = new RegisterUserRequest(hashedPassword);
        bus.post(request);
    }

    /**
     * Method to delete an users account and log the user out
     * <p>
     * This method sends a request to logout a user and delete an users account.
     * The requests are of the type DropUserRequest and LogoutRequest.
     *
     * @param user The user to remove
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.DropUserRequest
     * @since 2020-12-15
     */
    public void dropUser(User user) {
        DropUserRequest dropUserRequest = new DropUserRequest(user);
        LogoutRequest logoutRequest = new LogoutRequest();
        bus.post(logoutRequest);
        bus.post(dropUserRequest);
    }

    /**
     * Method to update the password of an user
     * <p>
     * This method sends a request to update the password of the user. The new password gets hashed.
     * The requests is of the type UpdateUserPasswordRequest.
     *
     * @param user The user to update
     * @param currentPassword the currently used password
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.UpdateUserPasswordRequest
     * @since 2020-03-14
     */
    @Override
    public void updateUserPassword(User user, String currentPassword) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User hashedPassword = new UserDTO(user.getUsername(), convertStringToHash(user.getPassword()), user.getEMail());
        String hashedCurrentPassword = convertStringToHash(currentPassword);
        UpdateUserPasswordRequest request = new UpdateUserPasswordRequest(hashedPassword, hashedCurrentPassword);
        bus.post(request);
    }

    /**
     * Method to update the email of the user
     * <p>
     * This method sends a request to update the email of the user.
     * The requests is of the type UpdateUserMailRequest.
     *
     * @param user The user to update
     * @author Carsten Dekker
     * @see de.uol.swp.common.user.request.UpdateUserMailRequest
     * @since 2020-03-14
     */
    @Override
    public void updateUserMail(User user) {
        UpdateUserMailRequest request = new UpdateUserMailRequest(user);
        bus.post(request);
    }

    /**
     * Method to update the profilePicture of the user
     * <p>
     * This method sends a request to update the profilePicture of the currently
     * logged in user. The request is of the type UpdateUserProfilePictureRequest.
     *
     * @author Carsten Dekker
     * @param user the user to update
     * @see de.uol.swp.common.user.request.UpdateUserProfilePictureRequest
     * @since 2021-04-15
     */
    @Override
    public void updateUserProfilePicture(User user) {
        UpdateUserProfilePictureRequest uuppr = new UpdateUserProfilePictureRequest(user);
        bus.post(uuppr);
    }


    @Override
    public void retrieveAllUsers() {
        RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
        bus.post(cmd);
    }

    // TODO JavaDoc unvollständig
    /**
     * Method to return a hashed password. It creates a char array out of the original password and hands this over to the
     * hashPassword method.
     *
     * @param password
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @author Marius Birk
     * @since 2021-03-04
     */
    public String convertStringToHash(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return Hex.encodeHexString(hashPassword(password.toCharArray()));
    }

    // TODO JavaDoc unvollständig
    /**
     * This method creates an byte array of the given Password. With help of the salt key and the keyfactory,
     * it creates a hashed password in form of a secretkey.
     *
     * @param password
     * @return encoded Password
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @author Marius Birk
     * @since 2021-03-04
     */
    private byte[] hashPassword(final char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final String SALT = "saltKey";
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(password, SALT.getBytes(), 10000, 512);
        SecretKey key = secretKeyFactory.generateSecret(spec);

        return key.getEncoded();
    }

    /**
     * Method to send a Ping
     * <p>
     * This method sends a request for a Ping Message.
     *
     * @param user from which the ping message is released
     * @author Philip Nitsche
     * @since 2021-01-22
     */

    public void sendPing(User user) {
        PingRequest pr = new PingRequest(user, System.currentTimeMillis());
        bus.post(pr);
    }

    /**
     * Method to send a Ping
     * <p>
     * This method starts a Timer for a Ping Message.
     *
     * @param user from which the ping message is released
     * @author Philip Nitsche
     * @since 2021-01-22
     */
    @Override
    public void startTimerForPing(User user) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPing(user);
            }
        }, 30000, 30000);
    }

    // TODO JavaDoc unvollständig
    /**
     * Method to send a Ping
     * <p>
     * This method stops the Timer for a Ping Message.
     *
     * @param
     * @author Philip Nitsche
     * @since 2021-01-22
     */
    @Override
    public void endTimerForPing() {
        timer.cancel();
    }
}
