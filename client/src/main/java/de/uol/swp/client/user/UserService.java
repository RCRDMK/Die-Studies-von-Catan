package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Hex;
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

    /**
     * Constructor
     *
     * @param bus The  EventBus set in ClientModule
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
     * @param username the name of the user
     * @param password the password of the user
     * @since 2017-03-17
     */
    @Override
    public void login(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        password = convertStringToHash(password);
        LoginRequest msg = new LoginRequest(username, password);
        bus.post(msg);
    }


    @Override
    public void logout(User username) {
        LogoutRequest msg = new LogoutRequest();
        bus.post(msg);
    }

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

    @Override
    public void updateUser(User user) throws InvalidKeySpecException, NoSuchAlgorithmException {
        User hashedPassword = new UserDTO(user.getUsername(), convertStringToHash(user.getPassword()), user.getEMail());
        UpdateUserRequest request = new UpdateUserRequest(hashedPassword);
        bus.post(request);
    }

    @Override
    public void retrieveAllUsers() {
        RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
        bus.post(cmd);
    }

    /**
     * Method to return a hashed password. It creates a char array out of the original password and hands this over to the
     * hashPassword method.
     * @since 2020-03-04
     * @author Marius Birk
     * @param password
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public String convertStringToHash(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return Hex.encodeHexString(hashPassword(password.toCharArray()));
    }

    /**
     * This method creates an byte array of the given Password. With help of the salt key and the keyfactory,
     * it creates a hashed password in form of a secretkey.
     * @since 2020-03-04
     * @author Marius Birk
     * @param password
     * @return encoded Password
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private byte[] hashPassword(final char[] password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        final String SALT = "saltKey";
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        PBEKeySpec spec = new PBEKeySpec(password, SALT.getBytes(), 10000, 512);
        SecretKey key = secretKeyFactory.generateSecret(spec);

        return key.getEncoded();
    }
}
