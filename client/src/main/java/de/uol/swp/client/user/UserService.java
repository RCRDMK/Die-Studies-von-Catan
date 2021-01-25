package de.uol.swp.client.user;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * This class is used to hide the communication details
 * implements de.uol.common.user.UserService
 *
 * @author Marco Grawunder
 * @see ClientUserService
 * @since 2017-03-17
 *
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
	public void login(String username, String password){
		LoginRequest msg = new LoginRequest(username, password);
		bus.post(msg);
	}


	@Override
	public void logout(User username){
		LogoutRequest msg = new LogoutRequest();
		bus.post(msg);
	}

	@Override
	public void createUser(User user) {
		RegisterUserRequest request = new RegisterUserRequest(user);
		bus.post(request);
	}

	/**
	 * Method to delete an users account and log the user out
	 *
	 * This method sends a request to logout a user and delete an users account.
	 * The requests are of the type DropUserRequest and LogoutRequest.
	 *
	 * @author Carsten Dekker
	 * @param user The user to remove
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
	public void updateUser(User user) {
		UpdateUserRequest request = new UpdateUserRequest(user);
		bus.post(request);
	}

	@Override
	public void retrieveAllUsers() {
		RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
		bus.post(cmd);
	}

	public void ping(User user) {
		PingRequest pr = new PingRequest(user, System.currentTimeMillis());
		bus.post(pr);
	}
}
