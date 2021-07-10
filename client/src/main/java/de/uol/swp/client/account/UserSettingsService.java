package de.uol.swp.client.account;

import java.util.regex.Pattern;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.RetrieveUserInformationRequest;

/**
 * Class that manages the UserSettingsPresenter
 *
 * @author Carsten Dekker
 * @since 2021-03-09
 */
@SuppressWarnings("UnstableApiUsage")
public class UserSettingsService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @author Carsten Dekker
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-03-09
     */
    @Inject
    public UserSettingsService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Method called after pressing the settings Button in the main menu scene
     * <p>
     * This method creates a new RetrieveUserMailRequest with the logged in user
     * and post in on the bus.
     *
     * @param user the logged in user
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.UserSettingsPresenter
     * @see RetrieveUserInformationRequest
     * @since 2021-03-14
     */
    public void retrieveUserMail(User user) {
        RetrieveUserInformationRequest retrieveUserInformationRequest = new RetrieveUserInformationRequest(user);
        eventBus.post(retrieveUserInformationRequest);
    }

    /**
     * Method called after pressing the Confirm Button
     * <p>
     * If an E-Mail is not valid, this method will return false.
     *
     * @author Carsten Dekker
     * @see de.uol.swp.client.account.UserSettingsPresenter
     * @since 2021-03-14
     */
    public boolean isValidEmailAddress(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null) { return false; }
        return pat.matcher(email).matches();
    }


}
